package com.geekband.huzhouapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/16
 */
public class ClassInfo implements Parcelable {
    private int imageId;
    private String classTitle;
    private int contentCount;
    private String dateTime;
    private int flag;
    private ArrayList<?> list;



    public ClassInfo(int imageId, String classTitle) {
        this.imageId = imageId;
        this.classTitle = classTitle;
    }

    public ClassInfo(ArrayList<?> list,int imageId, String classTitle,int contentCount,String dateTime,int flag) {
        this.imageId = imageId;
        this.classTitle = classTitle;
        this.contentCount = contentCount;
        this.dateTime = dateTime;
        this.flag = flag;
        this.list = list;
    }

    protected ClassInfo(Parcel in) {
        imageId = in.readInt();
        classTitle = in.readString();
        contentCount = in.readInt();
        dateTime = in.readString();
        flag = in.readInt();
        list = in.readArrayList(getClass().getClassLoader());


    }


    public static final Creator<ClassInfo> CREATOR = new Creator<ClassInfo>() {
        @Override
        public ClassInfo createFromParcel(Parcel in) {
            return new ClassInfo(in);
        }

        @Override
        public ClassInfo[] newArray(int size) {
            return new ClassInfo[size];
        }
    };

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public int getContentCount(){
        return contentCount;
    }

    public void setContentCount(int contentCount){
        this.contentCount = contentCount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ArrayList<?> getList() {
        return list;
    }

    public void setList(ArrayList<?> list) {
        this.list = list;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageId);
        dest.writeString(classTitle);
        dest.writeInt(contentCount);
        dest.writeString(dateTime);
        dest.writeInt(flag);
        dest.writeList(list);
    }
}
