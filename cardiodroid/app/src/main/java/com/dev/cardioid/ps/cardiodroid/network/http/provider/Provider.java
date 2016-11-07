package com.dev.cardioid.ps.cardiodroid.network.http.provider;

import android.os.Parcelable;

import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public abstract class Provider<I, T extends Parcelable> {

  protected I mService;

  protected Provider(Class apiInterface, String apiBaseUrl){
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    mService = (I) retrofit.create(apiInterface);
  }

  /**
   * Provides the translation between a completion and a callback used by the
   * Retrofit lib.
   *
   * @param completion
   *    what to do with the response.
   * @return
   *    an anonymous instance of {@link Callback} to be used internally by Retrofit.
   *
   * @see Callback
   * @see Completion
   */
  protected Callback<T> wrapTaskIntoCallback(final Completion<T> completion){
    return new Callback<T>() {
      /**
       * Invoked for a received HTTP response.
       *
       * Note: An HTTP response may still indicate an application-level
       * failure such as a 404 or 500.
       * Call {@link Response#isSuccessful()} to determine if the response indicates success.
       *
       * @param call
       *    the instance that represents the async work in progress
       * @param response
       *    the response
       */
      @Override
      public void onResponse(Call<T> call, Response<T> response) {

        final CallResult<T> result = response.isSuccessful() ?
            new CallResult<T>(response.body()) :
            new CallResult<T>(new Exception(response.errorBody().toString()));

        completion.onResult(result);
      }

      /**
       * Invoked when a network exception occurred talking to the server or when an unexpected
       * exception occurred creating the request or processing the response.
       *
       * @param call
       *    the instance that represents the async work in progress
       * @param t
       *    the instance if the exception thrown
       */
      @Override
      public void onFailure(Call<T> call, Throwable t) {
        completion.onResult(new CallResult<T>(new Exception(t)));
      }
    };
  }

}
