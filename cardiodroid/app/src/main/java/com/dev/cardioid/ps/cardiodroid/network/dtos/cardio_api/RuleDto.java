package com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api;

import android.os.Parcel;
import android.os.Parcelable;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;

/**
 * TODO
 */
public class RuleDto implements Parcelable {

    private int id;
    private String jsonRule;
    private boolean isPrivate;
    private String creator;
    private Rule mRule;

    public RuleDto(){

    }

    public RuleDto(Rule rule, boolean isPrivate, String email){
        this.id = rule.getID();
        this.jsonRule = rule.getNativeRule().toString();
        this.isPrivate = isPrivate;
        this.creator = email;
        this.mRule = rule;
    }

    public RuleDto(Rule rule){
        this(rule, false, "");
    }

    public int getId() {
        return id;
    }

    public Rule getRule() {
        return mRule;
    }

    public String getCreator() {
        return creator;
    }

    public String getJsonRule() {
        return jsonRule;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJsonRule(String jsonRule) {
        this.jsonRule = jsonRule;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setRule(Rule mRule) {
        this.mRule = mRule;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.jsonRule);
        dest.writeByte(this.isPrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.creator);
        dest.writeParcelable(this.mRule, flags);
    }

    protected RuleDto(Parcel in) {
        this.id = in.readInt();
        this.jsonRule = in.readString();
        this.isPrivate = in.readByte() != 0;
        this.creator = in.readString();
        this.mRule = in.readParcelable(Rule.class.getClassLoader());
    }

    public static final Parcelable.Creator<RuleDto> CREATOR = new Parcelable.Creator<RuleDto>() {
        @Override
        public RuleDto createFromParcel(Parcel source) {
            return new RuleDto(source);
        }

        @Override
        public RuleDto[] newArray(int size) {
            return new RuleDto[size];
        }
    };
}
