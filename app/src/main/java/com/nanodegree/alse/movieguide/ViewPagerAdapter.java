package com.nanodegree.alse.movieguide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by aharyadi on 5/23/16.
 */
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
          /*  case 2:
                TrailerFragment tab3 = new TrailerFragment();
                return tab3;*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
