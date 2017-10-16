package com.beanlife.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.prod.ProdVO;
import com.beanlife.R;
import com.beanlife.store.StoreVO;
import com.beanlife.checkout.CheckoutFragment;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vivienhuang on 2017/9/25.
 */

public class CartFragment extends Fragment {

    private final static String TAG = "Cart Fragment";
    CartFragment.CartDetailAdapter cartAdapter;
    String mem_ac;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        addRow(view, R.id.rv_cart_card);
        return view;
    }

    private void addRow(View view,int viewId){
        RecyclerView recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));

        //取得登入會員帳號
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        String user_ac = loginState.getString("userAc", "noLogIn");
        Log.d("user ac",user_ac);

        //取得購物車內的所有商品
        final List<Cart_listVO> cartProd = getCartVOList(user_ac);
        List<ProdVO> prodVOList = new ArrayList<ProdVO>();
        Hashtable<String,Integer> prodCount = new Hashtable<String,Integer>();

        Set storeNo = new HashSet();

        for(Cart_listVO prodList : cartProd){
            //取得所有商品VO
            prodVOList.add(getProdVO(prodList.getProd_no()));
            prodCount.put(prodList.getProd_no(),prodList.getProd_amount());
            //取得包含店家
            storeNo.add(getProdVO(prodList.getProd_no()).getStore_no());
        }

        //店家轉為list形式
        List<String> storeList = new ArrayList<String>();
        Iterator<String> storeIt = storeNo.iterator();
        while(storeIt.hasNext()){
            storeList.add(storeIt.next());
        }
        //加入各筆訂單以店家分類
        recyclerView.setAdapter(new CartFragment.CartCardAdapter(getActivity(), storeList, prodVOList, prodCount));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class CartCardAdapter extends
            RecyclerView.Adapter<CartFragment.CartCardAdapter.MyViewHolder> {
        private Context context;
//        private List<Cart_listVO> cartCardList;
//        private Set storeNoSet;
        private List<String> storeList;
        List<ProdVO> prodVOList;
        Hashtable<String,Integer> prodCount;

        CartCardAdapter(Context context, List<String> storeList, List<ProdVO> prodVOList,Hashtable<String,Integer> prodCount) {
            this.context = context;
            this.storeList = storeList;
            this.prodVOList = prodVOList;
            this.prodCount = prodCount;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView  cartStoreNameTv, cartStoreProdCountTv, cartStoreTotalPayTv, cartProdCountTv, cartTotalPayTv;
            Button cartProdListBt, cartProdCheckoutBt;
            ListView cartProdListLv;
            LinearLayout prodListDetailLv;

            MyViewHolder(View itemView) {
                super(itemView);

                cartStoreNameTv = (TextView) itemView.findViewById(R.id.cart_store_name_tv);
                cartProdCountTv = (TextView) itemView.findViewById(R.id.cart_store_prod_count_tv);
                cartTotalPayTv = (TextView) itemView.findViewById(R.id.cart_store_total_pay_tv);

                cartProdListBt = (Button) itemView.findViewById(R.id.cart_prod_list_bt);
                cartProdCheckoutBt = (Button) itemView.findViewById(R.id.cart_prod_checkout_bt);
                cartProdListLv = (ListView) itemView.findViewById(android.R.id.list);
                prodListDetailLv = (LinearLayout) itemView.findViewById(R.id.cart_prod_list_detail);

            }
        }

        @Override
        public int getItemCount() {
            return storeList.size();
        }

        @Override
        public CartFragment.CartCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.cart_card, viewGroup, false);
            return new CartFragment.CartCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CartFragment.CartCardAdapter.MyViewHolder viewHolder, int position) {
            final String storeNo = storeList.get(position);
            final List<ProdVO> cataProdVOList = new ArrayList<ProdVO>();
            final Integer totalPrice;
            final Hashtable<String,Integer> prodCountByStore = new Hashtable<String,Integer>();

            //prodVO分類
            for(ProdVO storeProdVO : prodVOList){

                if(storeProdVO.getStore_no().equals(storeNo)){
                    prodCountByStore.put(storeProdVO.getProd_no(), prodCount.get(storeProdVO.getProd_no()));
                    cataProdVOList.add(storeProdVO);
                }
            }
            cartAdapter = new CartDetailAdapter(getActivity(), cataProdVOList, storeNo,
                    prodCountByStore, viewHolder.cartTotalPayTv);

            //*******取得店家名稱*********
            viewHolder.cartProdListLv.setAdapter(cartAdapter);
            ViewGroup.LayoutParams listViewParams = viewHolder.cartProdListLv.getLayoutParams();
            final StoreVO storeVO = getStoreVO(storeNo);
            totalPrice = cartAdapter.getTotalPrice();

            viewHolder.cartStoreNameTv.setText(storeVO.getStore_name());
            viewHolder.cartProdCountTv.setText("商品共 " + cartAdapter.getCount() + " 項");
            viewHolder.cartTotalPayTv.setText("共計 ： $" + totalPrice);

            listViewParams.height = cartAdapter.getCount() * 250;

            //轉到結帳頁面
            viewHolder.cartProdCheckoutBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new CheckoutFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("prodVOList", (Serializable) cataProdVOList);
                    bundle.putSerializable("prodCount", (Serializable) prodCountByStore);

                    bundle.putSerializable("storeVO", storeVO);
                    bundle.putSerializable("totalPrice", totalPrice);

                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
    }

    List<Cart_listVO> getCartVOList(String mem_ac) {
        String cartVOListString = "";
        if (Common.networkConnected(getActivity())) {
            CommonTask retrieveCartList =
                    (CommonTask) new CommonTask().execute(Common.CART_URL,
                            "getVOsByMem", "mem_ac", mem_ac);
            try {
                cartVOListString = retrieveCartList.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Cart_listVO>>() {}.getType();
        return gson.fromJson(cartVOListString, listType);
    }

    private class CartDetailAdapter extends BaseAdapter {

        private Context context;
        private List<Cart_listVO> cart_listVOList;
        private List<ProdVO> prodVOList;
        private String mem_ac;
        private String storeNo;
        Hashtable<String,Integer> prodCount;
        private int totalPrice, storeItemCount;
        private TextView textV;

        public CartDetailAdapter(Context context, List<ProdVO> prodVOList,
                                    String storeNo, Hashtable<String,Integer>  prodCount, TextView textV) {
            //prodVOList = new ArrayList<ProdVO>();
            this.storeNo = storeNo;
            this.prodVOList = prodVOList;
            this.prodCount = prodCount;
            this.context = context;
            this.textV = textV;
        }

        @Override
        public int getCount() {
            return prodVOList.size();
        }

        @Override
        public Object getItem(int position) {
            return prodVOList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //取得購物車細項物品
        @Override
        public View getView(int position, View prodItem, ViewGroup parent) {
            final ProdVO prodVOinList = prodVOList.get(position);
            SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
            mem_ac = loginState.getString("userAc", "noLogIn");

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            //final String prod_no = cart_listListItem.getProd_no();

            if (prodItem == null) {
                prodItem = layoutInflater.inflate(R.layout.cart_detail, parent, false);
            }

            ImageView cartItemIv;
            final TextView cartNameTv, cartProdCountTv;
            final LinearLayout changeProdLl;
            final Button prodCountChangeBt, prodDeleteBt;
            final ImageView prodPlusIv, prodMinusIv;
            final EditText prodCountEt;
            cartItemIv = (ImageView) prodItem.findViewById(R.id.cart_item_iv);
            cartNameTv = (TextView) prodItem.findViewById(R.id.cart_prod_name);
            cartProdCountTv = (TextView) prodItem.findViewById(R.id.cart_prod_conut);
            prodCountChangeBt = (Button) prodItem.findViewById(R.id.prod_count_change_bt);
            changeProdLl = (LinearLayout) prodItem.findViewById(R.id.change_prod_ll);
            prodPlusIv = (ImageView) prodItem.findViewById(R.id.cart_prod_plus_iv);
            prodMinusIv = (ImageView) prodItem.findViewById(R.id.cart_prod_minus_iv);
            prodCountEt = (EditText) prodItem.findViewById(R.id.cart_prod_cont_et);
            prodDeleteBt = (Button) prodItem.findViewById(R.id.prod_delete_bt);
            final Integer[] prodCountToCarNum = new Integer[1];

            prodCountToCarNum[0] = prodCount.get(prodVOinList.getProd_no());
            prodCountEt.setText(prodCountToCarNum[0].toString());
            final String prodCountToCar = prodCountToCarNum[0].toString();

            prodPlusIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String prodCountToCar = prodCountEt.getText().toString();
                    if(prodCountToCar.length() == 0){
                        prodCountToCarNum[0] = 1;
                    } else {
                        prodCountToCarNum[0] = Integer.parseInt(prodCountToCar) + 1;
                    }
                    prodCountEt.setText(prodCountToCarNum[0].toString());
                }
            });

            prodMinusIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String prodCountToCar = prodCountEt.getText().toString();
                    if(prodCountToCar.length() == 0){
                        prodCountToCarNum[0] = 1;
                    } else {
                        if(prodCountToCarNum[0] > 1){
                            prodCountToCarNum[0] = Integer.parseInt(prodCountToCar) - 1;
                        } else {
                            prodCountToCarNum[0] = 1;
                        }
                    }
                    prodCountEt.setText(prodCountToCarNum[0].toString());
                }
            });


            prodCountChangeBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //填入修改數量事件處理
                    if(changeProdLl.getVisibility() == View.GONE){
                        cartProdCountTv.setVisibility(View.GONE);
                        changeProdLl.setVisibility(View.VISIBLE);
                        prodCountChangeBt.setText("確定");
                    }else{

                        String addToCarCount = prodCountEt.getText().toString();
                        CommonTask retrieveTask = (CommonTask) new CommonTask().execute(Common.CART_URL,
                                "updateCart_list", "mem_ac", mem_ac, "prod_no", prodVOinList.getProd_no().toString(),
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
                            Toast.makeText(view.getContext(), "已修改數量", Toast.LENGTH_SHORT).show();
                            cartProdCountTv.setText("$ " +prodVOinList.getProd_price() + " x "
                                    + addToCarCount);
                            prodCount.put(prodVOinList.getProd_no(), Integer.parseInt(addToCarCount));
                            textV.setText("共計 ： " + getTotalPrice());
                        }else{
                            Toast.makeText(view.getContext(), "超過商品剩餘數量", Toast.LENGTH_SHORT).show();
                        }
                        //reflashFragment();
                        cartProdCountTv.setVisibility(View.VISIBLE);
                        changeProdLl.setVisibility(View.GONE);
                        prodCountChangeBt.setText("修改");
                    }

                }
            });

            String prodName = prodVOinList.getProd_name();
            if(prodName.length() > 12){
                prodName = prodName.substring(0,12) + "...";
            }

            cartNameTv.setText(prodName);
            cartProdCountTv.setText("$ " +prodVOinList.getProd_price() + " x "
                    + prodCount.get(prodVOinList.getProd_no()).toString());
            new GetImageByPkTask(Common.PROD_URL, "prod_no", prodVOinList.getProd_no(), 200, cartItemIv).execute();

            prodDeleteBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonTask retrieveTask = (CommonTask) new CommonTask().execute(Common.CART_URL,
                            "deleteCart_list", "mem_ac", mem_ac, "prod_no", prodVOinList.getProd_no());
                    reflashFragment();
                }
            });

            return prodItem;
        }

        public int getTotalPrice(){
            totalPrice = 0;
            for(ProdVO prodVOinList : prodVOList) {
                totalPrice = totalPrice + prodVOinList.getProd_price() * prodCount.get(prodVOinList.getProd_no());
            }
            return totalPrice;
        }
    }

    private void reflashFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body, new CartFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public ProdVO getProdVO(String prod_no){
        ProdVO prodVO;
        String prodVOString = "";

        if (Common.networkConnected(getActivity())) {
            CommonTask retrieveCartList =
                    (CommonTask) new CommonTask().execute(Common.PROD_URL,
                            "getOneProd", "prod_no", prod_no);
            try {
                prodVOString = retrieveCartList.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<ProdVO>(){}.getType();

        prodVO = gson.fromJson(prodVOString, listType);
        return prodVO;
    }

    private StoreVO getStoreVO(String store_no){
        StoreVO storeVO;
        String storeVOString = "";

        if (Common.networkConnected(getActivity())) {
            CommonTask retrieveCartList =
                    (CommonTask) new CommonTask().execute(Common.STORE_URL,
                            "getOneStore", "store_no", store_no);
            try {
                storeVOString = retrieveCartList.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<StoreVO>() {}.getType();

        storeVO = gson.fromJson(storeVOString, listType);
        return storeVO;
    }
}
