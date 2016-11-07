package com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api;

import android.os.Parcel;

/**
 * Represent information about a group that can be sent to the API or retrieved from
 * there.
 */
public class GroupDto  implements ICardioApiDto{

    private String name;

    public GroupDto(){
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected GroupDto(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<GroupDto> CREATOR = new Creator<GroupDto>() {
        @Override
        public GroupDto createFromParcel(Parcel source) {
            return new GroupDto(source);
        }

        @Override
        public GroupDto[] newArray(int size) {
            return new GroupDto[size];
        }
    };
}
