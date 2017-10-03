package com.beanlife.mem;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.act.Fo_actVO;
import com.beanlife.checkout.CheckoutFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Java on 2017/9/4.
 */

public class MemberCenterFragment extends Fragment {
    private View view;
    private ImageView centerMemIv,editIv;
    private TextView centerMemAcTv, centerMemLvTv, centerMemNameTv, centerMemEmailTv, centerMemPhoneTv, centerMemAddTv,
            centerMemPtTv, centerMemDateTv, centerMemRegTv, centerMemProcTv, centerMemRoastTv;
    private MemVO memVO;
    private String mem_ac;
    private CommonTask retrieveMemVO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.member_center_fragment, container, false);
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        centerMemIv = (ImageView) view.findViewById(R.id.center_mem_img);
        editIv = (ImageView) view.findViewById(R.id.mem_center_edit_iv);
        centerMemAcTv = (TextView) view.findViewById(R.id.center_mem_ac_tv);
        centerMemLvTv = (TextView) view.findViewById(R.id.center_mem_lv_tv);
        centerMemNameTv = (TextView) view.findViewById(R.id.center_mem_name_tv);
        centerMemEmailTv = (TextView) view.findViewById(R.id.center_mem_email_tv);
        centerMemPhoneTv = (TextView) view.findViewById(R.id.center_mem_phone_tv);
        centerMemAddTv = (TextView) view.findViewById(R.id.center_mem_add_tv);
        centerMemPtTv = (TextView) view.findViewById(R.id.center_mem_pt_tv);
        centerMemDateTv = (TextView) view.findViewById(R.id.center_mem_date_tv);
        centerMemRegTv = (TextView) view.findViewById(R.id.center_mem_reg_tv);
        centerMemProcTv = (TextView) view.findViewById(R.id.center_mem_proc_tv);
        centerMemRoastTv = (TextView) view.findViewById(R.id.center_mem_roast_tv);

        memVO = new MemVO();
        memVO = getMemVO();

        centerMemAcTv.setText(memVO.getMem_ac());
        centerMemLvTv.setText(memVO.getGrade_no().toString());
        centerMemNameTv.setText(memVO.getMem_lname() + memVO.getMem_fname());
        centerMemEmailTv.setText(memVO.getMem_email());
        centerMemPhoneTv.setText(memVO.getMem_phone());
        centerMemAddTv.setText(memVO.getMem_add());
        centerMemPtTv.setText(memVO.getMem_pt() + " / " + memVO.getMem_total_pt());
        centerMemDateTv.setText(memVO.getMem_reg_date());
        String likeSet = memVO.getMem_set();
        String[] likeSetToken = likeSet.split(",");
        centerMemRegTv.setText(likeSetToken[0]);
        centerMemProcTv.setText(likeSetToken[1]);
        centerMemRoastTv.setText(likeSetToken[2]);

        new GetImageByPkTask(Common.MEM_URL, "mem_ac", memVO.getMem_ac(), 120, centerMemIv).execute();

        editIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MemberCenterEditFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("memVO", memVO);

                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    MemVO getMemVO(){
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        mem_ac = loginState.getString("userAc", "noLogIn");

        String memVOString = "";
        if(networkConnected()){
            retrieveMemVO = (CommonTask) new CommonTask().execute(Common.MEM_URL, "getOneMem",
                    "mem_ac", mem_ac);
            try {
                memVOString = retrieveMemVO.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        Gson gson = new Gson();
        Type listType = new TypeToken<MemVO>(){}.getType();
        return gson.fromJson(memVOString, listType);
    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

