package uk.me.feixie.coolweather.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.util.GlobalConstant;
import uk.me.feixie.coolweather.util.NumberHelper;
import uk.me.feixie.coolweather.util.UIUtils;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView tvTemp;
    private TextView tvDesc;
    private TextView tvHumidity;
    private TextView tvWindSpeed;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initToolbar();
        initViews();
    }

    private void initData() {

        x.Ext.init(getApplication());
        x.Ext.setDebug(true);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViews() {

        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvWindSpeed = (TextView) findViewById(R.id.tvWindSpeed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation!=null) {

            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addressList = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (addressList==null||addressList.size()==0) {
                    UIUtils.showToast(this,"No address found!");
                } else {
                    Address address = addressList.get(0);
//                    System.out.println(address.getCountryCode()+"/"+address.getLocality());
                    updateFromWeb(address.getLocality());
                }

            } catch (IOException e) {
                e.printStackTrace();
                UIUtils.showToast(this,"Service not available!");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateFromWeb(String cityName) {
        RequestParams url = new RequestParams(GlobalConstant.WEATHER_SERVER+cityName+GlobalConstant.OPEN_API_KEY+GlobalConstant.UNIT_CELSIUS);
//        System.out.println(url.toString());
        x.http().get(url, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
//                System.out.println(result);
                return false;
            }

            @Override
            public void onSuccess(String result) {
//                System.out.println(result);
                handleWeatherResponse(result);
                updateCurrentWeather();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void handleWeatherResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = weather.getString("description");
//            System.out.println(description);

            JSONObject main = jsonObject.getJSONObject("main");
            String temp = main.getString("temp");
//            System.out.println(temp);
            String humidity = main.getString("humidity");
//            System.out.println(humidity);

            JSONObject wind = jsonObject.getJSONObject("wind");
            String speed = wind.getString("speed");
//            System.out.println(speed);

            saveWeatherInfoToLocal(description,temp,humidity,speed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveWeatherInfoToLocal(String description, String temp, String humidity, String speed) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("description",description);
        editor.putString("temp",temp);
        editor.putString("humidity",humidity);
        editor.putString("speed",speed);
        editor.putString("current_time",sdf.format(new Date()));
        editor.apply();
    }

    private void updateCurrentWeather() {

        String temp = mSharedPreferences.getString("temp", "");
        if (!TextUtils.isEmpty(temp)) {
            double tempDouble = Double.parseDouble(temp);
            int tempRound = (int) NumberHelper.round(tempDouble, 0);
            tvTemp.setText(tempRound+"");
        }

        tvDesc.setText(mSharedPreferences.getString("description",""));
        tvHumidity.setText(mSharedPreferences.getString("humidity","")+"%");
        tvWindSpeed.setText(mSharedPreferences.getString("speed","")+" km/h");

    }

}
