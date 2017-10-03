package com.beanlife.mem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.R;
import com.google.gson.Gson;

import static android.view.View.VISIBLE;

/**
 * Created by vivienhuang on 2017/10/3.
 */

public class MemberCenterEditFragment extends Fragment {

    private View view;
    private ImageView centerMemEdIv;
    private TextView centerMemAcTv, centerMemLvTv;
    private EditText centerMemPswEt, centerMemPswChkEt, centerMemLnameEt, centerMemFnameEt, centerMemEmailEt,
            centerMemPhoneEt, centerMemAddEt, centerMemRegEt, centerMemProcEt, centerMemRoastEt;
    private LinearLayout chkPswLl;
    private Button chkBtn, cancelBtn;
    private MemVO memVO;
    private String mem_ac;
    private CommonTask retrieveMemVO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.member_center_edit_fragment, container, false);
        memVO = (MemVO) getArguments().getSerializable("memVO");
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        centerMemAcTv = (TextView) view.findViewById(R.id.center_mem_ac_ed_tv);
        centerMemLvTv = (TextView) view.findViewById(R.id.center_mem_lv_ed_tv);
        centerMemEdIv = (ImageView) view.findViewById(R.id.center_mem_ed_img);
        centerMemPswEt = (EditText) view.findViewById(R.id.center_mem_psw_et);
        centerMemPswChkEt = (EditText) view.findViewById(R.id.center_mem_psw_chk_et);
        centerMemLnameEt = (EditText) view.findViewById(R.id.center_mem_lname_et);
        centerMemFnameEt = (EditText) view.findViewById(R.id.center_mem_fname_et);
        centerMemEmailEt = (EditText) view.findViewById(R.id.center_mem_email_et);
        centerMemPhoneEt = (EditText) view.findViewById(R.id.center_mem_phone_et);
        centerMemAddEt = (EditText) view.findViewById(R.id.center_mem_add_et);
        centerMemRegEt = (EditText) view.findViewById(R.id.center_mem_reg_et);
        centerMemProcEt = (EditText) view.findViewById(R.id.center_mem_proc_et);
        centerMemRoastEt = (EditText) view.findViewById(R.id.center_mem_roast_et);
        chkPswLl = (LinearLayout) view.findViewById(R.id.center_mem_psw_chk_ll);
        chkBtn = (Button) view.findViewById(R.id.mem_ed_chk_btn);
        cancelBtn = (Button) view.findViewById(R.id.mem_ed_cancel_btn);


        centerMemAcTv.setText(memVO.getMem_ac());
        centerMemLvTv.setText(memVO.getGrade_no());
        centerMemPswEt.setText(memVO.getMem_pwd());
        centerMemLnameEt.setText(memVO.getMem_lname());
        centerMemFnameEt.setText(memVO.getMem_fname());

        centerMemEmailEt.setText(memVO.getMem_email());
        centerMemPhoneEt.setText(memVO.getMem_phone());
        centerMemAddEt.setText(memVO.getMem_add());

        String likeSet = memVO.getMem_set();
        String[] likeSetToken = likeSet.split(",");
        centerMemRegEt.setText(likeSetToken[0]);
        centerMemProcEt.setText(likeSetToken[1]);
        centerMemRoastEt.setText(likeSetToken[2]);

        centerMemPswEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!memVO.getMem_pwd().equals(centerMemPswEt.getText().toString())) {
                    chkPswLl.setVisibility(VISIBLE);
                } else {
                    chkPswLl.setVisibility(View.GONE);
                }
            }
        });

        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMemVO();
                switchFragment(new MemberCenterFragment());
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(new MemberCenterFragment());
            }
        });
    }

    private void setMemVO(){
        memVO.setMem_pwd(centerMemPswEt.getText().toString());
        memVO.setMem_lname(centerMemLnameEt.getText().toString());
        memVO.setMem_fname(centerMemFnameEt.getText().toString());
        memVO.setMem_email(centerMemEmailEt.getText().toString());
        memVO.setMem_phone(centerMemPhoneEt.getText().toString());
        memVO.setMem_add(centerMemAddEt.getText().toString());
        memVO.setMem_set(centerMemRegEt.getText().toString() + "," + centerMemProcEt.getText().toString() +
                "," + centerMemRoastEt.getText().toString());

        Gson gson = new Gson();
        String memVOString = gson.toJson(memVO);
        retrieveMemVO =(CommonTask) new CommonTask().execute(Common.MEM_URL, "updateMem" , "memVO", memVOString);
    }

    private void switchFragment(Fragment fragment) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
