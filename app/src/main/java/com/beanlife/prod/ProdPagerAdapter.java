package com.beanlife.prod;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.beanlife.review.ReviewFragment;
import com.beanlife.review.ReviewVO;
import com.beanlife.store.StorePageFragment;
import com.beanlife.store.StoreVO;

import java.util.List;

/**
 * Created by Java on 2017/9/11.
 */

class ProdPagerAdapter extends FragmentStatePagerAdapter {

    ProdVO prodVO;
    StoreVO storeVO;
    ReviewVO reviewVO;
    List<ReviewVO> reviewVOList;
    int nNumOfTabs;

    public ProdPagerAdapter(FragmentManager fm, int nNumOfTabs, ProdVO prodVO, StoreVO storeVO, List<ReviewVO> reviewVOList)
    {
        super(fm);
        this.nNumOfTabs=nNumOfTabs;
        this.prodVO = prodVO;
        this.storeVO = storeVO;
        this.reviewVOList = reviewVOList;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                ProductFragment productFragmentTab = new ProductFragment();
                productFragmentTab.getProdVO(prodVO);
                productFragmentTab.getStoreName(storeVO.getStore_name());
                return productFragmentTab;

            case 1:
                ReviewFragment commentFragmentTab = new ReviewFragment();
                commentFragmentTab.getReviewVOList(reviewVOList);
                return commentFragmentTab;

            case 2:
                StorePageFragment storePageFragmentTab = new StorePageFragment ();
                storePageFragmentTab.getStoreVO(storeVO);
                return storePageFragmentTab;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return nNumOfTabs;
    }
}
