package ie.sheehan.smarthome.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ie.sheehan.smarthome.fragment.SecurityFragment;
import ie.sheehan.smarthome.fragment.SettingsFragment;
import ie.sheehan.smarthome.fragment.StockFragment;
import ie.sheehan.smarthome.fragment.TemperatureFragment;


public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public static final int TAB_TEMPERATURE = 0;
    public static final int TAB_SECURITY = 1;
    public static final int TAB_STOCK = 2;
    public static final int TAB_SETTINGS = 3;


    public TabPagerAdapter(FragmentManager manager, int tabCount) {
        super(manager);
        this.tabCount = tabCount;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_TEMPERATURE:
                return new TemperatureFragment();
            case TAB_SECURITY:
                return new SecurityFragment();
            case TAB_STOCK:
                return new StockFragment();
            case TAB_SETTINGS:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
