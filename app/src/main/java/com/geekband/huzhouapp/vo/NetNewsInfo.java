package com.geekband.huzhouapp.vo;

/**
 * Created by Administrator on 2016/5/18
 */
public class NetNewsInfo  {

    private int id;
    private String newsTitle;
    private String newsPic;
    private String newsHTML;


    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsPic() {
        return newsPic;
    }

    public void setNewsPic(String newsPic) {
        this.newsPic = newsPic;
    }

    public String getNewsHTML() {
        return newsHTML;
    }

    public void setNewsHTML(String newsHTML) {
        this.newsHTML = newsHTML;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NetNewsInfo{" +
                "newsTitle='" + newsTitle + '\'' +
                ", newsPic='" + newsPic + '\'' +
                ", newsHTML='" + newsHTML + '\'' +
                ", id=" + id +
                '}';
    }
}
