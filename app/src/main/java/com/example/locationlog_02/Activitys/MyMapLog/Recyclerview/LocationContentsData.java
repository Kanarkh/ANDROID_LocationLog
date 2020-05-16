package com.example.locationlog_02.Activitys.MyMapLog.Recyclerview;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationContentsData implements Serializable {

    String day;
    String contentsText;
    ArrayList<String> contentsPhoto_uri;
    long id;
    private boolean isFirst;

    public LocationContentsData(String day, String contentsText, ArrayList<String> contentsPhoto_uri, long id) {
        this.day = day;
        this.contentsText = contentsText;
        this.contentsPhoto_uri = contentsPhoto_uri;
        this.id = id;

        //초기화
        isFirst = false;
    }

    public LocationContentsData() {
        //초기화
        isFirst = false;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<String> getContentsPhoto_uri() {
        return contentsPhoto_uri;
    }

    public void setContentsPhoto_uri(ArrayList<String> contentsPhoto_uri) {
        this.contentsPhoto_uri = contentsPhoto_uri;
    }

    public String getContentsText() {
        return contentsText;
    }

    public void setContentsText(String contentsText) {
        this.contentsText = contentsText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }
}
