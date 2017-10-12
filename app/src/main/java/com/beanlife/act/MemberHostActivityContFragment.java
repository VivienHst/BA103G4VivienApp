package com.beanlife.act;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vivienhuang on 2017/10/2.
 */

public class MemberHostActivityContFragment  extends Fragment {
    private View view;
    private FrameLayout part_container;
    private ImageView actCont, actQRCode, actClock;
    private ActVO actVO;
    private String mem_ac;

    MemberHostActivityContFragment(String mem_ac){
        this.mem_ac = mem_ac;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.member_activity_content_fragment, container, false);
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        actVO = new ActVO();
        actVO = (ActVO) getArguments().getSerializable("actVO");



        part_container = (FrameLayout)view.findViewById(R.id.mem_act_part_container);
        actCont = (ImageView) view.findViewById(R.id.mem_act_cont);
        actQRCode = (ImageView)view.findViewById(R.id.mem_act_qrcode);
        actClock =(ImageView) view.findViewById(R.id.mem_act_clock);
        //prodNameTv.setText(getArguments().getSerializable("prod_name").toString());
        actClock.setImageResource(R.drawable.check_list01);
        actQRCode.setImageResource(R.drawable.qr_code_scan01);
        switchFragment(new ActivityPageFragment());


        actCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(new ActivityPageFragment());
            }
        });

        actQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //存入偏好設定
                SharedPreferences loginState = getActivity().getSharedPreferences(Common.SCAN_STATE, MODE_PRIVATE);
                loginState.edit().putString("act_no", actVO.getAct_no()).apply();
                switchFragment(new MemberHostActivityScanFragment());
            }
        });

        actClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(new MemberHostActivityListFragment(mem_ac));
            }
        });

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        int resultCode = options.getInt("");
        IntentResult intentResult  = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    }

    private void switchFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("actVO", actVO);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mem_act_part_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}
