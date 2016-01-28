package uk.me.feixie.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Fei on 28/01/2016.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "cool_weather";

    public CoolWeatherOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table city (_id primary key autoincrement, name text, latitude text, longitude text, postcode text, country text)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
