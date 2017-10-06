package com.beanlife.prod;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beanlife.Common;
import com.beanlife.CommonRetrieveTask;
import com.beanlife.R;
import com.beanlife.RetrieveStoreTask;
import com.beanlife.review.ReviewVO;
import com.beanlife.store.StoreVO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Vivien on 2017/9/7.
 * 商品內頁 有TAB版本
 */

public class ProductWithTab extends Fragment {
    private ProdVO prod;
    private StoreVO store;
    private ReviewVO review;
    private  List<ReviewVO> reviewVOList;
    private RetrieveStoreTask retrieveStoreTask;
    private final static String TAG = "ProductWithTab";
    private Menu menu;


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            prod = (ProdVO) getArguments().getSerializable("prod");
            store = getStore(prod.getStore_no());
            reviewVOList = getReview(prod.getProd_no());

            View view = inflater.inflate(R.layout.product_page_w_tab, container, false);
            TabLayout tabLayout = (TabLayout) view.findViewById(R.id.prod_page_tab);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            final ViewPager viewPager = (ViewPager) view.findViewById(R.id.prod_tab_viewpager);
            final ProdPagerAdapter adapter =
                    new ProdPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), prod, store, reviewVOList);

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
        setHasOptionsMenu(true);
    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private StoreVO getStore(String store_no){
        StoreVO storeVO = new StoreVO();
        if(networkConnected()){
            retrieveStoreTask = (RetrieveStoreTask) new RetrieveStoreTask("getOneStore", store_no).execute(Common.STORE_URL);

            try {
                storeVO = (StoreVO) retrieveStoreTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Network Not Connect");
        }
        return storeVO;
    }

    private List<ReviewVO> getReview(String prod_no){
        List<ReviewVO> reviewList = new ArrayList<>();
//        ReviewVO reviewVO = new ReviewVO();
        if(networkConnected()){
            CommonRetrieveTask retrieveReviewTask =
                    (CommonRetrieveTask) new CommonRetrieveTask("getProdReview", "prod_no", prod_no)
                    .execute(Common.REVIEW_URL);
            Log.d("Review", retrieveReviewTask.toString());

            try {
                reviewList = (List) retrieveReviewTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Network Not Connect");
        }
        return reviewList;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        //顯示放大鏡
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_shopping_car).setVisible(isLogIn());
    }

    @Override
    public void onPause() {
        super.onPause();
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_shopping_car).setVisible(false);
    }

    private boolean isLogIn(){
        boolean isLogIn;
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        isLogIn = loginState.getBoolean("login", false);
        return isLogIn;
    }

}
