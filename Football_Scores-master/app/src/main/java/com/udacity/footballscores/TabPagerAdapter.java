package com.udacity.footballscores;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rahall4405 on 3/13/16.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    int numberOfTabs;
    private MainScreenFragment mainScreenFragment;
    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
//        Configuration config = FootballApp.getAppContext().getResources().getConfiguration();
        Date fragmentDate;

        fragmentDate = new Date(System.currentTimeMillis() + ((position - 2) * 86400000));

        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");



        switch (position) {
            case 0:
                MainScreenFragment tab1 = new MainScreenFragment();
                tab1.setFragmentDate(mformat.format(fragmentDate),position);
                return tab1;
            case 1:
                MainScreenFragment tab2 = new MainScreenFragment();
                tab2.setFragmentDate(mformat.format(fragmentDate), position);
                return tab2;
            case 2:
                MainScreenFragment tab3 = new MainScreenFragment();
                tab3.setFragmentDate(mformat.format(fragmentDate), position);
                return tab3;

            case 3:
                MainScreenFragment tab4 = new MainScreenFragment();
                tab4.setFragmentDate(mformat.format(fragmentDate), position);
                return tab4;
            case 4:
                MainScreenFragment tab5 = new MainScreenFragment();
                tab5.setFragmentDate(mformat.format(fragmentDate), position);
                return tab5;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        MainScreenFragment f = (MainScreenFragment) object;
        if (f != null) {
            f.update();
        }
        return super.getItemPosition(object);
    }


}
