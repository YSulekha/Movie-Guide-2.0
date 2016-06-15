package com.nanodegree.alse.movieguide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                DetailActivityFragment tab1 = new DetailActivityFragment();
                return tab1;
            case 1:
                TabFragment tab2 = new TabFragment();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
