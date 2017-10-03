package com.beanlife.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.beanlife.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by vivienhuang on 2017/10/2.
 */

public class MemberHostActivityContFragment  extends Fragment {
    View view;
    FrameLayout part_container;
    ImageView actCont, actQRCode, actClock;
    ActVO actVO;
    String mem_ac;


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
        mem_ac = (String) getArguments().getSerializable("mem_ac");


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
                switchFragment(new MemberHostActivityScanFragment());
            }
        });

        actClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(new MemberHostActivityListFragment());
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
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mem_act_part_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
