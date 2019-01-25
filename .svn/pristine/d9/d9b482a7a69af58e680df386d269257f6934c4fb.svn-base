package com.sdt.safefilemanager.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sdt.safefilemanager.activity.SecondActivity;

public class DAOHandler {

    private static DAOHandler instance;
    SQLiteDatabase m_database = null;
    String m_context_path;
    private DAOHandler() {
    }
    public static DAOHandler getInstance() {
        if (instance == null) {
            instance = new DAOHandler();
        }
        return instance;
    }

    public void initHandler(String path) {
        m_context_path = path;

        m_database = SQLiteDatabase.openOrCreateDatabase(m_context_path + "/config.db", null);

        Cursor cursor = m_database.rawQuery("select name from sqlite_master where type='table';", null);
        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            if (name.compareTo("configInfo") == 0)
                return;
        }

        //创建表SQL语句
        String stu_table="create table if not exists configInfo(_id integer primary key autoincrement,login_pwd text,bluetooth_mac text)";
        //执行SQL语句
        m_database.execSQL(stu_table);
    }
    public String getLoginPwd() {
        Cursor cursor = m_database.query("configInfo", null, null, null, null, null, null, null);
        if (cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();

        String str_pwd = cursor.getString(1);
        return str_pwd;
    }

    public boolean setLoginPwd(String str_pwd) {
        Cursor cursor = m_database.query("configInfo", null, null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            //实例化常量值
            ContentValues cValue = new ContentValues();
            //添加login_pwd
            cValue.put("login_pwd",str_pwd);
            //添加bluetooth_mac
            cValue.put("bluetooth_mac","");
            //调用insert()方法插入数据
            m_database.insert("configInfo",null,cValue);
            return true;
        }
        cursor.moveToFirst();
        Integer id = cursor.getInt(0);

        //实例化内容值
        ContentValues values = new ContentValues();
        //在values中添加内容
        values.put("login_pwd",str_pwd);
        //修改条件
        String whereClause = "_id=?";
        //修改添加参数
        String[] whereArgs={String.valueOf(id)};
        //修改
        m_database.update("configInfo",values,whereClause,whereArgs);

        return  true;
    }

    public String getBlueToothMac() {
        Cursor cursor = m_database.query("configInfo", null, null, null, null, null, null, null);
        if (cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        String str_mac = cursor.getString(2);
        return str_mac;
    }

    public boolean setBlueToothMac(String str_mac) {
        Cursor cursor = m_database.query("configInfo", null, null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            return false;
        }
        cursor.moveToFirst();
        Integer id = cursor.getInt(0);

        //实例化内容值
        ContentValues values = new ContentValues();
        //在values中添加内容
        values.put("bluetooth_mac",str_mac);
        //修改条件
        String whereClause = "_id=?";
        //修改添加参数
        String[] whereArgs={String.valueOf(id)};
        //修改
        m_database.update("configInfo",values,whereClause,whereArgs);

        return  true;
    }
}
