package com.example.locationlog_02.Activitys;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.locationlog_02.Activitys.ETC.OpenCV_Camera;
import com.example.locationlog_02.Activitys.MyMapLog.AddContentsDetailPhoto;
import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.LocationContentsData;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.PlaceData;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.TitleListData;
import com.example.locationlog_02.Activitys.MyMapTrace.MyMapTrace;
import com.example.locationlog_02.Activitys.Statistics.Setting;
import com.example.locationlog_02.Manager.ImageManager;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.DbHelper;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;

import java.util.ArrayList;
import java.util.Date;

public class QuickAddContents extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAMERA = 672;
    private static final int REQUEST_IMAGE_GALLERY = 922;
    private static final String TAG = "AddContents";

    //ActionBar
    ActionBar myActionBar;

    //List
    public ArrayList<TitleListData> categoryRecyclerDataList;
    ArrayList<PlaceData> locationRecyclerDataList;

    //Spinner
    ArrayList<String> spinnerList_category;
    ArrayList<String> spinnerList_place;

    //Camera
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름

    //View
    ImageView firstPhoto_imageView;
    EditText contents_editText;
    Button saveButton;
    TextView photoNumTextView;
    TextView dateTextView;
    Button editDateButton;
    Spinner categorySpinner;
    Spinner placeSpinner;

    //Image
    ArrayList<String> imageUriList = new ArrayList<>();

    //Intent momIntent;

    //SQLite
    SQLiteDatabase db;
    DbHelper dbHelper;
    LocationLogDBHelper logDBHelper;

    //date
    SimpleDateFormat formatDate;
    String theDate;
    LocationContentsData itemData;
    String dateView;
    private ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_add_contents);

        //actionBar
        myActionBar = getSupportActionBar();
        myActionBar.setTitle("추가하기");

        //View
        firstPhoto_imageView = findViewById(R.id.addContentsQuick_imageView);
        firstPhoto_imageView.setVisibility(View.GONE);
        contents_editText = findViewById(R.id.addContentsQuick_editText);
        saveButton = findViewById(R.id.addContentsQuick_saveButton);
        photoNumTextView = findViewById(R.id.addContentsQuick_photoNumTextView);
        dateTextView = findViewById(R.id.addContentsQuick_date_text);
        editDateButton = findViewById(R.id.addContentsQuick_editDate_button);


        //객체생성
        itemData = new LocationContentsData();

        //date
        formatDate = new SimpleDateFormat("YYYYMMddHHmmss");
        theDate = formatDate.format(new Date());

        itemData.setDay(theDate);
        dateView = String.format("%s년 %s월 %s일 %s시 %s분", theDate.substring(0, 4), theDate.substring(4, 6), theDate.substring(6, 8), theDate.substring(8, 10), theDate.substring(10, 12));
        dateTextView.setText(dateView);

        //매니저 생성.
        imageManager = new ImageManager(this);

        //SQL DB
        dbHelper = DbHelper.getInstance(this);
        db = dbHelper.getWritableDatabase();
        db.isOpen();
        logDBHelper = new LocationLogDBHelper(this);

        spinnerInit();

    }

    //스피너
    private void spinnerInit() {
        //View 선언
        categorySpinner = findViewById(R.id.addContentsQuick_category_spinner);
        placeSpinner = findViewById(R.id.addContentsQuick_place_spinner);

        //DB에서 카테고리 이름 가져오기.
        categoryRecyclerDataList = logDBHelper.readCategoryList();

        //어뎁터에 들어갈 리스트 만들기
        spinnerList_place = new ArrayList<>();

        spinnerList_category = new ArrayList<>();
        for (int i = 0; i < categoryRecyclerDataList.size(); i++) { //스피니의 데이터 리스트를 만들어준다.
            spinnerList_category.add(categoryRecyclerDataList.get(i).getTitle());
        }
        categorySpinner.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerList_category));

        final QuickAddContents thisContext = this;

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {
                locationRecyclerDataList = logDBHelper.readPlaceList(categorySpinner.getSelectedItem().toString());

                //어뎁터에 들어갈 플레이스 리스트 만들기.
                spinnerList_place = new ArrayList<>();
                for (int i = 0; i < locationRecyclerDataList.size(); i++) { //스피니의 데이터 리스트를 만들어준다.
                    spinnerList_place.add(locationRecyclerDataList.get(i).getTitle());
                }
                placeSpinner.setAdapter(new ArrayAdapter(thisContext, R.layout.support_simple_spinner_dropdown_item, spinnerList_place));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //DB에서 Place Data 가져오기
        locationRecyclerDataList = logDBHelper.readPlaceList(categorySpinner.getSelectedItem().toString());

        //어뎁터에 들어갈 플레이스 리스트 만들기.
        spinnerList_place = new ArrayList<>();
        for (int i = 0; i < locationRecyclerDataList.size(); i++) { //스피니의 데이터 리스트를 만들어준다.
            spinnerList_place.add(locationRecyclerDataList.get(i).getTitle());
        }
        placeSpinner.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerList_place));

    }


    //------------------------------------------------------------------------------------------------버튼클릭

    public void openCameraButton(View view) {
        permissionWriteStorage(); //쓰기 권한을 받은뒤 카메라 권한을 물어본다.
    }

    public void openGalleryButton(View view) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(com.example.locationlog_02.Activitys.QuickAddContents.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            openPhotoGalleryIntent();
        }
    }

    private void permissionWriteStorage() {
        int permissionCheck_ = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck_ == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(com.example.locationlog_02.Activitys.QuickAddContents.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            permissionCamera(); //카메라 권한을 물어본다.
        }
    }

    private void permissionCamera() {
        //permission Camera
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(com.example.locationlog_02.Activitys.QuickAddContents.this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            sendTakePhotoIntent();
        }
    }

    public void firstPhotoClick(View view) {

        ArrayList<String> uriStringList = new ArrayList<>();
        for (int i = 0; i < imageUriList.size(); i++) {
            uriStringList.add(String.valueOf(imageUriList.get(i)));
        }

        if (uriStringList.size() != 0) {
            Intent intent = new Intent(this, AddContentsDetailPhoto.class);
            intent.putStringArrayListExtra("imageList", uriStringList);
            startActivity(intent);
        }
    }

    public void saveButton(View view) {

        if (contents_editText.getText().toString().equals("") && imageUriList.size() == 0) {
            Toast.makeText(this, "사진을 등록하거나 글을 작성해주세요", Toast.LENGTH_SHORT).show();
        } else {

            if(spinnerList_place.size()!=0) { // 플레이스에 데이터가 없는 경우

                itemData.setDay(theDate);
                itemData.setContentsText(contents_editText.getText().toString());

                ArrayList<String> imageUriStringList = new ArrayList<>();
                for (int i = 0; i < imageUriList.size(); i++) {
                    imageUriStringList.add(String.valueOf(imageUriList.get(i)));
                }

                itemData.setContentsPhoto_uri(imageUriStringList);


                ////////////////////////////////

                String totalImage = "";

                for (int i = 0; i < itemData.getContentsPhoto_uri().size(); i++) {
                    totalImage += itemData.getContentsPhoto_uri().get(i) + MyMapLog.MARK;
                }

                //DB 데이터 추가하기.
                logDBHelper.savePlaceContents(categorySpinner.getSelectedItem().toString(), placeSpinner.getSelectedItem().toString(), itemData.getDay(), itemData.getContentsText(), totalImage);

                Intent intent = new Intent(this,MyMapLog.class);
                intent.putExtra("quickCategory",categorySpinner.getSelectedItem().toString());
                intent.putExtra("quickPlace",placeSpinner.getSelectedItem().toString());
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, "저장할 장소가 없습니다. 장소를 추가해주세요", Toast.LENGTH_SHORT).show();
            }

        }

    }

    //날짜 수정
    public void changDateButton(View view) {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                //Toast.makeText(AddContents.this, ""+i+i1+i2, Toast.LENGTH_SHORT).show();
                dateView = String.format("%s년 %s월 %s일 %s시 %s분", i, i1, i2, theDate.substring(8, 10), theDate.substring(10, 12));
                dateTextView.setText(dateView);

                String mon;
                String day;
                if (i1 < 10)
                    mon = "0" + String.valueOf(i1);
                else
                    mon = String.valueOf(i1);
                if (i2 < 10)
                    day = "0" + i2;
                else
                    day = String.valueOf(i2);

                theDate = i + mon + day + itemData.getDay().substring(8);

            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener, 2019, 8, 13);
        datePickerDialog.show();
    }

    //------------------------------------------------------------------------------------------------Permission
    //permission Camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == 0) {
                Toast.makeText(this, "카메라 권한이 승인되었습니다", Toast.LENGTH_SHORT).show();
                sendTakePhotoIntent();
            } else {
                Toast.makeText(this, "카메라 권한이 거절되었습니다", Toast.LENGTH_SHORT).show();
                //finish();
            }
        } else if (requestCode == 1) {
            if (grantResults[0] == 0) {
                Toast.makeText(this, "외부저장소 읽기 권한이 승인되었습니다", Toast.LENGTH_SHORT).show();
                openPhotoGalleryIntent();
            } else {
                Toast.makeText(this, "외부저장소 읽기 권한이 거절되었습니다", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            if (grantResults[0] == 0) {
                Toast.makeText(this, "외부저장소 쓰기 권한이 승인되었습니다", Toast.LENGTH_SHORT).show();
                permissionCamera();
            } else {
                Toast.makeText(this, "외부저장소 쓰기 권한이 거절되었습니다", Toast.LENGTH_SHORT).show();
                permissionCamera();
            }
        }
    }


    //------------------------------------------------------------------------------------------------기능
    //카메라 열기
    private void sendTakePhotoIntent() {
        //카메라 열기
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        Uri profileIconFile = new FileProvider.getUriForFile(this,getPackageName(),photoFile)
////        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(profileIconFile));
//        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA);

        Intent opencvCameraIntent = new Intent(this, OpenCV_Camera.class);
//        startActivity(opencvCameraIntent);
        startActivityForResult(opencvCameraIntent, REQUEST_IMAGE_CAMERA);
    }

    private void openPhotoGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
    }

    //------------------------------------------------------------------------------------------------Request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK) {

            //OpenCV에서 가져온 이미지.
            Uri cameraImg = Uri.parse(data.getStringExtra("img")); //file 확장자가 붙어있는 Uri 가 나온다.

            //앞부분 file:// 을 제거한다
            String tempImgUri = String.valueOf(cameraImg);
            tempImgUri = tempImgUri.substring(7);

            imageUriList.add(tempImgUri);

            //주소의 첫번째 사진등록
            firstPhoto_imageView.setImageBitmap(imageManager.resizeBitmapImage(imageManager.DecodeBitmapFile(imageUriList.get(0)), 300));

            firstPhoto_imageView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "사진을 클릭해서 수정할수 있습니다.", Toast.LENGTH_SHORT).show();

        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            //갤러리에서 가져온 사진
            ClipData clipData = data.getClipData();

            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                imageUriList.add(getRealPathFromURI(uri));
            }
            firstPhoto_imageView.setImageBitmap(imageManager.resizeBitmapImage(imageManager.DecodeBitmapFile(imageUriList.get(0)), 300));
            firstPhoto_imageView.setVisibility(View.VISIBLE);

            if (imageUriList.size() != 1 && imageUriList.size() != 0) {
                photoNumTextView.setText("+" + (imageUriList.size() - 1));
                photoNumTextView.setVisibility(View.VISIBLE);
            }
            Toast.makeText(this, "사진을 클릭해서 수정할수 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //절대경로 가져오기
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
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
