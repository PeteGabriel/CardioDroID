package com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api;

import android.os.Parcel;

/**
 * TODO
 */
public class GroupsDto  implements ICardioApiDto{

    private GroupDto[] groups;

    public GroupsDto(GroupDto[] groups){
        this.groups = groups;
    }


    public GroupDto[] getGroups() {
        return groups;
    }

    public void setGroups(GroupDto[] groups) {
        this.groups = groups;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.groups, flags);
    }

    protected GroupsDto(Parcel in) {
        this.groups = in.createTypedArray(GroupDto.CREATOR);
    }

    public static final Creator<GroupsDto> CREATOR = new Creator<GroupsDto>() {
        @Override
        public GroupsDto createFromParcel(Parcel source) {
            return new GroupsDto(source);
        }

        @Override
        public GroupsDto[] newArray(int size) {
            return new GroupsDto[size];
        }
    };
}
