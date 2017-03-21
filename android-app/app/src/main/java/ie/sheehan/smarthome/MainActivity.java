package ie.sheehan.smarthome;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ie.sheehan.smarthome.adapter.TabPagerAdapter;

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



    // ============================================================================================
    // DECLARING LISTENER METHODS
    // ============================================================================================
    public void openGraphActivity(View view) {
        adapter.temperatureFragment.openChart();
    }

    public void showFromDatePickerDialog(View view) {
        adapter.temperatureFragment.openSetFromDateDialog();
    }

    public void showToDatePickerDialog(View view) {
        adapter.temperatureFragment.openSetToDateDialog();
    }

}
