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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
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
import uk.me.feixie.coolweather.db.CoolWeatherDB;
import uk.me.feixie.coolweather.model.City;
import uk.me.feixie.coolweather.util.GlobalConstant;
import uk.me.feixie.coolweather.util.NumberHelper;
import uk.me.feixie.coolweather.util.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private TextView tvTemp;
    private TextView tvDesc;
    private TextView tvHumidity;
    private TextView tvWindSpeed;
    private SharedPreferences mSharedPreferences;
    private ImageView ivCurrentWeather;
    private ImageView ivCurrentWeatherCover;

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

        String current_city = mSharedPreferences.getString("current_city", "");
        String select_city = mSharedPreferences.getString("select_city", "");
        if (!TextUtils.isEmpty(current_city) && TextUtils.isEmpty(select_city)) {
            current_city = current_city.replaceAll("\\s+","");
            updateFromWeb(current_city);
        } else {
            select_city = select_city.replaceAll("\\s+","");
            updateFromWeb(select_city);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mSharedPreferences!=null) {
                String current_city = mSharedPreferences.getString("current_city", "");
                String select_city = mSharedPreferences.getString("select_city", "");

                if (!TextUtils.isEmpty(current_city) && TextUtils.isEmpty(select_city)) {
                    current_city = current_city.replaceAll("\\s+","");
                    updateFromWeb(current_city);

                } else {
                    select_city = select_city.replaceAll("\\s+","");
                    updateFromWeb(select_city);

                }
            }

        } else {
            if (ivCurrentWeather!=null && ivCurrentWeatherCover!=null) {
                ivCurrentWeather.clearAnimation();
                ivCurrentWeatherCover.clearAnimation();
            }
        }
    }

    private void initViews(View view) {

        tvTemp = (TextView) view.findViewById(R.id.tvTemp);
        tvDesc = (TextView) view.findViewById(R.id.tvDesc);
        tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
        tvWindSpeed = (TextView) view.findViewById(R.id.tvWindSpeed);
        ivCurrentWeather = (ImageView) view.findViewById(R.id.ivCurrentWeather);
        ivCurrentWeatherCover = (ImageView) view.findViewById(R.id.ivCurrentWeatherCover);
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

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {

            try {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addressList = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                if (addressList == null || addressList.size() == 0) {
                    UIUtils.showToast(getActivity(), "No address found!");
                } else {
                    Address address = addressList.get(0);
//                    System.out.println(address.getCountryCode()+"/"+address.getLocality());
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
                        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(getActivity());
                        coolWeatherDB.saveCity(city);
                    }

                    String select_city = mSharedPreferences.getString("select_city", "");
                    if (TextUtils.isEmpty(select_city)) {
                        updateFromWeb(address.getLocality());
                    } else {
                        updateFromWeb(select_city);
                    }
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

    private boolean checkCityInList(String city) {
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(getActivity());
        List<City> cities = coolWeatherDB.queryAllCity();
        for (int i = 0; i < cities.size(); i++) {
            boolean inList = cities.get(i).getName().equalsIgnoreCase(city);
            if (inList)
                return true;
        }
        return false;
    }

    public void updateFromWeb(String cityName) {
        RequestParams url = new RequestParams(GlobalConstant.WEATHER_SERVER + cityName + GlobalConstant.OPEN_API_KEY + GlobalConstant.UNIT_CELSIUS);
//        System.out.println(url.toString());
        x.http().get(url, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
//                System.out.println(result);
                handleWeatherResponse(result);
                updateCurrentWeather();
//                System.out.println("refresh");
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
            String icon = weather.getString("icon");
//            System.out.println(description);

            JSONObject main = jsonObject.getJSONObject("main");
            String temp = main.getString("temp");
//            System.out.println(temp);
            String humidity = main.getString("humidity");
//            System.out.println(humidity);

            JSONObject wind = jsonObject.getJSONObject("wind");
            String speed = wind.getString("speed");
//            System.out.println(speed);

            saveWeatherInfoToLocal(description, icon, temp, humidity, speed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveWeatherInfoToLocal(String description, String icon, String temp, String humidity, String speed) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("description", description);
        editor.putString("icon", icon);
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
            tvTemp.setText(tempRound + "°");
        }

        tvDesc.setText(mSharedPreferences.getString("description", ""));
        tvHumidity.setText(mSharedPreferences.getString("humidity", "") + "%");
        tvWindSpeed.setText(mSharedPreferences.getString("speed", "") + " m/s");

        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);

        final Animation rotate = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setDuration(100000);
        rotate.setInterpolator(new LinearInterpolator());

        String icon = mSharedPreferences.getString("icon", "");
        if (icon.equalsIgnoreCase("01d")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.sun_disc));
                    ivCurrentWeatherCover.setVisibility(View.INVISIBLE);
                    ivCurrentWeather.startAnimation(fadeIn);
                    ivCurrentWeather.startAnimation(rotate);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        } else if (icon.equalsIgnoreCase("02d")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.sun_disc));
                    ivCurrentWeatherCover.setImageDrawable(getResources().getDrawable(R.drawable.cloud_mist));
                    ivCurrentWeather.startAnimation(fadeIn);
                    ivCurrentWeather.startAnimation(rotate);
                    ivCurrentWeatherCover.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("03d") || icon.equalsIgnoreCase("03n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setVisibility(View.INVISIBLE);
                    ivCurrentWeatherCover.setImageDrawable(getResources().getDrawable(R.drawable.cloud_lite));
                    ivCurrentWeatherCover.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("04d") || icon.equalsIgnoreCase("04n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setVisibility(View.INVISIBLE);
                    ivCurrentWeatherCover.setImageDrawable(getResources().getDrawable(R.drawable.cloud_dark));
                    ivCurrentWeatherCover.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("09d") || icon.equalsIgnoreCase("09n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.cloud_lite));
                    ivCurrentWeatherCover.setImageDrawable(getResources().getDrawable(R.drawable.rain_drops_light));
                    ivCurrentWeather.startAnimation(fadeIn);
                    ivCurrentWeatherCover.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("10d") || icon.equalsIgnoreCase("10n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.cloud_dark));
                    ivCurrentWeatherCover.setImageDrawable(getResources().getDrawable(R.drawable.rain_drops_heavy));
                    ivCurrentWeather.startAnimation(fadeIn);
                    ivCurrentWeatherCover.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("11d") || icon.equalsIgnoreCase("11n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.cloud_dark));
                    ivCurrentWeatherCover.setImageDrawable(getResources().getDrawable(R.drawable.thunderbolts_glow));
                    ivCurrentWeather.startAnimation(fadeIn);
                    ivCurrentWeatherCover.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("13d") || icon.equalsIgnoreCase("13n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.cloud_dark));
                    ivCurrentWeatherCover.setImageDrawable(getResources().getDrawable(R.drawable.snow_flakes));
                    ivCurrentWeather.startAnimation(fadeIn);
                    ivCurrentWeatherCover.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("50d") || icon.equalsIgnoreCase("50n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.mist_dark));
                    ivCurrentWeatherCover.setVisibility(View.INVISIBLE);
                    ivCurrentWeather.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("01n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.moon));
                    ivCurrentWeatherCover.setVisibility(View.INVISIBLE);
                    ivCurrentWeather.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (icon.equalsIgnoreCase("02n")) {
            ivCurrentWeather.startAnimation(fadeOut);
            ivCurrentWeatherCover.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivCurrentWeather.setImageDrawable(getResources().getDrawable(R.drawable.moon));
                    ivCurrentWeatherCover.setImageDrawable(getResources().getDrawable(R.drawable.cloud_mist));
                    ivCurrentWeather.startAnimation(fadeIn);
                    ivCurrentWeatherCover.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }
}
