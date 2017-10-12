package com.beanlife.ord;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vivienhuang on 2017/10/8.
 * 訂單頁面
 */

public class MemberOrderFragment extends Fragment {
    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private OrdPagerAdapter adapter;
    private List<Fragment> fragmentList ;
    private CommonTask retriveOrdTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.order_fragment_with_tab, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.my_ord_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) view.findViewById(R.id.my_ord_viewpager);

        adapter = new OrdPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

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

    private class  OrdPagerAdapter extends FragmentStatePagerAdapter {

        int nNumOfTabs;
        FragmentManager fm;
        public OrdPagerAdapter(FragmentManager fm, int nNumOfTabs)
        {
            super(fm);
            this.nNumOfTabs=nNumOfTabs;
        }
        @Override
        public Fragment getItem(int position) {
            List<OrdVO> ordVOList = getOrdVOList();
            switch(position)
            {
                case 0:
                    OrderFragment tab1 = new OrderFragment(getStatList(ordVOList,"未付款"),"未付款");
                    return tab1;

                case 1:
                    OrderFragment tab2 = new OrderFragment(getStatList(ordVOList,"已付款"),"已付款");
                    return tab2;

                case 2:
                    OrderFragment tab3 = new OrderFragment(getStatList(ordVOList,"已出貨"),"已出貨");
                    return tab3;

                case 3:
                    OrderFragment tab4 = new OrderFragment(getStatList(ordVOList,"已取消"),"已取消");
                    return tab4;

                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            return nNumOfTabs;
        }
    }

    List<OrdVO> getOrdVOList(){
        String retrieveOrdString = "";

        //*****取得登入會員帳號*****
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        //沒有登入回傳noLogIn
        String mem_ac = loginState.getString("userAc", "noLogIn");

        if(networkConnected()){
            retriveOrdTask = (CommonTask)new CommonTask().execute(Common.ORD_URL, "getOrdByMem_ac", "mem_ac", mem_ac);

            try {
                retrieveOrdString = retriveOrdTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrdVO>>(){}.getType();
        return gson.fromJson(retrieveOrdString, listType);
    }

    List<OrdVO> getStatList(List<OrdVO> ordList, String ordState){
        List<OrdVO> ordStatList = new ArrayList<OrdVO>();
        for(OrdVO ordVO : ordList){
            if(ordVO.getOrd_stat().equals(ordState)){
                ordStatList.add(ordVO);
            }
        }
        return ordStatList;
    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
