package com.beanlife.checkout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.mem.MemVO;
import com.beanlife.ord.OrdVO;
import com.beanlife.ord.OrderFragment;
import com.beanlife.prod.ProdVO;
import com.beanlife.R;
import com.beanlife.store.StoreVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by vivienhuang on 2017/9/27.
 */

public class CheckoutFragment  extends Fragment {
    private View view;
    private ListView checkoutProdListlv;
    private TextView prodPayTv, feePayTv, totalPayTv, bankAcInfo;
    private EditText ordNameEt, ordPhoneEt, ordAddEt, bankNoEt,
            creditEt1, creditEt2, creditEt3, creditEt4, creditBackEt, creditDateM, creditDateY;
    private Button checkoutBt, checkMagicBt;
    private RadioGroup payWayRg;
    private List<ProdVO> prodVOList;
    private Hashtable<String,Integer> prodAmount;
    private String totalPrice, mem_ac, ord_name, ord_phone, ord_add, pay_info, ord_state;
    private OrdVO ordVO;
    private StoreVO storeVO;
    private MemVO memVO;
    private Integer total_pay, maxFee;
    private LinearLayout creditInfoLl;
    private List<Integer> feeList;
    private CommonTask retrieveMemVO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.ord_checkout_fragment, container, false);
        feeList = new ArrayList<Integer>();

        findView();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //設定可以控制actionBar標題
        setHasOptionsMenu(true);
    }

    private void findView(){

        prodPayTv = (TextView) view.findViewById(R.id.checkout_prod_pay_tv);
        feePayTv = (TextView) view.findViewById(R.id.checkout_fee_pay_tv);
        totalPayTv = (TextView) view.findViewById(R.id.checkout_total_pay_tv);
        bankAcInfo = (TextView) view.findViewById(R.id.ord_pay_info_bankac_tv);
        ordNameEt = (EditText) view.findViewById(R.id.ord_name_et);
        ordPhoneEt = (EditText) view.findViewById(R.id.ord_phone_et);
        ordAddEt = (EditText) view.findViewById(R.id.ord_add_et);
        bankNoEt = (EditText) view.findViewById(R.id.ord_pay_info_bankno_et);
        creditEt1 = (EditText) view.findViewById(R.id.ord_credit_et1);
        creditEt2 = (EditText) view.findViewById(R.id.ord_credit_et2);
        creditEt3 = (EditText) view.findViewById(R.id.ord_credit_et3);
        creditEt4 = (EditText) view.findViewById(R.id.ord_credit_et4);
        creditBackEt = (EditText) view.findViewById(R.id.ord_credit_back3_et);
        creditDateM = (EditText) view.findViewById(R.id.ord_credit_month_et);
        creditDateY = (EditText) view.findViewById(R.id.ord_credit_year_et);

        checkoutBt = (Button) view.findViewById(R.id.checkout_check_bt);
        checkMagicBt = (Button) view.findViewById(R.id.checkout_magic_bt);
        payWayRg = (RadioGroup) view.findViewById(R.id.payway_rg);
        checkoutProdListlv = (ListView) view.findViewById(android.R.id.list);
        creditInfoLl = (LinearLayout) view.findViewById(R.id.credit_info_ll);
        feeList = new ArrayList<Integer>();

        prodVOList = (List<ProdVO>) getArguments().getSerializable("prodVOList");
        prodAmount = (Hashtable<String, Integer>) getArguments().getSerializable("prodCount");
        total_pay = (Integer) getArguments().getSerializable("totalPrice");
        storeVO = (StoreVO) getArguments().getSerializable("storeVO");
        bankAcInfo.setText(storeVO.getStore_atm_info());

        CheckoutProdListAdapter checkListAdapter = new CheckoutProdListAdapter(getActivity(), prodVOList, prodAmount, maxFee );
        checkoutProdListlv.setAdapter(checkListAdapter);
        ViewGroup.LayoutParams listViewParams = checkoutProdListlv.getLayoutParams();
        listViewParams.height = checkListAdapter.getCount() * 260;

        if(total_pay <= storeVO.getStore_free_ship()){
            maxFee = checkListAdapter.getMaxFee();
        } else {
            maxFee = 0;
        }

        feePayTv.setText("NT$ " + maxFee);
        prodPayTv.setText("NT$ " + total_pay);

        totalPayTv.setText("NT$ " + (total_pay + maxFee));
        pay_info = "";

        payWayRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                int p = radioGroup.indexOfChild((RadioButton) view.findViewById(id));
                int count = radioGroup.getChildCount();
                switch(id){
                    case R.id.payway_bank_rb :
                        Log.d("Pay Way : ", "bank" );
                        bankNoEt.setVisibility(View.VISIBLE);
                        bankAcInfo.setVisibility(View.VISIBLE);
                        creditInfoLl.setVisibility(View.GONE);
                        pay_info = "銀行轉帳";
                        ord_state = "未付款";

                        break;
                    case R.id.payway_credit_rb :
                        Log.d("Pay Way : ", "credit" );
                        creditInfoLl.setVisibility(View.VISIBLE);
                        pay_info = "信用卡";
                        ord_state = "已付款";
                        bankNoEt.setVisibility(View.GONE);
                        bankAcInfo.setVisibility(View.GONE);
                        break;
                }
            }
        });

        memVO = getMemVO();
        ordNameEt.setText(memVO.getMem_lname() + memVO.getMem_fname());
        ordPhoneEt.setText(memVO.getMem_phone().toString());
        ordAddEt.setText(memVO.getMem_add());

        //************快速輸入測試用資料**************
        checkMagicBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordNameEt.setText("布朗先生");
                ordPhoneEt.setText("0988345678");
                ordAddEt.setText("中壢市中大路300號");
            }
        });

        //點擊確認按鈕
        checkoutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean payInfoCheck = false;
                Calendar addDate = Calendar.getInstance();
                int nowYear = addDate.get(Calendar.YEAR);
                int nowMonth = addDate.get(Calendar.MONTH) + 1;

                //確認資料是否填妥
                if (ordNameEt.getText().length() == 0 ){
                    Toast.makeText(view.getContext(), "請輸入姓名", Toast.LENGTH_SHORT).show();
                } else if (ordPhoneEt.getText().length() == 0 ){
                    Toast.makeText(view.getContext(), "請輸入電話", Toast.LENGTH_SHORT).show();
                } else if (ordAddEt.getText().length() == 0 ){
                    Toast.makeText(view.getContext(), "請輸入地址", Toast.LENGTH_SHORT).show();
                } else if(pay_info.equals("")){
                    Toast.makeText(view.getContext(), "請選擇付款方式", Toast.LENGTH_SHORT).show();
                } else if (pay_info.equals("信用卡")){
                    String creditMString = creditDateM.getText() + "";
                    String creditYString = creditDateY.getText() + "";

                    int creditYearIn = Integer.parseInt(creditYString);
                    int creditMonthIn = Integer.parseInt(creditMString);
                    if(creditEt1.getText().length() < 4
                            || creditEt2.getText().length() < 4
                            || creditEt3.getText().length() < 4
                            || creditEt4.getText().length() < 4
                            || creditBackEt.getText().length() < 3
                            || creditDateM.getText().length() < 2
                            || creditDateY.getText().length() < 4){
                        Toast.makeText(view.getContext(), "請輸入完整付款資訊", Toast.LENGTH_SHORT).show();
                    } else if(nowYear > creditYearIn  && creditMonthIn > 12){
                        Toast.makeText(view.getContext(), "請輸入正確的日期", Toast.LENGTH_SHORT).show();
                    } else if(nowYear == creditYearIn  && creditMonthIn <= nowMonth){
                        Toast.makeText(view.getContext(), "請輸入正確的日期", Toast.LENGTH_SHORT).show();
                    } else {
                        pay_info = "C" + creditEt1.getText() + creditEt2.getText()
                                + creditEt3.getText() + creditEt4.getText();
                        payInfoCheck = true;
                    }

                } else if(pay_info.equals("銀行轉帳")){
                    if(bankNoEt.getText().length() < 5){
                        Toast.makeText(view.getContext(), "請填入匯款資訊", Toast.LENGTH_SHORT).show();
                    }else {
                        pay_info = "B" + bankNoEt.getText();
                        payInfoCheck = true;
                    }
                }

                //送出資料
                if (networkConnected() && payInfoCheck) {

                    String getOrdStr = "";
                    CommonTask sentOrd =(CommonTask) new CommonTask().
                            execute(Common.ORD_URL, "newAnOrder", "ordVO", new Gson().toJson(getOrdVO()),
                                    "Ord_listVO", new Gson().toJson(prodAmount));

                    try {
                        getOrdStr = sentOrd.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(view.getContext(), "已送出訂單", Toast.LENGTH_SHORT).show();
                    Fragment fragment = new OrderFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            }
        });
    }

    private OrdVO getOrdVO(){
        ordVO = new OrdVO();
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        mem_ac = loginState.getString("userAc", "noLogIn");
        ord_name = ordNameEt.getText().toString().trim();
        ord_phone = ordPhoneEt.getText().toString().trim();
        ord_add = ordAddEt.getText().toString().trim();

        ordVO.setMem_ac(mem_ac);
        ordVO.setOrd_name(ord_name);
        ordVO.setOrd_phone(ord_phone);
        ordVO.setOrd_add(ord_add);
        ordVO.setTotal_pay(total_pay);
        ordVO.setSend_fee(maxFee);
        ordVO.setOrd_stat(ord_state);
        ordVO.setPay_info(pay_info);
        return ordVO;
    }

    private MemVO getMemVO(){
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
        ConnectivityManager conManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class CheckoutProdListAdapter extends BaseAdapter{

        Context context ;
        List<ProdVO> prodVOList;
        Hashtable<String,Integer> prodAmount;
        List<Integer> feeList = new ArrayList<>();
        Integer maxFee ;

        CheckoutProdListAdapter(Context context , List<ProdVO> prodVOList, Hashtable<String,Integer> prodAmount, Integer maxFee){
            this.context = context ;
            this.prodVOList =  prodVOList;
            this.prodAmount = prodAmount;
            this.maxFee = maxFee;
        }

        @Override
        public int getCount() {
            return prodVOList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View listItem, ViewGroup parent) {
            final TextView checkNameTv, checkProdCountTv;
            final ImageView checkProdIv;

            ProdVO prodVO = prodVOList.get(position);
            Integer prodCount = prodAmount.get(prodVO.getProd_no());
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            if (listItem == null) {
                listItem = layoutInflater.inflate(R.layout.ord_checkout_list, parent, false);
            }
            checkProdIv = (ImageView) listItem.findViewById(R.id.check_list_item_iv);
            checkNameTv = (TextView) listItem.findViewById(R.id.check_list_prod_name);
            checkProdCountTv = (TextView) listItem.findViewById(R.id.check_list_prod_conut);
            new GetImageByPkTask(Common.PROD_URL, "prod_no", prodVO.getProd_no(), 200, checkProdIv).execute();
            checkNameTv.setText(prodVO.getProd_name());
            checkProdCountTv.setText(prodCount.toString());

            return listItem;
        }

        Integer getMaxFee(){
            for(ProdVO feeProdVO : prodVOList){
                feeList.add(feeProdVO.getSend_fee());
            }
            Collections.sort(feeList);
            Collections.reverse(feeList);
            maxFee = feeList.get(0);
            return maxFee;
        }
    }
}
