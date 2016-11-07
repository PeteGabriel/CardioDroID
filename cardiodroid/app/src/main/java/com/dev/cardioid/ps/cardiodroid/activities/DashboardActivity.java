package com.dev.cardioid.ps.cardiodroid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.contexts.ContextEnvironment;
import com.dev.cardioid.ps.cardiodroid.contexts.validation.RulesValidator;
import com.dev.cardioid.ps.cardiodroid.events.BleConnectionBreakdownEvent;
import com.dev.cardioid.ps.cardiodroid.events.BleEventBus;
import com.dev.cardioid.ps.cardiodroid.events.ConnectivityChangeEvent;
import com.dev.cardioid.ps.cardiodroid.events.DeviceAddressEvent;
import com.dev.cardioid.ps.cardiodroid.events.ExhaustionStateAvailableEvent;
import com.dev.cardioid.ps.cardiodroid.events.NetworkConnEvent;
import com.dev.cardioid.ps.cardiodroid.events.NewRegisteredUserIdEvent;
import com.dev.cardioid.ps.cardiodroid.events.WeatherConditionSavedEvent;
import com.dev.cardioid.ps.cardiodroid.fragments.DashboardFragment;
import com.dev.cardioid.ps.cardiodroid.network.http.updates.UpdateWeatherStorageService;
import com.dev.cardioid.ps.cardiodroid.services.ble.BleDeviceCommunicationService;
import com.dev.cardioid.ps.cardiodroid.services.ble.SearchBleDeviceService;
import com.dev.cardioid.ps.cardiodroid.services.location.FetchAddressIntentService;
import com.dev.cardioid.ps.cardiodroid.utils.NetworkUtils;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Subscribe;
import java.text.DateFormat;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * This activity represents the dashboard panel where the user can see
 * the information about each aspect of the application's context.
 */
public class DashboardActivity extends SingleFragmentActivity
    implements OnConnectionFailedListener, ConnectionCallbacks,
    DashboardFragment.IRetryAction {

  /**
   * Debug purposes.
   */
  public static final String TAG = Utils.makeLogTag(DashboardActivity.class);

  /**
   * An instance of the hosted fragment.
   */
  private DashboardFragment mMonitorFragment;

  /**
   * An instance to collect information about the
   * device's location from Google Play Services.
   */
  private GoogleApiClient mLocationApiClient;
  private LocationRequest mLocationRequest;

  /**
   * A reference to the singleton instance of Application.
   */
  private CardioDroidApplication mApp;

  private ResultReceiver resultReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "OnCreate Call");

    //the singleton instance of Application
    mApp = (CardioDroidApplication) getApplicationContext();
    resultReceiver = new AddressResultReceiver(new Handler());

    mServiceIsBounded = false;

    //bind the bus to an instance of this class
    BleEventBus.getInstance().register(this);

    /*BluetoothAdapter bluetoothAdapter = mApp.getBluetoothAdapter();
    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()){ //&& NetworkUtils.isConnected(mApp)){
      Log.d(TAG, "Bluetooth Adapter NOT NULL");
      Log.d(TAG, "Initiating BLE Communication");
      initBleCommunication();
    }*/
  }

  //methods from super-class
  @Override protected Fragment createFragment() {
    mMonitorFragment = new DashboardFragment();
    return mMonitorFragment;
  }

  @Override protected void setupContentView() {
    setContentView(R.layout.activity_dashboard_layout);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  @Override protected int getFrameContainer() {
    return R.id.container;
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "OnStart Call");
    //guarantees android lifecycle correct execution
    initLocationApiService();
  }

  @Override protected void onStop() {
    super.onStop();
    Log.d(TAG, "OnStop Call");

    /*if (mLocationApiClient != null) {
     mLocationApiClient.disconnect();
    }*/
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "OnDestroy Call");
    if (mBoundedService != null){
      unbindService(mServiceConnection);
    }

    BleEventBus.getInstance().unregister(this);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.dashboard_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      //go to settings
      case R.id.settings_dashboard_icon:
        startActivity(new Intent(this, UserPreferencesActivity.class));
        return true;

      //go to manage rules panel
      case R.id.manage_rules_dashboard_icon:
        startActivity(new Intent(this, RuleManagerActivity.class));
        return true;

      //face-based UI
      case R.id.see_the_faces_dashboard_icon:
        startActivity(new Intent(this, FaceStateActivity.class));
        return true;

      //sign out
      case R.id.signout_dashboard_icon:
        signOut();
        return true;

      //register with CardioWheel system
      case R.id.register_dashboard_icon:
        handleRegisterClick();
        return true;

      //bind to the "Ble/cardiowheel" Service
      case R.id.connect_ble_device_dashboard_icon:
        BluetoothAdapter bluetoothAdapter = mApp.getBluetoothAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) { //&& NetworkUtils.isConnected(mApp)){
          Log.d(TAG, "Bluetooth Adapter NOT NULL");
          Log.d(TAG, "Initiating BLE Communication");
          initBleCommunication();
        }else{
          ToastUtils.showError("Activate Bluetooth first !", mApp);
        }
        return true;

      case R.id.auth_against_ble_device:
        //estratégia de autenticar um utilizador de forma automatica
        String userId = PreferencesUtils.getRegisteredUserId(mApp);
        if (mApp.isUserIdentified() || !userId.equals(PreferencesUtils.PREFERENCES_READ_OP_DEFAULT_VALUE)) {
          String authStrategy = PreferencesUtils.getIdentificationProcess(mApp);
          this.performAuthStrategy(authStrategy);
        }else{
          ToastUtils.showShortMessage("Register first", mApp);
        }
        return true;


      //TODO add others
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * What to do when the register dashboard icon is clicked.
   */
  private void handleRegisterClick() {
    //se a app já tem um UserID, mostrar apenas aviso
    if (PreferencesUtils.isUserRegistered(mApp)){
      showUserIdPresentMessage();
    }else{
      if (mBoundedService != null) {
        boolean registerProcessInitiated = mBoundedService.initRegisterProcess();
        if (!registerProcessInitiated) {
          String warning = getResources().getString(R.string.connection_to_ble_device_not_established);
          ToastUtils.showError(warning, mApp);
        }
      }else{
        String msg = getResources().getString(R.string.error_toast_BLE_device_not_found);
        ToastUtils.showError(msg, mApp);
      }
    }
  }

  private void showUserIdPresentMessage(){
    String id = PreferencesUtils.getRegisteredUserId(mApp);
    String msg = getResources().getString(R.string.already_register_message) + "ID: " + id;
    ToastUtils.showShortMessage(msg, this);
  }

  /**
   * Builds the object and calls "connect" to establish a connection
   * with the system service. Accordingly with how the process ends,
   * one of the callback methods
   * {@link OnConnectionFailedListener#onConnectionFailed(ConnectionResult)},
   * {@link GoogleApiClient.ConnectionCallbacks#onConnected(Bundle)} or
   * {@link GoogleApiClient.ConnectionCallbacks#onConnectionSuspended(int)}
   * will be invoked.
   */
  private void initLocationApiService() {
    Log.d(TAG, "Initiating Location API Service");
    final int LOCATION_REQUEST_UPDATE = 5000,
        LOCATION_DISPLACEMENT = 20;

    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(LOCATION_REQUEST_UPDATE);
    mLocationRequest.setFastestInterval(5000);

    mLocationRequest.setSmallestDisplacement(LOCATION_DISPLACEMENT);//em metros

    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    mLocationApiClient = new Builder(this)
        .addApi(LocationServices.API)
        .addOnConnectionFailedListener(this)
        .addConnectionCallbacks(this)
        .build();

    //async. One of the Callbacks below will be called.
    mLocationApiClient.connect();
    mApp.setLocationApiClient(mLocationApiClient);
  }

  /**
   * If the BLE device's address is
   */
  private void initBleCommunication() {

    if (!PreferencesUtils.isDeviceAddressFound(mApp)){
      Log.d(TAG, "Device still not found. Search for device..");
      String msg = getResources().getString(R.string.error_toast_BLE_device_not_found);
      ToastUtils.showError(msg, mApp);
      startBleSearchService();
      return;
    }
    if (!bindToService()) {
      Log.w(TAG, "Could NOT bind to Service.");
      String msg = getResources().getString(R.string.error_toast_BLE_device_not_found);
      ToastUtils.showShortMessage(msg, mApp);
    }
  }

  /**
   * Get the BLE device's address and perform
   * a bind to te BoundService component in order
   * to communicate with the device.
   */
  private boolean bindToService() {
    if (mServiceIsBounded) return true;

    String addr = mApp.getBleDeviceAddress();
    Intent intentToBind = new Intent(getApplicationContext(), BleDeviceCommunicationService.class);
    intentToBind.putExtra(BleDeviceCommunicationService.SAVED_INTENT_EXTRA_KEY, addr);
    return bindService(intentToBind, mServiceConnection, Context.BIND_AUTO_CREATE);
  }


  /**
   * Bound Service used to communicate with the BLE device.
   */
  private BleDeviceCommunicationService mBoundedService;

  private boolean mServiceIsBounded;

  /**
   * Code to manage BLE CardioWheel Service life cycle.
   */
  private final ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
      Log.d(TAG, "On Ble CardioWheel ServiceConnected");

      mBoundedService = ((BleDeviceCommunicationService.LocalBinder) service).getService();
      Log.d(TAG, "Bound");
      ToastUtils.showShortMessage("Devices are now bounded.", mApp);

      mServiceIsBounded = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      Log.d(TAG, "On Ble CardioWheel ServiceConnected");
      mServiceIsBounded = false;
    }

  };

  private void performAuthStrategy(String strategy){
    /*switch (strategy){
      case PreferencesUtils.AUTH_PROCESS:
        mBoundedService.initAuthIdentificationProcess();
        break;
      case PreferencesUtils.ID_PROCESS:
        mBoundedService.initIdentificationProcess();
        break;
    }*/
    mBoundedService.initAuthIdentificationProcess();
  }


  /**
   * Initiate the service component that will
   * look for BLE devices around the android device.
   * If some matches the information provided
   * by our protocol documentation, the address of that
   * device is saved for later use.
   */
  private void startBleSearchService() {
    Intent searchForBleDeviceIntent = new Intent(this, SearchBleDeviceService.class);
    startService(searchForBleDeviceIntent);
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d(TAG, "GoogleApiLocation OnConnectionFailed Call");
  }

  @Override public void onConnected(@Nullable Bundle bundle) {
    Log.d(TAG, "GoogleApiLocation OnConnected Call");

    //code must explicitly check for this permissions
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      //permissions must have been granted before arriving here
      String msg = getResources().getString(R.string.location_permissions_not_granted);
      ToastUtils.showShortMessage(msg, this);
      return;
    }

    //request for updates about the device's location
    LocationServices.FusedLocationApi.requestLocationUpdates(mLocationApiClient, mLocationRequest, mLocationListener);
  }

  @Override public void onConnectionSuspended(int i) {
    Log.d(TAG, "GoogleApiLocation OnConnectionSuspended call");
  }

  /**
   * Instance used to handle updates of new locations.
   */
  private LocationListener mLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      if (location == null){
        Log.w(TAG, "Location is null");
        return;
      }

      @SuppressLint("DefaultLocale")
      String newLocationAddress = String.format("%s - Latitude = %f : Longitude = %f",
              DateFormat.getTimeInstance().format(location.getTime()),
              location.getLatitude(),
              location.getLongitude());

      Log.d(TAG, newLocationAddress);

      //when there isn't any weather conditions atm
      if (mApp.getCurrentWeather() == null){
        Intent customIntent = UpdateWeatherStorageService.createIntent(mApp,location);
        startService(customIntent);
      }else{
        mMonitorFragment.displayWeatherInfo(mApp.getCurrentWeather());
      }

      mApp.setCurrentLocation(location); //update data

      // Determine whether a Geocoder is available.
      if (!Geocoder.isPresent()) {
        //TODO mudar esta mensagem
        ToastUtils.showMessage(getString(R.string.no_geocoder_available), mApp);
        return;
      }
      FetchAddressIntentService.fetchAddress(mApp, location, resultReceiver);
    }

  };

  /**
   * An instance of this class must be sent to the system service
   * that connects to the google API in order to handle a new location address
   * received from that API.
   *
   * {@link FetchAddressIntentService} in order to translate a certain
   * {@link Location} object into an human-readable address.
   */
  @SuppressLint("ParcelCreator")
  private class AddressResultReceiver extends ResultReceiver {
    public AddressResultReceiver(Handler handler) {
      super(handler);
    }

    @Override protected void onReceiveResult(int resultCode, Bundle resultData) {
      // Display the address string
      // or an error message sent from the intent service.
      if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
        String addressFound = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
        Log.d(TAG, "ADDRESS FOUND: " + addressFound);
        mMonitorFragment.displayLocation(addressFound);
        return;
      }

      ToastUtils.showError(getString(R.string.no_address_found), mApp);
    }
  }

  @Override
  public void onRetryClick() {
    Log.d(TAG, "Retry Click");

    boolean isConnected = NetworkUtils.isConnected(mApp.getApplicationContext());
    if (isConnected){
      mMonitorFragment.updateViewAccordinglyWithConn(isConnected);
    }
  }




  /**
   * Perform the sign out process.
   */
  private void signOut() {
    Auth.GoogleSignInApi.signOut(mApp.getAuthGoogleApiClient()).setResultCallback(
        new ResultCallback<Status>() {
          @Override
          public void onResult(Status status) {
            if (status.isSuccess()){
              Log.d(TAG, "Sign Out Success");
              startActivity(new Intent(mApp, LoginActivity.class));
              finish();
            }
          }
        });
  }


  /************************************************************/
  /*      Subscrições de eventos através de OTTO              */
  /************************************************************/

  /**
   * Subscrição de notificações de novas informações acerca do estado do tempo.
   */
  @Subscribe
  public void onNewWeatherConditionSaved(WeatherConditionSavedEvent evt) {
    Log.d(TAG, "NewWeatherConditionSaved event received !");
    mMonitorFragment.displayWeatherInfo(evt.getWeatherCondition());
    //init validation of rules
    RulesValidator.startService(mApp, ContextEnvironment.Types.WEATHER);
  }

  /**
   * Subscriber method that received notifications about the state
   * of the internet signal.
   *
   * @param evt The type of event received
   */
  @Subscribe
  public void onInternetConnectivityChange(final NetworkConnEvent evt){
    Log.d(TAG, "Internet Connectivity Change event received ! State: " + evt.isConnected());
    mMonitorFragment.updateViewAccordinglyWithConn(evt.isConnected());
  }

  /**
   * Subscriber method that received notifications about the state
   * of the bluetooth signal.
   *
   * @param evt The type of event received
   */
  @Subscribe
  public void onBluetoothStateChange(ConnectivityChangeEvent evt){
    mMonitorFragment.updateViewAccordinglyWithConn(evt.isConnected());
  }

  /**
   * Subscriber method that receives updated states
   * from the service that maintains a connection open to
   * the ble device.
   *
   * @param evt The type of event received
   */
  @Subscribe
  public void onNewExhaustionStateAvailable(ExhaustionStateAvailableEvent evt) {
    Log.d(TAG, "ExhaustionStateAvailable event received !");
    //save value
    mApp.setExhaustionStateValue(evt.getStateValue());
    //display state
    mMonitorFragment.newStateAvailable(evt.getStateValue());
    //init validation of rules
    RulesValidator.startService(mApp, ContextEnvironment.Types.EXHAUSTION);
  }

  /**
   * Subscriber method that receives the User ID that comes from
   * the register process.
   *
   * @param evt The type of event received
   */
  @Subscribe
  public void onNewUserIdSaved(final NewRegisteredUserIdEvent evt) {
    Log.d(TAG, "New Registered User ID event received !");
    runOnUiThread(new Runnable() {
      @Override public void run() {
        ToastUtils.showMessage("NEW USER ID: " + evt.getUserID(), mApp);
      }
    });
  }

  /**
   * Subscriber method that receives an event that tells
   * the component if the BLE device address was found or not
   * after performing a search.
   *
   * @param evt The type of event received
   */
  @Subscribe
  public void onNewDeviceAddrReceived(final DeviceAddressEvent evt){
    //ignore if there isnt an address
    if (!evt.hasAddress()){
      ToastUtils.showShortMessage(getResources().getString(R.string.error_toast_BLE_device_not_found), mApp);
      return;
    }

    String address = evt.getAddress();
    Log.d(TAG, "BLE Device Address Found: " + address);

    CardioDroidApplication app = ((CardioDroidApplication) this.getApplicationContext());
    app.saveBleDeviceAddress(address);

    //bindToService();
  }

  @Subscribe
  public void onBleConnectionStateChanged(final BleConnectionBreakdownEvent evt) {
    Log.d(TAG, "New Registered User ID event received !");
      runOnUiThread(new Runnable() {
        @Override public void run() {
          mServiceIsBounded = false;
          mMonitorFragment.clearChartValues();
          ToastUtils.showMessage("CardioWheel disconnected !!", mApp);
        }
      });
  }
}
