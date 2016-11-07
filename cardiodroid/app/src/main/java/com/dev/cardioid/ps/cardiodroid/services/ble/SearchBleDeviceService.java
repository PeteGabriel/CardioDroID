package com.dev.cardioid.ps.cardiodroid.services.ble;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.activities.DashboardActivity;
import com.dev.cardioid.ps.cardiodroid.receivers.BleAddressFoundReceiver;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This service searchs for the BLE device with a certain
 *
 * Serviço que procura por um dispositivo Ble com um determinado nome e
 * tenta estabelecer uma ligação. Caso tenha sucesso, a instancia que representa
 * esta ligação é enviada para a Activity {@link DashboardActivity}.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SearchBleDeviceService extends Service {

  public static final String TAG = Utils.makeLogTag(SearchBleDeviceService.class);

  private BluetoothAdapter mBluetoothAdapter;

  //search party components
  private BluetoothLeScanner mLEScanner;
  private ScanSettings mScanSettings;
  private ArrayList<ScanFilter> mSearchFilters;

  private Handler mHandler;
  private Handler mUiHandler;

  private BluetoothGatt mGatt;

  private int mServiceId;



  @Override public void onCreate() {
    super.onCreate();

    mHandler = new Handler();
    mUiHandler = new Handler(getMainLooper());

    mBluetoothAdapter = ((CardioDroidApplication)getApplication()).getBluetoothAdapter();

    if (!(mBluetoothAdapter == null) && mBluetoothAdapter.isEnabled()) {
      Log.d(TAG, "Adapter is enabled. Setting up parameters for scan.");
      if (Build.VERSION.SDK_INT > 22) {
        setupSearchPartyM();
      }else{
        setupSearchParty();
      }
    }

  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    mServiceId = startId;
    Log.d(TAG, "ID: " + startId);
    scanLeDevice(true);
    return START_NOT_STICKY;
  }


  @Override
  public void onDestroy() {
    if (mGatt == null) {
      return;
    }
    mGatt.close();
    mGatt = null;
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void setupSearchPartyM(){
    mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
    mScanSettings = new ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setNumOfMatches(1)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .build();
    mSearchFilters = new ArrayList<>();
  }

  private void setupSearchParty(){
    mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
    mScanSettings = new ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .build();
    mSearchFilters = new ArrayList<>();
  }



  /**
   * Constante utilizada para indicar timeout de uma pesquisa
   * por um dispositivo BLE.
   *
   * Este valor deve ser entendido nas unidades de milisegundos (ms).
   */
  private static final long SCAN_PERIOD = 5000;

  /**
   * Com base no parametro fornecido, inicia ou termina uma pesquisa
   * de um dispositivo BLE. Se a pesquisa for iniciada, após um dado
   * intervalo de tempo é cancelada. Esta prática é aconcelhada
   * pela Google de forma a poupar recursos do dispositivo.
   */
  private void scanLeDevice(final boolean enable) {
    if (enable) {
      mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          Log.d("TAG", "Stopping scan after 5 secs (inside handler)");
          if (Build.VERSION.SDK_INT < 21) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
          } else {
            if (mLEScanner == null) return;
            mLEScanner.stopScan(mScanCallback);
          }
        }
      }, SCAN_PERIOD);

      if (Build.VERSION.SDK_INT < 21) {
        Log.d(TAG, "Starting scan for an API smaller than 21");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
      } else {
        Log.d(TAG, "Starting scan for an API bigger than 21");
        if (mLEScanner == null) return;
        mLEScanner.startScan(mSearchFilters, mScanSettings, mScanCallback);
      }
    } else {
      if (Build.VERSION.SDK_INT < 21) {
        Log.d(TAG, "Stopping scan for an API smaller than 21");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
      } else {
        Log.d(TAG, "Stopping scan for an API bigger than 21");
        if (mLEScanner == null) return;
        mLEScanner.stopScan(mScanCallback);
      }
    }
  }


  private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
      String advertisingContent = Utils.bytesToString(scanRecord);
      String name = device.getName(), address = device.getAddress();
      String log = String.format(Locale.getDefault(),
          "Device found: %s - %s - rssi: %d dbs - Advertisement Content: %s",
          name, address, rssi, advertisingContent);
      Log.d(TAG, log);

      if (name != null && name.equals(BleDefinedUuid.Service.CARDIO_SERVICE_NAME)){
        Intent sendAddressFoundEvent = new Intent(BleAddressFoundReceiver.DEVICE_ADDRESS_HANDLER_ACTION);
        sendAddressFoundEvent.putExtra(BleAddressFoundReceiver.DEVICE_ADDRESS_HANDLER_KEY, address);
        sendBroadcast(sendAddressFoundEvent);
        stopSelf(mServiceId);
      }
    }
  };


  /**
   * Bluetooth LE scan callbacks.
   * Scan results are reported using these callbacks.
   */
  private ScanCallback mScanCallback = new ScanCallback() {

    /**
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines how this callback was triggered. Could be one of
     * {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
     * {@link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
     * {@link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
     * @param result A Bluetooth LE scan result.
     */
    @Override public void onScanResult(int callbackType, ScanResult result) {
      Log.d(TAG, "Result: " + result.toString());
      Log.d(TAG, "Device Name: " + result.getDevice().getName());
      Log.d(TAG, "Device Address: " + result.getDevice().getAddress());
      Intent sendAddressFoundEvent = new Intent(BleAddressFoundReceiver.DEVICE_ADDRESS_HANDLER_ACTION);
      sendAddressFoundEvent.putExtra(BleAddressFoundReceiver.DEVICE_ADDRESS_HANDLER_KEY,
          result.getDevice().getAddress());
      sendBroadcast(sendAddressFoundEvent);
      stopSelf(mServiceId);
    }


    /**
     * Callback when batch results are delivered.
     *
     * @param results List of scan results that are previously scanned.
     */
    @Override public void onBatchScanResults(List<ScanResult> results) {
      for (ScanResult sr : results) {
        Log.d(TAG, "BatchScanResults - Results: " + sr.toString());
      }
    }

    /**
     * Callback when scan could not be started.
     */
    @Override public void onScanFailed(int errorCode) {
      Log.e("Scan Failed", "Error Code: " + errorCode);
      Intent sendAddressFoundEvent = new Intent(BleAddressFoundReceiver.DEVICE_ADDRESS_HANDLER_ACTION);
      sendBroadcast(sendAddressFoundEvent);
      stopSelf(mServiceId);
    }
  };








}
