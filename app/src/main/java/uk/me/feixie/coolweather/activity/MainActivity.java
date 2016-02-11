package uk.me.feixie.coolweather.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.fragment.CurrentWeatherFragment;
import uk.me.feixie.coolweather.fragment.LocationFragment;
import uk.me.feixie.coolweather.fragment.OneDayWeatherFragment;
import uk.me.feixie.coolweather.fragment.WeatherForecastFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager vpMain;
    private MenuItem mItemRefresh;
    private MenuItem mItemAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        TextView textView = new TextView(this);
        textView.setText("London");
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(20);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("textview clicked");
            }
        });
        supportActionBar.setCustomView(textView);
//        supportActionBar.setHomeActionContentDescription("London");
        supportActionBar.setDisplayShowCustomEnabled(true);
    }

    private void initViews() {

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        vpMain = (ViewPager) findViewById(R.id.vpMain);
        MyPagerAdapter adapter = new MyPagerAdapter(supportFragmentManager);
        vpMain.setAdapter(adapter);
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
            System.out.println("refresh clicked");
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            System.out.println("setting");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ArrayList<Fragment> list = new ArrayList();
            list.add(new LocationFragment());
            list.add(new CurrentWeatherFragment());
            list.add(new OneDayWeatherFragment());
            list.add(new WeatherForecastFragment());
            return list.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
