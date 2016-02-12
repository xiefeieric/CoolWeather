package uk.me.feixie.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.coolweather.model.City;

/**
 * Created by Fei on 28/01/2016.
 */
public class CoolWeatherDB {

    private static CoolWeatherDB sCoolWeatherDB;
    private SQLiteDatabase db;
    public static final String TABLE_NAME = "city";

    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (sCoolWeatherDB == null) {
            sCoolWeatherDB = new CoolWeatherDB(context);
        }
        return sCoolWeatherDB;
    }

    public synchronized void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
//            name text, latitude text, longitude text, postcode text, country text)
            values.put("name", city.getName());
            values.put("latitude", city.getLatitude());
            values.put("longitude", city.getLongitude());
            values.put("postcode", city.getPostcode());
            values.put("country", city.getCountry());
            values.put("status", city.getStatus());
            db.insert(TABLE_NAME, null, values);
        }
    }

    public synchronized List<City> queryAllCity() {

        List<City> cities = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                City city = new City();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                city.setName(name);
                String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                city.setLatitude(latitude);
                String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                city.setLongitude(longitude);
                String postcode = cursor.getString(cursor.getColumnIndex("postcode"));
                city.setPostcode(postcode);
                String country = cursor.getString(cursor.getColumnIndex("country"));
                city.setCountry(country);
                String status = cursor.getString(cursor.getColumnIndex("status"));
                city.setStatus(status);
                cities.add(city);
            }
        }

        return cities;
    }

    public synchronized void deleteCity(City city) {
        if (city != null) {
            db.delete(TABLE_NAME,"name=?",new String[]{city.getName()});
        }
    }
}
