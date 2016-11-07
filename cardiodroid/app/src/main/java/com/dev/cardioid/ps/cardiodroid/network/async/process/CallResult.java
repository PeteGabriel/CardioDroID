package com.dev.cardioid.ps.cardiodroid.network.async.process;

import android.support.annotation.NonNull;

/**
 * Class whose instances are used to host the asynchronous operation result.
 * Instances are immutable and therefore they can be safely shared by multiple threads.
 *
 */
public final class CallResult<T> {
  private final T result;
  private final Exception error;

  /**
   * Prevent instantiation from outside code.
   * @param result The operation's result, if it executed successfully.
   * @param error The operation's error, if one occurred.
   */
  private CallResult(T result, Exception error) {
    this.result = result;
    this.error = error;
  }

  /**
   * Initiates an instance with the given result, thereby signalling successful completion.
   * @param result The operation's result.
   */
  public CallResult(@NonNull T result) {
    this(result, null);
  }

  /**
   * Initiates an instance with the given exception, thereby signalling a flawed completion.
   * @param error The operation's error.
   */
  public CallResult(@NonNull Exception error) {
    this(null, error);
  }



  /**
   * Gets the operation result.
   * @return The weather information.
   * @throws Exception The error that occurred while trying to get the weather information,
   * if one actually existed.
   */
  @NonNull
  public T getResult() throws Exception {
    if(error != null)
      throw error;
    return result;
  }
}