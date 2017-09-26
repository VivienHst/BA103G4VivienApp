package com.beanlife.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.ProdVO;
import com.beanlife.R;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

/**
 * Created by vivienhuang on 2017/9/25.
 */

public class CartFragment extends Fragment {

    private final static String TAG = "Cart Fragment";
    CartFragment.CartDetailAdapter cartAdapter;

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

        Set storeNo = new HashSet();

        for(Cart_listVO prodList : cartProd){
            //取得所有商品VO
            prodVOList.add(getProdVO(prodList.getProd_no()));
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
        recyclerView.setAdapter(new CartFragment.CartCardAdapter(getActivity(), storeList, prodVOList));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class CartCardAdapter extends
            RecyclerView.Adapter<CartFragment.CartCardAdapter.MyViewHolder> {
        private Context context;
        private List<Cart_listVO> cartCardList;
        private Set storeNoSet;
        private List<String> storeList;
        List<ProdVO> prodVOList;

        CartCardAdapter(Context context, List<String> storeList, List<ProdVO> prodVOList) {
            this.context = context;
            this.storeList = storeList;
            this.prodVOList = prodVOList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView  cartStoreNameTv, cartStoreProdCountTv, cartStoreTotalPayTv, cartProdCountTv, cartTotalPayTv;
            Button cartProdListBt;
            ListView cartProdListLv;
            LinearLayout prodListDetailLv;

            MyViewHolder(View itemView) {
                super(itemView);

                cartStoreNameTv = (TextView) itemView.findViewById(R.id.cart_store_name_tv);
//                cartStoreProdCountTv = (TextView) itemView.findViewById(R.id.cart_store_prod_count_tv);
//                cartStoreTotalPayTv = (TextView) itemView.findViewById(R.id.cart_store_total_pay_tv);
                cartProdCountTv = (TextView) itemView.findViewById(R.id.cart_store_prod_count_tv);
                cartTotalPayTv = (TextView) itemView.findViewById(R.id.cart_store_total_pay_tv);
                cartProdListBt = (Button) itemView.findViewById(R.id.cart_prod_list_bt);
                cartProdListLv = (ListView) itemView.findViewById(android.R.id.list);
                prodListDetailLv = (LinearLayout) itemView.findViewById(R.id.prod_list_detail);
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
            List<ProdVO> cataProdVOList = new ArrayList<ProdVO>();

            //prodVO分類
            for(ProdVO storeProdVO : prodVOList){
                if(storeProdVO.getStore_no().equals(storeNo)){
                    cataProdVOList.add(storeProdVO);
                }
            }
            cartAdapter = new CartDetailAdapter(getActivity(), cataProdVOList, storeNo);

            //*******取得店家名稱*********


            viewHolder.cartStoreNameTv.setText(storeNo);
            viewHolder.cartProdListLv.setAdapter(cartAdapter);
            ViewGroup.LayoutParams listViewParams = viewHolder.cartProdListLv.getLayoutParams();

            listViewParams.height = cartAdapter.getCount()*240;
        }
    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    List<Cart_listVO> getCartVOList(String mem_ac) {
        String cartVOListString = "";
        if (networkConnected()) {
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
        Type listType = new TypeToken<List<Cart_listVO>>() {
        }.getType();
        return gson.fromJson(cartVOListString, listType);
    }

    private class CartDetailAdapter extends BaseAdapter {

        private Context context;
        private List<Cart_listVO> cart_listVOList;
        private List<ProdVO> prodVOList;
        private String mem_ac;
        private String storeNo;

        public CartDetailAdapter(Context context, List<ProdVO> prodVOList, String storeNo) {
            //prodVOList = new ArrayList<ProdVO>();
            this.storeNo = storeNo;
            this.prodVOList = prodVOList;
            this.context = context;
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

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            //final String prod_no = cart_listListItem.getProd_no();

            if (prodItem == null) {
                prodItem = layoutInflater.inflate(R.layout.cart_detail, parent, false);
            }

            ImageView cartItemIv;
            TextView cartNameTv, cartProdCountTv;
            Button prodCountChangeBt;
            cartItemIv = (ImageView) prodItem.findViewById(R.id.cart_item_iv);
            cartNameTv = (TextView) prodItem.findViewById(R.id.cart_prod_name);
            cartProdCountTv = (TextView) prodItem.findViewById(R.id.cart_prod_conut);
            prodCountChangeBt = (Button) prodItem.findViewById(R.id.prod_count_change_bt);

            cartNameTv.setText(prodVOinList.getProd_name());
            new GetImageByPkTask(Common.PROD_URL, "prod_no", prodVOinList.getProd_no(), 200, cartItemIv).execute();

           // cartProdCountTv.setText(cart_listListItem.getProd_amount().toString());
            return prodItem;
        }
    }

    public ProdVO getProdVO(String prod_no){
        ProdVO prodVO = new ProdVO();
        String prodVOString = "";

        if (networkConnected()) {
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
        Type listType = new TypeToken<ProdVO>() {}.getType();

        prodVO = gson.fromJson(prodVOString, listType);
        return prodVO;
    }
}
