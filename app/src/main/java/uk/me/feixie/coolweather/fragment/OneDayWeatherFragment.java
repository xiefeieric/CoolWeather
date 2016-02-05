package uk.me.feixie.coolweather.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.util.GlobalConstant;
import uk.me.feixie.coolweather.util.NumberHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class OneDayWeatherFragment extends Fragment {

    private TextView tvOneDayDesc;
    private TextView tv3HourTime;
    private TextView tv6HourTime;
    private TextView tv9HourTime;
    private TextView tv12HourTime;
    private TextView tv15HourTime;
    private TextView tv18HourTime;
    private TextView tv21HourTime;
    private TextView tv24HourTime;
    private TextView tv3HourTemp;
    private TextView tv6HourTemp;
    private TextView tv9HourTemp;
    private TextView tv12HourTemp;
    private TextView tv15HourTemp;
    private TextView tv18HourTemp;
    private TextView tv21HourTemp;
    private TextView tv24HourTemp;

    private List<TextView> timeList;
    private List<TextView> tempList;

    private SharedPreferences mSharedPreferences;

    public OneDayWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one_day_weather, container, false);
        initData();
        initViews(view);
        String current_city = mSharedPreferences.getString("current_city", "");
        if (!TextUtils.isEmpty(current_city)) {
            updateFromWeb(current_city);
        }
        return view;
    }

    private void initData() {

        x.Ext.init(getActivity().getApplication());
        x.Ext.setDebug(true);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    private void initViews(View view) {

        timeList = new ArrayList<>();
        tempList = new ArrayList<>();

        tvOneDayDesc = (TextView) view.findViewById(R.id.one_day_desc);
        tv3HourTime = (TextView) view.findViewById(R.id.tv_3_hour_time);
        timeList.add(tv3HourTime);
        tv6HourTime = (TextView) view.findViewById(R.id.tv_6_hour_time);
        timeList.add(tv6HourTime);
        tv9HourTime = (TextView) view.findViewById(R.id.tv_9_hour_time);
        timeList.add(tv9HourTime);
        tv12HourTime = (TextView) view.findViewById(R.id.tv_12_hour_time);
        timeList.add(tv12HourTime);
        tv15HourTime = (TextView) view.findViewById(R.id.tv_15_hour_time);
        timeList.add(tv15HourTime);
        tv18HourTime = (TextView) view.findViewById(R.id.tv_18_hour_time);
        timeList.add(tv18HourTime);
        tv21HourTime = (TextView) view.findViewById(R.id.tv_21_hour_time);
        timeList.add(tv21HourTime);
        tv24HourTime = (TextView) view.findViewById(R.id.tv_24_hour_time);
        timeList.add(tv24HourTime);
        tv3HourTemp = (TextView) view.findViewById(R.id.tv_3_hour_temp);
        tempList.add(tv3HourTemp);
        tv6HourTemp = (TextView) view.findViewById(R.id.tv_6_hour_temp);
        tempList.add(tv6HourTemp);
        tv9HourTemp = (TextView) view.findViewById(R.id.tv_9_hour_temp);
        tempList.add(tv9HourTemp);
        tv12HourTemp = (TextView) view.findViewById(R.id.tv_12_hour_temp);
        tempList.add(tv12HourTemp);
        tv15HourTemp = (TextView) view.findViewById(R.id.tv_15_hour_temp);
        tempList.add(tv15HourTemp);
        tv18HourTemp = (TextView) view.findViewById(R.id.tv_18_hour_temp);
        tempList.add(tv18HourTemp);
        tv21HourTemp = (TextView) view.findViewById(R.id.tv_21_hour_temp);
        tempList.add(tv21HourTemp);
        tv24HourTemp = (TextView) view.findViewById(R.id.tv_24_hour_temp);
        tempList.add(tv24HourTemp);

    }

    private void updateFromWeb(String cityName) {
        RequestParams url = new RequestParams(GlobalConstant.ONE_DAY_WEATHER_SERVER + cityName + GlobalConstant.OPEN_API_KEY + GlobalConstant.UNIT_CELSIUS);
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
//                updateCurrentWeather();
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
            JSONArray list = jsonObject.getJSONArray("list");
            for (int i = 0; i < 8; i++) {
                JSONObject jsonItem = (JSONObject) list.get(i);
                JSONObject main = jsonItem.getJSONObject("main");
                String temp = main.getString("temp");
                int temperature = (int) NumberHelper.round(Double.parseDouble(temp),0);
                JSONArray weather = jsonItem.getJSONArray("weather");
                JSONObject weatherItem = (JSONObject) weather.get(0);
                String description = weatherItem.getString("description");
                String dt_txt = jsonItem.getString("dt_txt");
                String[] split = dt_txt.split(" ");
                String time = split[1];
                time = time.substring(0, 5);
//                System.out.println(temp + " / " + description + " / " + time + "\n");
                timeList.get(i).setText(time);
                tempList.get(i).setText(temperature+"°");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
