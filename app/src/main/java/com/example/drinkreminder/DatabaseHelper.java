package com.example.drinkreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Pavan on 6/30/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME="Reminder.db";
    public static final String TABLE_NAME="reminder_table";
    public static final String COL_1="ID";
    public static final String COL_2="HOUR";
    public static final String COL_3="MINUTE";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME +" " +
                "(ID INTEGER PRIMARY KEY,HOUR TEXT,MINUTE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int Id,String hour,String minute){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1,Id);
        contentValues.put(COL_2,hour);
        contentValues.put(COL_3,minute);
        long result=db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }


//    public Cursor getAllData(){
//        SQLiteDatabase db=this.getWritableDatabase();
//        Cursor res=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
////        db.close();
//        return res;
//    }
//
////

    public ArrayList<String> getData(){
        ArrayList<String> reminderlist=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);

        while(res.moveToNext()){
            StringBuffer buffer=new StringBuffer();
            buffer.append(res.getString(0)+" : ");
            buffer.append("Time : "+res.getString(1)+":");
            buffer.append(""+res.getString(2)+"\n");

            reminderlist.add(buffer.toString());
        }
        res.close();
        db.close();
        return reminderlist;
    }

    public boolean updateData(String id,String hour,String minute){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,hour);
        contentValues.put(COL_3,minute);
        db.update(TABLE_NAME,contentValues,"id = ?",new String[]{id});
        db.close();
        return true;
    }

    public void deleteData(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,"ID = ?",new String[] {id});
        db.close();
    }
}
