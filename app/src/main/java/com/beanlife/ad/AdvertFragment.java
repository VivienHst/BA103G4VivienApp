package com.beanlife.ad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;

/**
 * Created by vivienhuang on 2017/10/7.
 */

public class AdvertFragment extends Fragment {
    View view;
    String ad_no;
    AdVO adVO;
    ImageView adIv;
    TextView adInfo;

    public AdvertFragment(AdVO adVO){
        this.adVO = adVO;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.advert_fragment, container, false);
//        adIv = (ImageView) view.findViewById(R.id.ad_img);
//
//        new GetImageByPkTask(Common.AD_URL,"ad_no", adVO.getAd_no(), 800, adIv).execute();
        findView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        adIv = (ImageView) view.findViewById(R.id.ad_img);
        adInfo = (TextView) view.findViewById((R.id.ad_info_tv));

        adInfo.setText(adVO.getAd_title());
        new GetImageByPkTask(Common.AD_URL,"ad_no", adVO.getAd_no(), 800, adIv).execute();
    }
}
