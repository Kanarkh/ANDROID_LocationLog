package com.example.locationlog_02.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.LocationContentsData;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.PlaceData;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.TitleListData;
import com.example.locationlog_02.Activitys.MyMapTrace.Recyclerview.TraceLogData;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LocationLogDBHelper {
    private static final String TAG = "LocationLogDBHelper";
    private Context context;
    private SQLiteDatabase db;
    private DbHelper dbHelper;

    public LocationLogDBHelper(Context context) {
        this.context = context;
        dbHelper = DbHelper.getInstance(this.context);
        writableDb();
        dbHelper.onCreate(db);

        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            saveCategory("무제");
        }
    }

    private void writableDb() {
        db = dbHelper.getWritableDatabase();
    }

    private void readableDb() {
        db = dbHelper.getReadableDatabase();
    }


    public void saveCategory(String category) {
        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY, category);
        db.insert(DataContract.LocationlogEntry.TABLE_NAME, null, contentValues);
    }

    public void savePlace(String category, String place, double latitude, double longitude) {

        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY, category);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_PLACE, place);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE, latitude);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE, longitude);
        db.insert(DataContract.LocationlogEntry.TABLE_NAME, null, contentValues);
    }

    public void savePlaceContents(String category, String place, String date, String writing, String uri) {

        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY, category);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_PLACE, place);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_DATE, date);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_WRITING, writing);

        if (uri != null)
            if (uri.equals("") == false) //사진이 없는경우 데이터를 저장하지 않게 하기위해 실행
                contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_URI, uri);
        //저장 실행
        db.insert(DataContract.LocationlogEntry.TABLE_NAME, null, contentValues);
    }

    //category 데이터가 있는것중 PLACE가 NULL인 데이터를 찾는다./ 그런 데이터가 없다면 -1을 리턴한다.
    private long findEmtyLocation(String categoryName) {
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)).equals(categoryName) == true) { //입력된 카테고리를 찾는다.
                if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) == null) { //비어있다면, 카테고리만 저장되있다면.
                    return cursor.getInt(cursor.getColumnIndex(DataContract.LocationlogEntry._ID)); //비어있다면 해당 커서 Id를 리턴해준다.
                }
            }
        }
        return -1;
    }

    public void saveData(String category, String place, double latitude, double longitude, String Date, String writing, String uri) {
        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY, category);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_PLACE, place);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE, latitude);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE, longitude);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_DATE, Date);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_WRITING, writing);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_URI, uri);
        db.insert(DataContract.LocationlogEntry.TABLE_NAME, null, contentValues);
    }

    //위치가 바뀐것들을 전부 저장한다.
    public void updateCategoryAll(ArrayList<TitleListData> list) {

        for (int i = 0; i < list.size(); i++) {
            String title = list.get(i).getTitle();
            long id = list.get(i).getId();

            if (updateCategory(id, title) == false) {
                Log.e(TAG, "updateCategoryAll 업데이트 실패"+",id:"+id);
            }
        }
    }

    public boolean updateCategory(long id, String title) {
        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY, title);
        return db.update(DataContract.LocationlogEntry.TABLE_NAME, contentValues, DataContract.LocationlogEntry._ID + "=" + id, null) > 0;
    }

    //위치가 바뀐것들을 전부 저장한다.
    public void updatePlaceAll(ArrayList<PlaceData> list) {

        for (int i = 0; i < list.size(); i++) {
            String title = list.get(i).getTitle();
            long id = list.get(i).getId();

            if (updatePlace(id, title) == false) {
                Log.e(TAG, "updatePlaceAll 업데이트 실패"+",id:"+id);
            } else {
//                Log.e(TAG,"업데이트 성공");
            }
        }
    }

    //플레이스 데이터가 수정되었을때
    public void updateCategoryChangeInData(String fromCategory, String toCategory) { //해당 메소드를 부르는곳이 수정하기 부분이기때문에 장소 이름도 바뀔수 있다.
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();

        boolean terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null; //카테고리가 널이 아니고
        //카테고리가 널이 아닌것들중
        if (terms0) {
            String category = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)); //카테고리 이름을 가져온다
            if (category.equals(fromCategory)) { //카테고리의 이름을 비교한다
                long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));  // 아이디를 읽어온다
                updateCategory(id,toCategory);//새로 업데이트 해준다
            }
        }

        while (cursor.moveToPrevious()) {
            terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null;
            if (terms0 ) {

                String category = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
                if (category.equals(fromCategory) ) {
                    long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));  // 아이디를 읽어온다
                    updateCategory(id,toCategory);//새로 업데이트 해준다
                }

            }
        }
    }


    //플레이스 데이터가 수정되었을때
    public void updatePlaceCategory(String fromCategory, String toCategory, String fromPlace, String toPlace) { //해당 메소드를 부르는곳이 수정하기 부분이기때문에 장소 이름도 바뀔수 있다.
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();
        ArrayList<Long> updateList = new ArrayList<>();

        boolean terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null; //카테고리가 널이 아니고
        boolean terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null; //플레이스가 널이 아닐때
        //카테고리와 플레이스가 둘다 있는 데이터 /
        if (terms0 && terms1) {

            String category = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)); //카테고리 이름을 가져온다
            String place = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)); // 플레이스 이름을 가져온다
            if (category.equals(fromCategory) && place.equals(fromPlace)) { //카테고리 이름과 플레이스 이름이 바뀌기 전 데이터와 동일한지 확인한다.
                long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));  // 아이디를 읽어온다
                updatePlace(id, toCategory, toPlace);  //새로 업데이트 해준다
//                updateList.add(id);
            }
        }

        while (cursor.moveToPrevious()) {
            terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null;
            terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null;
            if (terms0 && terms1) {

                String category = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
                String place = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE));
                if (category.equals(fromCategory) && place.equals(fromPlace)) {
                    long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
                    updatePlace(id, toCategory, toPlace);
//                    updateList.add(id);
                }

            }
        }
    }


    public boolean updatePlace(long id, String place) {
        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_PLACE, place);
        return db.update(DataContract.LocationlogEntry.TABLE_NAME, contentValues, DataContract.LocationlogEntry._ID + "=" + id, null) > 0;
    }

    public boolean updatePlace(long id, String category, String place) {
        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY, category);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_PLACE, place);
        return db.update(DataContract.LocationlogEntry.TABLE_NAME, contentValues, DataContract.LocationlogEntry._ID + "=" + id, null) > 0;
    }

    public boolean updatePlace(long id, String category, String place, double latitude, double longitude) {
        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY, category);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_PLACE, place);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE, latitude);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE, longitude);
        return db.update(DataContract.LocationlogEntry.TABLE_NAME, contentValues, DataContract.LocationlogEntry._ID + "=" + id, null) > 0;
    }

    public boolean updateContents(long id, String date, String writing, String uriBunch) {
        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_DATE, date);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_WRITING, writing);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_URI, uriBunch);
        return db.update(DataContract.LocationlogEntry.TABLE_NAME, contentValues, DataContract.LocationlogEntry._ID + "=" + id, null) > 0;
    }

    public void updateContentsAll(ArrayList<LocationContentsData> list) {


        for (int i = 0; i < list.size(); i++) {
            String date = list.get(i).getDay();
            String writing = list.get(i).getContentsText();
            long id = list.get(i).getId();

            String uriBunch = "";
            for (int j = 0; j < list.get(i).getContentsPhoto_uri().size(); j++) {
                uriBunch += list.get(i).getContentsPhoto_uri().get(j) + MyMapLog.MARK;
            }


            if (updateContents(id, date, writing, uriBunch) == false) {
                Log.e(TAG, "updateContentsAll 업데이트 실패"+",id:"+id);
            } else {
//                Log.e(TAG,"업데이트 성공");
            }
        }
    }

    public void readAllDbData(ArrayList<TitleListData> categoryRecyclerDataList) {
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);

        //뒷번호부터 읽어온다.
        cursor.moveToLast();
        //뒷번호가 리스트에 추가안된다.
        long id = cursor.getInt(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
        String title = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
        categoryRecyclerDataList.add(new TitleListData(title, id));
        Log.e("helper", "id : " + id + ",title : " + title);

        while (cursor.moveToPrevious()) {
            id = cursor.getInt(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
            title = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
            categoryRecyclerDataList.add(new TitleListData(title, id));
            Log.e("helper", "id : " + id + ",title : " + title);
        }
    }

    public ArrayList<TitleListData> readCategoryList() {
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();
        ArrayList<TitleListData> titleList = new ArrayList<>();

        boolean terms0 = cursor.getString(1) != null;
        boolean terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) == null;
        if (terms0 && terms1) {
            long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
            String category = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
            titleList.add(new TitleListData(category, id));
        }

        while (cursor.moveToPrevious()) {
            terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null;
            terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) == null;
            if (terms0 && terms1) {
                long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
                String category = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
                titleList.add(new TitleListData(category, id));
            }
        }
        return titleList;
    }

    public ArrayList<PlaceData> readPlaceList(String category) {
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();
        ArrayList<PlaceData> titleList = new ArrayList<>();

        boolean terms0 = false;

        if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null)
            terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)).equals(category) == true; // 카테고리와 이름이 같은지
        boolean terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null; //Place에 값이 있는지
        boolean terms2 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE)) == null;  // Date 가 Null인지 (Date는 place에 기록되는 데이터에만 저장된다.

        if (terms0 && terms1 && terms2) {
            long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
            String cursorCategory = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
            String cursorPlace = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE));
            double cursorLatitude = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE));
            double cursorLongitude = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE));
            titleList.add(new PlaceData(cursorCategory, cursorPlace, cursorLatitude, cursorLongitude, id));
        }

        while (cursor.moveToPrevious()) {
            if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null)
                terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)).equals(category) == true; // 카테고리와 이름이 같은지
            terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null; //Place에 값이 있는지
            terms2 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE)) == null;  // Date 가 Null인지 (Date는 place에 기록되는 데이터에만 저장된다.

            if (terms0 && terms1 && terms2) {
                long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
                String cursorCategory = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
                String cursorPlace = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE));
                double cursorLatitude = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE));
                double cursorLongitude = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE));
                titleList.add(new PlaceData(cursorCategory, cursorPlace, cursorLatitude, cursorLongitude, id));
            }
        }
        return titleList;
    }

    public ArrayList<LocationContentsData> readPlaceContentsList(String category, String place) {
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();
        ArrayList<LocationContentsData> titleList = new ArrayList<>();

        boolean terms0;
        boolean terms1;
        if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null)
            terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)).equals(category) == true; // 카테고리와 이름이 같은지
        else terms0 = false;

        if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null)
            terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)).equals(place) == true; //Place에 값이 있는지
        else terms1 = false;
        boolean terms2 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE)) != null;  // Date 에 날짜가 있는지

        if (terms0 && terms1 && terms2) {
            long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
//            String cursorCategory = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
//            String cursorPlace = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE));
            String cursorDate = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE));
            String cursorWriting = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_WRITING));
            String cursorUri = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_URI));

            //저장된 Uri 덩어리 split 하기
            ArrayList<String> uriList = new ArrayList<>();

            if (cursorUri != null) {
                String[] array = cursorUri.split(MyMapLog.MARK);
                for (int i = 0; i < array.length; i++) {
                    uriList.add(array[i]);
                }
            }
            titleList.add(new LocationContentsData(cursorDate, cursorWriting, uriList, id));
        }

        while (cursor.moveToPrevious()) {
            if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null)
                terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)).equals(category) == true; // 카테고리와 이름이 같은지
            else terms0 = false;
            if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null)
                terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)).equals(place) == true; //Place에 값이 있는지
            else terms1 = false;
            terms2 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE)) != null;  // Date 에 날짜가 있는지

            if (terms0 && terms1 && terms2) {
                long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
//            String cursorCategory = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
//            String cursorPlace = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE));
                String cursorDate = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE));
                String cursorWriting = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_WRITING));
                String cursorUri = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_URI));

                //저장된 Uri 덩어리 split 하기
                ArrayList<String> uriList = new ArrayList<>();

                if (cursorUri != null) {
                    String[] array = cursorUri.split(MyMapLog.MARK);
                    for (int i = 0; i < array.length; i++) {
                        uriList.add(array[i]);
                    }
                }
                titleList.add(new LocationContentsData(cursorDate, cursorWriting, uriList, id));
            }
        }
        return titleList;
    }
    public LatLng readPosition(String category, String place) {
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();

        boolean terms0;
        boolean terms1;
        if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null)
            terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)).equals(category) == true; // 카테고리와 이름이 같은지
        else terms0 = false;

        if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null)
            terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)).equals(place) == true; //Place에 값이 있는지
        else terms1 = false;
        boolean terms2 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE)) == null;  // Date 에 날짜가 없어야한다.

        if (terms0 && terms1 && terms2) {
//            long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
            double lat = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE));
            double lon = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE));
            LatLng tempLat = new LatLng(lat,lon);
            return tempLat;
        }

        while (cursor.moveToPrevious()) {

            if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)) != null)
                terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY)).equals(category) == true; // 카테고리와 이름이 같은지
            else terms0 = false;

            if (cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null)
                terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)).equals(place) == true; //Place에 값이 있는지
            else terms1 = false;
             terms2 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE)) == null;  // Date 에 날짜가 없어야한다.

            if (terms0 && terms1 && terms2) {
//            long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
                double lat = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE));
                double lon = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE));
                LatLng tempLat = new LatLng(lat,lon);
                return tempLat;
            }
        }
        return null;
    }
    public ArrayList<MarkerOptions> readPlaceAllDataMarkerList() {
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();
        ArrayList<MarkerOptions> markerList = new ArrayList<>();

        boolean terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null; //Place에 값이 있는지
        boolean terms2 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE)) == null;  // Date 가 Null인지 (Date는 place에 기록되는 데이터에만 저장된다.

        if (terms1 && terms2) {
            //DB에서 읽어오기
            //long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
            String cursorCategory = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
            String cursorPlace = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE));
            double cursorLatitude = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE));
            double cursorLongitude = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE));

            //마커옵션 생성
            MarkerOptions tempMarker = new MarkerOptions();
            tempMarker.position(new LatLng(cursorLatitude,cursorLongitude));
            tempMarker.title(cursorCategory);
            tempMarker.snippet(cursorPlace).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            markerList.add(tempMarker);
        }

        while (cursor.moveToPrevious()) {

            terms1 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE)) != null; //Place에 값이 있는지
            terms2 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_DATE)) == null;  // Date 가 Null인지 (Date는 place에 기록되는 데이터에만 저장된다.

            if (terms1 && terms2) {
                //long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
                String cursorCategory = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY));
                String cursorPlace = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_PLACE));
                double cursorLatitude = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE));
                double cursorLongitude = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE));

                //마커옵션 생성
                MarkerOptions tempMarker = new MarkerOptions();
                tempMarker.position(new LatLng(cursorLatitude,cursorLongitude));
                tempMarker.title(cursorCategory);
                tempMarker.snippet(cursorPlace).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                markerList.add(tempMarker);
            }
        }
        return markerList;
    }

    public int readLastId() {
        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();
        return cursor.getInt(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
    }

    public boolean delete(long id) {
        writableDb();
        return db.delete(DataContract.LocationlogEntry.TABLE_NAME, "_id = " + id, null) > 0;
    }

    public void close() {
        db.close();
    }

    //------------------------------------------------------------------------------------------------ Location Trace
    public void saveTraceLog(String title, String startDate, String endDate, double distance, String img, String data, String places) {

        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_TRACETITLE, title);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_TRACESTARTDATE, startDate);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_TRACEENDDATE, endDate);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_TRACEDISTANCE, distance);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_TRACEIMG, img);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_TRACEDATA, data);
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_TRACEPLACE, places);
        db.insert(DataContract.LocationlogEntry.TABLE_NAME, null, contentValues);
    }

    public boolean updateTrace(long id, String title) {
        // 트레이스 데이터는 이름과 이미지만 수정이 가능하다.
        writableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.LocationlogEntry.COLUMN_NAME_TRACETITLE, title);
        return db.update(DataContract.LocationlogEntry.TABLE_NAME, contentValues, DataContract.LocationlogEntry._ID + "=" + id, null) > 0;
    }

    public ArrayList<TraceLogData> readTraceList() {
        //트레이스 기록을 읽어온다. 넓 값이 있으면 안된다.

        readableDb();
        Cursor cursor = db.query(DataContract.LocationlogEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();
        ArrayList<TraceLogData> traceList = new ArrayList<>();

        boolean terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACETITLE)) != null;
        if (terms0) {
            long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
            String title = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACETITLE));
            String startDate = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACESTARTDATE));
            String endDate = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEENDDATE));
            double distance = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEDISTANCE));
            String img = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEIMG));
            String data = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEDATA));
            String place = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEPLACE));
            traceList.add(new TraceLogData(title, startDate, endDate, distance, img, data, place, id));
        }

        while (cursor.moveToPrevious()) {
            terms0 = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACETITLE)) != null;
            if (terms0) {
                long id = cursor.getLong(cursor.getColumnIndex(DataContract.LocationlogEntry._ID));
                String title = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACETITLE));
                String startDate = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACESTARTDATE));
                String endDate = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEENDDATE));
                double distance = cursor.getDouble(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEDISTANCE));
                String img = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEIMG));
                String data = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEDATA));
                String place = cursor.getString(cursor.getColumnIndex(DataContract.LocationlogEntry.COLUMN_NAME_TRACEPLACE));
                traceList.add(new TraceLogData(title, startDate, endDate, distance, img, data, place, id));
            }
        }
        return traceList;
    }
}
