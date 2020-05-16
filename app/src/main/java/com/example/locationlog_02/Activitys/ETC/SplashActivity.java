package com.example.locationlog_02.Activitys.ETC;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.R;

public class SplashActivity extends AppCompatActivity {

    SplashActivity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar bar = getSupportActionBar();
        bar.hide();


        //
        new SplashTask().execute();
    }

    class SplashTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Thread.sleep(500);
            }catch (Exception e){

            }
            publishProgress();

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Intent intent = new Intent(thisActivity, MyMapLog.class);
            startActivity(intent);
            thisActivity.finish();
        }
    }
}
