package com.beanlife.act;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Vivien on 2017/8/30.
 */

public class ActivityPagerAdapter extends FragmentStatePagerAdapter {
    int nNumOfTabs;
    public ActivityPagerAdapter(FragmentManager fm, int nNumOfTabs)
    {
        super(fm);
        this.nNumOfTabs=nNumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                ActivityFragment tab1=new ActivityFragment();
                return tab1;
            case 1:
                ActivityFragment tab2=new ActivityFragment();
                return tab2;
            case 2:
                ActivityFragment tab3=new ActivityFragment();
                return tab3;
        }
        return null;
    }

    @Override
    public int getCount() {
        return nNumOfTabs;
    }
}
