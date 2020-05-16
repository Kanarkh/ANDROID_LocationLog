package com.example.locationlog_02.Activitys.ETC;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.locationlog_02.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Semaphore;


public class OpenCV_Camera extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener {

    //View
    LinearLayout filterLayout;
    ConstraintLayout cameraLayout;
    Button exitFilterButton;
    SeekBar filterSeekBar;

    ImageButton filterBasic;
    ImageButton filterRed;
    ImageButton filterBlue;
    ImageButton filterGreen;
    ImageButton filterNegative;

    //OpenCV
    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;

    //State 변수
    long screenMod = 0;
    long filterValue = 0;
    int changeCameraIndex = 0; // 0은 후면. 1은 전면


    //Native C++
    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult, long filterMod, long filterValue); //자바 코드에 C++ JNI 함수와 연결할 메소드를 선언합니다.
    public native void RotateImage(long matAddrInput, long matAddrResult, int angle);


    //OpenCV 네이티브 라이브러리와 C++ 코드로 빌드된 라이브러리를 로드합니다.
    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    //세마포어 -사진저장에 사용
    private final Semaphore writeLock = new Semaphore(1);

    public void getWriteLock() throws InterruptedException {
        writeLock.acquire();
    }

    public void releaseWriteLock() {
        writeLock.release();
    }

    //기울기 센서.
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    int pitch = 0;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_open_cv__camera);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        //View
        cameraLayout = findViewById(R.id.openCVcamera_cameraLayout);
        filterLayout = findViewById(R.id.openCVcamera_filterLayout);
        filterLayout.setVisibility(View.GONE);
        filterSeekBar = findViewById(R.id.openCVcamera_filterSeekBar);
        filterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filterValue=seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                filterValue=seekBar.getProgress();
                Log.e(TAG+"SeekValue : ",String.valueOf(filterValue));
            }
        });

        exitFilterButton = findViewById(R.id.openCVcamera_exitFilterButton);
        exitFilterButton.setVisibility(View.GONE);

        //filter button
         filterBasic=findViewById(R.id.openCVcamera_filter_basic);
         filterRed=findViewById(R.id.openCVcamera_filter_red);
         filterBlue=findViewById(R.id.openCVcamera_filter_blue);
         filterGreen=findViewById(R.id.openCVcamera_filter_green);
         filterNegative=findViewById(R.id.openCVcamera_filter_negative);

        //open CV
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(changeCameraIndex); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        //App Bar 감추기
        ActionBar ab = getSupportActionBar();
        ab.hide();

        //센서
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    } //end onCreate

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        //센서
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        //센서
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    //카메라로부터 캡처된 영상을 onCameraFrame에서 받습니다.

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        try {
            getWriteLock(); //세마포어

            matInput = inputFrame.rgba();

            //if ( matResult != null ) matResult.release(); fix 2018. 8. 18
//            Log.e(TAG, "matInput Type: "+String.valueOf(matInput.type())); // 결과 : 24
            if (matResult == null)
                matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type()); //결과를 저장할 matResult객체를 생성합니다.

            if (changeCameraIndex == 1) { //전면 화면일경우 화면회전.
                Core.flip(matInput, matInput, 1);
            }


            ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr(), screenMod,filterValue); //ConvertRGBtoGray에 입력 Mat객체와 결과를 가져올 Mat객체 주소를 전달합니다.

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        releaseWriteLock();


//        //컬러 영상을 보이게 하려면 matResult 대신에 matInput을 리턴하세요
//        if (screenMod == 0) {
//            return matInput; // 일반화면을 보여준다.
//        } else {
//            return matResult; // OpenCV를 보여준다.
//        }
        return matResult; // 0인경우는 내부에서 처리해줄것.

    }


    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA"};


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(OpenCV_Camera.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    //------------------------------------------------------------------------------------------------ 버튼

    public void rotationThiScreen(View view) {

        if (changeCameraIndex == 0) { // 0이다 => 이전에 실행한 화면이 0이다.
            changeCameraIndex = 1;
            mOpenCvCameraView.disableView();
            mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)
            mOpenCvCameraView.enableView();
        } else {
            changeCameraIndex = 0; // 0이 아니다 => 이전에 실행했던 화면이 1이다.
            mOpenCvCameraView.disableView();
            mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
            mOpenCvCameraView.enableView();
        }

    }

    public void saveThisPhoto(View view) {

        try {
            //세마포어
            getWriteLock();

            //갤러리 주소 연결
            File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
            path.mkdirs();

            //오늘 날짜
            SimpleDateFormat formatDate = new SimpleDateFormat("YYYYMMddhhmmss");
            String theDate = formatDate.format(new Date());

            File file = new File(path, "openCVimage_" + theDate + ".jpg"); //오늘날짜로 파일 이름

            String filename = file.toString();


            Imgproc.cvtColor(matResult, matResult, Imgproc.COLOR_BGR2RGBA);
            if (pitch > 45) //핸드폰 기울기가 세로이면
                RotateImage(matResult.getNativeObjAddr(), matResult.getNativeObjAddr(), 90); //화면을 세워서 저장한다.
            boolean ret = Imgcodecs.imwrite(filename, matResult);

            if (ret) Log.d(TAG, "SUCESS");
            else Log.d(TAG, "FAIL");

//            Log.e(TAG, String.valueOf(Uri.fromFile(file)));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            sendBroadcast(mediaScanIntent);

            //인텐트로 사진 URI 넘기기
            Intent resultIntent = new Intent();
            resultIntent.putExtra("img", String.valueOf(Uri.fromFile(file)));
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //세마포어
        releaseWriteLock();
    }

    //-----------------------------------------------------------------------------------------------  센서 이벤트
    public static float[] computeOrientation(float[] accel, float[] magnetic) {
        float[] inR = new float[16];
        float[] I = new float[16];
        float[] outR = new float[16];
        float[] values = new float[3];

        SensorManager.getRotationMatrix(inR, I, accel, magnetic);
        SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
        SensorManager.getOrientation(outR, values);
        return values;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long lastComputedTime = 0;

        if (sensorEvent.sensor == mAccelerometer) {

            System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length);
            mLastAccelerometerSet = true;

        } else if (sensorEvent.sensor == mMagnetometer) {

            System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
            mLastMagnetometerSet = true;
        }


        if (mLastAccelerometerSet && mLastMagnetometerSet) {

            long tempTime = System.currentTimeMillis();
            if (tempTime - lastComputedTime > 1000) {
                float[] orientationValues = computeOrientation(mLastAccelerometer, mLastMagnetometer);

                pitch = (int) ((360 * orientationValues[1]) / (2 * Math.PI)); //여기서 기울기값을 읽어온다.
                pitch = Math.abs(pitch); // 절대값으로 바꾼다.
                //경사도
                //pitchValue.setText("pitch="+String.valueOf(pitch));
//                Log.e(TAG, String.valueOf(pitch));
            }
        }
    }//end sencerChange

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //------------------------------------------------------------------------------------------------필터 버튼
    public void changeFilterScreen(View view) {
        // 0 ~ 5 까지 숫자를 누를때마다 순서대로 바꿔준다.
        // screenMod는 openCV의 모드를 결정한다.
//        screenMod++;
//
//        if (screenMod == 16)
//            screenMod = 0;
        cameraLayout.setVisibility(View.GONE);
        filterLayout.setVisibility(View.VISIBLE);
        exitFilterButton.setVisibility(View.VISIBLE);
    }

    public void exitFilter(View view){
        cameraLayout.setVisibility(View.VISIBLE);
        filterLayout.setVisibility(View.GONE);
        exitFilterButton.setVisibility(View.GONE);
    }
    public void filter00(View view){
        screenMod=0;  //origin
        changeBttonBackgraund(0);
    }
    public void filter01(View view){
        screenMod=1; //Anti Red
        changeBttonBackgraund(1);
    }
    public void filter02(View view){
        screenMod=2; //Anti Green
        changeBttonBackgraund(2);
    }
    public void filter03(View view){
        screenMod=3; //Anti Blue
        changeBttonBackgraund(3);
    }
    public void filter04(View view){
        screenMod=4; //Anti Screen
        changeBttonBackgraund(4);
    }

    private void changeBttonBackgraund(int num){
        switch (num){
            case 0:
                filterBasic.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon_select));
                filterRed.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterBlue.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterGreen.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterNegative.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                break;
            case 1:
                filterBasic.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterRed.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon_select));
                filterBlue.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterGreen.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterNegative.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                break;
            case 2:
                filterBasic.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterRed.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterBlue.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterGreen.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon_select));
                filterNegative.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                break;
            case 3:
                filterBasic.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterRed.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterBlue.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon_select));
                filterGreen.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterNegative.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                break;
            case 4:
                filterBasic.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterRed.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterBlue.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterGreen.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon));
                filterNegative.setImageDrawable(getResources().getDrawable(R.drawable.button_tetragon_select));
                break;
        }
    }
}