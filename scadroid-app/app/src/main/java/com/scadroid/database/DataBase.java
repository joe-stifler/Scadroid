package com.scadroid.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.scadroid.R;
import com.scadroid.domain.Point;
import com.scadroid.domain.Profile;

/**
 * Created by joe on 13/06/15.
 */
public class DataBase {
    private SQLiteDatabase db;
    private SharedPreferences prefs; // used to recover the datas about the server
    private Context context;

    public DataBase(Context context) {
        DataBaseCore auxDb = new DataBaseCore(context);
            db = auxDb.getWritableDatabase();

        this.context = context;

        // capturando o ip armazenado nas configurações
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void closeDataBase(){
       db.close();
    }

    // used to store the several hosts with scada
    public void insertDataBase(String ip, String name) {
        ContentValues values = new ContentValues();
            values.put("url", ip);
            values.put("name", name);
        db.insert("databases", null, values);
    }

    // used to store the points with your configurations
    public void insertPoint(Point point) {
        ContentValues values = new ContentValues();
            values.put("db_url", prefs.getString("serverIp", ""));
            values.put("name", point.getName());
            values.put("icon", point.getIconString());
            values.put("category", point.getCategoryString());
            values.put("writable", point.getWritable());
            values.put("update_rate", point.getUpdate());
            values.put("data_type", point.getType());
            values.put("price", point.getPrice());
        db.insert("points", null, values);
    }

    public void updatePoint(Point point) {
        ContentValues values = new ContentValues();
            values.put("icon", point.getIconString());
            values.put("writable", point.getWritable());
            values.put("category", point.getCategoryString());
            values.put("update_rate", point.getUpdate());
            values.put("data_type", point.getType());
            values.put("price", point.getPrice());
        db.update("points", values, "_id = " + point.getId(), null);
    }

    public void deleteDataBase(String url){
        db.setForeignKeyConstraintsEnabled(true);
        db.delete("databases", "url = "+ url, null);
    }

    public void deletePoint(Point point) {
        db.delete("points", "_id = " + point.getId(), null);
    }

    public List<Profile> searchDataBases() {
        List<Profile> profiles = new ArrayList<Profile>();
        String[] columns = new String[]{"_id", "url", "name"};
        Cursor cursor = db.query("databases", columns, null, null, null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                ProfileDrawerItem aux = new ProfileDrawerItem();
                    aux.setName(""+cursor.getString(2));
                    aux.setEmail(cursor.getString(1));
                    aux.setIcon(context.getResources().getDrawable(R.drawable.logo_50));

                Profile p = new Profile();
                    p.setProfile(aux);
                    p.setServerId(cursor.getString(1));
                    p.setBackground(R.drawable.wallpaper);
                profiles.add(p);
            } while(cursor.moveToNext());
        }

        return profiles;
    }

    public List<Point> searchPoints() {
        List<Point> list = new ArrayList<Point>();
        String[] columns = new String[]{"_id", "name", "icon", "category", "writable", "update_rate", "data_type", "price"};

        Cursor cursor = db.query("points", columns, null, null, null, null, "name ASC");

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Point point = new Point(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getDouble(7));
                list.add(point);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }
}
