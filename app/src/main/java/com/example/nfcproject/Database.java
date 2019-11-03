package com.example.nfcproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "student.db";
    public static final String TABLE_NAME = "User";
    public static final String COL_1 = "XNUMBER";
    public static final String COL_2 = "FNAME";
    public static final String COL_3 = "LNAME";
    public static final String COL_4 = "EMAIL";

    public Database(Context context){
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS USER");
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(XNUMBER TEXT, FNAME TEXT, LNAME TEXT, EMAIL TEXT);");

    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(XNUMBER TEXT, FNAME TEXT, LNAME TEXT, EMAIL TEXT);");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public  boolean insertData(String xnum, String fname, String lname, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, xnum);
        cv.put(COL_2, fname);
        cv.put(COL_3, lname);
        cv.put(COL_4, email);
        Log.i("XNUM", "XNUM" + xnum);
        Long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
    public Cursor getAllDataWhereXNUMBER(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +" where XNUMBER='X00136708'", null);
        return res;
    }

    public Integer deleteData(String xnumber){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "XNUMBER = ?", new String[] {xnumber});
    }

}
