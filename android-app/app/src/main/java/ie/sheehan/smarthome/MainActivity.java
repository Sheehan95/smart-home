package ie.sheehan.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ie.sheehan.smarthome.adapter.TabPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Temperature");
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_temperature));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_security));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_stock));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_settings));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case TabPagerAdapter.TAB_TEMPERATURE:
                        toolbar.setTitle("Temperature");
                        break;
                    case TabPagerAdapter.TAB_SECURITY:
                        toolbar.setTitle("Security");
                        break;
                    case TabPagerAdapter.TAB_STOCK:
                        toolbar.setTitle("Stock");
                        break;
                    case TabPagerAdapter.TAB_SETTINGS:
                        toolbar.setTitle("Settings");
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

    public void openGraphActivity(View view) {
        startActivity(new Intent(this, ChartActivity.class));
    }

}
