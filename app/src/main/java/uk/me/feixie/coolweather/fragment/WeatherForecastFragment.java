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

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.util.GlobalConstant;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherForecastFragment extends Fragment {

    private RecyclerView rvWeatherForecast;
    private SharedPreferences mSharedPreferences;


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
        MyAdapter adapter = new MyAdapter();
        rvWeatherForecast.setAdapter(adapter);
    }

    private void updateFromWeb(String cityName) {
        RequestParams url = new RequestParams(GlobalConstant.ONE_DAY_WEATHER_SERVER + cityName + GlobalConstant.OPEN_API_KEY + GlobalConstant.UNIT_CELSIUS+"&cnt=7");
//        System.out.println(url.toString());
        x.http().get(url, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
//                System.out.println(result);
                return false;
            }

            @Override
            public void onSuccess(String result) {
                System.out.println(result);
//                handleWeatherResponse(result);
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


    public class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

}
