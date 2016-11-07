package com.dev.cardioid.ps.cardiodroid.network.dtos;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

/**
 * This class represents the content sent through an HTTP response for
 * any successful HTTP request to the Weather API.
 * The content is injected via the JSON converter specified in the
 * gradle build file.
 *
 *
 */
public final class WeatherObservation implements Parcelable {

    @SerializedName("current_observation")
    @Expose
    private CurrentObservation currentObservation;

    /**
     * No args constructor for use in serialization
     *
     */
    public WeatherObservation() {
    }

    /**
     *
     * @param currentObservation
     */
    public WeatherObservation(CurrentObservation currentObservation) {
      this.currentObservation = currentObservation;
    }

    /**
     *
     * @return
     *     The currentObservation
     */
    public CurrentObservation getCurrentObservation() {
      return currentObservation;
    }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WeatherObservation that = (WeatherObservation) o;
    return Objects.equals(currentObservation, that.currentObservation);
  }

  @Override public int hashCode() {
    return Objects.hash(currentObservation);
  }

  @Override
    public String toString() {
      return "WeatherObservation : " + this.currentObservation.toString();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.currentObservation, flags);
    }

    protected WeatherObservation(Parcel in) {
        this.currentObservation = in.readParcelable(CurrentObservation.class.getClassLoader());
    }

    public static final Parcelable.Creator<WeatherObservation> CREATOR =
        new Parcelable.Creator<WeatherObservation>() {
            @Override public WeatherObservation createFromParcel(Parcel source) {
                return new WeatherObservation(source);
            }

            @Override public WeatherObservation[] newArray(int size) {
                return new WeatherObservation[size];
            }
        };
}
