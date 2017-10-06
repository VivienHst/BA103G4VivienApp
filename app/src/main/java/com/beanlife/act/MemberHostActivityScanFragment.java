package com.beanlife.act;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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

public class MemberHostActivityScanFragment extends android.support.v4.app.Fragment {
    private View view;
    private TextView hostScanTv;
    private ImageView hostScanIv;
    private String mem_ac;
    private ActVO actVO;
    Button actQRCode;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.host_scan_result_fragment, container, false);
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        hostScanTv = (TextView) view.findViewById(R.id.host_scan_result_tv);
        hostScanIv =  (ImageView) view.findViewById(R.id.host_scan_result_iv);
        actQRCode = (Button) view.findViewById(R.id.act_host_scan_bt);
        actQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                // Set to true to enable saving the barcode image and sending its path in the result Intent.
                integrator.setBarcodeImageEnabled(true);
                // Set to false to disable beep on scan.
                integrator.setBeepEnabled(false);
                // Use the specified camera ID.
                integrator.setCameraId(0);
                // By default, the orientation is locked. Set to false to not lock.
                integrator.setOrientationLocked(false);
                // Set a prompt to display on the capture screen.
                integrator.setPrompt("Scan a QR Code");
                // Initiates a scan
                integrator.initiateScan();

            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        IntentResult intentResult  = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        TextView hostScanTv = (TextView) view.findViewById(R.id.host_scan_result_tv);
//        ImageView hostScanIv =  (ImageView) view.findViewById(R.id.host_scan_result_iv);
//        Act_pairVO act_pairVO = new Act_pairVO();
//        String getScanResult = intentResult.getContents();
//        SharedPreferences loginState = getActivity().getSharedPreferences(Common.SCAN_STATE, MODE_PRIVATE);
//        String act_no= loginState.getString("act_no", "no act_no");
//
//
//        //QRCode為Beanlife專用
//        if(getScanResult.startsWith("Beanlife")){
//            String[] resultString = getScanResult.split(",");
//            String actVOString = "";
//            if(resultString[1].equals(act_no)){
//                //查詢會員
//                CommonTask retrieveActVO = (CommonTask) new CommonTask().execute(Common.ACT_URL, "updateActPair", "act_no" ,
//                        resultString[1], "mem_ac", resultString[2]);
//                try {
//                    actVOString = retrieveActVO.get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//
//                Gson gson = new Gson();
//                Type listType = new TypeToken<Act_pairVO>(){}.getType();
//                act_pairVO = gson.fromJson(actVOString, listType);
//
//                hostScanTv.setText(act_pairVO.getMem_ac() + " " + act_pairVO.getChk_state());
//                new GetImageByPkTask(Common.MEM_URL, "mem_ac", resultString[1], 300, hostScanIv).execute();
//
//                Log.d("Scan" , intentResult.getContents());
//            } else {
//                hostScanTv.setText("錯誤的活動配對");
//            }
//        } else {
//            hostScanTv.setText("錯誤的QRCode");
//        }
//    }



}
