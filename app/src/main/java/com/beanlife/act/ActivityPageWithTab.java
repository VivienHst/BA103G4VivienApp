package com.beanlife.act;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beanlife.R;

/**
 * Created by Vivien on 2017/9/3.
 */

public class ActivityPageWithTab extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.my_act_tab, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.my_act_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.my_act_viewpager);
        final ActPagerAdapter adapter = new ActPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class  ActPagerAdapter extends FragmentStatePagerAdapter {

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
                    MemberHostActivityFragment tab3 = new MemberHostActivityFragment ();
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

}
