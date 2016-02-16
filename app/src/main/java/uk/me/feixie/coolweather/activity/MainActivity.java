package uk.me.feixie.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.db.CoolWeatherDB;
import uk.me.feixie.coolweather.fragment.CurrentWeatherFragment;
import uk.me.feixie.coolweather.fragment.LocationFragment;
import uk.me.feixie.coolweather.fragment.OneDayWeatherFragment;
import uk.me.feixie.coolweather.fragment.WeatherForecastFragment;
import uk.me.feixie.coolweather.model.City;
import uk.me.feixie.coolweather.util.UIUtils;

public class MainActivity extends AppCompatActivity {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private ViewPager vpMain;
    private MenuItem mItemRefresh;
    private MenuItem mItemAdd;
    private List<City> mCityList;
    private MyPagerAdapter mAdapter;
    private SharedPreferences mSharedPreferences;
    private TextView mTextView;
    private SparseArrayCompat<Fragment> mFragmentArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFragmentArray = new SparseArrayCompat<>();
        initToolbar();
        initViews();
        initListeners();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
//        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setTitle("");
        mTextView = new TextView(this);
        if (TextUtils.isEmpty(mSharedPreferences.getString("select_city",""))) {
            mTextView.setText(mSharedPreferences.getString("current_city",""));
        } else {
            mTextView.setText(mSharedPreferences.getString("select_city",""));
        }
        mTextView.setTextColor(getResources().getColor(R.color.white));
        mTextView.setTextSize(20);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println("textview clicked");
            }
        });
        supportActionBar.setCustomView(mTextView);
//        supportActionBar.setHomeActionContentDescription("London");
        supportActionBar.setDisplayShowCustomEnabled(true);
    }

    private void initViews() {

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        mFragmentArray.put(0,new LocationFragment());
        mFragmentArray.put(1,new CurrentWeatherFragment());
        mFragmentArray.put(2,new OneDayWeatherFragment());
        mFragmentArray.put(3,new WeatherForecastFragment());

        vpMain = (ViewPager) findViewById(R.id.vpMain);
        mAdapter = new MyPagerAdapter(supportFragmentManager);
        vpMain.setAdapter(mAdapter);
        vpMain.setCurrentItem(1);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.vpiMain);
        circlePageIndicator.setViewPager(vpMain,1);
    }

    private void initListeners() {
        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkCurrentPager(position);
                if (position==0) {
                    mTextView.setText("Location");
                } else {
                    if (TextUtils.isEmpty(mSharedPreferences.getString("select_city",""))) {
                        mTextView.setText(mSharedPreferences.getString("current_city",""));
                    } else {
                        mTextView.setText(mSharedPreferences.getString("select_city",""));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void checkCurrentPager(int position) {
        if (position==0) {
            mItemRefresh.setVisible(false);
            mItemAdd.setVisible(true);
        } else {
            mItemRefresh.setVisible(true);
            mItemAdd.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mItemRefresh = menu.findItem(R.id.action_refresh);
        mItemAdd = menu.findItem(R.id.action_add);
        checkCurrentPager(vpMain.getCurrentItem());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
//            System.out.println("refresh clicked");
            refreshWeather();
        }

        if (id == R.id.action_add) {
//            System.out.println("add clicked");
            addCity();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            System.out.println("setting");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshWeather() {

        String current_city = mSharedPreferences.getString("current_city", "");
        String select_city = mSharedPreferences.getString("select_city", "");

        CurrentWeatherFragment currentWeatherFragment = (CurrentWeatherFragment) mFragmentArray.get(1);
        OneDayWeatherFragment oneDayWeatherFragment = (OneDayWeatherFragment) mFragmentArray.get(2);
        WeatherForecastFragment weatherFragment = (WeatherForecastFragment) mFragmentArray.get(3);

        if (!TextUtils.isEmpty(current_city) && TextUtils.isEmpty(select_city)) {
            current_city = current_city.replaceAll("\\s+","");
            currentWeatherFragment.updateFromWeb(current_city);
            oneDayWeatherFragment.updateFromWeb(current_city);
            weatherFragment.updateFromWeb(current_city);
        } else {
            select_city = select_city.replaceAll("\\s+","");
            currentWeatherFragment.updateFromWeb(select_city);
            oneDayWeatherFragment.updateFromWeb(select_city);
            weatherFragment.updateFromWeb(select_city);
        }
    }

    private void addCity() {

        try {

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            UIUtils.showToast(this,e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            UIUtils.showToast(this,e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                City city = new City();
                String name = place.getName().toString();
                city.setName(name);
                String[] splitAddress = place.getAddress().toString().split(",");
                String country = splitAddress[splitAddress.length-1];
                city.setCountry(country);
                city.setLatitude(String.valueOf(place.getLatLng().latitude));
                city.setLongitude(String.valueOf(place.getLatLng().longitude));

                if (checkCityName(name)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("Location already added");
                    builder.setPositiveButton("OK",null);
                    builder.show();
                } else {

                    CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(MainActivity.this);
                    coolWeatherDB.saveCity(city);

                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                UIUtils.showToast(this,status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private boolean checkCityName(String cityName) {
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(this);
        mCityList = coolWeatherDB.queryAllCity();
        for (int i = 0; i < mCityList.size(); i++) {
            if (cityName.equalsIgnoreCase(mCityList.get(i).getName())) {
                return true;
            }
        }
        return false;
    }


    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentArray.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }


    }

}
