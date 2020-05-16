package com.example.locationlog_02.Activitys.ETC;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locationlog_02.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class TreasureActivity extends AppCompatActivity implements Runnable{
    //ActionBar
    ActionBar myActionBar;
    ImageView hintImage;
    ProgressBar timeProgress;

    //핸들러
    Handler handler = new Handler();

    //상태변수
    int progressInt;
    int level=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure);

        //actionBar
        myActionBar = getSupportActionBar();
        myActionBar.setTitle("보물찾기");
        myActionBar.setDisplayHomeAsUpEnabled(true);

        //init

        //View
        hintImage = findViewById(R.id.treasure_hintImage);
        hintImage.setImageDrawable(getResources().getDrawable(R.drawable.image_treasure_4));
        timeProgress = findViewById(R.id.treasure_progressBar);
        timeProgress.setProgress(100);
        progressInt =100;

        //Thread
        Thread thisThread = new Thread(this);
        thisThread.start();
    }

    //------------------------------------------------------------------------------------------------Button
    public void scanButton(View view){

        //ZXing 바코드 리더기를 켠다.
        IntentIntegrator qrScan;
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("발견한 보물의 바코드를 사각형 안에 비춰주세요.");
        qrScan.initiateScan();
        //https://park-duck.tistory.com/108?category=843507 //QRcode Reder 참조 블로그
    }

    //------------------------------------------------------------------------------------------------Back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //Thread
    @Override
    public void run() {
        while (true){

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    timeProgress.setProgress(progressInt);

                    //시간에 따라 힌트가 달라진다.
                   if(progressInt==25){
                        hintImage.setImageDrawable(getResources().getDrawable(R.drawable.image_treasure_1));
                        level=1;
                    }else if(progressInt==50){
                        hintImage.setImageDrawable(getResources().getDrawable(R.drawable.image_treasure_2));
                       level=2;
                    }else if(progressInt==75){
                        hintImage.setImageDrawable(getResources().getDrawable(R.drawable.image_treasure_3));
                       level=3;
                    }
                }
            });
            progressInt--;

        }
    }


    //바코드 리더기 Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // todo
                if(result.getContents().equals("http://ksh")==true){
                    //보물 Code가 맞다면 보상 Activity
                    Toast.makeText(this, "보물발견!", Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(this, FindTreasure.class);
                    intent.putExtra("Level",level);
                    startActivity(intent);
                    fileList(); // 종료
                }else{
                    Toast.makeText(this, "보물이 아니네요! 다시 도전해봐요!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
