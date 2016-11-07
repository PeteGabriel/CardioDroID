package com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents the data sent in every request
 * made by the app to the Web Service,
 */
public class AuthTokenDto implements Parcelable {

    private String token;

    public AuthTokenDto(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
    }

    protected AuthTokenDto(Parcel in) {
        this.token = in.readString();
    }

    public static final Parcelable.Creator<AuthTokenDto> CREATOR = new Parcelable.Creator<AuthTokenDto>() {
        @Override
        public AuthTokenDto createFromParcel(Parcel source) {
            return new AuthTokenDto(source);
        }

        @Override
        public AuthTokenDto[] newArray(int size) {
            return new AuthTokenDto[size];
        }
    };
}
