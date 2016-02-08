package uk.me.feixie.coolweather.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.model.DayWeather;
import uk.me.feixie.coolweather.util.DividerItemDecoration;
import uk.me.feixie.coolweather.util.GlobalConstant;
import uk.me.feixie.coolweather.util.NumberHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherForecastFragment extends Fragment {

    private RecyclerView rvWeatherForecast;
    private SharedPreferences mSharedPreferences;
    private List<DayWeather> mDayWeatherList;
    private MyAdapter mAdapter;


    public WeatherForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initData();
        initViews(view);
        return view;
    }

    private void initData() {
        x.Ext.init(getActivity().getApplication());
        x.Ext.setDebug(true);
        String current_city = mSharedPreferences.getString("current_city", "");
        if (!TextUtils.isEmpty(current_city)) {
            updateFromWeb(current_city);
        }
    }

    private void initViews(View view) {
        rvWeatherForecast = (RecyclerView) view.findViewById(R.id.rvWeatherForecast);
        rvWeatherForecast.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvWeatherForecast.setHasFixedSize(true);
        mAdapter = new MyAdapter();
        rvWeatherForecast.setAdapter(mAdapter);
//        rvWeatherForecast.addItemDecoration(new DividerItemDecoration(getActivity()));

    }

    private void updateFromWeb(String cityName) {
        RequestParams url = new RequestParams(GlobalConstant.WEATHER_FORECAST_SERVER + cityName + GlobalConstant.OPEN_API_KEY + GlobalConstant.UNIT_CELSIUS+"&cnt=7");
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
                mAdapter.notifyDataSetChanged();
//                System.out.println(mDayWeatherList.toString());
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
            mDayWeatherList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray list = jsonObject.getJSONArray("list");
//            System.out.println(list.length());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            for (int i = 0; i < list.length(); i++) {

                DayWeather dayWeather = new DayWeather();
                dayWeather.setId(i);

                if (dayOfWeek+i>0 && dayOfWeek+i<8) {
                    String day = checkDayOfWeek(dayOfWeek + i);
                    dayWeather.setDayOfWeek(day);

                } else {
                    String day = checkDayOfWeek(dayOfWeek + i - 7);
                    dayWeather.setDayOfWeek(day);
                }

                JSONObject jsonItem = (JSONObject) list.get(i);
                JSONObject temp = jsonItem.getJSONObject("temp");
                String min = temp.getString("min");
                String max = temp.getString("max");
                int minTemp = (int) NumberHelper.round(Double.parseDouble(min),0);
                dayWeather.setMinTemperature(String.valueOf(minTemp));
                int maxTemp = (int) NumberHelper.round(Double.parseDouble(max),0);
                dayWeather.setMaxTemperature(String.valueOf(maxTemp));

                JSONArray weather = jsonItem.getJSONArray("weather");
                JSONObject weatherItem = (JSONObject) weather.get(0);
                String main = weatherItem.getString("main");
                dayWeather.setWeatherMain(main);
                String description = weatherItem.getString("description");
                dayWeather.setWeatherDescription(description);
                String icon = weatherItem.getString("icon");
                dayWeather.setWeatherIcon(icon);
                mDayWeatherList.add(dayWeather);
//                System.out.println(temp + " / " + description + " / " + time + "\n");
//                timeList.get(i).setText(time);
//                tempList.get(i).setText(temperature+"°");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String checkDayOfWeek(int dayOfWeek) {

        if (dayOfWeek== Calendar.MONDAY) {
            return "Monday";
        }
        if (dayOfWeek==Calendar.TUESDAY) {
            return "Tuesday";
        }
        if (dayOfWeek==Calendar.WEDNESDAY) {
            return "Wednesday";
        }
        if (dayOfWeek==Calendar.THURSDAY) {
            return "Thursday";
        }
        if (dayOfWeek==Calendar.FRIDAY) {
            return "Friday";
        }
        if (dayOfWeek==Calendar.SATURDAY) {
            return "Saturday";
        }
        if (dayOfWeek==Calendar.SUNDAY) {
            return "Sunday";
        }

        return null;
    }


    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_rv_day_forecast,null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            DayWeather dayWeather = mDayWeatherList.get(position);
            holder.tvDayOfWeek.setText(dayWeather.getDayOfWeek());
            holder.tvDayWeatherDesc.setText(dayWeather.getWeatherDescription());
            x.image().bind(holder.ivWeatherIcon,GlobalConstant.WEATHER_ICON_PATH+dayWeather.getWeatherIcon()+".png");
            holder.tvDayMaxTemp.setText(dayWeather.getMaxTemperature()+"°");
            holder.tvDayMinTemp.setText(dayWeather.getMinTemperature()+"°");
        }

        @Override
        public int getItemCount() {
            if (mDayWeatherList!=null) {
                return mDayWeatherList.size();
            } else {
                return 0;
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDayOfWeek;
        public TextView tvDayWeatherDesc;
        public ImageView ivWeatherIcon;
        public TextView tvDayMaxTemp;
        public TextView tvDayMinTemp;
        public LinearLayout llDayForecast;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvDayOfWeek = (TextView) itemView.findViewById(R.id.tvDayOfWeek);
            tvDayWeatherDesc = (TextView) itemView.findViewById(R.id.tvDayWeatherDesc);
            ivWeatherIcon = (ImageView) itemView.findViewById(R.id.ivWeatherIcon);
            tvDayMaxTemp = (TextView) itemView.findViewById(R.id.tvDayMaxTemp);
            tvDayMinTemp = (TextView) itemView.findViewById(R.id.tvDayMinTemp);

            llDayForecast = (LinearLayout) itemView.findViewById(R.id.llDayForecast);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.bottomMargin = 5;
            llDayForecast.setLayoutParams(params);
        }
    }

}
