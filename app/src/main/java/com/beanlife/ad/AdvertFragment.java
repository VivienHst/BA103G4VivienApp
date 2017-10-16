package com.beanlife.ad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.act.ActivityPageFragment;
import com.beanlife.prod.ProdVO;
import com.beanlife.prod.ProductWithTab;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by vivienhuang on 2017/10/7.
 */

public class AdvertFragment extends Fragment {
    private View view;
    private AdVO adVO;
    private ImageView adIv;
    private TextView adInfo;
    private CommonTask retrieveProdTask;

    public AdvertFragment(AdVO adVO){
        this.adVO = adVO;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.advert_fragment, container, false);
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

        adIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ProductWithTab();
                Bundle bundle = new Bundle();
                bundle.putSerializable("prod", getOneProd());
                fragment.setArguments(bundle);
                Fragment pFragment = getParentFragment();
                FragmentManager fragmentManager = pFragment.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    ProdVO getOneProd(){
        String prodVOString = "";
        if(Common.networkConnected(getActivity())){

            retrieveProdTask = (CommonTask) new CommonTask().execute(Common.PROD_URL, "getOneProd",
                    "prod_no", adVO.getProd_no());

            try {
                prodVOString = retrieveProdTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ProdVO>(){}.getType();
            return  gson.fromJson(prodVOString, listType);
        } else {
            return null;
        }
    }
}
