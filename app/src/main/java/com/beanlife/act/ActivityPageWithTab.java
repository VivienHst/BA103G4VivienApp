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
import java.util.List;


/**
 * Created by Vivien on 2017/9/3.
 * 會員活動內頁
*/
public class ActivityPageWithTab extends Fragment {
    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ActPagerAdapter adapter;
    private List<Fragment> fragmentList ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.my_act_tab, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.my_act_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) view.findViewById(R.id.my_act_viewpager);

        adapter = new ActPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(3);

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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class  ActPagerAdapter extends FragmentStatePagerAdapter {

        int nNumOfTabs;
        FragmentManager fm;
        public ActPagerAdapter(FragmentManager fm, int nNumOfTabs)
        {
            super(fm);
            this.nNumOfTabs=nNumOfTabs;
        }
        @Override
        public Fragment getItem(int position) {
            //return fragmentList.get(position);
//
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
