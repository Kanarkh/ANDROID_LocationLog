package com.example.locationlog_02.Activitys.MyMapLog.Recyclerview;

public class TitleListData {
    private String title;
    private int totalNum;
    private long id;

    public TitleListData() {
    }

    public TitleListData(String title) {
        this.title = title;
    }

    public TitleListData(String title, long id) {
        this.title = title;
        this.id = id;
    }

    public TitleListData(String title, int totalNum, long id) {
        this.title = title;
        this.totalNum = totalNum;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
