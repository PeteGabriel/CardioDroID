package com.dev.cardioid.ps.cardiodroid.activities;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.contexts.ContextEnvironment;
import com.dev.cardioid.ps.cardiodroid.events.BleEventBus;
import com.dev.cardioid.ps.cardiodroid.events.ConnectivityChangeEvent;
import com.dev.cardioid.ps.cardiodroid.events.ExhaustionStateAvailableEvent;
import com.dev.cardioid.ps.cardiodroid.fragments.FacesFragment;
import com.dev.cardioid.ps.cardiodroid.services.ble.BleDeviceCommunicationService;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.squareup.otto.Subscribe;

/**
 * Represents graphically the current state of exhaustion.
 */
public class FaceStateActivity extends SingleFragmentActivity {

  public static final String TAG = Utils.makeLogTag(FaceStateActivity.class);

  private CardioDroidApplication mApp;

  private boolean mBounded;

  private FacesFragment mFrag;

  /**
   * Bound Service used to communicate with the BLE device.
   */
  private BleDeviceCommunicationService mBoundedService;

  @Override protected Fragment createFragment() {
    return new FacesFragment();
  }

  @Override protected void setupContentView() {
    setContentView(R.layout.activity_face_state_layout);
    //toolbar has an icon to go back to the dashboard.
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  @Override protected int getFrameContainer() {
    return R.id.faces_container;
  }


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mApp = (CardioDroidApplication) getApplication();
    //default behavior
    mBounded = false;
    //bind the bus to an instance of this class
    BleEventBus.getInstance().register(this);

    //without bluetooth, we dont show any faces.
    BluetoothAdapter bluetoothAdapter = mApp.getBluetoothAdapter();
    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()){
      if (bindToServiceIfPossible()){
        mBounded = true;
        Log.d(TAG, "Service is bound and can now receive states.");
      }else{
        Log.e(TAG, "Could NOT bind to Service.");
      }
    }else{ //bluetooth not available
      Log.w(TAG,"Bluetooth is not active!");
    }

  }

  private boolean bindToServiceIfPossible(){
    return PreferencesUtils.isDeviceAddressFound(mApp) && bindToService();
  }

  @Override protected void onResume() {
    super.onResume();
    getSupportActionBar().setTitle("");
    mFrag.setYellowFace();
  }

  /**
   * Code to manage Service life cycle.
   */
  private final ServiceConnection mServiceConnection = new ServiceConnection() {

    public void onServiceConnected(ComponentName componentName, IBinder service) {
      mBoundedService = ((BleDeviceCommunicationService.LocalBinder) service).getService();
      Log.d(TAG, "On ServiceConnected");
    }

    public void onServiceDisconnected(ComponentName componentName) {
      mBoundedService = null;
      Log.i(TAG, "BleDeviceCommunicationService disconnected");
    }
  };

  /**
   * Tries to bind to the service that can communicate with
   * BLE device to obtain information about the exhaustion state.
   */
  private boolean bindToService() {
    String addr = PreferencesUtils.getDeviceAddress(mApp);
    Intent intentToBind = new Intent(getApplicationContext(), BleDeviceCommunicationService.class)
        .putExtra(BleDeviceCommunicationService.SAVED_INTENT_EXTRA_KEY, addr);
    return bindService(intentToBind, mServiceConnection, Context.BIND_IMPORTANT);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.the_faces_menu, menu);
    return true;
  }

  /**
   * What to do when the user pushes one of the icons.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.see_dashboard_view:
        finish();
        break;

      case R.id.settings_icon:
        startActivity(new Intent(this, UserPreferencesActivity.class));
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onAttachFragment(Fragment fragment) {
    mFrag = (FacesFragment) fragment;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BleEventBus.getInstance().unregister(this);
  }

  @Override protected void onStop() {
    super.onStop();
    if (mBounded) {
      unbindService(mServiceConnection);
      mBounded = false;
    }
  }

  @Subscribe
  public void onBluetoothStateChange(ConnectivityChangeEvent evt){
    if (evt.isConnected()) {
      bindToServiceIfPossible();
    }else {
      mFrag.setUndefinedFace();
    }
  }

  /**
   * This method might run in some other thread than the UI THread.
   * To play defensive, run any graphical update inside the helper method "runOnUiThread".
   */
  @Subscribe
  public void exhaustionStateAvailable(final ExhaustionStateAvailableEvent evt) {
    Log.d(TAG, "ExhaustionStateAvailable event received !");
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        switch (evt.getStateValue()){
          case ContextEnvironment.ExhaustionStates.LOW:
            mFrag.setGreenFace();
            break;
          case ContextEnvironment.ExhaustionStates.MEDIUM:
            mFrag.setYellowFace();
            break;
          case ContextEnvironment.ExhaustionStates.HIGH:
            mFrag.setRedFace();
            break;
        }
      }
    });
  }



}
