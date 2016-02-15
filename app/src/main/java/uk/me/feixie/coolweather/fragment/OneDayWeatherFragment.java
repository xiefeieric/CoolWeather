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
import java.util.List;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.model.HourWeather;
import uk.me.feixie.coolweather.util.GlobalConstant;
import uk.me.feixie.coolweather.util.NumberHelper;
import uk.me.feixie.coolweather.util.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class OneDayWeatherFragment extends Fragment {

    private int clickPosition;

    private TextView tvOneDayDesc;
    private RecyclerView rvHourForecast;
    private List<HourWeather> mHourWeatherList;

    private SharedPreferences mSharedPreferences;
    private HourAdapter mAdapter;

    public OneDayWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one_day_weather, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initData();
        initViews(view);

        return view;
    }

    private void initData() {

        x.Ext.init(getActivity().getApplication());
        x.Ext.setDebug(true);

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

    private void initViews(View view) {

        tvOneDayDesc = (TextView) view.findViewById(R.id.one_day_desc);
        rvHourForecast = (RecyclerView) view.findViewById(R.id.rvHourForecast);
        rvHourForecast.setHasFixedSize(true);
        rvHourForecast.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HourAdapter();
        rvHourForecast.setAdapter(mAdapter);
    }

    public void updateFromWeb(String cityName) {
        RequestParams url = new RequestParams(GlobalConstant.ONE_DAY_WEATHER_SERVER + cityName + GlobalConstant.OPEN_API_KEY + GlobalConstant.UNIT_CELSIUS);
//        System.out.println(url.toString());
        x.http().get(url, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
//                System.out.println(result);
                handleWeatherResponse(result);
                mAdapter.notifyDataSetChanged();
                tvOneDayDesc.setText(mHourWeatherList.get(0).getDescription());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                UIUtils.showToast(getContext(), "Network Error! Please try again later!");
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
            mHourWeatherList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray list = jsonObject.getJSONArray("list");

            for (int i = 0; i < 8; i++) {

                HourWeather hourWeather = new HourWeather();

                JSONObject jsonItem = (JSONObject) list.get(i);
                JSONObject main = jsonItem.getJSONObject("main");
                String temp = main.getString("temp");
                int temperature = (int) NumberHelper.round(Double.parseDouble(temp), 0);
                hourWeather.setTemperature(String.valueOf(temperature));

                JSONArray weather = jsonItem.getJSONArray("weather");
                JSONObject weatherItem = (JSONObject) weather.get(0);
                String description = weatherItem.getString("description");
                hourWeather.setDescription(description);

                String icon = weatherItem.getString("icon");
                hourWeather.setIcon(icon);

                String dt_txt = jsonItem.getString("dt_txt");
                String[] split = dt_txt.split(" ");
                String time = split[1];
                time = time.substring(0, 5);
                hourWeather.setTime(time);

                mHourWeatherList.add(hourWeather);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class HourAdapter extends RecyclerView.Adapter<HourViewHolder> {

        @Override
        public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(),R.layout.item_rv_hour_forecast,null);
            HourViewHolder hourViewHolder = new HourViewHolder(view);
            return hourViewHolder;
        }

        @Override
        public void onBindViewHolder(HourViewHolder holder, int position) {
            HourWeather hourWeather = mHourWeatherList.get(position);
            holder.tv_hour_time.setText(hourWeather.getTime());
            x.image().bind(holder.iv_hour,GlobalConstant.WEATHER_ICON_PATH + hourWeather.getIcon() + ".png");
            holder.tv_hour_temp.setText(hourWeather.getTemperature()+"°");
            if (position == clickPosition) {
                holder.llHourForecast.setBackgroundColor(getResources().getColor(R.color.selectedColor));
            } else {
                holder.llHourForecast.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }

        @Override
        public int getItemCount() {
            if (mHourWeatherList!=null) {
                return mHourWeatherList.size();
            }
            return 0;
        }
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_hour_time;
        public ImageView iv_hour;
        public TextView tv_hour_temp;
        public LinearLayout llHourForecast;

        public HourViewHolder(View itemView) {
            super(itemView);

            tv_hour_time = (TextView) itemView.findViewById(R.id.tv_hour_time);
            iv_hour = (ImageView) itemView.findViewById(R.id.iv_hour);
            tv_hour_temp = (TextView) itemView.findViewById(R.id.tv_hour_temp);

            llHourForecast = (LinearLayout) itemView.findViewById(R.id.llHourForecast);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            llHourForecast.setLayoutParams(params);
            llHourForecast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPosition = getAdapterPosition();
                    mAdapter.notifyDataSetChanged();
                    tvOneDayDesc.setText(mHourWeatherList.get(clickPosition).getDescription());
                }
            });
        }
    }

}
