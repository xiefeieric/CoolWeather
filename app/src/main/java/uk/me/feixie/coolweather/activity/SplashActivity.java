package uk.me.feixie.coolweather.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.db.CoolWeatherDB;
import uk.me.feixie.coolweather.model.City;
import uk.me.feixie.coolweather.util.GlobalConstant;
import uk.me.feixie.coolweather.util.UIUtils;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Handler mHandler = new Handler() {
    };
    private long mStartTime;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mStartTime = SystemClock.currentThreadTimeMillis();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(){
            @Override
            public void run() {
                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                            .addConnectionCallbacks(SplashActivity.this)
                            .addOnConnectionFailedListener(SplashActivity.this)
                            .addApiIfAvailable(LocationServices.API)
                            .build();
                }
                mGoogleApiClient.connect();
            }
        }.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {

            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addressList = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);

                if (addressList == null || addressList.size() == 0) {
                    System.out.println("No address found!");

                } else {

                    Address address = addressList.get(0);
                    String current_city = mSharedPreferences.getString("current_city", "");

                    if (TextUtils.isEmpty(current_city)) {
                        mSharedPreferences.edit().putString("current_city", address.getLocality()).apply();
                        boolean cityInList = checkCityInList(mSharedPreferences.getString("current_city", ""));

                        if (!cityInList) {
                            City city = new City();
                            city.setName(address.getLocality());
                            city.setLongitude(String.valueOf(address.getLongitude()));
                            city.setLatitude(String.valueOf(address.getLatitude()));
                            city.setPostcode(address.getPostalCode());
                            city.setCountry(address.getCountryName());
                            city.setStatus(GlobalConstant.LOCATION_STATUS_CURRENT);
                            CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(this);
                            coolWeatherDB.saveCity(city);
                        }

                    } else {

                        if (!address.getLocality().equalsIgnoreCase(current_city)) {
                            if (!checkCityInList(address.getLocality())) {
                                mSharedPreferences.edit().putString("current_city", address.getLocality()).apply();
                                City city = new City();
                                city.setName(address.getLocality());
                                city.setLongitude(String.valueOf(address.getLongitude()));
                                city.setLatitude(String.valueOf(address.getLatitude()));
                                city.setPostcode(address.getPostalCode());
                                city.setCountry(address.getCountryName());
                                city.setStatus(GlobalConstant.LOCATION_STATUS_CURRENT);
                                CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(this);
                                coolWeatherDB.updateCurrentCity(city);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        long locationAvailableTime = SystemClock.currentThreadTimeMillis();
        long timeDifference = locationAvailableTime - mStartTime;
        if (timeDifference < 3000) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, timeDifference);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        UIUtils.showToast(this,connectionResult.getErrorMessage());
    }

    private boolean checkCityInList(String city) {
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(this);
        List<City> cities = coolWeatherDB.queryAllCity();
        for (int i = 0; i < cities.size(); i++) {
            boolean inList = cities.get(i).getName().equalsIgnoreCase(city);
            if (inList)
                return true;
        }
        return false;
    }

}
