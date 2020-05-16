package com.example.locationlog_02.AsyncTask_Study;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.Activitys.MyMapTrace.MyMapTrace;
import com.example.locationlog_02.Activitys.Statistics.Setting;
import com.example.locationlog_02.R;

import java.net.URL;

public class QuickCamera extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_camera);

//        Intent intent = new Intent(this, QuickCamera.class);
//        startActivity(intent);
//        finish();
    }
    //------------------------------------------------------------------------------------------------화면이동

    public void changeActivityMyMapLog(View view) {
        Intent changeIntent = new Intent(this, MyMapLog.class);
        startActivity(changeIntent);
        finish();
    }

    public void changeActivitySetting(View view) {
        Intent changeIntent = new Intent(this, Setting.class);
        startActivity(changeIntent);
        finish();
    }
    public void changeActivityMyMapTrace(View view) {
        Intent changeIntent = new Intent(this, MyMapTrace.class);
        startActivity(changeIntent);
        finish();
    }
}

class FilesTask extends AsyncTask<URL, Integer, Long>{

    //class AsyncTask<Params, Progress, Result>
    // Params : doInBackground 파라미터 타입이 되며, execute 메소드 인자 값이 됩니다.
    // Progress : doInBackground 작업 시 진행 단위의 타입으로 onProgressUpdate 파라미터 타입 입니다.
    // Result : doInBackground 리턴값으로 onPostExecute 파라미터 타입 입니다. 

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    //step 1
    //onPreExecute(), invoked on the UI thread before the task is executed. This step is normally used to setup the task, for instance by showing a progress bar in the user interface.

    @Override
    protected Long doInBackground(URL... urls) {
        return null;
    }
    //MUST
    //step 2
    //doInBackground(Params...), invoked on the background thread immediately after onPreExecute() finishes executing.
    // This step is used to perform background computation that can take a long time.
    // The parameters of the asynchronous task are passed to this step. //비동기 태스크의 매개 변수는 이 단계로 전달된다.
    // The result of the computation must be returned by this step and will be passed back to the last step. //계산 결과는 이 단계까지 반환되어야 하며 마지막 단계로 다시 전달될 것이다.
    // This step can also use publishProgress(Progress...) to publish one or more units of progress.
    // These values are published on the UI thread, in the onProgressUpdate(Progress...) step.



    @Override  //progress
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
    //setp 3
    //onProgressUpdate(Progress...), invoked on the UI thread after a call to publishProgress(Progress...).
    // The timing of the execution is undefined.
    // This method is used to display any form of progress in the user interface while the background computation is still executing.
    // For instance, it can be used to animate a progress bar or show logs in a text field.

    @Override //Result
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
    }
    //step 4
    //onPostExecute(Result), invoked on the UI thread after the background computation finishes.
    // The result of the background computation is passed to this step as a parameter.
}
