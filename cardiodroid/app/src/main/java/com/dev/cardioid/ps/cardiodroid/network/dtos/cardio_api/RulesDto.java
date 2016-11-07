package com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO
 */
public class RulesDto implements Parcelable {

    private RuleDto[] rules;

    public RulesDto(RuleDto[] rules){
        this.rules = rules;
    }

    public RuleDto[] getRules() {
        return rules;
    }

    public void setRules(RuleDto[] rules) {
        this.rules = rules;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.rules, flags);
    }

    protected RulesDto(Parcel in) {
        this.rules = in.createTypedArray(RuleDto.CREATOR);
    }

    public static final Parcelable.Creator<RulesDto> CREATOR = new Parcelable.Creator<RulesDto>() {
        @Override
        public RulesDto createFromParcel(Parcel source) {
            return new RulesDto(source);
        }

        @Override
        public RulesDto[] newArray(int size) {
            return new RulesDto[size];
        }
    };
}
