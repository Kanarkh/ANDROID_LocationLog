package com.example.locationlog_02.Activitys.MyMapLog.Recyclerview;

public class PlaceData {
    private String category;
    private String title;
    private double latitude;
    private double longitude;
    private int totalNum;
    private long id;

    public PlaceData(String category, String title, double latitude, double longitude) {
        this.category = category;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlaceData(String category, String title, double latitude, double longitude, long id) {
        this.category = category;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }
    //    public PlaceData(String category, String title) {
//        this.category = category;
//        this.title = title;
//    }


//    public PlaceData(String title) {
//        this.title = title;
//    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public void setId(long id) {
        this.id = id;
    }
}
