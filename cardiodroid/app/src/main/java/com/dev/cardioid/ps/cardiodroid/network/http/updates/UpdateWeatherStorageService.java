package com.dev.cardioid.ps.cardiodroid.network.http.updates;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.events.BleEventBus;
import com.dev.cardioid.ps.cardiodroid.events.WeatherConditionSavedEvent;
import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.network.dtos.WeatherObservation;
import com.dev.cardioid.ps.cardiodroid.network.http.provider.WeatherProvider;
import com.dev.cardioid.ps.cardiodroid.utils.NetworkUtils;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * This service performs an HTTP request to the weather web API
 * in order to obtain new data related to the current weather condition
 * for any given time and broadcasts the result.
 *
 * This service gives the possibility for schedulers to schedule updates
 * of information related to the weather context.
 *
 * This service is of type "started" since we want to manage the lifecycle of it
 * to make sure the async operations performed here finish correctly.
 */
public class UpdateWeatherStorageService extends Service {

  private static final String TAG = Utils.makeLogTag(UpdateWeatherStorageService.class);

  public static final String LOCATION_DATA = "location_data_complete_value";


  public static Intent createIntent(Context ctx, Location location){
    Intent customIntent = new Intent(ctx, UpdateWeatherStorageService.class);
    customIntent.putExtra(UpdateWeatherStorageService.LOCATION_DATA, location);
    return customIntent;
  }


  public UpdateWeatherStorageService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Use startService instead.");
  }

  /**
   * Localiza o provider por via de Application. Efectua um pedido para obter
   * dados relativos à condição meteorológica e fornece o callback que será
   * executado quando o resultado estiver disponivel. O callback encarrega-se
   * de lançar um evento com a informação obtida.
   */
  @Override
  public int onStartCommand(Intent intent, int flags, final int startId) {
    super.onStartCommand(intent, flags, startId);

    //preferred connection type. Got it from settings
    long preferredConn = PreferencesUtils.getConnectionSpecifiedByUser(getApplicationContext());

    //if not connected properly, dont do nothing
    if (!NetworkUtils.isConnected(getApplicationContext(), preferredConn)){
      Log.d(TAG, "Not connected to a network.");
      stopSelf(startId);
      return START_NOT_STICKY;
    }

    String latitude = "", longitude  = "";
    final CardioDroidApplication app = (CardioDroidApplication) getApplicationContext();
    WeatherProvider provider = app.getWeatherProvider();

    if (intent.hasExtra(LOCATION_DATA)){
      Location loc = intent.getParcelableExtra(LOCATION_DATA);
      latitude = String.valueOf(loc.getLatitude());
      longitude = String.valueOf(loc.getLongitude());
    }else{
      stopSelf(startId);
      return START_NOT_STICKY;
    }

    if (!NetworkUtils.isConnected(getApplicationContext(), preferredConn)){
      Log.d(TAG, "Not connected to a network.");
      stopSelf(startId);
      return START_NOT_STICKY;
    }

    //Warning: this method calls stopSelf. dont call it afterwards
    getWeatherConditionsAsync(startId, latitude, longitude, app, provider);

    return START_NOT_STICKY;
  }

  private void getWeatherConditionsAsync(final int startId, String latitude, String longitude,
      final CardioDroidApplication app, WeatherProvider provider) {
    //query the weather API provider
    provider.getWeatherConditionsAsync(latitude, longitude, new Completion<WeatherObservation>() {
      @Override
      public void onResult(CallResult<WeatherObservation> result) {
          try{
            WeatherObservation weatherInfo = result.getResult();
            Log.d(TAG, weatherInfo.toString());

              if (app.getCurrentWeather() == null ||
                  !app.getCurrentWeather().equals(weatherInfo)){
                //sent event because its different from what we had previously

                //TODO estes dois métodos deviam ir para um receiver
                app.setCurrentWeatherCondition(weatherInfo);
                BleEventBus.getInstance().post(new WeatherConditionSavedEvent(weatherInfo));
              }

            stopSelf(startId);
          }catch(Exception e){
            Log.e(TAG, "An error occurred while trying to retrieve the weather info from the result.");
            stopSelf(startId);
          }
      }
    });
  }
}
