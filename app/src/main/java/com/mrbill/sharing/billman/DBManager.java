package com.mrbill.sharing.billman;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by eoghk on 2017-05-09.
 */

public class DBManager extends SQLiteOpenHelper{
    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE COSTMAP (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, cost TEXT, costPerson Text, participants Text)");
        //COSTMAP이라는 이름의 테이블 생성
        //_id는 자동으로 내가 입력할 때 증가하는 숫자
        //title, cost, costPerson, participants 모두 문자열이고 하나 씩 표에서 열 임
    }
    // DB 업그레이드를 위해 버전이 변경될 때
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(String title, String cost, String costPerson, ArrayList<String> participants){
        //Writable한 DB열기
        SQLiteDatabase db = getWritableDatabase();
        String name="";
        for (String str:participants)
            name+=str+"::";
        db.execSQL("INSERT INTO COSTMAP VALUES(null, '"+ title +"','"+ cost +"','"+ costPerson +"','"+ name  +"');");
        db.close();
    }
    public void update(String title, String cost, String costPerson, ArrayList<String> participants){
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("UPDATE COSTMAP SET cost='"+cost+"' WHERE title='" + title + "';");//where title(title이 일치하는 곳에) set date(date를 바꾼다.)
        db.close();
    }
    public void delete(String title){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM COSTMAP WHERE title='" + title + "';");
        db.close();
    }
    public ArrayList<CostData> getData(){
        //Readable한 DB열기

        ArrayList<CostData> data = new ArrayList<CostData>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM COSTMAP",null); //Query : 조회하다 -> 행단위로 모두 조회
        while(cursor.moveToNext()){
            CostData d = new CostData(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
            data.add(d);
        }
        return data;
    }
}