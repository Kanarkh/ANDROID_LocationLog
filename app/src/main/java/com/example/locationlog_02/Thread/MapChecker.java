package com.example.locationlog_02.Thread;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.locationlog_02.Activitys.ETC.TreasureActivity;
import com.example.locationlog_02.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;


public class MapChecker implements Runnable {

    private static final String TAG = "MapChecker";

    private Context context;

    private static MapChecker instance; //Singletion pattern
    private static Thread threadInstance; //Singletion pattern
    private static boolean isRunThread = false; //Thread 동작상태 확인

    private FusedLocationProviderClient mFusedLocationClient; //위치를 가져오기 위함
    Handler mHandler = new Handler();

    //Treasure

    ArrayList<Location> locationsList = new ArrayList<>();
    boolean isTreasureRun = false;
    Location treasureLocation = null;

    private MapChecker() {

    }

    public static MapChecker getInstance() {

        if (instance == null)
            instance = new MapChecker();

        return instance;
    }

    public static Thread getThreadInstance() {
        if (instance != null) {
            if (threadInstance == null)
                threadInstance = new Thread(instance);
        } else
            Log.e(TAG, "instance Null");

        return threadInstance;
    }

    public void setInit(Context context) {
        this.context = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    Location firstLocation;

    @Override
    public void run() {
        setTreasurePoint(); // 보물위치
        isRunThread = true;

        //퍼미션 확인
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

        //현재위치 가져오기
        mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                firstLocation = new Location("첫 위치");
                firstLocation.setLatitude(location.getLatitude());
                firstLocation.setLongitude(location.getLongitude());
            }
        });

        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
            //거리를 가져오기 위한 리스너.
            mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

//                    Log.e(TAG + " latitude", "" + location.getLatitude());
//                    Log.e(TAG + " longitude", "" + location.getLongitude());
//                    Log.e(TAG+" DistanceBetween_0",""+location1.distanceTo(location));
                    //Log.e(TAG+"_위치편차",""+firstLocation.distanceTo(location));

                    if (isTreasureRun == false && locationsList.size() != 0) {
//
                        for (int i = 0; i < locationsList.size(); i++) {
                            if (location.distanceTo(locationsList.get(i)) <= 30) { // 보물과 내 위치가 10M 내 접근하였다.
                                isTreasureRun = true;
                                treasureLocation = locationsList.get(i); //이 위치의 보물을 기역한다.
                                dialog();
                            }
                        }
                    } else {
                        // isTreasureRun = =true;
                        // 보물찾기가 동작중이거나 취소되었다

                        if (location.distanceTo(treasureLocation) > 50) { // 보물과의 위치가 30m 이상 떨어졌다.
                            isTreasureRun = false; //보물찾기를 다시 할수있다. //보물찾기가 끝나야 다른위치도 검색이 되게할것.
                        }

                    }
                }
            });


        }//end While
        threadInstance = null; //end thread;
        isRunThread = false;
    }

    private void setTreasurePoint() {
        Location location1;
        Location location2;
        location1 = new Location("남성역 T 월드");
        location1.setLatitude(37.485021);
        location1.setLongitude(126.970710);
        location2 = new Location("남성역 한스델리");
        location2.setLatitude(37.48370);
        location2.setLongitude(126.97250);
        locationsList.add(location1);
        locationsList.add(location2);
    }

    //-------------------------------------------------------------------------------------------------
    private int checkSelfPermission(String accessFineLocation) {
        return 0;
    }

    public static boolean isIsRunThread() {
        return isRunThread;
    }

    public static void setIsRunThread(boolean isRunThread) {
        MapChecker.isRunThread = isRunThread;
    }

    //------------------------------------------------------------------------------------------------- 보물찾기 Dialog
    private void dialog() {
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_public_simpletext);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final Button okButton = (Button) dlg.findViewById(R.id.dialog_publicText_okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.dialog_publicText_cancelButton);
        CheckBox checkBox = dlg.findViewById(R.id.dialog_publicText_checkBox);
        final TextView titleText = dlg.findViewById(R.id.dialog_publicText_title);
        final TextView textView = dlg.findViewById(R.id.dialog_publicText_text);
//        checkBox.setVisibility(View.VISIBLE);
        titleText.setText("보물찾기");
        textView.setText("보물지도를 찾았다! 보물을 찾아볼까요?");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TreasureActivity.class);
                context.startActivity(intent);
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dlg.dismiss();
            }
        });

    }
}
