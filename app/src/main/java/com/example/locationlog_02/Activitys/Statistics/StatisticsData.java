package com.example.locationlog_02.Activitys.Statistics;

public class StatisticsData {
    String Date;
    long getTimeData;
    double distance;

    public StatisticsData(String date, long getTimeData, double distance) {
        Date = date;
        this.getTimeData = getTimeData;
        this.distance = distance;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public long getGetTimeData() {
        return getTimeData;
    }

    public void setGetTimeData(long getTimeData) {
        this.getTimeData = getTimeData;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
