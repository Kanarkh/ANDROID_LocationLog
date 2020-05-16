package com.example.locationlog_02.Activitys.MyMapTrace;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.Activitys.MyMapTrace.Dialog.DialogStartTrace;
import com.example.locationlog_02.Activitys.MyMapTrace.Recyclerview.TraceLogData;
import com.example.locationlog_02.Activitys.MyMapTrace.Recyclerview.TraceLogRecyclerAdapter;
import com.example.locationlog_02.Activitys.QuickAddContents;
import com.example.locationlog_02.Activitys.Statistics.Setting;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;
import com.example.locationlog_02.Thread.MapChecker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class MyMapTrace extends AppCompatActivity implements TraceLogRecyclerAdapter.RecyclerClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String MARK = "☆";
    //리사이클러뷰
    RecyclerView recyclerView;
    public TraceLogRecyclerAdapter adapter;
    ArrayList<TraceLogData> recyclerDataList;

    // 상태변수
    private boolean isTraceRun;
    //    private String traceName;
    //VIew
    Button traceRunButton;

    //트레이스 데이터
    TraceLogData traceObject;

    //DB controller
    LocationLogDBHelper logDBHelper;

    //GoogleMap
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient; //현위치

    //
    TraceThread traceThread;
    //view
    TextView traceTitle;
    TextView traceTime;
    TextView traceDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map_trace);

        getSupportActionBar().setTitle("Location Trace");

        //View
        traceRunButton = findViewById(R.id.mapTrace_startstopButton);
        //view
        traceTitle = findViewById(R.id.mapTrace_traceTitle_textView);
        traceTime = findViewById(R.id.mapTrace_startTime_textView);
        traceDistance = findViewById(R.id.mapTrace_traceDistance_textView);
        //초기화
        isTraceRun = false;
        //DB
        logDBHelper = new LocationLogDBHelper(this);
        //리사이클러뷰
        recyclerView = findViewById(R.id.mapTrace_recyclerView);
        setTreacRecyclerData();
        setTreacRecyclerView();
        showMeTraceDetailLayout(false); //리사이클러뷰 Layout을 보여준다.
        //googleMap

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.myMap_trace_googleMap);
        mapFragment.getMapAsync(this);

        //현위치
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //MapChecker
        MapChecker mapChecker = MapChecker.getInstance();
        mapChecker.setInit(this);
    }

    //------------------------------------------------------------------------------------------------리사이클러뷰
    private void setTreacRecyclerData() {
//        recyclerDataList.add(new TraceLogData("전주여행", "2018-09-22 18:35", 4));
//        recyclerDataList.add(new TraceLogData("검단사거리", "2018-09-13 23:11", 14));
        recyclerDataList = logDBHelper.readTraceList();
    }

    private void setTreacRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TraceLogRecyclerAdapter(this, this);
        adapter.setItems(recyclerDataList);
        recyclerView.setAdapter(adapter);
    }

    //------------------------------------------------------------------------------------------------화면이동

    public void changeActivityMyMapLog(View view) {
        Intent changeIntent = new Intent(this, MyMapLog.class);
        changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(changeIntent);
        finish();
    }

    public void changeActivitySetting(View view) {
        Intent changeIntent = new Intent(this, Setting.class);
        changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(changeIntent);
        finish();
    }

    public void changeActivityCamera(View view) {
        Intent changeIntent = new Intent(this, QuickAddContents.class);
        changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(changeIntent);
        finish();
    }
    //------------------------------------------------------------------------------------------------기능

    private void showMeTraceDetailLayout(boolean isDetailNeed) {
        if (isDetailNeed == true) {
            findViewById(R.id.myMap_trace_detailInfo_Layout).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.myMap_trace_detailInfo_Layout).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    //------------------------------------------------------------------------------------------------버튼
    public void onStratStopButton(View view) {
        if (isTraceRun == false) {
            //객체 생성
            traceObject = new TraceLogData();

            //추적 시작
            DialogStartTrace dialogStartTrace = new DialogStartTrace(this, traceObject);
            dialogStartTrace.callFunction("", true, 0);
        } else {
            //추적 정지
            SimpleDateFormat formatDate = new SimpleDateFormat("YYYY-MM-dd hh:mm");
            String theDate = formatDate.format(new Date());
            traceObject.setStopTime(theDate); //정지시간.
            traceObject.setDistance(traceThread.distunceM);
            recyclerDataList.add(0, traceObject);
            adapter.notifyDataSetChanged();


            //map line 저장.
            String mapLine = "";
            for (int i = 0; i < traceThread.pinMapList.size(); i++) {
                Location mLocation = traceThread.pinMapList.get(i);
                mapLine += mLocation.getLatitude() + MARK + mLocation.getLongitude() + "【】";
            }

            //DB 저장
            //null이 없어야하기에 임의값을 넣는다.
            traceObject.setImgIcon("");
            traceObject.setTraceData(mapLine);
            traceObject.setTracePlaces("");

            logDBHelper.saveTraceLog(traceObject.getTitle(), traceObject.getStartTime(), traceObject.getStopTime(), traceObject.getDistance(), traceObject.getImgIcon(), traceObject.getTraceData(), traceObject.getTracePlaces());

            //다시 Thread 초기화
            setTraceRun(false); //thread 정지
            traceObject = null;
        }
    }

    //------------------------------------------------------------------------------------------------리사이클러뷰 클릭 이벤트
    @Override
    public void onTraceLogItemClicked(int pos) {
        TraceLogData logItem = recyclerDataList.get(pos);
        LinkedList<LatLng> latLngList = new LinkedList<>();
        String[] location = logItem.getTraceData().split(MyMapLog.MARK); //큰단위로 쪼갠다.
        for (int i = 0; i < location.length; i++) {
            String[] latiLonti = location[i].split(MARK);
            latLngList.add(new LatLng(Double.parseDouble(latiLonti[0]), Double.parseDouble(latiLonti[1])));
        }
        mMap.clear();
        if (latLngList.size() > 1) {
            for (int i = 0; i < latLngList.size(); i++) {

                if (i == 0) { //맨처음
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLngList.get(i));
                    markerOptions.title("시작 위치");
                    markerOptions.alpha(0.5f);
                    //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngList.get(i)));
                } else if (i > 0) { // 0 이후부터 시작한다.
                    mMap.addPolyline(new PolylineOptions()
                            .add(latLngList.get(i - 1), latLngList.get(i))
                            .width(5)
                            .color(Color.BLUE));
                }

                if (i == latLngList.size() - 1) {//마지막 위치
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLngList.get(i));
                    markerOptions.title("종료 위치");
                    markerOptions.alpha(0.5f);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(markerOptions);
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------getSet
    Thread thread;

    public void setTraceRun(boolean traceRun) {
        isTraceRun = traceRun;
        if (isTraceRun == true) {  //시작 이벤트

            //view
            traceTitle.setText(traceObject.getTitle());
            traceTime.setText("시작시간 : " + traceObject.getStartTime());
            traceDistance.setText("이동거리 : " + "0m");
            traceRunButton.setText("정지");

            //thread
            traceThread = TraceThread.getInstance();
            thread = TraceThread.getThreadInstance();
            traceThread.setInitContext(this, mMap);
            if (thread != null)
                thread.start();

            showMeTraceDetailLayout(true);
        } else { //정지 이벤트
            traceRunButton.setText("시작");
            if (thread != null) {
                thread.interrupt();
            }
            thread = null;
            showMeTraceDetailLayout(false);
        }
    }

    //------------------------------------------------------------------------------------------------googleMap
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");
//        mMap.addMarker(markerOptions);

        //Permission check
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);//내위치 지도에 표시
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //내위치로 이동하는 버튼 활성화
        mMap.getUiSettings().setZoomControlsEnabled(true); //줌 버튼 활성화
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));  //moveCamera 메소드를 사용하여 카메라를 지정한 경도, 위도로 이동합니다.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16)); //CameraUpdateFactory.zoomTo 메소드를 사용하여 지정한 단계로 카메라 줌을 조정합니다.  1 단계로 지정하면 세계지도 수준으로 보이고 숫자가 커질수록 상세지도가 보입니다.

        //현위치
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

            }
        });

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng thisPos = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(thisPos));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}

//------------------------------------------------------------------------------------------------ Thread
class TraceThread implements Runnable {

    private TraceThread() {
    }

    private FusedLocationProviderClient mFusedLocationClient; //위치를 가져오기 위함

    private static TraceThread instance;
    private static Thread threadInstance;
    private MyMapTrace context;
    private GoogleMap gMap;
    protected LinkedList<Location> pinMapList = new LinkedList<>();
    private Handler handler = new Handler();
    //위치
    private Location preLocation; //이전위치

    LinkedList<Location> errLocation = new LinkedList<>();//에러위치를 보관한다
    float preV = 0, curV = 0;
    int errCnt=0;

    //상태
    protected long distunceM = 0; //총 길이.

    public static TraceThread getInstance() {
        if (instance == null)
            instance = new TraceThread();
        return instance;
    }

    public void setInitContext(MyMapTrace context, GoogleMap gMap) { //Activity가 바뀔때마다 새로 넣어줘야한다.
        this.context = context;
        this.gMap = gMap;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    }

    public static Thread getThreadInstance() {
        if (instance != null) {
            if (threadInstance == null)
                threadInstance = new Thread(instance);
        } else {
            return null;
        }
        return threadInstance;
    }

    public void threadStop() {
        if (threadInstance != null)
            threadInstance.interrupt();
    }

    //가속도 필터
    //GPS가 정확하지 않다. 신호가 약해지면 위치정확도가 떨어지게 되고 갑자기 엉뚱한 위치가 기록되기도 한다.
    //이러한 현상을 막기위해 가속도 필터를 적용하였다.
    //속도로 제한을 주게되면 버스등을 탔을때 기록되지 않기 때문에 가속도를 이용하여. 현재속도와 이전속도의 차이를 구하여(가속도) 일정 범위 이상의 차이가 나는 가속도의 값은 무시한다.
    //(고민. 속도에 문제가 생겼을떄 기준을 어디로 잡아야하나. 마지막 기록된 위치로 기준을 잡아야하나. 아니면 이전현위치를 기준으로 잡아야하나.
    //마지막 위치를 기준으로 잡게됬을경우 이삼초 기록이 되지 않게되면. 계속해서 위치가 갱신되지 않게된다.
    //바로 이전의 현위치를 기준으로 잡게될경우 이초이상 해당 위치에 고정될경우 기록이 되는 경우가 생기게된다.
    //오류에 대한 문제가 3초이상 발생할 경우 (오류의 데이터를 모두 수집하고. 오류의 데이터중 마지막 기록과의 거리차가 가장 짧은 데이터를 기록한다.

    @Override
    public void run() {
        //시작전 초기화
        handler.post(new Runnable() {
            @Override
            public void run() {
                gMap.clear();
            }
        });
        // Thread 몸통
        while (true) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() { //위치를 읽어온다.
                @Override
                public void onSuccess(Location location) { //현위치 읽기에 성공했다

                    if (preLocation != null) {
                        //이전값이 있는경우의 처리

                        curV = location.distanceTo(preLocation); //m/s 의 s가 1이기때문에.

                        if ((curV - preV) < 30) { //가속도 차이가 날 경우 30m/s = > 54km/h , 현재 - 이전속도이기 때문에 이전속도가 더 빨랐을경우(감속하는 경우)는 음수가 뜬다. 우리가 필터링하려는건 속력이 갑자기 빨라졌을경우기때문에 상관없다.

                            errCnt=0;//기록에 성공했을시 초기화 해준다.
                            errLocation.clear();//다음 오류검사를 위해 초기화 해준다
                            // 5M 이상 크기가 차이날 경우.
                            if (preLocation.distanceTo(location) > 3) {
                                //이전 위치와 현재 위치를 그린다.

                                distunceM += preLocation.distanceTo(location);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        context.traceDistance.setText("이동거리 : " + distunceM + "m");
                                    }
                                });

                                gMap.addPolyline(new PolylineOptions()
                                        .add(new LatLng(preLocation.getLatitude(), preLocation.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude()))
                                        .width(5)
                                        .color(Color.BLUE)
                                );

                                pinMapList.add(location); //마지막 값을 저장한다.
                                preLocation = location; //이전값 저장
                            } else {
                                //10M가 안됬을경우 preLocation을 업데이트 하지않는다.
                            }
                        }else{ // 가속도 허용범위를 벗어났다.
                            if(errCnt>10) { //오류회수가 11회째다. 오류회수중 이전위치와 가장 거리가 짧은 것을 현위치로 기록한다.
                                //오류위치중 이전위치와 가장 위치가 짧은 위치를 기록한다.
                                float tempDis = 0; //거리를 임시보관
                                int thatNum=9999;//기록할 i의 값
                                errCnt++; // 오류 회수를 기록한다.
                                errLocation.add(location); // 현위치도 기록한다.
                                for (int i = 0; i < errLocation.size(); i++) {
                                    if(i==0){ //초기값
                                        tempDis = errLocation.get(i).distanceTo(preLocation);
                                        thatNum = i;
                                    }else{
                                        if(tempDis<errLocation.get(i).distanceTo(preLocation)){ //이전에 검사했던 것보다 거리가 짧게 나왔을경우.
                                            tempDis = errLocation.get(i).distanceTo(preLocation);
                                            thatNum = i;
                                        }
                                    }
                                }// end for errLocaion
                                if(thatNum!=9999){ // 초기로 선언해준것이 아니라면.
                                    //기록한다
                                    distunceM += preLocation.distanceTo(errLocation.get(thatNum)); //검사한 위치로 기록한다.
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            context.traceDistance.setText("이동거리 : " + distunceM + "m");
                                        }
                                    });

                                    gMap.addPolyline(new PolylineOptions()
                                            .add(new LatLng(preLocation.getLatitude(), preLocation.getLongitude()), new LatLng(errLocation.get(thatNum).getLatitude(), errLocation.get(thatNum).getLongitude()))
                                            .width(5)
                                            .color(Color.BLUE)
                                    );

                                    pinMapList.add(errLocation.get(thatNum)); //마지막 값을 저장한다.
                                    preLocation = errLocation.get(thatNum); //이전값 저장
                                    errCnt=0;
                                    errLocation.clear();//다음 오류검사를 위해 초기화 해준다
                                }
                            }else {
                                errCnt++; // 오류 회수를 기록한다.
                                errLocation.add(location); // 오류위치를 기록한다.
                            }
                        }

                    } else {
                        //이전값이 없다. 첫 데이터
                        preLocation = location; //이전값 저장
                        pinMapList.add(location); //시작점의 위치를 넣어준다.

                        preV = 0; //시작속도를 0으로해도 달리던 차에서도 동작이 되나?

                        //시작위치 마커를 만들어준다.
                        LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(mLatLng);
                        markerOptions.title("시작 위치");
                        markerOptions.alpha(0.5f);
                        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        gMap.addMarker(markerOptions);
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
                    }
                }// end onSuccess
            }); //end getLastLocation
        } //end while

        //다시 초기화
        threadInstance = null;
        distunceM = 0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                gMap.clear();
            }
        });
        pinMapList.clear();
    }

    private int checkSelfPermission(String accessFineLocation) {
        return 0;
    }
}