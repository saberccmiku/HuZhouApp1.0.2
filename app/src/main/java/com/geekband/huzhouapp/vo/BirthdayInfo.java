package com.geekband.huzhouapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/6/27
 */
public class BirthdayInfo implements Parcelable {
    private String avatarImage ;
    private String realName;
    private String date;
    private String userId;

    public BirthdayInfo(){}

    protected BirthdayInfo(Parcel in) {
        avatarImage = in.readString();
        realName = in.readString();
        date = in.readString();
        userId = in.readString();
    }

    public static final Creator<BirthdayInfo> CREATOR = new Creator<BirthdayInfo>() {
        @Override
        public BirthdayInfo createFromParcel(Parcel in) {
            return new BirthdayInfo(in);
        }

        @Override
        public BirthdayInfo[] newArray(int size) {
            return new BirthdayInfo[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "BirthdayInfo{" +
                "avatarImage='" + avatarImage + '\'' +
                ", realName='" + realName + '\'' +
                ", date='" + date + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatarImage);
        dest.writeString(realName);
        dest.writeString(date);
        dest.writeString(userId);
    }
}
