package ie.sheehan.smarthome;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ie.sheehan.smarthome.adapter.TabPagerAdapter;
import ie.sheehan.smarthome.service.IntrusionService;

/**
 * Main Activity.
 */
public class MainActivity extends AppCompatActivity {

    private TabPagerAdapter adapter;



    // ============================================================================================
    // ACTIVITY LIFECYCLE METHODS
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (! isServiceRunning(IntrusionService.class)) {
            startService(new Intent(this, IntrusionService.class));
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_environment_fragment);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_temperature));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_security));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_stock));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_settings));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case TabPagerAdapter.TAB_TEMPERATURE:
                        toolbar.setTitle(R.string.title_environment_fragment);
                        break;
                    case TabPagerAdapter.TAB_SECURITY:
                        toolbar.setTitle(R.string.title_security_fragment);
                        break;
                    case TabPagerAdapter.TAB_STOCK:
                        toolbar.setTitle(R.string.title_stock_fragment);
                        break;
                    case TabPagerAdapter.TAB_SETTINGS:
                        toolbar.setTitle(R.string.title_settings_fragment);
                        break;
                    default:
                        toolbar.setTitle(R.string.app_name);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }



    // ============================================================================================
    // DECLARING LISTENER METHODS
    // ============================================================================================
    public void openGraphActivity(View view) {
        adapter.environmentFragment.openChart();
    }

    public void showFromDatePickerDialog(View view) {
        adapter.environmentFragment.openSetFromDateDialog();
    }

    public void showToDatePickerDialog(View view) {
        adapter.environmentFragment.openSetToDateDialog();
    }

    public void openCameraActivity(View view) {
        adapter.securityFragment.openCamera();
    }

    public void openIntrusionActivity(View view) {
        adapter.securityFragment.openIntrusionView();
    }

    public void openScaleCalibrationActivity(View view) {
        adapter.stockFragment.openScaleCalibrationActivity();
    }

}
