package ie.sheehan.smarthome.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ie.sheehan.smarthome.fragment.ScheduleFragment;
import ie.sheehan.smarthome.fragment.SecurityFragment;
import ie.sheehan.smarthome.fragment.SettingsFragment;
import ie.sheehan.smarthome.fragment.StockFragment;
import ie.sheehan.smarthome.fragment.EnvironmentFragment;

/**
 * An implementation of {@link FragmentStatePagerAdapter} for displaying {@link Fragment}s as tabs
 * in a single {@link android.app.Activity}.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public static final int TAB_TEMPERATURE = 0;
    public static final int TAB_SECURITY = 1;
    public static final int TAB_STOCK = 2;
    public static final int TAB_SCHEDULE = 3;
    public static final int TAB_SETTINGS = 4;

    public EnvironmentFragment environmentFragment = new EnvironmentFragment();
    public SecurityFragment securityFragment = new SecurityFragment();
    public StockFragment stockFragment = new StockFragment();
    public ScheduleFragment scheduleFragment = new ScheduleFragment();
    public SettingsFragment settingsFragment = new SettingsFragment();


    /**
     * Default constructor.
     *
     * @param manager for creating and launching the fragment
     * @param tabCount the number of tabs the adapter will manage
     */
    public TabPagerAdapter(FragmentManager manager, int tabCount) {
        super(manager);
        this.tabCount = tabCount;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_TEMPERATURE:
                return environmentFragment;
            case TAB_SECURITY:
                return securityFragment;
            case TAB_STOCK:
                return stockFragment;
            case TAB_SCHEDULE:
                return scheduleFragment;
            case TAB_SETTINGS:
                return settingsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
