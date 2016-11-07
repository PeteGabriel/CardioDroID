package com.dev.cardioid.ps.cardiodroid.network.http.provider;

import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.network.dtos.WeatherObservation;
import com.dev.cardioid.ps.cardiodroid.network.http.api.WeatherApiService;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.util.Locale;
import retrofit2.Call;

/**
 * This class represents a specific implementation of the interface {@link WeatherApiService}.
 *
 * An instance of this class provides access to the api specified by
 * that very same interface.
 *
 * @see "http://api.wunderground.com/weather/api/d/docs"
 */
public final class WeatherProvider extends Provider<WeatherApiService, WeatherObservation>{

  private final String TAG = Utils.makeLogTag(WeatherProvider.class);

  /**
   * The base url for the given API.
   */
  private static final String BASE_URL = "http://api.wunderground.com/api/";

  /**
   * The api key used to communicate with the api.
   */
  private final String API_KEY = "bbed7272916f8d2f";

  /**
   * The format expected in every response.
   */
  private final String JSON_FORMAT = "json";


  /**
   * Ctor
   */
  public WeatherProvider(){
    super(WeatherApiService.class, BASE_URL);
  }

  /**
   * Request the weather condition from the weather API. This method is async.
   * The instance returned should not be used to "enqueue" another request since is stated
   * in the documentation that once the request is made the instance cannot be used anymore unless
   * the clone method is called but try not to do that.
   *
   * An example of how to use this method would be a service to initiate the request and once the
   * callback is performed call "Service#stopSelf" to stop the service.
   *
   * @param task
   *    the callback that specifies what to do with the response
   * @return
   *    an instance of {@link Call} that represents the async work in progress
   */
  public Call<WeatherObservation> getWeatherConditionsAsync(String latitude, String longitude,
                                                            final Completion<WeatherObservation> task){

    String query = String.format("%s,%s", latitude, longitude);
    Log.d(TAG, "WeatherAPI is about to query: " + query);
    String lang = Locale.getDefault().getLanguage().toUpperCase();
    Call<WeatherObservation> future = mService.getWeatherConditions(API_KEY, lang, query, JSON_FORMAT);

    future.enqueue(wrapTaskIntoCallback(task));
    return future;
  }
}
