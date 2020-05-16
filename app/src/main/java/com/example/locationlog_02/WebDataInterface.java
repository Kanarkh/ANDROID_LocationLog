package com.example.locationlog_02;

import android.webkit.JavascriptInterface;


public class WebDataInterface {
    String day0;
    String day1;
    String day2;
    String day3;
    int dis0;
    int dis1;
    int dis2;
    int dis3;

    public WebDataInterface() {
    }

    public void setData(int intex, String day, int dis){
        if(intex==0){
            day0 = day;
            dis0 = dis;
        }else if(intex==1){
            day1 = day;
            dis1 = dis;
        }else if(intex==2){
            day2 = day;
            dis2 = dis;
        }else if(intex==3){
            day3 = day;
            dis3 = dis;
        }
    }

    @JavascriptInterface
    public int GetDis0() {
        return dis0;
    }
    @JavascriptInterface
    public int GetDis1() {
        return dis1;
    }
    @JavascriptInterface
    public int GetDis2() {
        return dis2;
    }
    @JavascriptInterface
    public int GetDis3() {
        return dis3;
    }

    @JavascriptInterface
    public String GetDay0() {
        return day0;
    }
    @JavascriptInterface
    public String GetDay1() {
        return day1;
    }
    @JavascriptInterface
    public String GetDay2() {
        return day2;
    }
    @JavascriptInterface
    public String GetDay3() {
        return day3;
    }

}
