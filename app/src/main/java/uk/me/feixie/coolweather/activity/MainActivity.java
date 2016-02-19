package uk.me.feixie.coolweather.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.db.CoolWeatherDB;
import uk.me.feixie.coolweather.fragment.CurrentWeatherFragment;
import uk.me.feixie.coolweather.fragment.LocationFragment;
import uk.me.feixie.coolweather.fragment.OneDayWeatherFragment;
import uk.me.feixie.coolweather.fragment.WeatherForecastFragment;
import uk.me.feixie.coolweather.model.City;
import uk.me.feixie.coolweather.util.GlobalConstant;
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
        ShareSDK.initSDK(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFragmentArray = new SparseArrayCompat<>();
        initToolbar();
        initViews();
        initListeners();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshWeather();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
//        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setTitle("");
        mTextView = new TextView(this);
        if (TextUtils.isEmpty(mSharedPreferences.getString("select_city", ""))) {
            mTextView.setText(mSharedPreferences.getString("current_city", ""));
        } else {
            mTextView.setText(mSharedPreferences.getString("select_city", ""));
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
        mFragmentArray.put(0, new LocationFragment());
        mFragmentArray.put(1, new CurrentWeatherFragment());
        mFragmentArray.put(2, new OneDayWeatherFragment());
        mFragmentArray.put(3, new WeatherForecastFragment());

        vpMain = (ViewPager) findViewById(R.id.vpMain);
        mAdapter = new MyPagerAdapter(supportFragmentManager);
        vpMain.setAdapter(mAdapter);
        vpMain.setCurrentItem(1);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.vpiMain);
        circlePageIndicator.setViewPager(vpMain, 1);
    }

    private int pagePosition;

    private void initListeners() {

        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);

        Animation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new AccelerateInterpolator());
        rotate.setDuration(500);


        Animation scale = new ScaleAnimation(1.0f, 0.1f, 1.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(500);
        scale.setInterpolator(new LinearInterpolator());

        final AnimationSet animationSet = new AnimationSet(this, null);
        animationSet.addAnimation(rotate);
        animationSet.addAnimation(scale);

        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

//                checkCurrentPager(position);
                pagePosition = position;

                if (position == 0) {

                    mTextView.startAnimation(fadeOut);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mTextView.setText("Location");
                            mTextView.startAnimation(fadeIn);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    mItemRefresh.getActionView().startAnimation(animationSet);
                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            checkCurrentPager(pagePosition);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                } else if (position == 1) {
                    mTextView.startAnimation(fadeOut);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (TextUtils.isEmpty(mSharedPreferences.getString("select_city", ""))) {
                                mTextView.setText(mSharedPreferences.getString("current_city", ""));
                            } else {
                                mTextView.setText(mSharedPreferences.getString("select_city", ""));
                            }
                            mTextView.startAnimation(fadeIn);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    mItemAdd.getActionView().startAnimation(animationSet);
                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            checkCurrentPager(pagePosition);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vpMain.getCurrentItem() != 0) {
                    selectCity();
                }
            }
        });
    }

    private void selectCity() {
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(this);
        final List<City> cityList = coolWeatherDB.queryAllCity();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] items = new CharSequence[cityList.size()];
        for (int i = 0; i < cityList.size(); i++) {
            items[i] = cityList.get(i).getName() + ", " + cityList.get(i).getCountry();
        }
        builder.setTitle("Select Location");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                City city = cityList.get(which);
//                System.out.println(city.getName());
                mSharedPreferences.edit().putString("select_city", city.getName()).apply();
                mSharedPreferences.edit().putString("location_click_position", String.valueOf(which)).apply();
                mTextView.setText(city.getName());
                CurrentWeatherFragment currentWeatherFragment = (CurrentWeatherFragment) mFragmentArray.get(1);
                ImageView ivCurrentWeather = currentWeatherFragment.getIvCurrentWeather();
                if (ivCurrentWeather != null) {
                    ivCurrentWeather.clearAnimation();
                }
                refreshWeather();

            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    private void checkCurrentPager(int position) {
        if (position == 0) {
            mItemRefresh.getActionView().clearAnimation();
            mItemRefresh.setVisible(false);
            mItemRefresh.getActionView().setVisibility(View.GONE);
            mItemAdd.setVisible(true);
        } else {
            mItemRefresh.setVisible(true);
            mItemAdd.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        final Animation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setDuration(1000);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        mItemRefresh = menu.findItem(R.id.action_refresh);
        View view = View.inflate(this, R.layout.refresh_action_view, null);
        mItemRefresh.setActionView(view);
        mItemRefresh.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemRefresh.getActionView().startAnimation(rotate);
                refreshWeather();
            }
        });

        mItemAdd = menu.findItem(R.id.action_add);
        View addView = View.inflate(this, R.layout.add_action_view, null);
        mItemAdd.setActionView(addView);
        mItemAdd.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCity();
            }
        });

//        MenuItem itemSetting = menu.findItem(R.id.action_settings);
//        View viewSetting = View.inflate(this,R.layout.setting_action_view,null);
//        itemSetting.setActionView(viewSetting);


        checkCurrentPager(vpMain.getCurrentItem());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_refresh) {
//            System.out.println("refresh clicked");
//            refreshWeather();
//        }

//        if (id == R.id.action_add) {
//            System.out.println("add clicked");
//            addCity();
//        }

        if (id == R.id.action_apps) {
            Intent intent = new Intent(this, AppsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            return true;
        }

        if (id == R.id.action_share) {
            showShare();
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
            current_city = current_city.replaceAll("\\s+", "");
            currentWeatherFragment.updateFromWeb(current_city);
            oneDayWeatherFragment.updateFromWeb(current_city);
            weatherFragment.updateFromWeb(current_city);
        } else {
            select_city = select_city.replaceAll("\\s+", "");
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
            UIUtils.showToast(this, e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            UIUtils.showToast(this, e.getMessage());
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
                String country = splitAddress[splitAddress.length - 1];
                city.setCountry(country);
                city.setLatitude(String.valueOf(place.getLatLng().latitude));
                city.setLongitude(String.valueOf(place.getLatLng().longitude));
                city.setStatus(GlobalConstant.LOCATION_STATUS_INPUT);

                if (checkCityName(name)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("Location already added");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                } else {

                    CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(MainActivity.this);
                    coolWeatherDB.saveCity(city);

                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                UIUtils.showToast(this, status.getStatusMessage());
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

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        String applicationLabel = "App Share";
        try {
            ApplicationInfo applicationInfo = getApplication().getPackageManager().getApplicationInfo(getPackageName(), 0);
            applicationLabel = String.valueOf(getPackageManager().getApplicationLabel(applicationInfo));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        oks.setTitle(applicationLabel);

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("I would recommend this app for you." + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("https://play.google.com/store/apps/details?id=" + getPackageName());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("https://play.google.com/store/apps/details?id=" + getPackageName());
// 启动分享GUI
        oks.show(this);
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
