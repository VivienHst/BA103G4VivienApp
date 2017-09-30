package com.beanlife.act;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Vivien on 2017/9/3.
 * 需要改名，設定會員活動頁面
 */

public class ActPagerAdapter extends FragmentStatePagerAdapter {

    int nNumOfTabs;
    public ActPagerAdapter(FragmentManager fm, int nNumOfTabs)
    {
        super(fm);
        this.nNumOfTabs=nNumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                MemberFollowActivityFragment tab1 = new MemberFollowActivityFragment();
                return tab1;

            case 1:
                MemberPartiActivityFragment tab2 = new MemberPartiActivityFragment ();
                return tab2;

            case 2:
                MemberPartiActivityFragment tab3 = new MemberPartiActivityFragment ();
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return nNumOfTabs;
    }
}
