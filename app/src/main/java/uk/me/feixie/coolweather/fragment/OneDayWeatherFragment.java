package uk.me.feixie.coolweather.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import uk.me.feixie.coolweather.util.UIUtils;

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

    private ImageView iv3Hour;
    private ImageView iv6Hour;
    private ImageView iv9Hour;
    private ImageView iv12Hour;
    private ImageView iv15Hour;
    private ImageView iv18Hour;
    private ImageView iv21Hour;
    private ImageView iv24Hour;

    private List<TextView> timeList;
    private List<TextView> tempList;
    private List<ImageView> iconList;

    private LinearLayout ll3Hour;
    private LinearLayout ll6Hour;
    private LinearLayout ll9Hour;
    private LinearLayout ll12Hour;
    private LinearLayout ll15Hour;
    private LinearLayout ll18Hour;
    private LinearLayout ll21Hour;
    private LinearLayout ll24Hour;

    private List<String> descList;

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
        initListeners();

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
        iconList = new ArrayList<>();

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

        iv3Hour = (ImageView) view.findViewById(R.id.iv_3_hour);
        iconList.add(iv3Hour);
        iv6Hour = (ImageView) view.findViewById(R.id.iv_6_hour);
        iconList.add(iv6Hour);
        iv9Hour = (ImageView) view.findViewById(R.id.iv_9_hour);
        iconList.add(iv9Hour);
        iv12Hour = (ImageView) view.findViewById(R.id.iv_12_hour);
        iconList.add(iv12Hour);
        iv15Hour = (ImageView) view.findViewById(R.id.iv_15_hour);
        iconList.add(iv15Hour);
        iv18Hour = (ImageView) view.findViewById(R.id.iv_18_hour);
        iconList.add(iv18Hour);
        iv21Hour = (ImageView) view.findViewById(R.id.iv_21_hour);
        iconList.add(iv21Hour);
        iv24Hour = (ImageView) view.findViewById(R.id.iv_24_hour);
        iconList.add(iv24Hour);

        ll3Hour = (LinearLayout) view.findViewById(R.id.ll_3_hour);
        ll3Hour.setBackgroundColor(getResources().getColor(R.color.selectedColor));
        ll6Hour = (LinearLayout) view.findViewById(R.id.ll_6_hour);
        ll9Hour = (LinearLayout) view.findViewById(R.id.ll_9_hour);
        ll12Hour = (LinearLayout) view.findViewById(R.id.ll_12_hour);
        ll15Hour = (LinearLayout) view.findViewById(R.id.ll_15_hour);
        ll18Hour = (LinearLayout) view.findViewById(R.id.ll_18_hour);
        ll21Hour = (LinearLayout) view.findViewById(R.id.ll_21_hour);
        ll24Hour = (LinearLayout) view.findViewById(R.id.ll_24_hour);
    }

    private void initListeners() {

        if (descList!=null) {
            ll3Hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLayoutClick(v);
                    tvOneDayDesc.setText(descList.get(0));
                }
            });

            ll6Hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLayoutClick(v);
                    tvOneDayDesc.setText(descList.get(1));
                }
            });

            ll9Hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLayoutClick(v);
                    tvOneDayDesc.setText(descList.get(2));
                }
            });

            ll12Hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLayoutClick(v);
                    tvOneDayDesc.setText(descList.get(3));
                }
            });

            ll15Hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLayoutClick(v);
                    tvOneDayDesc.setText(descList.get(4));
                }
            });

            ll18Hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLayoutClick(v);
                    tvOneDayDesc.setText(descList.get(5));
                }
            });

            ll21Hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLayoutClick(v);
                    tvOneDayDesc.setText(descList.get(6));
                }
            });

            ll24Hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLayoutClick(v);
                    tvOneDayDesc.setText(descList.get(7));
                }
            });
        }

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
                tvOneDayDesc.setText(descList.get(0));
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtils.showToast(getActivity(), "Network Error! Please try again later!");
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
            descList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray list = jsonObject.getJSONArray("list");
            for (int i = 0; i < 8; i++) {
                JSONObject jsonItem = (JSONObject) list.get(i);
                JSONObject main = jsonItem.getJSONObject("main");
                String temp = main.getString("temp");
                int temperature = (int) NumberHelper.round(Double.parseDouble(temp), 0);
                JSONArray weather = jsonItem.getJSONArray("weather");
                JSONObject weatherItem = (JSONObject) weather.get(0);
                String description = weatherItem.getString("description");
                descList.add(description);
                String icon = weatherItem.getString("icon");
                String dt_txt = jsonItem.getString("dt_txt");
                String[] split = dt_txt.split(" ");
                String time = split[1];
                time = time.substring(0, 5);
                System.out.println(temp + " / " + description + " / " + time + "\n");
                timeList.get(i).setText(time);
                tempList.get(i).setText(temperature + "°");
                x.image().bind(iconList.get(i), GlobalConstant.WEATHER_ICON_PATH + icon + ".png");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkLayoutClick(View v) {
        ll3Hour.setBackgroundColor(getResources().getColor(R.color.mainColor));
        ll6Hour.setBackgroundColor(getResources().getColor(R.color.mainColor));
        ll9Hour.setBackgroundColor(getResources().getColor(R.color.mainColor));
        ll12Hour.setBackgroundColor(getResources().getColor(R.color.mainColor));
        ll15Hour.setBackgroundColor(getResources().getColor(R.color.mainColor));
        ll18Hour.setBackgroundColor(getResources().getColor(R.color.mainColor));
        ll21Hour.setBackgroundColor(getResources().getColor(R.color.mainColor));
        ll24Hour.setBackgroundColor(getResources().getColor(R.color.mainColor));
        v.setBackgroundColor(getResources().getColor(R.color.selectedColor));
    }


}
