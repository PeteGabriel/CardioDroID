package com.dev.cardioid.ps.cardiodroid.services.location;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This service runs in the background and is responsible for fetching
 * the address corresponding to a given geographic location. It is now
 * possible to fetch an address based upon given coordinates without blocking the
 * Main Thread (UI).
 */
public class FetchAddressIntentService extends IntentService {

  public static final String TAG = Utils.makeLogTag(FetchAddressIntentService.class);

  private static final String PACKAGE_NAME =
      "com.dev.cardioid.ps.cardiodroid.services.location";
  private static final String FETCH_ADDRESS_ACTION = PACKAGE_NAME + ".action.fetch.address.action";
  private static final String PARAM_LOCATION = PACKAGE_NAME + ".extra.param.location";
  private static final String PARAM_RESULT_RECEIVER = PACKAGE_NAME + ".extra.param.result.receiver";

  /**
   * Results can be obtained by using the key. The data passed to the ResultReceiver
   * will be inside a Bundle which uses a key-value approach to store things inside.
   * This key is the one that should be used to retrieve the result (address).
   */
  public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".result.data.key";

  /**
   * This is the result code sent along with the data (address)
   * in the ResultReceiver. This code means a valid address was obtained.
   */
  public static final int SUCCESS_RESULT = 0;

  /**
   * This is the result code sent to indicate no valid address was retrieved.
   */
  public static final int FAILURE_RESULT = 1;


  public FetchAddressIntentService() {
    super("FetchAddressIntentService");
  }

  /**
   * Starts this service to fetch an address with the given parameters. If
   * the service is already performing some task this action will be queued.
   *
   * @see IntentService
   */
  public static void fetchAddress(Context context, Location location, ResultReceiver receiver) {
    Log.d(TAG, "Fetch Address");
    Intent intent = new Intent(context, FetchAddressIntentService.class);
    intent.setAction(FETCH_ADDRESS_ACTION);
    intent.putExtra(PARAM_LOCATION, location);
    intent.putExtra(PARAM_RESULT_RECEIVER, receiver);
    context.startService(intent);
  }


  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "OnHandleIntent");
    if (intent != null) {
      final String action = intent.getAction();
      if (FETCH_ADDRESS_ACTION.equals(action)) {
        final Location location = intent.getParcelableExtra(PARAM_LOCATION);
        final  ResultReceiver receiver = intent.getParcelableExtra(PARAM_RESULT_RECEIVER);
        translateLocation(location, receiver);
      }
    }
  }

  /**
   * Retrieve the street address data.
   */
  private void translateLocation(Location location, ResultReceiver receiver) {
    Log.d(TAG, "Translate Location");

    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    String errorMessage = "";

    List<Address> addresses = null;

    try {
      //sync call to the method that resolves coordinates into an address
      addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
    } catch (IOException ioException) {
      // Catch network or other I/O problems.
      errorMessage = getString(R.string.service_not_available);
      Log.e(TAG, errorMessage, ioException);
    } catch (IllegalArgumentException illegalArgumentException) {
      // Catch invalid latitude or longitude values.
      errorMessage = getString(R.string.invalid_lat_long_used);
      Log.e(TAG, errorMessage + ". " +
          "Latitude = " + location.getLatitude() +
          ", Longitude = " +
          location.getLongitude(), illegalArgumentException);
    }

    // Handle case where no address was found.
    if (addresses == null || addresses.size()  == 0) {
      if (errorMessage.isEmpty()) {
        errorMessage = getString(R.string.no_address_found);
        Log.e(TAG, errorMessage);
      }
      deliverResultToReceiver(receiver, FAILURE_RESULT, errorMessage);
    } else {
      Address address = addresses.get(0);
      ArrayList<String> addressFragments = new ArrayList<String>();

      // Fetch the address lines using getAddressLine,
      // join them, and send them to the thread.
      for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
        addressFragments.add(address.getAddressLine(i));
      }
      Log.i(TAG, getString(R.string.address_found));
      String message = TextUtils.join(System.getProperty("line.separator"), addressFragments);
      deliverResultToReceiver(receiver, SUCCESS_RESULT, message);
    }
  }

  /**
   * Return the address to the requestor.
   */
  private void deliverResultToReceiver(ResultReceiver receiver, int resultCode, String message) {
    Log.d(TAG, "Deliver Resultc                                                                ");

    Bundle bundle = new Bundle();
    bundle.putString(RESULT_DATA_KEY, message);
    receiver.send(resultCode, bundle);
  }
}
