package com.example.locationlog_02.Activitys.MyMapLog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationlog_02.Activitys.MyMapLog.Dialog.DialogAddMapMarker;
import com.example.locationlog_02.Activitys.MyMapLog.Dialog.DialogAddTitle;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.LocationContentsData;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.LocationContentsRecyclerAdapter;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.PlaceData;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.PlaceRecyclerAdapter;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.TitelListRecyclerAdapter;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.TitleItemTouchHelperCallback;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.TitleListData;
import com.example.locationlog_02.Activitys.MyMapTrace.MyMapTrace;
import com.example.locationlog_02.Activitys.QuickAddContents;
import com.example.locationlog_02.Activitys.Statistics.Setting;
import com.example.locationlog_02.PublicDialog.FinishAppDialog;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.DbHelper;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyMapLog extends AppCompatActivity implements TitelListRecyclerAdapter.RecyclerClickListener,
        PlaceRecyclerAdapter.RecyclerPlaceClickListener, LocationContentsRecyclerAdapter.RecyclerVlickListener,
        OnMapReadyCallback {
    // OnMapReadyCallback : 구글맵에 필요한 인터페이스를 구현한다 선언함

    //Request CODE
    private static final int REQUEST_ADDCONTENTS = 10;
    private static final int REQUEST_ADDCONTENTS_CORRECTION = 11;
    private static final String TAG = "MyMapLog";
    public static final String MARK = "【】";

    //view
    LinearLayout mapView;
    LinearLayout mapListView_category;
    LinearLayout mapListView_location;
    LinearLayout mapListView_contents;
    Button mapButton;
    Button mapListButton;
    TextView categoryText0, categoryText1;
    TextView placeText;
    EditText searchAddressEditText;
    //상태저장 변수
    String whatMapList = "map";

    //리사이클러뷰
    RecyclerView categoryRecyclerView;
    RecyclerView locationRecyclerView;
    RecyclerView contentsRecyclerView;
    TitelListRecyclerAdapter categoryAdapter;
    PlaceRecyclerAdapter locationAdapter;
    LocationContentsRecyclerAdapter contentsAdapter;
    public ArrayList<TitleListData> categoryRecyclerDataList;
    ArrayList<PlaceData> locationRecyclerDataList;
    ArrayList<LocationContentsData> contentsRecyclerDataList;

    //SQLite
    SQLiteDatabase db;
    DbHelper dbHelper;
    LocationLogDBHelper logDBHelper;

    //state
    public int presentCategoryNum = -1;
    public String presentCategoryName = null;
    public String presentPlaceName = null;

    //GoogleMap
    private GoogleMap mMap; //구글맵
    private FusedLocationProviderClient fusedLocationClient; //현위치
    private Marker preMarker; //롱클릭으로 지정하여 생성한 마커
    private LatLng markerLatLng; //롱클릭으로 클릭된 마커의 위치
    private ArrayList<MarkerOptions> markerOptionsList; //DB에서 마커옵션을 가져온다.
    private ClusterManager<clusterItem> mClusterManager;
    
    //지오코딩
    Geocoder geocoder; //지오코딩, 위/경도 <-> 주소
    List<Address> geocoderList = null;

    //보물찾기
    Thread mapCheckerThread;
    MapChecker mapChecker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map_log);

        getSupportActionBar().setTitle("Location Log");

        //view
        mapView = findViewById(R.id.myMap_map);
        mapListView_category = findViewById(R.id.myMap_mapList_category);
        mapListView_location = findViewById(R.id.myMap_mapList_locationList);
        mapListView_contents = findViewById(R.id.myMap_mapList_contents);
        mapButton = findViewById(R.id.myMap_mapButton);
        mapListButton = findViewById(R.id.myMap_mapListButton);
        categoryText0 = findViewById(R.id.maMap_categoryText_0);
        categoryText1 = findViewById(R.id.maMap_categoryText_1);
        placeText = findViewById(R.id.maMap_placeText_0);

        //화면 초기화.
        CanYouChangeItToThisScreen(this.whatMapList);
        WhichButtonIsActiviated("map");

        //SQL DB
        dbHelper = DbHelper.getInstance(this);
        db = dbHelper.getWritableDatabase();
        db.isOpen();
        logDBHelper = new LocationLogDBHelper(this);

        //리사이클러뷰
        categoryRecyclerView = findViewById(R.id.myMap_mpaList_category_recyclerview);
        locationRecyclerView = findViewById(R.id.myMap_mpaList_locationList_recyclerview);
        contentsRecyclerView = findViewById(R.id.myMap_mpaList_contentsList_recyclerview);

        setInitRecyclerData();
        setCategoryRecyclerView();
        setLocationRecyclerView();
        setContentsRecyclerView();

        //googleMap
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.myMap_log_googleMap);
        mapFragment.getMapAsync(this);


        ////현위치
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        //MapChecker 보물찾기
        mapChecker = MapChecker.getInstance();
        mapCheckerThread = MapChecker.getThreadInstance();
        mapChecker.setInit(this);

        if (MapChecker.isIsRunThread() == false)
            mapCheckerThread.start();


        //QuitckAddContents.class 에서 추가한 것을 바로보여주기위해 처리함.
        Intent momIntent = getIntent();
        String quickCategory =momIntent.getStringExtra("quickCategory");
        String quickPlace =momIntent.getStringExtra("quickPlace");

        if(quickCategory!=null && quickPlace!=null){
            //좌측 세로 카테고리 타이틀 변경
            String tempText = quickCategory;
            String tempSubString = "";
            for (int i = 0; i < tempText.length(); i++) {
                tempSubString += tempText.substring(i, i + 1);
            }
            categoryText0.setText(tempSubString);
            categoryText1.setText(tempSubString);

            //선택된 카테고리의 번호.
//            this.presentCategoryNum = position; //없으면 어떻게되지??
            presentCategoryName = tempText;

            //locationRecyclerDataList 변경
            if (locationRecyclerDataList != null) {
                logDBHelper.updatePlaceAll(locationRecyclerDataList);
            }
            locationRecyclerDataList = logDBHelper.readPlaceList(tempText);
            locationAdapter.setItems(locationRecyclerDataList);

            //카테고리 Place 데이터
            String tempText1 = quickPlace;
            String tempSubString1 = "";

            for (int i = 0; i < tempText1.length(); i++) {
                tempSubString1 += tempText1.substring(i, i + 1);
            }
            placeText.setText(tempSubString1);

            //선택된 장소 이름
            presentPlaceName = tempText1;

            if (contentsRecyclerDataList != null)
                logDBHelper.updateContentsAll(contentsRecyclerDataList); //이전데이터 업데이트

            //새로운 데이터 불러오기
            contentsRecyclerDataList = logDBHelper.readPlaceContentsList(presentCategoryName, presentPlaceName);
            contentsAdapter.setItems(contentsRecyclerDataList);

            //UI 처리
            WhichButtonIsActiviated("mapList");
            CanYouChangeItToThisScreen("contents");
        }

        //지오코딩
        geocoderInit();
    }

    //------------------------------------------------------------------------------------------------생명주기


    @Override
    protected void onStop() {
        super.onStop();
        logDBHelper.updateCategoryAll(categoryRecyclerDataList); //위치교환 때문에, 다시 저장한다

        if (locationRecyclerDataList != null)
            logDBHelper.updatePlaceAll(locationRecyclerDataList);
        if (contentsRecyclerDataList != null)
            logDBHelper.updateContentsAll(contentsRecyclerDataList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapCheckerThread.interrupt();
    }

    //------------------------------------------------------------------------------------------------초기화
    private void geocoderInit() {
        geocoder = new Geocoder(this);
        searchAddressEditText = findViewById(R.id.myMap_searchMapAddress_editText);
    }

    //------------------------------------------------------------------------------------------------GoogleMap


    //OnMapReadyCallback 인터페이스의 onMapReady 메소드를 구현해줘야 합니다.
    //맵이 사용할 준비가 되었을 때(NULL이 아닌 GoogleMap 객체를 파라미터로 제공해 줄 수 있을 때)  호출되어지는 메소드입니다.

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        //클러스터
        mClusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        //퍼미션
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
        mMap.setMyLocationEnabled(true); //내위치 보이도록 활성화
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //내위치 버튼 활성화
//        mMap.setBuildingsEnabled(true); //빌딩높이 볼수있는 터치기능 활성화화
        mMap.getUiSettings().setZoomControlsEnabled(true); //줌 버튼 활성화
        mMap.setOnMapLongClickListener(mapLongClickListener); // 지도 롱클릭 리스너 지정
        mMap.setOnInfoWindowClickListener(infoWindowClickListener); //마커 정보창 클릭 리스너
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));  //moveCamera 메소드를 사용하여 카메라를 지정한 경도, 위도로 이동합니다.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10)); //CameraUpdateFactory.zoomTo 메소드를 사용하여 지정한 단계로 카메라 줌을 조정합니다.  1 단계로 지정하면 세계지도 수준으로 보이고 숫자가 커질수록 상세지도가 보입니다.

        //현위치
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
            }
        });

        //마커 업데이트
        mapMarkerReset();

        //현위치
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng thisPos = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(thisPos));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            }
        });
    }

    //마커 초기화.
    public void mapMarkerReset() { //수정 삭제에 필요하다.
//        mMap.clear();
        markerOptionsList = logDBHelper.readPlaceAllDataMarkerList(); // 마커목록 가져오기.
        mClusterManager.clearItems(); //초기화

        //지도에 Place Marker 표시하기.
        for (int i = 0; i < markerOptionsList.size(); i++) {
            //mMap.addMarker(markerOptionsList.get(i)); //맵에 마커 추가 //클러스터랑 중복되서 추가가된다.

            //클러스터에도 추가
            clusterItem clusterItem = new clusterItem(markerOptionsList.get(i).getPosition().latitude, markerOptionsList.get(i).getPosition().longitude, markerOptionsList.get(i).getTitle(), markerOptionsList.get(i).getSnippet());
            mClusterManager.addItem(clusterItem);
        }
        mClusterManager.cluster();
    }


    GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            //귀여워서
//                Toast.makeText(MyMapLog.this, "ᕕ( ᐛ )ᕗ", Toast.LENGTH_SHORT).show();
            //클릭한 위치의 주소를 토스트
            Toast.makeText(MyMapLog.this, getCurrentAddress(latLng), Toast.LENGTH_SHORT).show();

            //클릭시 생성된 마커 지우기.
            if (preMarker != null)
                preMarker.remove();

            // 롱클릭한 위치의 마커 등록하기
            MarkerOptions marker = new MarkerOptions();
            marker.snippet("" + latLng.latitude + "," + latLng.longitude);
            marker.title("");
            marker.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            preMarker = mMap.addMarker(marker);

            //롱클릭한 위치로 맵 포커스 이동
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            //이 위치 기역하기.
            markerLatLng = latLng;

        }
    };
    //지도 정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            if (marker.getTitle() != null && marker.getSnippet() != null) {

                //카테고리쪽 데이터 준비

                //좌측 세로 카테고리 타이틀 변경
                String tempText = marker.getTitle();
                String tempSubString = "";
                for (int i = 0; i < tempText.length(); i++) {
                    tempSubString += tempText.substring(i, i + 1);
                }
                categoryText0.setText(tempSubString);
                categoryText1.setText(tempSubString);

                //선택된 카테고리의 번호.
//            this.presentCategoryNum = position; //없으면 어떻게되지??
                presentCategoryName = tempText;

                //locationRecyclerDataList 변경
                if (locationRecyclerDataList != null) {
                    logDBHelper.updatePlaceAll(locationRecyclerDataList);
                }
                locationRecyclerDataList = logDBHelper.readPlaceList(tempText);
                locationAdapter.setItems(locationRecyclerDataList);

                //카테고리 Place 데이터
                String tempText1 = marker.getSnippet();
                String tempSubString1 = "";

                for (int i = 0; i < tempText1.length(); i++) {
                    tempSubString1 += tempText1.substring(i, i + 1);
                }
                placeText.setText(tempSubString1);

                //선택된 장소 이름
                presentPlaceName = tempText1;

                if (contentsRecyclerDataList != null)
                    logDBHelper.updateContentsAll(contentsRecyclerDataList); //이전데이터 업데이트

                //새로운 데이터 불러오기
                contentsRecyclerDataList = logDBHelper.readPlaceContentsList(presentCategoryName, presentPlaceName);
                contentsAdapter.setItems(contentsRecyclerDataList);

                //UI 처리
                WhichButtonIsActiviated("mapList");
                CanYouChangeItToThisScreen("contents");
            }
        }
    };

    //------------------------------------------------------------------------------------------------리사이클러뷰
    private void setInitRecyclerData() {
        categoryRecyclerDataList = logDBHelper.readCategoryList();
    }

    private void setCategoryRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryAdapter = new TitelListRecyclerAdapter(this, categoryRecyclerDataList);
        categoryAdapter.setOnClickListener(this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TitleItemTouchHelperCallback(categoryAdapter));
        itemTouchHelper.attachToRecyclerView(categoryRecyclerView);
    }

    private void setLocationRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        locationRecyclerView.setLayoutManager(layoutManager);
        locationAdapter = new PlaceRecyclerAdapter(this, locationRecyclerDataList);
        locationAdapter.setOnClickListener(this);
        locationRecyclerView.setAdapter(locationAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TitleItemTouchHelperCallback(locationAdapter));
        itemTouchHelper.attachToRecyclerView(locationRecyclerView);
    }

    private void setContentsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        contentsRecyclerView.setLayoutManager(layoutManager);
        contentsAdapter = new LocationContentsRecyclerAdapter(this, this);
        contentsAdapter.setItems(contentsRecyclerDataList);
        contentsRecyclerView.setAdapter(contentsAdapter);
    }

    //------------------------------------------------------------------------------------------------목    록

    //------------------------------------------------------------------------------------------------화면이동
    public void changeActivitySetting(View view) {
        Intent changeIntent = new Intent(this, Setting.class);
//        changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(changeIntent);
        finish();
    }

    public void changeActivityMyMapTrace(View view) {
        Intent changeIntent = new Intent(this, MyMapTrace.class);
//        changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(changeIntent);
        finish();
    }

    public void changeActivityCamera(View view) {
        Intent changeIntent = new Intent(this, QuickAddContents.class);
//        changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(changeIntent);
        finish();
    }

    //------------------------------------------------------------------------------------------------버튼클릭

    //상단 버튼
    public void showMap(View view) {
        CanYouChangeItToThisScreen("map");
        WhichButtonIsActiviated("map");
        mapMarkerReset(); //마커 초기화
    }

    public void showMapList(View view) {

        WhichButtonIsActiviated("mapList");
        if (this.contentsRecyclerDataList != null) {
            CanYouChangeItToThisScreen("contents");
        } else if (this.locationRecyclerDataList != null) {
            CanYouChangeItToThisScreen("location");
        } else {
            CanYouChangeItToThisScreen("category");
        }
    }

    //Map-Category 추가버튼
    public void addMapCategoryAdd(View view) {
        DialogAddTitle titleDialog = new DialogAddTitle(this, categoryAdapter);
        titleDialog.callFunction("", true, 0);
    }

    //Map-location 장소추가 버튼
    public void addLocationButtuon(View view) {
        //장소추가 지도화면으로 이동시킨다.
        CanYouChangeItToThisScreen("map");
    }

    //Map-Contents 추가버튼
    public void addContentsActivity(View view) {
        Intent changeIntent = new Intent(this, AddContents.class);
        changeIntent.putExtra("isAdd", true);
        startActivityForResult(changeIntent, REQUEST_ADDCONTENTS);
    }

    //Map-Contents 맵이동 버튼
    public void moveMapthisPlacePoint(View view) {
        CanYouChangeItToThisScreen("map");
        LatLng showLat =logDBHelper.readPosition(this.presentCategoryName,this.presentPlaceName);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(showLat));
    }

    //Map-하단 Marker 추가버튼
    public void addMapMarkerDialog(View view) {
        if (markerLatLng != null) {
            DialogAddMapMarker markerDialog = new DialogAddMapMarker(this, locationAdapter);
            markerDialog.setInit(getCurrentAddress(markerLatLng), markerLatLng);
            markerDialog.callFunction(categoryRecyclerDataList, presentCategoryNum);
        } else {
            Toast.makeText(this, "지도위에서 저장하고 싶은 위치를 롱클릭해서 마커를 표시해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    //mapList 사이드 분류이동 버튼들
    public void changeScreenCategory(View view) {
        CanYouChangeItToThisScreen("category");
        if (locationRecyclerDataList != null) {
            logDBHelper.updatePlaceAll(locationRecyclerDataList);
        }
    }

    public void changeScreenLocation(View view) {
        CanYouChangeItToThisScreen("location");
    }

    //주소찾기 버튼
    public void findAddressButton(View view) {

        if(searchAddressEditText.getText().toString().equals("")==false){
            //검색어가 있다면.
            String searchWord = searchAddressEditText.getText().toString();

            try {
                geocoderList = geocoder.getFromLocationName(searchWord,10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(geocoderList!=null){
                if(geocoderList.size()==0){
                    Toast.makeText(this, "주소정보가 없습니다. 다른주소를 검색해주세요", Toast.LENGTH_SHORT).show();
                }
                else { //검색결과가 있다.

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchAddressEditText.getWindowToken(), 0);

                    //검색위치
                    LatLng latLng = new LatLng(geocoderList.get(0).getLatitude(),geocoderList.get(0).getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)); // 위치로 이동한다.
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------기능구현
    private void CanYouChangeItToThisScreen(String screen) {
        //메서드에 원하는 화면을 넣으면 그것을 제외하고 나머지는 gone시킨다.
        // 1.map 2.category 3.location 4.contents
        if (screen.equals("map")) {
            mapView.setVisibility(View.VISIBLE);
            mapListView_category.setVisibility(View.GONE);
            mapListView_location.setVisibility(View.GONE);
            mapListView_contents.setVisibility(View.GONE);
            this.whatMapList = "map";
        } else if (screen.equals("category")) {
            mapView.setVisibility(View.GONE);
            mapListView_category.setVisibility(View.VISIBLE);
            mapListView_location.setVisibility(View.GONE);
            mapListView_contents.setVisibility(View.GONE);
            if (locationRecyclerDataList != null) {
                logDBHelper.updatePlaceAll(locationRecyclerDataList);
            }
            this.locationRecyclerDataList = null;
            this.whatMapList = "category";
        } else if (screen.equals("location")) {
            mapView.setVisibility(View.GONE);
            mapListView_category.setVisibility(View.GONE);
            mapListView_location.setVisibility(View.VISIBLE);
            mapListView_contents.setVisibility(View.GONE);
            if (contentsRecyclerDataList != null)
                logDBHelper.updateContentsAll(contentsRecyclerDataList); //이전데이터 업데이트
            this.contentsRecyclerDataList = null;
            this.whatMapList = "location";
        } else if (screen.equals("contents")) {
            mapView.setVisibility(View.GONE);
            mapListView_category.setVisibility(View.GONE);
            mapListView_location.setVisibility(View.GONE);
            mapListView_contents.setVisibility(View.VISIBLE);

            this.whatMapList = "contents";
        }
    }

    private void WhichButtonIsActiviated(String buttonName) {
        //상단 [목록][지도] 버튼 색깔 바꾸기
        if (buttonName.equals("map")) {
            mapButton.setBackgroundResource(R.drawable.button_tetragon_select);
            mapButton.setTextColor(Color.BLACK);
            mapListButton.setBackgroundResource(R.drawable.button_tetragon);
            mapListButton.setTextColor(Color.WHITE);
        } else if (buttonName.equals("mapList")) {
            mapButton.setBackgroundResource(R.drawable.button_tetragon);
            mapButton.setTextColor(Color.WHITE);
            mapListButton.setBackgroundResource(R.drawable.button_tetragon_select);
            mapListButton.setTextColor(Color.BLACK);
        }
    }

    public String getCurrentAddress(LatLng latlng) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    //다이어로그에서 추가가 완료되면 실행되는 메서드
    public void completearker(String category, String title, double latitude, double longtitude) {
//        MarkerOptions tempMarkerOption = new MarkerOptions();
//        tempMarkerOption.title(category).snippet(title).position(new LatLng(latitude, longtitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        //mMap.addMarker(tempMarkerOption).showInfoWindow(); //마커를 맵에 추가시키고 말머리를 항상 띄어둔다.

        //클러스터 추가
        clusterItem clusterItem = new clusterItem(latitude,longtitude,category,title);
        mClusterManager.addItem(clusterItem);
        mClusterManager.cluster();

        if (preMarker != null)
            preMarker.remove(); // 지도에 표시된 프리마커를 지운다.

        //리사이클러뷰

    }
    //---------------------------------------------------------------------------------------------- 리사이클러뷰 클릭 이벤트

    // Category Title 클릭 이벤트
    @Override
    public void onItemClicked(int position) {
        CanYouChangeItToThisScreen("location");

        //좌측 세로 카테고리 타이틀 변경
        String tempText = categoryRecyclerDataList.get(position).getTitle();
        String tempSubString = "";
        for (int i = 0; i < tempText.length(); i++) {
            tempSubString += tempText.substring(i, i + 1);
        }
        categoryText0.setText(tempSubString);
        categoryText1.setText(tempSubString);

        //선택된 카테고리의 번호.
        this.presentCategoryNum = position;
        presentCategoryName = tempText;

        //locationRecyclerDataList 변경
        if (locationRecyclerDataList != null) {
            logDBHelper.updatePlaceAll(locationRecyclerDataList);
        }
        locationRecyclerDataList = logDBHelper.readPlaceList(tempText);
        locationAdapter.setItems(locationRecyclerDataList);
    }

    //Place 클릭 이벤트
    @Override
    public void onPlaceItemClicked(int position) {
        CanYouChangeItToThisScreen("contents");
        String tempText = locationRecyclerDataList.get(position).getTitle();
        String tempSubString = "";

        for (int i = 0; i < tempText.length(); i++) {
            tempSubString += tempText.substring(i, i + 1);
        }
        placeText.setText(tempSubString);

        //선택된 장소 이름
        presentPlaceName = tempText;

        if (contentsRecyclerDataList != null)
            logDBHelper.updateContentsAll(contentsRecyclerDataList); //이전데이터 업데이트

        //새로운 데이터 불러오기
        contentsRecyclerDataList = logDBHelper.readPlaceContentsList(presentCategoryName, presentPlaceName);
        contentsAdapter.setItems(contentsRecyclerDataList);
    }

    //contents 클릭(자세히보기)
    @Override
    public void onContentsItemClicked(int pos) {
        Log.e("MyMapLog,nContentsItemClicked", "" + pos);
        Intent changeIntent = new Intent(this, AddContents.class);
        changeIntent.putExtra("itemData", contentsRecyclerDataList.get(pos));
        changeIntent.putExtra("isDetail", true);
        startActivityForResult(changeIntent, REQUEST_ADDCONTENTS);
    }

    //contents 수정
    @Override
    public void onContentsCorrectionClicked(int pos) {
        Intent changeIntent = new Intent(this, AddContents.class);
        changeIntent.putExtra("itemData", contentsRecyclerDataList.get(pos));
        changeIntent.putExtra("isCorrection", true);
        changeIntent.putExtra("thisPos", pos);
        startActivityForResult(changeIntent, REQUEST_ADDCONTENTS_CORRECTION);
    }

    //------------------------------------------------------------------------------------------------ ActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDCONTENTS && resultCode == RESULT_OK) { //추가
            LocationContentsData itemData = (LocationContentsData) data.getSerializableExtra("item");
            contentsAdapter.addNewItem(itemData);


            contentsRecyclerView.scrollToPosition(0);

            String totalImage = "";

            for (int i = 0; i < itemData.getContentsPhoto_uri().size(); i++) {
                totalImage += itemData.getContentsPhoto_uri().get(i) + MyMapLog.MARK;
            }

            //DB 데이터 추가하기.
            logDBHelper.savePlaceContents(presentCategoryName, presentPlaceName, itemData.getDay(), itemData.getContentsText(), totalImage);

        } else if (requestCode == REQUEST_ADDCONTENTS_CORRECTION && resultCode == RESULT_OK) {  //수정하기
            LocationContentsData itemData = (LocationContentsData) data.getSerializableExtra("item"); //수정된 데이터
            int pos = data.getIntExtra("thisPos", 0); //수정할 위치
            itemData.setId(contentsRecyclerDataList.get(pos).getId()); //id를 가져온다.
            contentsRecyclerDataList.remove(pos); //수정전 데이터를 지운다
            contentsRecyclerDataList.add(pos, itemData); //수정된 데이터를 삽입해준다

            //위치정렬
            contentsAdapter.dateFrstCheck();
//            contentsAdapter.dateSortingList();

            //적용
            contentsAdapter.notifyDataSetChanged();
            contentsRecyclerView.scrollToPosition(0);

            //DB 수정.
            ////Uri 묶기
            String totalImage = "";
            for (int i = 0; i < itemData.getContentsPhoto_uri().size(); i++) {
                totalImage += itemData.getContentsPhoto_uri().get(i) + MyMapLog.MARK;
            }
            ////DB 업데이트
            logDBHelper.updateContents(itemData.getId(), itemData.getDay(), itemData.getContentsText(), totalImage);
        }
    }

    //------------------------------------------------------------------------------------------------ BackButton


    @Override
    public void onBackPressed() {
        //이전버튼 누를때.
        //현위치를 확인한다.
        if (whatMapList.equals("map")) {
            FinishAppDialog dialog = new FinishAppDialog(this, this);
            dialog.callFunction();
        } else if (whatMapList.equals("category")) {
            FinishAppDialog dialog = new FinishAppDialog(this, this);
            dialog.callFunction();
        } else if (whatMapList.equals("location")) {
            CanYouChangeItToThisScreen("category");
        } else if (whatMapList.equals("contents")) {
            CanYouChangeItToThisScreen("location");
        }
    }
}
