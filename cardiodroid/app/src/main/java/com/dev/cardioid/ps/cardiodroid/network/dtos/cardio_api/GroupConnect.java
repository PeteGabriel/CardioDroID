package com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents the connection made between
 * a user and a chosen group from the app interface.
 */
public class GroupConnect implements Parcelable {

    private String userName;

    private String groupName;

    public GroupConnect(){}

    public GroupConnect(String userName, String groupName){
        this.groupName = groupName;
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getUserName() {
        return userName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.groupName);
    }

    protected GroupConnect(Parcel in) {
        this.userName = in.readString();
        this.groupName = in.readString();
    }

    public static final Parcelable.Creator<GroupConnect> CREATOR = new Parcelable.Creator<GroupConnect>() {
        @Override
        public GroupConnect createFromParcel(Parcel source) {
            return new GroupConnect(source);
        }

        @Override
        public GroupConnect[] newArray(int size) {
            return new GroupConnect[size];
        }
    };
}
