package com.dev.cardioid.ps.cardiodroid.network.dtos;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

/**
 *
 * This class represents internal data used by the {@link WeatherObservation} class.
 *
 */
public class CurrentObservation implements Parcelable {
  @SerializedName("weather")
  @Expose
  private String weather;
  @SerializedName("temperature_string")
  @Expose
  private String temperatureString;
  @SerializedName("temp_f")
  @Expose
  private Double tempF;
  @SerializedName("temp_c")
  @Expose
  private Double tempC;
  @SerializedName("relative_humidity")
  @Expose
  private String relativeHumidity;
  @SerializedName("icon_url")
  @Expose
  private String iconUrl;

  /**
   * No args constructor for use in serialization
   *
   */
  public CurrentObservation() {
  }

  /**
   *
   * @param tempC
   * @param relativeHumidity
   * @param iconUrl
   * @param temperatureString
   * @param weather
   * @param tempF
   */
  public CurrentObservation(String weather, String temperatureString, Double tempF, Double tempC,
      String relativeHumidity, String iconUrl) {
    this.weather = weather;
    this.temperatureString = temperatureString;
    this.tempF = tempF;
    this.tempC = tempC;
    this.relativeHumidity = relativeHumidity;
    this.iconUrl = iconUrl;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CurrentObservation that = (CurrentObservation) o;
    return Objects.equals(weather, that.weather) &&
        Objects.equals(temperatureString, that.temperatureString) &&
        Objects.equals(tempF, that.tempF) &&
        Objects.equals(tempC, that.tempC) &&
        Objects.equals(relativeHumidity, that.relativeHumidity) &&
        Objects.equals(iconUrl, that.iconUrl);
  }

  @Override public int hashCode() {
    return Objects.hash(weather, temperatureString, tempF, tempC, relativeHumidity, iconUrl);
  }

  /**
   *
   * @return
   *     The weather
   */
  public String getWeather() {
    return weather;
  }

  /**
   *
   * @param weather
   *     The weather
   */
  public void setWeather(String weather) {
    this.weather = weather;
  }

  /**
   *
   * @return
   *     The temperatureString
   */
  public String getTemperatureString() {
    return temperatureString;
  }

  /**
   *
   * @param temperatureString
   *     The temperature_string
   */
  public void setTemperatureString(String temperatureString) {
    this.temperatureString = temperatureString;
  }

  /**
   *
   * @return
   *     The tempF
   */
  public Double getTempF() {
    return tempF;
  }

  /**
   *
   * @param tempF
   *     The temp_f
   */
  public void setTempF(Double tempF) {
    this.tempF = tempF;
  }

  public CurrentObservation withTempF(Double tempF) {
    this.tempF = tempF;
    return this;
  }

  /**
   *
   * @return
   *     The tempC
   */
  public Double getTempC() {
    return tempC;
  }

  /**
   *
   * @param tempC
   *     The temp_c
   */
  public void setTempC(Double tempC) {
    this.tempC = tempC;
  }

  public CurrentObservation withTempC(Double tempC) {
    this.tempC = tempC;
    return this;
  }

  /**
   *
   * @return
   *     The relativeHumidity
   */
  public String getRelativeHumidity() {
    return relativeHumidity;
  }

  /**
   *
   * @param relativeHumidity
   *     The relative_humidity
   */
  public void setRelativeHumidity(String relativeHumidity) {
    this.relativeHumidity = relativeHumidity;
  }

  /**
   *
   * @return
   *     The iconUrl
   */
  public String getIconUrl() {
    return iconUrl;
  }

  /**
   *
   * @param iconUrl
   *     The icon_url
   */
  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public CurrentObservation withWeather(String weather) {
    this.weather = weather;
    return this;
  }

  public CurrentObservation withTemperatureString(String temperatureString) {
    this.temperatureString = temperatureString;
    return this;
  }

  public CurrentObservation withRelativeHumidity(String relativeHumidity) {
    this.relativeHumidity = relativeHumidity;
    return this;
  }

  public CurrentObservation withIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder stb = new StringBuilder("");
    return stb.append(this.getWeather())
        .append(this.getTemperatureString())
        .append(this.getRelativeHumidity())
        .append(this.getTempC())
        .append(this.getTempF())
        .append(this.getIconUrl()).toString();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.weather);
    dest.writeString(this.temperatureString);
    dest.writeValue(this.tempF);
    dest.writeValue(this.tempC);
    dest.writeString(this.relativeHumidity);
    dest.writeString(this.iconUrl);
  }

  protected CurrentObservation(Parcel in) {
    this.weather = in.readString();
    this.temperatureString = in.readString();
    this.tempF = (Double) in.readValue(Double.class.getClassLoader());
    this.tempC = (Double) in.readValue(Double.class.getClassLoader());
    this.relativeHumidity = in.readString();
    this.iconUrl = in.readString();
  }

  public static final Parcelable.Creator<CurrentObservation> CREATOR =
      new Parcelable.Creator<CurrentObservation>() {
        @Override public CurrentObservation createFromParcel(Parcel source) {
          return new CurrentObservation(source);
        }

        @Override public CurrentObservation[] newArray(int size) {
          return new CurrentObservation[size];
        }
      };
}