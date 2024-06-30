package com.nationalhackaton.smartlearningassist.DatabaseManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbHelperClass extends SQLiteOpenHelper {
    private static final String TABLE_NAME="TODO_TABLE";
    private static final String DATABASE_NAME="TODO_DATABASE";

    public DbHelperClass(@Nullable Context context) {
        super(context,DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableQuery="CREATE TABLE "+TABLE_NAME+"( ID integer primary key autoincrement , TASK TEXT ,STATUS INTEGER ) ";
        db.execSQL(tableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropQuery="DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(dropQuery);
        onCreate(db);
    }

    //---------------ADDING FUNCTION IN SQL---------------//
    public boolean addDataToSQL(String task,int status){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("TASK",task);
        cv.put("STATUS",status);
        float isExecuted = db.insert(TABLE_NAME,null,cv);
        if(isExecuted==-1){
            return false;
        }else {
            return true;
        }
    }//end of add Function
    //-------------------------------------------//
    //---------RETRIEVING DATA FUNCTION----------//
    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        String selectingAllQuery="select * from "+TABLE_NAME +" order by ID desc";
        return db.rawQuery(selectingAllQuery,null);
    }//end of retrieving function
    //-------------------------------------------//
    //-----FUNCTION FOR UPDATING ------//
    public void updateStatusOfTask(int status,int id){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("STATUS",status);
        float error = db.update(TABLE_NAME,values,"ID=?", new String[]{id+""});
        if(error==-1){
            Log.d("Error code :" ,-1+"");
        }else{
            Log.d("Error code :" ,"one");
        }
    }//end of status updating function

    public void updateTask(int id, String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TASK", task);
        int rowsAffected = db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
        if (rowsAffected == 1) {
            Log.d("Update Status:", "Task updated successfully");
        } else {
            Log.d("Update Status:", "Failed to update task");
        }
    }
    //--------Deletion Of the Task----------//
    public void deleteTask(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        long errorCode=db.delete(TABLE_NAME,"ID=?",new String[]{id+""});
        if(errorCode==-1){
            Log.d("Error code :" ,"Deletion Error");
        }else{
            Log.d("Error code :" ,"Deletion Successful");
        }
    }

}
