package com.beanlife;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Java on 2017/9/14.
 */

public class AdPagerAdapter extends PagerAdapter {
    public Context context;
    public ArrayList<ImageView> imgs;

    public AdPagerAdapter(Context context, ArrayList<ImageView> imgs) {
        this.context = context;
        this.imgs  = imgs;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }


}
