package com.example.locationlog_02.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedManager {
    //TimeMode Shared에 저장하기.
    private Context context;
    private String sharedName="NoName";

    //생성자
    public SharedManager(Context context) {
        this.context = context;
    }


    //저장하기 String
    public void sharedSaveString(String  shared_Name,String key, String value) {
        setSharedFileName(shared_Name);
        sharedSaveString(key,value);
    }
    //저장하기
    public void sharedSaveString(String key, String value) {
        SharedPreferences Shared = context.getSharedPreferences( sharedName, context.MODE_PRIVATE);
        SharedPreferences.Editor SharedEditor = Shared.edit();

        SharedEditor.putString(key,value);
        SharedEditor.apply();
    }
    //저장하기 Int
    public void sharedSaveInt(String  shared_Name,String key, int value) {
        setSharedFileName(shared_Name);
        sharedSaveInt(key,value);
    }
    //저장하기
    public void sharedSaveInt(String key, int value) {
        SharedPreferences Shared = context.getSharedPreferences( sharedName, context.MODE_PRIVATE);
        SharedPreferences.Editor SharedEditor = Shared.edit();

        SharedEditor.putInt(key,value);
        SharedEditor.apply();
    }
    //저장하기 Boolean
    public void sharedSaveBoolean(String  shared_Name,String key, boolean value) {
        setSharedFileName(shared_Name);
        sharedSaveBoolean(key,value);
    }
    //저장하기
    public void sharedSaveBoolean(String key, Boolean value) {
        SharedPreferences Shared = context.getSharedPreferences( sharedName, context.MODE_PRIVATE);
        SharedPreferences.Editor SharedEditor = Shared.edit();

        SharedEditor.putBoolean(key,value);
        SharedEditor.apply();
    }
    //저장하기 Float
    public void sharedSaveFloat(String  shared_Name,String key, float value) {
        setSharedFileName(shared_Name);
        sharedSaveFloat(key,value);
    }
    //저장하기
    public void sharedSaveFloat(String key, Float value) {
        SharedPreferences Shared = context.getSharedPreferences( sharedName, context.MODE_PRIVATE);
        SharedPreferences.Editor SharedEditor = Shared.edit();

        SharedEditor.putFloat(key,value);
        SharedEditor.apply();
    }
    //저장하기 Long
    public void sharedSaveLong(String  shared_Name,String key, long value) {
        setSharedFileName(shared_Name);
        sharedSaveLong(key,value);
    }
    //저장하기
    public void sharedSaveLong(String key, long value) {
        SharedPreferences Shared = context.getSharedPreferences( sharedName, context.MODE_PRIVATE);
        SharedPreferences.Editor SharedEditor = Shared.edit();

        SharedEditor.putLong(key,value);
        SharedEditor.apply();
    }
    //저장하기 StringSet
    public void sharedSaveStringSet(String  shared_Name,String key, Set<String> value) {
        setSharedFileName(shared_Name);
        sharedSaveStringSet(key,value);
    }
    //저장하기
    public void sharedSaveStringSet(String key, Set<String> value) {
        SharedPreferences Shared = context.getSharedPreferences( sharedName, context.MODE_PRIVATE);
        SharedPreferences.Editor SharedEditor = Shared.edit();

        SharedEditor.putStringSet(key,value);
        SharedEditor.apply();
    }


    //읽어오기
    //String
    public String ReadString(String  shared_Name,String key) {
        setSharedFileName(shared_Name);
        return ReadString(key);
    }
    public String ReadString(String key) {
        SharedPreferences Shared = context.getSharedPreferences( sharedName, context.MODE_PRIVATE);
        return Shared.getString(key,""); // s1은 defValue 이다.
    }

    //읽어오기 Boolean
    public boolean ReadBoolean(String  shared_Name,String key) {
        setSharedFileName(shared_Name);
        return ReadBoolean(key);
    }
    //저장하기
    public Boolean ReadBoolean(String key) {
        SharedPreferences Shared = context.getSharedPreferences( sharedName, context.MODE_PRIVATE);
        return Shared.getBoolean(key,false); // s1은 defValue 이다.
    }


    //Get Set
    public String getSharedFileName() {
        return sharedName;
    }

    public void setSharedFileName(String sharedName) {
        this.sharedName = sharedName;
    }
}
