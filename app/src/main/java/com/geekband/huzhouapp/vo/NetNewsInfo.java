package com.geekband.huzhouapp.vo;

/**
 * Created by Administrator on 2016/5/18
 */
public class NetNewsInfo  {

    private int id;
    private String newsTitle;//新闻标题
    private String publishTime;//发布时间
    private String publisher;//发布人
    private String fromDepartment;//来源部门
    private String imagePath;//图片路径
    private String newsContent;//新闻内容


    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getFromDepartment() {
        return fromDepartment;
    }

    public void setFromDepartment(String fromDepartment) {
        this.fromDepartment = fromDepartment;
    }

    @Override
    public String toString() {
        return "NetNewsInfo{" +
                "id=" + id +
                ", newsTitle='" + newsTitle + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", publisher='" + publisher + '\'' +
                ", fromDepartment='" + fromDepartment + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", newsContent='" + newsContent + '\'' +
                '}';
    }
}
