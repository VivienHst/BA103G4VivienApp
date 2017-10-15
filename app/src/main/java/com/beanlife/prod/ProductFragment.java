package com.beanlife.prod;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.RetrieveProdTask;
import com.beanlife.store.StoreVO;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Java on 2017/8/27.
 */

public class ProductFragment extends Fragment {
//    private View prodPage;
    private TextView storeNameTv, prodNameTv, prodContTv, prodSendfeeTv, prodPriceTv, prodUnitTv, prodTypeTv,
            prodGradeTv, prodContryTv, prodRegionTv, prodFarmTv, prodFarmerTv, prodElTv, prodProcTv,
            prodRoastTv, prodAromaTv, prodSupTv;
    private ImageView productIv, prodPlusIv, prodMinusIv;
    private LinearLayout addToCarLl;
    private EditText prodCountEt;
    private RatingBar prodRating;
    private ProdVO prodVO;
    private StoreVO storeVO;
    private View view;
    private String storeName;
    private Button addToCar;
    Integer prodCountToCarNum;
    private ProdAttrView prodAttrView;
    Menu menu;

    ProductFragment(ProdVO prodVO, StoreVO storeVO){
        this.prodVO = prodVO;
        this.storeVO = storeVO;
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
        setHasOptionsMenu(true);
    }

    private boolean isLogIn(){
        boolean isLogIn;
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        isLogIn = loginState.getBoolean("login", false);
        return isLogIn;
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
        addToCarLl = (LinearLayout) view.findViewById(R.id.add_to_car_ll);

        prodAttrView = (ProdAttrView) view.findViewById(R.id.prodAttrGv);

        new GetImageByPkTask(Common.PROD_URL, action, prodVO.getProd_no(), 800, productIv).execute();
        prodNameTv.setText(prodVO.getProd_name());
        storeNameTv.setText(storeVO.getStore_name());
        prodContTv.setText(prodVO.getProd_cont());
        String freeShip = "";
        if(storeVO.getStore_free_ship() != null){
            freeShip = " 滿" + storeVO.getStore_free_ship() + "免運費";
        }

        prodSendfeeTv.setText("運費 : " + prodVO.getSend_fee().toString() + freeShip);
        prodPriceTv.setText("NT$" + prodVO.getProd_price().toString());
        prodUnitTv.setText(prodVO.getProd_wt().toString() + "磅");
        prodTypeTv.setText("豆種 : " + ((!(prodVO.getBean_type()==null))?prodVO.getBean_type():""));
        prodGradeTv.setText("生豆等級 : " + ((!(prodVO.getBean_grade()==null))?prodVO.getBean_grade():""));
        prodContryTv.setText("生產國 : " + prodVO.getBean_contry());

        prodRegionTv.setText("地區 : " + ((!(prodVO.getBean_region()==null))?prodVO.getBean_region():""));
        prodFarmTv.setText("農場 : " + ((!(prodVO.getBean_farm()==null))?prodVO.getBean_farm():""));
        prodFarmerTv.setText("生產者 : " + ((!(prodVO.getBean_farmer()==null))?prodVO.getBean_farmer():""));
        prodElTv.setText("海拔 : " + ((!(prodVO.getBean_el()==null))?prodVO.getBean_el():"") + "公尺");
        prodProcTv.setText("處理法 : " + prodVO.getProc());
        prodRoastTv.setText("烘焙度 : " + switchRoast(prodVO.getRoast()));
        prodAromaTv.setText("香味 : " + ((!(prodVO.getBean_aroma()==null))?prodVO.getBean_aroma():""));
        prodSupTv.setText("尚餘數量 : " + prodVO.getProd_sup());

        //產品屬性雷達圖設定值
        prodAttrView.setAcid(prodVO.getBean_attr_acid());
        prodAttrView.setAroma(prodVO.getBean_attr_aroma());
        prodAttrView.setAfter(prodVO.getBean_attr_after());
        prodAttrView.setBal(prodVO.getBean_attr_bal());
        prodAttrView.setBody(prodVO.getBean_attr_body());

        prodRating.setRating(Float.parseFloat(getProdScore(prodVO.getProd_no())));

        if(isLogIn()){
            addToCarLl.setVisibility(View.VISIBLE);
        } else{
            addToCarLl.setVisibility(View.GONE);
        }

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
        addToCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addToCarCount = prodCountEt.getText().toString();
                SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
                String mem_ac = loginState.getString("userAc", "noLogIn");
                Log.d("mem_ac", mem_ac);

                CommonTask retrieveTask = (CommonTask) new CommonTask().execute(Common.CART_URL,
                        "addCart_list", "mem_ac", mem_ac, "prod_no", prodVO.getProd_no().toString(),
                        "prod_amount", addToCarCount);

                String getIsAddToCar = "";
                try {
                    getIsAddToCar = retrieveTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                Type listType = new TypeToken<Boolean>(){}.getType();
                boolean isAddToCar =  gson.fromJson(getIsAddToCar, listType);

                if(isAddToCar){
                    Toast.makeText(view.getContext(), "已新增至購物車", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(view.getContext(), "超過商品剩餘數量", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String switchRoast(String roast){
        if(roast.equals("0")){
            roast = "極淺焙";
        }else if (roast.equals("1")){
            roast = "淺焙";
        }else if (roast.equals("2")){
            roast = "中焙";
        }else if (roast.equals("3")){
            roast = "中深焙";
        }else if (roast.equals("4")){
            roast = "城市烘焙";
        }else if (roast.equals("5")){
            roast = "深焙";
        }else if (roast.equals("6")){
            roast = "法式烘焙";
        }else if (roast.equals("7")){
            roast = "重焙";
        }
        return roast;
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
