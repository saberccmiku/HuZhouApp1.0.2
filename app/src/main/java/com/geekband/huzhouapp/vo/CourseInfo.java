package com.geekband.huzhouapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/5/27
 */
public class CourseInfo implements Parcelable{
    private int id;
    //课程名
    private String title;
    //选修必修
    private String type;
    //课程简介
    private String intro;
    //详细内容
    private String point;
    //学习时长
    private String time;
    //详细内容
    private String detailed;

    public CourseInfo(Parcel in) {
        id = in.readInt();
        title = in.readString();
        type = in.readString();
        intro = in.readString();
        point = in.readString();
        time = in.readString();
        detailed = in.readString();
    }

    public static final Creator<CourseInfo> CREATOR = new Creator<CourseInfo>() {
        @Override
        public CourseInfo createFromParcel(Parcel in) {
            return new CourseInfo(in);
        }

        @Override
        public CourseInfo[] newArray(int size) {
            return new CourseInfo[size];
        }
    };

    public CourseInfo() {

    }

    public String getDetailed() {
        return detailed;
    }

    public void setDetailed(String detailed) {
        this.detailed = detailed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "CourseInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", intro='" + intro + '\'' +
                ", point='" + point + '\'' +
                ", time='" + time + '\'' +
                ", detailed='" + detailed + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(intro);
        dest.writeString(point);
        dest.writeString(time);
        dest.writeString(detailed);
    }
}
