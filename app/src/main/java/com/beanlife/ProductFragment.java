package com.beanlife;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

/**
 * Created by Java on 2017/8/27.
 *
 *
 */

public class ProductFragment extends Fragment {
//    private View prodPage;
    private TextView storeNameTv, prodNameTv, prodContTv, prodSendfeeTv, prodPriceTv, prodUnitTv, prodTypeTv,
            prodGradeTv, prodContryTv, prodRegionTv, prodFarmTv, prodFarmerTv, prodElTv, prodProcTv,
            prodRoastTv, prodAromaTv, prodSupTv;
    private ImageView productIv, prodPlusIv, prodMinusIv;
    private EditText prodCountEt;
    private RatingBar prodRating;
    private ProdVO prodVO;
    private View view;
    private String storeName;
    private Button addToCar;

    public void getProdVO(ProdVO prodVO){
        this.prodVO = prodVO;
    }

    public void getStoreName(String storeName){
        this.storeName = storeName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.product_page_cont, container, false);
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    private void findView(){

        String action = "prod_no";
        productIv = (ImageView) view.findViewById(R.id.productIv);
        prodPlusIv = (ImageView) view.findViewById(R.id.prod_plus_iv);
        prodMinusIv = (ImageView) view.findViewById(R.id.prod_minus_iv);
        prodCountEt = (EditText) view.findViewById(R.id.prod_cont_et);
        prodNameTv = (TextView) view.findViewById(R.id.prodName);
        storeNameTv = (TextView) view.findViewById(R.id.prodStore);
        prodContTv = (TextView) view.findViewById(R.id.prodPageDesc);
        prodSendfeeTv = (TextView) view.findViewById(R.id.prodSendFee);
        prodPriceTv = (TextView) view.findViewById(R.id.prodPrice);
        prodUnitTv = (TextView) view.findViewById(R.id.prodUnit);
        prodTypeTv = (TextView) view.findViewById(R.id.prodType);
        prodGradeTv = (TextView) view.findViewById(R.id.prodGrade);
        prodContryTv = (TextView) view.findViewById(R.id.prodContry);
        prodRegionTv = (TextView) view.findViewById(R.id.prodRegion );
        prodFarmTv = (TextView) view.findViewById(R.id.prodFarm);
        prodFarmerTv = (TextView) view.findViewById(R.id.prodFarmer);
        prodElTv = (TextView) view.findViewById(R.id.prodEl);
        prodProcTv = (TextView) view.findViewById(R.id.prodProc);
        prodRoastTv = (TextView) view.findViewById(R.id.prodRoast);
        prodAromaTv = (TextView) view.findViewById(R.id.prodAroma);
        prodRating = (RatingBar) view.findViewById(R.id.prod_rb);
        prodSupTv = (TextView) view.findViewById(R.id.prodSup);
        addToCar =  (Button) view.findViewById(R.id.add_to_car_bt);

        new GetImageByPkTask(Common.PROD_URL, action, prodVO.getProd_no(), 800, productIv).execute();
        prodNameTv.setText(prodVO.getProd_name());
        storeNameTv.setText(storeName);
        prodContTv.setText(prodVO.getProd_cont());
        prodSendfeeTv.setText("運費 : " + prodVO.getSend_fee().toString() + "滿1000免運費");
        prodPriceTv.setText("$" + prodVO.getProd_price().toString() + "NT");
        prodUnitTv.setText(prodVO.getProd_wt().toString() + "磅");
        prodTypeTv.setText("豆種 : " + prodVO.getBean_type());
        prodGradeTv.setText("生豆等級 : " + prodVO.getBean_grade());
        prodContryTv.setText("生產國 : " + prodVO.getBean_contry());
        prodRegionTv.setText("地區 : " + prodVO.getBean_region());
        prodFarmTv.setText("農場 : " + prodVO.getBean_farm());
        prodFarmerTv.setText("生產者 : " + prodVO.getBean_farmer());
        prodElTv.setText("海拔 : " + prodVO.getBean_el() + "公尺");
        prodProcTv.setText("處理法 : " + prodVO.getProc());
        prodRoastTv.setText("烘焙度 : " + prodVO.getRoast());
        prodAromaTv.setText("香味 : " + prodVO.getBean_aroma());
        prodSupTv.setText("尚餘數量 : " + prodVO.getProd_sup());

        prodRating.setRating(Float.parseFloat(getProdScore(prodVO.getProd_no())));

        String prodCountToCar = prodCountEt.getText().toString();
        prodPlusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String prodCountToCar = prodCountEt.getText().toString();
                Integer prodCountToCarNum;
                if(prodCountToCar.length() == 0){
                    prodCountToCarNum = 1;
                } else {
                    prodCountToCarNum = Integer.parseInt(prodCountToCar);
                    prodCountToCarNum = prodCountToCarNum + 1;
                }

                prodCountEt.setText(prodCountToCarNum.toString());
            }
        });

        prodMinusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String prodCountToCar = prodCountEt.getText().toString();
                Integer prodCountToCarNum;
                if(prodCountToCar.length() == 0){
                    prodCountToCarNum = 1;
                } else {
                    prodCountToCarNum = Integer.parseInt(prodCountToCar);
                    if(prodCountToCarNum > 1){
                        prodCountToCarNum = prodCountToCarNum - 1;
                    } else {
                        prodCountToCarNum = 1;
                    }

                }

                prodCountEt.setText(prodCountToCarNum.toString());
            }
        });

        //加入購物車
//        addToCar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getProdScore(String prod_no){
        String getProdScore = null;
        if(networkConnected()){

            try {
                getProdScore = new RetrieveProdTask("getScore", prod_no).execute(Common.PROD_URL).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("get Product Score", "Network Not Connect");
        }
        return getProdScore;
    }
}
