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

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.fragment.CurrentWeatherFragment;
import uk.me.feixie.coolweather.fragment.OneDayWeatherFragment;
import uk.me.feixie.coolweather.fragment.WeatherForecastFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager vpMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            list.add(new CurrentWeatherFragment());
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
