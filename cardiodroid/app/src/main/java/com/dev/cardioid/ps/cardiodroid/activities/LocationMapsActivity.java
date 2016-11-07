package com.dev.cardioid.ps.cardiodroid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.utility_dialog.GeofenceRadiusRegulatorDialog;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts.JsonLocationModel;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import static com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.utility_dialog.GeofenceRadiusRegulatorDialog.OnRadiusSetupListener;

/**
 * This activity displays all the elements that give the user
 * the possibility of creating a location-based condition.
 *
 * If this class extends FragmentActivity and if the toolbar
 * is inside the layout you used, it will be set by default.
 */
public class LocationMapsActivity extends AppCompatActivity
    implements OnMapReadyCallback,
               OnRadiusSetupListener {

  public static final String TAG = Utils.makeLogTag(LocationMapsActivity.class);


  public static final String LOCATION_CONDITION_KEY = "location.maps.activity.condition.key";
  public static final String LOCATION_INSTANCE_KEY = "location.maps.activity.instance.key";
  public static final String LOCATION_GEO_REQUEST_KEY = "location.maps.activity.geofence.request.key";
  public static final String LOCATION_GEO_RADIUS_KEY = "location.maps.activity.geofence.radius.key";

  /**
   * Reference to the map.
   */
  private GoogleMap mMap;

  private Circle mGeofenceCircle;

  /**
   * Reference to the button that starts to
   * create a new location-based condition.
   */
  private Button mBtnCreateCondition;

  /**
   * The reference to the single marker positioned
   * in the map.
   */
  private Marker mSingleMarker;

  /**
   * Reference to the action bar's button that lets the user
   * configure the condition that is being created.
   */
  private MenuItem mBtnConfigureButton;

  private CardioDroidApplication mApp;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "OnCreate Call");
    setContentView(R.layout.activity_location_maps_layout);
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    mApp = (CardioDroidApplication)getApplicationContext();

    /*
    It will load your preferences from XML, and last parameter (readAgain) will guarantee that user
    preferences won't be overwritten and at the same time, we can get the default values if set.
    */
    PreferenceManager.setDefaultValues(this, R.xml.preference, false);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    mBtnCreateCondition = (Button)findViewById(R.id.create_location_condition_button);
    mBtnCreateCondition.setOnClickListener(createConditionHandler());


  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "OnCreateOptionsMenu Call");
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_options_activity_menu, menu);

    mBtnConfigureButton = menu.findItem(R.id.map_icon_configure_condition);
    mBtnConfigureButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        new GeofenceRadiusRegulatorDialog().show(getFragmentManager(), null);
        return true;
      }
    });

    //cannot configure without any marker positioned in the map
    if (mSingleMarker == null){
      mBtnConfigureButton.setVisible(false);
    }

    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.clear_map:
        if (mSingleMarker != null)
          mSingleMarker.remove();
        mSingleMarker = null;
        if (mGeofenceCircle != null)
          mGeofenceCircle.remove();
        mGeofenceCircle = null;
        mBtnConfigureButton.setVisible(false); //cant config anymore
        break;

    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Let the main button handle all the logic applied to the process
   * of creating a new location-based condition.
   */
  private View.OnClickListener createConditionHandler(){
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.d(TAG, "OnCreateCondition Handler event");
        if (mSingleMarker != null && mGeofenceCircle != null) {
          LatLng coords = mSingleMarker.getPosition();
          double radius = mGeofenceCircle.getRadius();

          //handle the creation of a new geofence
          String idKey = coords.toString() + String.valueOf(radius);
          mApp.getListOfGeofences().add(new Geofence.Builder()
                  .setRequestId(idKey) // string to identify this geofence
                  .setCircularRegion(coords.latitude, coords.longitude, (float) radius)
                  .setExpirationDuration(Geofence.NEVER_EXPIRE)
                  .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                  .build());

          JSONObject value = JsonLocationModel.create(coords.latitude, coords.longitude, radius, idKey);
          Intent newLocationRuleIntent = new Intent();
          newLocationRuleIntent.putExtra(LOCATION_CONDITION_KEY, value.toString());
          newLocationRuleIntent.putExtra(LOCATION_INSTANCE_KEY, coords);
          newLocationRuleIntent.putExtra(LOCATION_GEO_REQUEST_KEY, idKey);
          newLocationRuleIntent.putExtra(LOCATION_GEO_RADIUS_KEY, radius);

          setResult(RESULT_OK, newLocationRuleIntent);
          finish();
        }else{
          ToastUtils.showMessage("Create a new geofence", getApplicationContext());
        }
      }
    };
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   */
  @Override public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    CardioDroidApplication app = (CardioDroidApplication)getApplicationContext();

    int typeOfMapLayout = PreferencesUtils.getTypeOfMapLayout(app);
    mMap.setMapType(typeOfMapLayout);

    Location location = app.getCurrentLocation();
    if (location != null) {
      MarkerOptions customMarker = new MarkerOptions()
          .position(new LatLng(location.getLatitude(), location.getLongitude()));

      //TODO adicionar o titulo como sendo a morada obtida para aquela localização

      mSingleMarker = mMap.addMarker(customMarker);
      mMap.moveCamera(CameraUpdateFactory.newLatLng(mSingleMarker.getPosition()));
      mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    //common settings and handlers
    mMap.setOnMapClickListener(handleClickOnMap());

  }


  /**
   * Handler for each click over the map.
   */
  private GoogleMap.OnMapClickListener handleClickOnMap(){
    return new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng latLng) {
        Log.d(TAG, "OnMapClick");
        if (mSingleMarker != null){
          mSingleMarker.remove();
        }
        mSingleMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        if (mGeofenceCircle != null) {
          double radius = mGeofenceCircle.getRadius();
          tryRemoveGeofenceCircle();
          addNewZone(mSingleMarker.getPosition(), radius);
        }
        //let configurations be made
        mBtnConfigureButton.setVisible(true);
      }
    };
  }

  private void addNewZone(LatLng position, double radius){
    CircleOptions circle = new CircleOptions()
        .center(position)
        .radius(radius)
        .fillColor(0x40ff0000)
        .strokeColor(Color.BLUE)
        .strokeWidth(5);

    mGeofenceCircle = mMap.addCircle(circle);
  }

  @Override
  public void onRadiusValueRegulated(int progress) {
    tryRemoveGeofenceCircle();
    addNewZone(mSingleMarker.getPosition(), (double)progress);
  }

  private void tryRemoveGeofenceCircle(){
    if (mGeofenceCircle != null){
      mGeofenceCircle.remove();
      mGeofenceCircle = null;
    }
  }
}
