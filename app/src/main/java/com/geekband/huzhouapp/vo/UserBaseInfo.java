package com.geekband.huzhouapp.vo;

/**
 * Created by Administrator on 2016/5/25
 */
public class UserBaseInfo {
    private int id;
    private String userName;
    private String contentId;
    private String realName;
    private String phoneNum;
    private String IDcard;
    private String emailAddress;
    private String sex;
    private String policeNum;
    private String education;
    private String toWorkTime;
    private String toPoliceTime;
    private String rank;
    private String policePost;
    public  String avatarUrl ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userNmae) {
        this.userName = userName;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getIDcard() {
        return IDcard;
    }

    public void setIDcard(String IDcard) {
        this.IDcard = IDcard;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddres) {
        this.emailAddress = emailAddres;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPoliceNum() {
        return policeNum;
    }

    public void setPoliceNum(String policeNum) {
        this.policeNum = policeNum;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getToWorkTime() {
        return toWorkTime;
    }

    public void setToWorkTime(String toWorkTime) {
        this.toWorkTime = toWorkTime;
    }

    public String getToPoliceTime() {
        return toPoliceTime;
    }

    public void setToPoliceTime(String toPoliceTime) {
        this.toPoliceTime = toPoliceTime;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPolicePost() {
        return policePost;
    }

    public void setPolicePost(String policePost) {
        this.policePost = policePost;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "UserBaseInfo{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", contentId='" + contentId + '\'' +
                ", realName='" + realName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", IDcard='" + IDcard + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", sex='" + sex + '\'' +
                ", policeNum='" + policeNum + '\'' +
                ", education='" + education + '\'' +
                ", toWorkTime='" + toWorkTime + '\'' +
                ", toPoliceTime='" + toPoliceTime + '\'' +
                ", rank='" + rank + '\'' +
                ", policePost='" + policePost + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
