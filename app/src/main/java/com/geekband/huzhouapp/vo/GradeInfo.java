package com.geekband.huzhouapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/5/30
 */
public class GradeInfo implements Parcelable {
    private  int id ;
    private  String needGrade;
    private  String requiredCourse;
    private  String electiveCourse;
    private  String alreadyGrade;

    public GradeInfo() {
    }

    protected GradeInfo(Parcel in) {
        id = in.readInt();
        needGrade = in.readString();
        requiredCourse = in.readString();
        electiveCourse = in.readString();
        alreadyGrade = in.readString();
    }

    public static final Creator<GradeInfo> CREATOR = new Creator<GradeInfo>() {
        @Override
        public GradeInfo createFromParcel(Parcel in) {
            return new GradeInfo(in);
        }

        @Override
        public GradeInfo[] newArray(int size) {
            return new GradeInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNeedGrade() {
        return needGrade;
    }

    public void setNeedGrade(String needGrade) {
        this.needGrade = needGrade;
    }

    public String getRequiredCourse() {
        return requiredCourse;
    }

    public void setRequiredCourse(String requiredCourse) {
        this.requiredCourse = requiredCourse;
    }

    public String getElectiveCourse() {
        return electiveCourse;
    }

    public void setElectiveCourse(String electiveCourse) {
        this.electiveCourse = electiveCourse;
    }

    public String getAlreadyGrade() {
        return alreadyGrade;
    }

    public void setAlreadyGrade(String alreadyGrade) {
        this.alreadyGrade = alreadyGrade;
    }

    @Override
    public String toString() {
        return "GradeInfo{" +
                "id=" + id +
                ", needGrade='" + needGrade + '\'' +
                ", requiredCourse='" + requiredCourse + '\'' +
                ", electiveCourse='" + electiveCourse + '\'' +
                ", alreadyGrade='" + alreadyGrade + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(needGrade);
        dest.writeString(requiredCourse);
        dest.writeString(electiveCourse);
        dest.writeString(alreadyGrade);
    }
}
