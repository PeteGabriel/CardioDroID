package com.dev.cardioid.ps.cardiodroid.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * This class represents the concept of an user
 * that is build from the information received
 * from the auth token after performing
 * authentication with the Google service.
 */
public class User implements Parcelable {

    private String email;
    private String name;
    private String idToken;
    private String id;
    private Uri userPhotoUrl;


    public User(GoogleSignInAccount account){
        email = account.getEmail();
        name = account.getDisplayName();
        idToken = account.getIdToken();
        id = account.getId();
        userPhotoUrl = account.getPhotoUrl();
    }


    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getUserName() {
        return name;
    }

    public Uri getUserPhotoUrl() {
        return userPhotoUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeString(this.idToken);
        dest.writeString(this.id);
        dest.writeParcelable(this.userPhotoUrl, flags);
    }

    protected User(Parcel in) {
        this.email = in.readString();
        this.name = in.readString();
        this.idToken = in.readString();
        this.id = in.readString();
        this.userPhotoUrl = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
