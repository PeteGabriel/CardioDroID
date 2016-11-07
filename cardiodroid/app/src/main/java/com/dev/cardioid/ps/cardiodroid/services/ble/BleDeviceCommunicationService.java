package com.dev.cardioid.ps.cardiodroid.services.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.events.BleConnectionBreakdownEvent;
import com.dev.cardioid.ps.cardiodroid.events.BleEventBus;
import com.dev.cardioid.ps.cardiodroid.events.ExhaustionStateAvailableEvent;
import com.dev.cardioid.ps.cardiodroid.events.NewRegisteredUserIdEvent;
import com.dev.cardioid.ps.cardiodroid.receivers.IdProcessAnswerHandler;
import com.dev.cardioid.ps.cardiodroid.receivers.NewUserIdHandlerReceiver;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.GATT_SERVER;

/**
 * This component represents a bound service and it is used to
 * accomplish communication between our application and the ble peripheral device.
 *
 *
 */
public class BleDeviceCommunicationService extends Service {

  private static final String TAG = Utils.makeLogTag(BleDeviceCommunicationService.class);

  private BluetoothAdapter mBluetoothAdapter;

  private Handler mHandler;

  private BluetoothDevice mDeviceToConnect;

  public static final String SAVED_INTENT_EXTRA_KEY = "extra.from.intent.key";

  @Override public void onCreate() {
    super.onCreate();

    mBluetoothAdapter = ((CardioDroidApplication)getApplication()).getBluetoothAdapter();

    mHandler = new Handler(getMainLooper());
  }

  private boolean isBound = false;

  public boolean areDevicesBounded() {
    final BluetoothManager bluetoothManager =
        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    boolean b = bluetoothManager.getConnectionState(mDeviceToConnect, GATT_SERVER) == BluetoothAdapter.STATE_CONNECTED;
    return b;
  }

  /**
   * Manage the BLE service
   */
  public class LocalBinder extends Binder {
    public BleDeviceCommunicationService getService() {
      return BleDeviceCommunicationService.this;
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    connectToDeviceFound(intent.getStringExtra(SAVED_INTENT_EXTRA_KEY));
    return binder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return super.onUnbind(intent);
  }

  private final IBinder binder = new LocalBinder();



  private BluetoothGatt mGatt;

  private void connectToDeviceFound(String addr) {
    if (mGatt == null) {
        mDeviceToConnect = mBluetoothAdapter.getRemoteDevice(addr);
        Log.d(TAG, "Remote Device With Address: " + mDeviceToConnect.getName() + " - " + mDeviceToConnect.getAddress());
        mGatt = mDeviceToConnect.connectGatt(getApplicationContext(), false, gattCallback);
    }
  }


  private final int BLANK_MODE = -1;
  private final int IN_AUTH_MODE = 0;
  private final int IN_REGISTER_MODE = 1;
  private final int IN_ID_MODE = 2;

  private int inMode;

  private void changeMode(final int mode){
    inMode = mode;
  }


  /************* Methods provided as a comunication interface by this Bound Service **************/

  private boolean sendWriteOperation(UUID characteristicID, String value){
    BluetoothGattService service = mGatt.getService(BleDefinedUuid.Service.CARDIO_SERVICE);
    if (service == null){
      return false;
    }
    BluetoothGattCharacteristic arpChar = service.getCharacteristic(characteristicID);
    arpChar.setValue(value);
    return mGatt.writeCharacteristic(arpChar);
  }


  public boolean initRegisterProcess() {
    return sendWriteOperation(BleDefinedUuid.Characteristic.ACCESS_RIGHTS_PROCESS,
        BleDefinedUuid.Protocol.R_PROCESS);
  }

  public boolean initAuthIdentificationProcess(){
    return sendWriteOperation(BleDefinedUuid.Characteristic.ACCESS_RIGHTS_PROCESS,
        BleDefinedUuid.Protocol.A_PROCESS);
  }

  public boolean initIdentificationProcess() {
    return sendWriteOperation(BleDefinedUuid.Characteristic.ACCESS_RIGHTS_PROCESS,
        BleDefinedUuid.Protocol.I_PROCESS);
  }

  /***********************************************************************************************/



  /**
   * Public API for the Bluetooth GATT Profile.
   * This class provides Bluetooth GATT functionality to enable communication
   * with Bluetooth Smart or Smart Ready devices.
   */
  private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

    /**
     * Estes membros formam um algoritmo de state-machine que visa
     * possibilitar que um cliente (android app) possa registar o seu
     * interesse em receber notificações sempre que o valor de uma
     * dada caracteristica é modificado.
     *
     * Para registar este interesse é necessário escrever um descriptor
     * com o valor "0x2902" numa dada caracteristica. No entanto, várias
     * escritas em série não são suportadas pela API, fazendo com que
     * seja o programador a "sincronizar" cada uma delas. Assim, após cada
     * escrita de um descriptor, a maq-de-estados avança e possibilita novamente
     * uma nova escrita. Este algoritmo também tira partido da implementação concreta
     * que realizámos no dispositivo BLE uma vez que sabemos exactamente qual caracteristica
     * estará disponivel num dado estado. Conclui-se que, o algoritmo é dependente da implementação
     * do dispositivo BLE.
     */
    private int mStateOfNotification = 0;

    private void reset() { mStateOfNotification = 0; }

    private void advance() { mStateOfNotification++; }


    /*
    ATTENTION:
      Despite the pattern present in the names of each method ("on.."),
      the callbacks defined here DO NOT RUN IN THE MAIN THREAD (THREAD UI).
      Use the HandlerThread from onCreate to communicate with it.
     */

    /**
     * Callback triggered as a result of a remote characteristic notification.
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic) {
      Log.d(TAG, "Characteristic "+characteristic.getUuid() + " changed value.");

      if (characteristic.getUuid().equals(BleDefinedUuid.Characteristic.AUTH_ID_MESSENGER_CHAR)){
        String value = Utils.bytesToString(characteristic.getValue());
        switch (inMode){
          case IN_REGISTER_MODE:
            handleRegisterMode(value);
            break;
          case IN_AUTH_MODE:
            handleAuthMode(value);
            break;
          case IN_ID_MODE:
            handleIdentificationMode(value);
            break;
        }
      }

      if (characteristic.getUuid().equals(BleDefinedUuid.Characteristic.EXHAUSTION_MEASUREMENT_RATE)){
        String value = Utils.bytesToString(characteristic.getValue());
        Log.d(TAG, "Novo Estado de Fadiga: " + value);
        BleEventBus.getInstance().post(new ExhaustionStateAvailableEvent(value));
      }
    }


    /**
     * Callback indicating the result of a characteristic write operation.
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic, int status) {
      Log.d(TAG, "onCharacteristicWrite");
      String value = Utils.bytesToString(characteristic.getValue());
      String log = String.format("Name: %s - New Value: %s", characteristic.getUuid(), value);
      Log.d(TAG, log);


      if (characteristic.getUuid().equals(BleDefinedUuid.Characteristic.ACCESS_RIGHTS_PROCESS)){
        actIfWriteOverARP(value);

        if (inMode == IN_AUTH_MODE){ //send ID
          String idValue = PreferencesUtils.getRegisteredUserId(getApplicationContext());
          Log.d(TAG, "Sending USER_ID through AIM: " + idValue);
          writeIntoCharacteristic(BleDefinedUuid.Characteristic.AUTH_ID_MESSENGER_CHAR, idValue);
        }
      }
    }

    /**
     * Callback invoked when the list of remote services, characteristics and descriptors
     * for the remote device have been updated, ie new services have been discovered.
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      Log.d(TAG, "Total Services Discovered: " + gatt.getServices().size());
      BluetoothGattService cardioDroidService = gatt.getService(BleDefinedUuid.Service.CARDIO_SERVICE);

      if (cardioDroidService == null) return;

      //begins the notification process.
      setNotifyNextSensor(gatt);
    }

    /**
     * Callback indicating when GATT client has connected/disconnected to/from a remote
     * GATT server.
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      Log.d(TAG, "Connection State Change: "+status+" && "+ newState);
      if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
        Log.d(TAG, "Connection State Change: GATT_SUCCESS  && STATE_CONNECTED");
        isBound = true;
        gatt.discoverServices();
      }else{
        isBound = false;
        if (newState == BluetoothProfile.STATE_DISCONNECTED){
          Log.d(TAG, "Connection State Change: DISCONNECTED");
          BleEventBus.getInstance().post(new BleConnectionBreakdownEvent());
        }else{
          Log.d(TAG, "Connection State Change: OTHER");
        }
      }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                  int status) {
      //one was already written, advance to the next state
      //and write another descriptor.
      advance();
      setNotifyNextSensor(gatt);
    }


    private void handleRegisterMode(String value) {
      Log.d(TAG, "New ID received after registration: " + value);
      BleEventBus.getInstance().post(new NewRegisteredUserIdEvent(value));
      sendBroadcast(NewUserIdHandlerReceiver.getHandlerIntent(value));
      changeMode(BLANK_MODE);
    }

    /**
     * Performs the authentication protocol's validation of the userID.
     * against the user id previously saved.
     *
     * @param userID
     *  the received user id
     */
    private void handleAuthMode(String userID) {
      Log.d(TAG, "Response received from AIM: " + userID);
      changeMode(BLANK_MODE);

      Intent notifyAnswerIntent = new Intent(IdProcessAnswerHandler.ID_PROCESS_ANSWER_RECEIVED_ACTION);
      notifyAnswerIntent.putExtra(IdProcessAnswerHandler.ID_PROCESS_ANSWER_RECEIVED_KEY, userID);
      sendBroadcast(notifyAnswerIntent);
    }

    /**
     * Performs the identification protocol's validation of the userID.
     * against the user id previously saved.
     *
     * @param userID
     *  the user id received
     */
    private void handleIdentificationMode(String userID) {
      Log.d(TAG, "ID received from CardioWheel (AIM): " + userID);
      String myUserId = PreferencesUtils.getRegisteredUserId(getApplicationContext());
      changeMode(BLANK_MODE);

      boolean wasIdentified = myUserId.equals(userID);
      String answer = wasIdentified ? BleDefinedUuid.Protocol.OK_ANSWER :
          BleDefinedUuid.Protocol.NOT_OK_ANSWER;
      //TODO obter string de forma dinamica

      final String message = wasIdentified ? "Identification successful." :
          "Identification not successful.";


      writeIntoCharacteristic(BleDefinedUuid.Characteristic.AUTH_ID_MESSENGER_CHAR, answer);
      mHandler.post(new Runnable() {
        @Override public void run() {
          ToastUtils.showShortMessage(message, getApplicationContext());
        }
      });

      if (wasIdentified){
        Intent notifyAnswerIntent = new Intent(IdProcessAnswerHandler.ID_PROCESS_ANSWER_RECEIVED_ACTION);
        notifyAnswerIntent.putExtra(IdProcessAnswerHandler.ID_PROCESS_ANSWER_RECEIVED_KEY, userID);
        sendBroadcast(notifyAnswerIntent);
      }

    }


    /**
     * Write a certain value to the characteristic with the given UUID.
     *
     * @param car
     *  UUID of a certain characteristic
     * @param value
     *  the value to be written
     */
    private void writeIntoCharacteristic(UUID car, String value){
      BluetoothGattService service = mGatt.getService(BleDefinedUuid.Service.CARDIO_SERVICE);
      if (service == null) return;
      BluetoothGattCharacteristic arpChar = service.getCharacteristic(car);
      arpChar.setValue(value);
      mGatt.writeCharacteristic(arpChar);
    }

    /**
     * Change mode according with the process indication given by parameter.
     * The mode helps the service knowing where it stands in terms of protocol procedures.
     *
     */
    private void actIfWriteOverARP(String value){
      switch (value){
        case BleDefinedUuid.Protocol.R_PROCESS:
          changeMode(IN_REGISTER_MODE);
          break;
        case BleDefinedUuid.Protocol.A_PROCESS:
          changeMode(IN_AUTH_MODE);
          break;
        case BleDefinedUuid.Protocol.I_PROCESS:
          changeMode(IN_ID_MODE);
          break;
        default:
          changeMode(BLANK_MODE);
      }
    }

    /**
     * It notifies a certain characteristic based upon a simple state-machine mechanism.
     *
     * @param gatt
     *  an instance of {@link BluetoothGatt} that represents the peripheral.
     */
    private void setNotifyNextSensor(BluetoothGatt gatt) {
      BluetoothGattService service = gatt.getService(BleDefinedUuid.Service.CARDIO_SERVICE);
      BluetoothGattCharacteristic characteristic;
      switch (mStateOfNotification) {
        case 0:
          Log.i(TAG, "Set notify EMR");
          characteristic = service
                  .getCharacteristic(BleDefinedUuid.Characteristic.EXHAUSTION_MEASUREMENT_RATE);
          break;
        case 1:
          Log.i(TAG, "Set notify AIM");
          characteristic = service
                  .getCharacteristic(BleDefinedUuid.Characteristic.AUTH_ID_MESSENGER_CHAR);
          break;
        case 2:
          Log.i(TAG, "Set notify ARP");
          characteristic = service
                  .getCharacteristic(BleDefinedUuid.Characteristic.ACCESS_RIGHTS_PROCESS);
          break;
        default:
          Log.i(TAG, "All Notifications Enabled");
          reset();
          return;
      }

      //Enable local notifications
      gatt.setCharacteristicNotification(characteristic, true);

      //delay is advised
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      //Enabled remote notifications
      BluetoothGattDescriptor desc = characteristic
              .getDescriptor(BleDefinedUuid.Descriptor.CHAR_CLIENT_CONFIG);
      desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      gatt.writeDescriptor(desc);
    }

  };

}
