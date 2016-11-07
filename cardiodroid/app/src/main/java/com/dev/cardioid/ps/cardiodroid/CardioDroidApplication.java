package com.dev.cardioid.ps.cardiodroid;

import android.app.AlarmManager;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.models.CircularArea;
import com.dev.cardioid.ps.cardiodroid.models.User;
import com.dev.cardioid.ps.cardiodroid.network.dtos.WeatherObservation;
import com.dev.cardioid.ps.cardiodroid.network.http.api.CardioDroidApiService;
import com.dev.cardioid.ps.cardiodroid.network.http.provider.CardioDroidProvider;
import com.dev.cardioid.ps.cardiodroid.network.http.provider.WeatherProvider;
import com.dev.cardioid.ps.cardiodroid.repo.IRepository;
import com.dev.cardioid.ps.cardiodroid.repo.Repository;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

/**
 * The singleton instance that extends {@link Application}.
 */
public class CardioDroidApplication extends Application {

  /*
   * Debug purposes
   */
  public static final String TAG = Utils.makeLogTag(CardioDroidApplication.class);

  /**
   * Represents the local device Bluetooth adapter.
   */
  private BluetoothAdapter mBluetoothAdapter;

  /**
   * An instance of {@link User}
   * that contains information about the authenticated user.
   */
  private User mPersonalUserAccountInfo;

  /**
   * The BLE device's MAC address.
   */
  //private String mDeviceAddress;

  /**
   * Helps keeping record if user was identified.
   */
  private boolean userIdentified;

  /**
   * Reference to the alarm manager system service.
   */
  private AlarmManager mAlarmManager;

  /**
   * Reference to the weather API service provider.
   */
  private WeatherProvider mWeatherProvider;

  /**
   * Reference to the CardioDroid API service provider.
   */
  private CardioDroidProvider mCardioApiProvider;

  /**
   * Reference to the data storage repository.
   */
  private IRepository mRepository;

  private WeatherObservation mWeatherCondition;

  private Location mCurrLocation;


  private List<Geofence> mGeofenceList;
  private List<CircularArea> mCircularAreasList;

  private GoogleApiClient mAuthGoogleApiClient;
  private GoogleApiClient mLocationGoogleApiClient;

  @Override public void onCreate() {
    super.onCreate();
    JodaTimeAndroid.init(this);

    setupGoogleApiClient();

    final BluetoothManager bluetoothManager =
        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = bluetoothManager.getAdapter();



    mWeatherProvider = new WeatherProvider();
    mCardioApiProvider = new CardioDroidProvider(CardioDroidApiService.class);

    mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    userIdentified = false;

    mRepository = new Repository(this);

    scheduleUpdateOfWeatherConditions();

    mGeofenceList = new ArrayList<>();
    mCircularAreasList = new ArrayList<>();
  }


  public List<CircularArea> getCircularAreasList() {
    return mCircularAreasList;
  }

  public void setLocationApiClient(GoogleApiClient mLocationGoogleApiClient) {
    this.mLocationGoogleApiClient = mLocationGoogleApiClient;
  }

  public GoogleApiClient getLocationApiClient() {
    return mLocationGoogleApiClient;
  }

  //not thread safe. handle carefully
  public List<Geofence> getListOfGeofences() {
    return mGeofenceList;
  }

  public synchronized void setupGoogleApiClient(){
    if (mAuthGoogleApiClient == null){
      // Configure sign-in to request the user's ID, email address, and basic
      // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
      GoogleSignInOptions gso = new GoogleSignInOptions
          .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          //.requestIdToken(getResources().getString(R.string.server_client_id))//will be verified at server-side
          .requestEmail()
          .build();

      mAuthGoogleApiClient = new GoogleApiClient.Builder(this)
          .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
              Log.d(TAG, "GoogleAPI Client OnConnectionFailed");
            }
          })
          .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override public void onConnected(@Nullable Bundle bundle) {
              Log.d(TAG, "GOOGLE API SIGN IN ON_CONNECTED");
            }

            @Override public void onConnectionSuspended(int i) {
              Log.d(TAG, "GoogleAPI Client onConnectionSuspended");
            }
          })
          .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
          .build();
      mAuthGoogleApiClient.connect();
    }
  }

  public synchronized IRepository getRepository() {
    return mRepository;
  }


  /**********************************************************
   * Bluetooth stuff
   **********************************************************/
  public synchronized BluetoothAdapter getBluetoothAdapter() {
    return mBluetoothAdapter;
  }

  public synchronized String getBleDeviceAddress() {
    return PreferencesUtils.getDeviceAddress(this);
  }

  public synchronized void saveBleDeviceAddress(String addr){
    PreferencesUtils.saveDeviceAddress(this, addr);
  }



  /**********************************************************
   * Bluetooth stuff
   **********************************************************/

  public User getPersonalUserAccountInfo() {
    return mPersonalUserAccountInfo;
  }

  public void setPersonalUserAccountInfo(GoogleSignInAccount account){
    mPersonalUserAccountInfo = new User(account);
  }



  /**********************************************************
   * Weather context stuff
   **********************************************************/
  public synchronized void setCurrentWeatherCondition(WeatherObservation bean){
    mWeatherCondition = bean;
  }

  public WeatherObservation getCurrentWeather() {
    return mWeatherCondition;
  }



  /**********************************************************
   * Location context stuff
   **********************************************************/
  public synchronized void setCurrentLocation(Location location){
    mCurrLocation = location;
  }

  public Location getCurrentLocation() {
    return mCurrLocation;
  }


  /**********************************************************
   * Http Retrofit APIs stuff
   **********************************************************/
  public synchronized WeatherProvider getWeatherProvider() {
    return mWeatherProvider;
  }

  public CardioDroidProvider getCardioApiProvider() {
    return mCardioApiProvider;
  }


  /**********************************************************
   * Exhaustion context stuff
   **********************************************************/
  private String exhaustionStateValue;

  public synchronized String getExhaustionStateValue() {
    return exhaustionStateValue;
  }

  public synchronized void setExhaustionStateValue(String exhaustionStateValue) {
    this.exhaustionStateValue = exhaustionStateValue;
  }

  public synchronized  void setUserAsIdentified(boolean ided){
    this.userIdentified = ided;
  }

  public synchronized boolean isUserIdentified(){
    return this.userIdentified;
  }


  private void scheduleUpdateOfWeatherConditions(){
    //TODO valor de intervalo deveria vir das configurações
    //Intent customIntent = new Intent(this, UpdateWeatherStorageService.class);
    //final PendingIntent pendingIntent = PendingIntent.getService(this, 0, customIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    //mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
  }


  public synchronized GoogleApiClient getAuthGoogleApiClient() {
    return mAuthGoogleApiClient;
  }

}
