package com.scadroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joe on 13/06/15.
 */

public class DataBaseCore extends SQLiteOpenHelper {
    private static final String DB_NAME = "datapoints";
    private static final int DB_VERSION = 1;

    public DataBaseCore(Context ctx){
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // tabela usada para armazenar as configurações de ip de cada residência
        db.execSQL("create table databases(_id integer primary key autoincrement," +
                                          "url text not null, " +
                                          "name text);");
        // tabela usada para guardar os dados de cada ponto já relacionados com o endereço ip
        db.execSQL("create table points(_id integer primary key autoincrement," +
                   " db_url text REFERENCES databases(url) ON DELETE SET DEFAULT," +
                   " name text not null," +
                   " icon text," +
                   " category text," +
                   " writable integer," +
                   " update_rate integer," +
                   " data_type text," +
                   " price double)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2){
        db.execSQL("");
        onCreate(db);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
