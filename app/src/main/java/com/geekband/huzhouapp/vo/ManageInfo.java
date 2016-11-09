package com.geekband.huzhouapp.vo;

/**
 * Created by Administrator on 2016/5/19
 */
public class ManageInfo  {

    private String title;
    private String content;
    private int imageId;

    public ManageInfo(String title, String content, int imageId) {
        this.title = title;
        this.content = content;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
