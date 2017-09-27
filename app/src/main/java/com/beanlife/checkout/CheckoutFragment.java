package com.beanlife.checkout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.GetImageByPkTask;
import com.beanlife.OrdVO;
import com.beanlife.ProdVO;
import com.beanlife.R;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;


/**
 * Created by vivienhuang on 2017/9/27.
 */

public class CheckoutFragment  extends Fragment {
    private View view;
    private ListView checkoutProdListlv;
    private TextView prodPayTv, feePayTv, totalPayTv;
    private EditText ordNameEt, ordPhoneEt, ordAddEt, bankNoEt, creditEt1, creditEt2, creditEt3, creditEt4, creditBackEt;
    private Button checkoutBt;
    private RadioGroup payWayRg;
    private List<ProdVO> prodVOList;
    private Hashtable<String,Integer> prodAmount;
    private String totalPrice;
    private OrdVO ordVO;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.ord_checkout_fragment, container, false);
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
        ordNameEt = (EditText) view.findViewById(R.id.ord_name_et);
        ordPhoneEt = (EditText) view.findViewById(R.id.ord_phone_et);
        ordAddEt = (EditText) view.findViewById(R.id.ord_add_et);
        bankNoEt = (EditText) view.findViewById(R.id.ord_pay_info_bankno_et);
        creditEt1 = (EditText) view.findViewById(R.id.ord_credit_et1);
        creditEt2 = (EditText) view.findViewById(R.id.ord_credit_et2);
        creditEt3 = (EditText) view.findViewById(R.id.ord_credit_et3);
        creditEt4 = (EditText) view.findViewById(R.id.ord_credit_et4);
        creditBackEt = (EditText) view.findViewById(R.id.ord_cradit_back4_et);
        checkoutBt = (Button) view.findViewById(R.id.checkout_check_bt);
        payWayRg = (RadioGroup) view.findViewById(R.id.payway_rg);
        checkoutProdListlv = (ListView) view.findViewById(android.R.id.list);

        prodVOList = (List<ProdVO>) getArguments().getSerializable("prodVOList");
        prodAmount = (Hashtable<String, Integer>) getArguments().getSerializable("prodCount");
        CheckoutProdListAdapter checkListAdapter = new CheckoutProdListAdapter(getActivity(), prodVOList, prodAmount);
        checkoutProdListlv.setAdapter(checkListAdapter);
        ViewGroup.LayoutParams listViewParams = checkoutProdListlv.getLayoutParams();
        listViewParams.height = checkListAdapter.getCount() * 260;

    }

    private OrdVO getOrdVO(){
        ordVO = new OrdVO();
        //ordVO.getMem_ac() =
        return ordVO;
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

        CheckoutProdListAdapter(Context context , List<ProdVO> prodVOList, Hashtable<String,Integer> prodAmount){
            this.context = context ;
            this.prodVOList =  prodVOList;
            this.prodAmount = prodAmount;
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
    }

}
