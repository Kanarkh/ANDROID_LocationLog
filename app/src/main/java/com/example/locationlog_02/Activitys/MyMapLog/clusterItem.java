package com.example.locationlog_02.Activitys.MyMapLog;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class clusterItem implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    public clusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public clusterItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
