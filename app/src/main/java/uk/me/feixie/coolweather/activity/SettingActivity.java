package uk.me.feixie.coolweather.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.me.feixie.coolweather.R;

public class SettingActivity extends AppCompatActivity {

    private LinearLayout llUnits;
    private TextView tvCTemp;
    private TextView tvFTemp;
    private boolean toggle;
    private Animation mScaleUp;
    private Animation mScaleDown;
    private SharedPreferences mSharedPreferences;

    public static final String TEMP_CELSIUS = "c_temp";
    public static final String TEMP_FAHRENHEIT = "f_temp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolbar();
        initViews();
        initListeners();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_left);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_backspace_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mScaleUp = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleUp.setDuration(500);
        mScaleUp.setFillAfter(true);

        mScaleDown = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleDown.setDuration(500);
        mScaleDown.setFillAfter(true);

        llUnits = (LinearLayout) findViewById(R.id.llUnits);
        tvFTemp = (TextView) findViewById(R.id.tvFTemp);
        tvCTemp = (TextView) findViewById(R.id.tvCTemp);

        String setting_temp = mSharedPreferences.getString("setting_temp", "");
        if (TextUtils.isEmpty(setting_temp)) {
            tvCTemp.setTextColor(getResources().getColor(android.R.color.black));
            tvCTemp.startAnimation(mScaleUp);
        } else {
            if (setting_temp.equalsIgnoreCase(TEMP_CELSIUS)) {
                tvCTemp.setTextColor(getResources().getColor(android.R.color.black));
                tvCTemp.startAnimation(mScaleUp);
                toggle = false;
            } else if (setting_temp.equalsIgnoreCase(TEMP_FAHRENHEIT)) {
                tvFTemp.setTextColor(getResources().getColor(android.R.color.black));
                tvFTemp.startAnimation(mScaleUp);
                toggle = true;
            }
        }

    }

    private void initListeners() {

        llUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTemp();
            }
        });

    }

    private void switchTemp() {
        if (toggle) {
            tvCTemp.setTextColor(getResources().getColor(android.R.color.black));
            tvCTemp.startAnimation(mScaleUp);
            tvFTemp.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvFTemp.startAnimation(mScaleDown);
            toggle = false;
            mSharedPreferences.edit().putString("setting_temp", TEMP_CELSIUS).apply();
        } else {
            tvCTemp.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvCTemp.startAnimation(mScaleDown);
            tvFTemp.setTextColor(getResources().getColor(android.R.color.black));
            tvFTemp.startAnimation(mScaleUp);
            toggle = true;
            mSharedPreferences.edit().putString("setting_temp", TEMP_FAHRENHEIT).apply();
        }
    }

}
