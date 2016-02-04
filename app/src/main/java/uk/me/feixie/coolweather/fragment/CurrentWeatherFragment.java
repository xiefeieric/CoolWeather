package uk.me.feixie.coolweather.fragment;


import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView tvTemp;
    private TextView tvDesc;
    private TextView tvHumidity;
    private TextView tvWindSpeed;
    private SharedPreferences mSharedPreferences;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        initData();
        initViews(view);
        return view;
    }

    private void initData() {

        x.Ext.init(getActivity().getApplication());
        x.Ext.setDebug(true);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    private void initViews(View view) {

        tvTemp = (TextView) view.findViewById(R.id.tvTemp);
        tvDesc = (TextView) view.findViewById(R.id.tvDesc);
        tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
        tvWindSpeed = (TextView) view.findViewById(R.id.tvWindSpeed);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            try {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addressList = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (addressList == null || addressList.size() == 0) {
                    UIUtils.showToast(getActivity(), "No address found!");
                } else {
                    Address address = addressList.get(0);
//                    System.out.println(address.getCountryCode()+"/"+address.getLocality());
                    mSharedPreferences.edit().putString("current_city",address.getLocality()).apply();
                    updateFromWeb(address.getLocality());
                }

            } catch (IOException e) {
                e.printStackTrace();
                UIUtils.showToast(getActivity(), "Service not available!");
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
        RequestParams url = new RequestParams(GlobalConstant.WEATHER_SERVER + cityName + GlobalConstant.OPEN_API_KEY + GlobalConstant.UNIT_CELSIUS);
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

            saveWeatherInfoToLocal(description, temp, humidity, speed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveWeatherInfoToLocal(String description, String temp, String humidity, String speed) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("description", description);
        editor.putString("temp", temp);
        editor.putString("humidity", humidity);
        editor.putString("speed", speed);
        editor.putString("current_time", sdf.format(new Date()));
        editor.apply();
    }

    private void updateCurrentWeather() {

        String temp = mSharedPreferences.getString("temp", "");
        if (!TextUtils.isEmpty(temp)) {
            double tempDouble = Double.parseDouble(temp);
            int tempRound = (int) NumberHelper.round(tempDouble, 0);
            tvTemp.setText(tempRound + "");
        }

        tvDesc.setText(mSharedPreferences.getString("description", ""));
        tvHumidity.setText(mSharedPreferences.getString("humidity", "") + "%");
        tvWindSpeed.setText(mSharedPreferences.getString("speed", "") + " km/h");

    }
}
