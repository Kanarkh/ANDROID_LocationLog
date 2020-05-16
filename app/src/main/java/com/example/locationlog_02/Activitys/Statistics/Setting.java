package com.example.locationlog_02.Activitys.Statistics;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.Activitys.MyMapTrace.MyMapTrace;
import com.example.locationlog_02.Activitys.MyMapTrace.Recyclerview.TraceLogData;
import com.example.locationlog_02.Activitys.QuickAddContents;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;
import com.example.locationlog_02.WebDataInterface;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Setting extends AppCompatActivity { //Statistic

    private static final String TAG = "Statistic";
    //View
    TextView totalDistanceTextView;
    //DB controller
    LocationLogDBHelper logDBHelper;
    //List
    ArrayList<TraceLogData> traceDataList; //Read Trace List
    ArrayList<StatisticsData> statisticsList = new ArrayList<>();

    //
    int pointIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("Statistics");

        setDataBase(); //DB 읽어오기
        setTotalDistance(); //총거리 표시

        setStatisticData(); //통계데이터 정렬

        setGoogleChart(0); //초기값 구글데이터에 표시하기
    }

    //------------------------------------------------------------------------------------------------초기화

    private void setStatisticData() {
        //같은 데이터를 합쳐준다.
        for (int i = 0; i < statisticsList.size(); i++) {
            for (int j = i+1; j < statisticsList.size(); j++) {
                if(statisticsList.get(i).getTimeData ==statisticsList.get(j).getTimeData){
                    //같은 날짜 데이터이다.
                    statisticsList.get(i).distance += statisticsList.get(j).getDistance(); //거리를 더해준다.
                    statisticsList.remove(j);
                    j--;
                }
            }
            if(statisticsList.size() <= i){ //다음 검사할 데이터가 없다면 빠져나온다.
                break;
            }
        }

        //내림차순으로 정렬을 한다.
        int maxNum=0;
        for (int i = 0; i < statisticsList.size(); i++) {
            maxNum = i; //초기화
            for (int z = i; z < statisticsList.size(); z++) {
                if(statisticsList.get(maxNum).getTimeData>statisticsList.get(z).getTimeData){ // '<' 이 방향 이해안간다. 왜 예측한것(최대값)이 안나오고 최소값으로 정렬이 되는가
                    // 새로운 최대값.
                    maxNum = z;
                }
            }
            //최대값을 맨 앞으로 이동시킨다.
            StatisticsData tempData = statisticsList.get(maxNum); //최대값.
            statisticsList.remove(maxNum);
            statisticsList.add(0,tempData);
        }

        for (int i = 0; i < statisticsList.size(); i++) {
            Log.e(TAG+"정리후:","ID : "+i+"/"+statisticsList.get(i).Date + "/"+String.valueOf(statisticsList.get(i).getGetTimeData())+"/"+String.valueOf(statisticsList.get(i).getDistance()));
        }
    }

    private void setGoogleChart(int startIndex) {
        //구글차트에 값 넘겨줄 인터페이스 생성
        WebDataInterface webAppInterface = new WebDataInterface();
        for (int i = 0; i < 4; i++) {
            webAppInterface.setData(i,statisticsList.get(startIndex+i).getDate(), (int) statisticsList.get(startIndex+i).getDistance());
        }

        String url = "file:///android_asset/www/columncharts.html";
        WebView chart = (WebView) findViewById(R.id.setting_webView);
        chart.addJavascriptInterface(webAppInterface, "Android");
        chart.getSettings().setJavaScriptEnabled(true);
        chart.loadUrl(url);
    }

    private void setDataBase() { //DB에서 Trace Tata 읽어오기
        //DB
        logDBHelper = new LocationLogDBHelper(this);
        //Trace 데이터 가져오기.
        traceDataList = logDBHelper.readTraceList();
    }
    private void setTotalDistance() { //총거리 데이터 읽어오기.
        //View 초기화
        totalDistanceTextView=findViewById(R.id.setting_distanceTextView);
        //데이터 계산하기
        double tempDistance=0;

        //Statistic Data List에 필요한 정보
        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-mm-dd");
        for (int i = 0; i < traceDataList.size(); i++) {
            tempDistance +=traceDataList.get(i).getDistance();

            //statistic Data List 만들기
            String ymd = traceDataList.get(i).getStartTime().substring(0,10);
            Date date = null;
            try {
                date = format0.parse(ymd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String ymdNew = ymd.substring(0,4)+"년 "+ymd.substring(5,7)+"월 "+ymd.substring(8,10)+"일";
            statisticsList.add(new StatisticsData(ymdNew,date.getTime(),traceDataList.get(i).getDistance()));
            Log.e(TAG,  ymdNew+ "/"+String.valueOf(date.getTime())+"/"+String.valueOf(traceDataList.get(i).getDistance()));
        }

        totalDistanceTextView.setText(String.valueOf(tempDistance)+"m");
    }

    //------------------------------------------------------------------------------------------------버튼클릭
    public void preChart(View view){
        if(pointIndex>0){
            pointIndex--;//시작위치변경
            setGoogleChart(pointIndex);
        }
    }
    public void nextChart(View view){

        pointIndex++; //시작위치 변경
        if(statisticsList.size() <= pointIndex+3){
            //이동불가
            pointIndex--; //이동이 불가능하기에 추가한 값을 원복시킨다.
        }else{
            //이동가능
            setGoogleChart(pointIndex);
        }
    }
    //------------------------------------------------------------------------------------------------화면이동

    public void changeActivityMyMapLog(View view){
        Intent changeIntent = new Intent(this, MyMapLog.class);
        startActivity(changeIntent);
        finish();
    }

    public void changeActivityMyMapTrace(View view){
        Intent changeIntent = new Intent(this, MyMapTrace.class);
        startActivity(changeIntent);
        finish();
    }
    public void changeActivityCamera(View view) {
        Intent changeIntent = new Intent(this, QuickAddContents.class);
        startActivity(changeIntent);
        finish();
    }

}
