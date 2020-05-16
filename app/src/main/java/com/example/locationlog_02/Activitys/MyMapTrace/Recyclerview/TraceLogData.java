package com.example.locationlog_02.Activitys.MyMapTrace.Recyclerview;

public class TraceLogData {
    private String title;
    private String startTime;
    private String stopTime;
    private double distance;
    private String imgIcon;
    private String traceData;
    private String tracePlaces;
    private long id;

    public TraceLogData() {
    }

    public TraceLogData(String title, String startTime, String stopTime, double distance, String imgIcon, String traceData, String tracePlaces, long id) {
        this.title = title;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.distance = distance;
        this.imgIcon = imgIcon;
        this.traceData = traceData;
        this.tracePlaces = tracePlaces;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getImgIcon() {
        return imgIcon;
    }

    public void setImgIcon(String imgIcon) {
        this.imgIcon = imgIcon;
    }

    public String getTraceData() {
        return traceData;
    }

    public void setTraceData(String traceData) {
        this.traceData = traceData;
    }

    public String getTracePlaces() {
        return tracePlaces;
    }

    public void setTracePlaces(String tracePlaces) {
        this.tracePlaces = tracePlaces;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
