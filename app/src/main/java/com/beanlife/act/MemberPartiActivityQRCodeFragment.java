package com.beanlife.act;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.R;
import com.beanlife.qrcode.Contents;
import com.beanlife.qrcode.QRCodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vivienhuang on 2017/10/1.
 */

public class MemberPartiActivityQRCodeFragment extends Fragment {
    private View view;
    private TextView partiActTv;
    private ImageView QRCodeIv;
    private String mem_ac;
    private ActVO actVO;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.member_activity_qrcode_fragment, container, false);
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        partiActTv = (TextView) view.findViewById(R.id.parti_act_tital);
        QRCodeIv = (ImageView) view.findViewById(R.id.act_check_qrcode_iv);
        actVO = new ActVO();
        actVO = (ActVO) getArguments().getSerializable("actVO");
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        mem_ac = loginState.getString("userAc", "noLogIn");

        partiActTv.setText(actVO.getAct_name());
        int dimension = getResources().getDisplayMetrics().widthPixels;
        final String act_pairString = actVO.getAct_no() + "," + mem_ac;
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(act_pairString, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), dimension);
        Bitmap bitmap = null;
        try {
            bitmap = qrCodeEncoder.encodeAsBitmap();
            QRCodeIv.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}
