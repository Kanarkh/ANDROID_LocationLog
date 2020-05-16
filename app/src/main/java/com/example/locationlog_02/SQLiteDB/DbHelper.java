package com.example.locationlog_02.SQLiteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper sInstance; //하나의 인스턴스만 가져도 된다, 싱글톤 패턴

    private static final int DB_VERSION = 5;
    private static final String DB_NAME = "LocationLog.db";

    private static final String SQL_CREATE_LOCATIONLOG_ENTRIES =
            String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s REAL, %s REAL, %s TEXT, %s TEXT, %s TEXT " +
                            ", %s TEXT, %s TEXT, %s REAL, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    DataContract.LocationlogEntry.TABLE_NAME,
                    DataContract.LocationlogEntry._ID,
                    DataContract.LocationlogEntry.COLUMN_NAME_CATEGORY, //String
                    DataContract.LocationlogEntry.COLUMN_NAME_PLACE, //String
                    DataContract.LocationlogEntry.COLUMN_NAME_LATITUDE, //double
                    DataContract.LocationlogEntry.COLUMN_NAME_LONGITUDE,  //double
                    DataContract.LocationlogEntry.COLUMN_NAME_DATE,  //double
                    DataContract.LocationlogEntry.COLUMN_NAME_WRITING,  //String
                    DataContract.LocationlogEntry.COLUMN_NAME_URI, //String
                    DataContract.LocationlogEntry.COLUMN_NAME_TRACETITLE, //String
                    DataContract.LocationlogEntry.COLUMN_NAME_TRACESTARTDATE, //String
                    DataContract.LocationlogEntry.COLUMN_NAME_TRACEENDDATE, //String
                    DataContract.LocationlogEntry.COLUMN_NAME_TRACEDISTANCE, //double
                    DataContract.LocationlogEntry.COLUMN_NAME_TRACEIMG, //String
                    DataContract.LocationlogEntry.COLUMN_NAME_TRACEDATA, //String
                    DataContract.LocationlogEntry.COLUMN_NAME_TRACEPLACE//String
            );

    //create table if not exists
    //삭제
    private static final String SQL_DELETE_LOCATIONLOG_ENTRIES = "DROP TABLE IF EXISTS " + DataContract.LocationlogEntry.TABLE_NAME;

    //팩토리 매소드?
    public static DbHelper getInstance(Context context) {
        if (sInstance == null) {
            return sInstance = new DbHelper(context);
        }
        return sInstance;
    }

    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //수행이 되면서 DB 생성
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATIONLOG_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //DB_VERSION을 올려줬을때.
        //이전 테이블(i)과 변경 테이블(i1)을 처리해줘야한다.
        sqLiteDatabase.execSQL(SQL_DELETE_LOCATIONLOG_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
