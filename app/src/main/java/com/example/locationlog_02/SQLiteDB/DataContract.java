package com.example.locationlog_02.SQLiteDB;

import android.provider.BaseColumns;

public final class DataContract {
    private DataContract() {
    }

    public static class LocationlogEntry implements BaseColumns {
        public static final String TABLE_NAME = "locationLog";
        public static final String COLUMN_NAME_CATEGORY = "category";   //String
        //        public static final String COLUMN_NAME_CATEGORYPOS ="categorypos";   //String
        public static final String COLUMN_NAME_PLACE = "place";         //string
        //        public static final String COLUMN_NAME_PLACEPOS ="placepos";         //string
        public static final String COLUMN_NAME_LATITUDE = "latitude";   //double
        public static final String COLUMN_NAME_LONGITUDE = "longitude"; //double
        public static final String COLUMN_NAME_DATE = "date";           //String
        public static final String COLUMN_NAME_WRITING = "writing";     //String
        public static final String COLUMN_NAME_URI = "uri";             //String
        public static final String COLUMN_NAME_TRACETITLE = "traceTitle"; //String
        public static final String COLUMN_NAME_TRACESTARTDATE = "traceStartDate"; //String
        public static final String COLUMN_NAME_TRACEENDDATE = "traceEndData"; //String
        public static final String COLUMN_NAME_TRACEDISTANCE = "traceDistance"; //double
        public static final String COLUMN_NAME_TRACEIMG = "traceImg"; //String
        public static final String COLUMN_NAME_TRACEDATA = "traceData"; //String
        public static final String COLUMN_NAME_TRACEPLACE = "tracePlaces"; //String

    }
}
