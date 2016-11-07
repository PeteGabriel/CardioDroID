package com.dev.cardioid.ps.cardiodroid.network.http.api;

import com.dev.cardioid.ps.cardiodroid.network.dtos.WeatherObservation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * The interface provided to communicate with the weather web API.
 */
public interface WeatherApiService {

  /**
   *
   */
  @GET("/api/{api_key}/conditions/lang:{lang}/q/{query}.{format}")
  Call<WeatherObservation> getWeatherConditions(@Path("api_key") String apiKey,
                                                @Path("lang")String lang,
                                                @Path("query") String query,
                                                @Path("format") String format);

  class WeatherApiValues{
    /** All of the possible weather values can be found at:
     * {https://www.wunderground.com/weather/api/d/docs?d=resources/phrase-glossary&MR=1}
     * under the 'Current Condition Phrases' section.
     * */
    public static final String[] POSSIBLE_WEATHER_VALUES = new String[]{
        "Light Rain",
        "Heavy Rain",
        "Light Snow",
        "Heavy Snow",
        "Light Mist",
        "Heavy Mist",
        "Light Fog",
        "Heavy Fog",
        "Clear",
        "Overcast"};
  }

}
