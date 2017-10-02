package com.beanlife.act;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by vivienhuang on 2017/10/2.
 */

public class MemberHostActivityScanFragment  extends android.support.v4.app.Fragment {
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
}
