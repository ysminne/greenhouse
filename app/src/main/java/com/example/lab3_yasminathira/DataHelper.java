package com.example.lab3_yasminathira;

import android.content.Context;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {
    //DB Name
    private static final String DATABASE_NAME = "personalbiodata.db";
    //DB Version
    private static final int DATABASE_VERSION = 1;
    //Create construtor for Datahelper
    public DataHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override

    public void onCreate(SQLiteDatabase db) {
        String sql = "create table biodata(no integer primary key, name text null, dob text null, gender text null,address text null);";
        Log.d("Data", "onCreate: " + sql);
        db.execSQL(sql);
    }
    @Override

    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){
    }
}
