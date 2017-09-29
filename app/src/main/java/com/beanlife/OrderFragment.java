package com.beanlife;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

/**
 * Created by vivienhuang on 2017/9/22.
 * 訂單瀏覽頁面
 */

public class OrderFragment extends Fragment {

    private AsyncTask retriveOrdTask;
    private final static String TAG = "Order Fragment";
    OrderDetailAdapter orderAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.order_fragment, container, false);
        addRow(view, R.id.rv_order_card);
        return view;
    }

    private void addRow(View view,int viewId){
        RecyclerView recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));

        //*****取得登入會員帳號*****
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        //沒有登入回傳noLogIn
        String user_ac = loginState.getString("userAc", "noLogIn");
        Log.d("user ac",user_ac);
        final List<OrdVO> ord = getOrdVOList(user_ac);
        //加入各筆訂單
        recyclerView.setAdapter(new OrderFragment.OrderCardAdapter(getActivity(), ord));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class OrderCardAdapter extends
            RecyclerView.Adapter<OrderFragment.OrderCardAdapter.MyViewHolder> {
        private Context context;
        private List<OrdVO> OrderCardList;

        OrderCardAdapter(Context context, List<OrdVO> orderCardList) {
            this.context = context;
            this.OrderCardList = orderCardList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView orderStateIv;
            TextView orderStateTv, orderNoTv, orderDateTv, orderProdCountTv, orderTotalPayTv;
            Button ordListBt;
            ListView ordListLv;
            LinearLayout ordListDetailLv;

            MyViewHolder(View itemView) {
                super(itemView);
                orderStateIv = (ImageView) itemView.findViewById(R.id.order_state_iv);
                orderStateTv = (TextView) itemView.findViewById(R.id.order_state_tv);
                orderNoTv = (TextView) itemView.findViewById(R.id.order_no);
                orderDateTv = (TextView) itemView.findViewById(R.id.order_date);
                orderProdCountTv = (TextView) itemView.findViewById(R.id.order_prod_count);
                orderTotalPayTv = (TextView) itemView.findViewById(R.id.order_total_pay);
                ordListBt = (Button) itemView.findViewById(R.id.ord_list_bt);
                ordListLv = (ListView) itemView.findViewById(android.R.id.list);
                ordListDetailLv = (LinearLayout) itemView.findViewById(R.id.ord_list_detail);
            }
        }

        @Override
        public int getItemCount() {
            return OrderCardList.size();
        }

        @Override
        public OrderFragment.OrderCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.order_card, viewGroup, false);
            return new OrderFragment.OrderCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final OrderFragment.OrderCardAdapter.MyViewHolder viewHolder, int position) {
            final OrdVO ordVO = OrderCardList.get(position);
            viewHolder.orderStateIv.setImageResource(R.drawable.order_state01);
            viewHolder.orderStateTv.setText(ordVO.getOrd_stat());
            viewHolder.orderNoTv.setText(ordVO.getOrd_no());
            Log.d("prod no", ordVO.getOrd_no());
            viewHolder.orderDateTv.setText(ordVO.getOrd_date());
            viewHolder.orderTotalPayTv.setText("$" + ordVO.getTotal_pay().toString());

            //加入訂單細項
            orderAdapter = new OrderDetailAdapter(getActivity(), ordVO.getOrd_no());
            viewHolder.ordListLv.setAdapter(orderAdapter);
            ViewGroup.LayoutParams listViewParams = viewHolder.ordListDetailLv.getLayoutParams();

            listViewParams.height = orderAdapter.getCount()*240;

            //訂單細項開關
            viewHolder.ordListBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(viewHolder.ordListDetailLv.getVisibility() == view.GONE){
                        viewHolder.ordListDetailLv.setVisibility(view.VISIBLE);
                    } else {
                        viewHolder.ordListDetailLv.setVisibility(view.GONE);
                    }
                }
            });
        }
    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    List<OrdVO> getOrdVOList(String mem_ac){
        List<OrdVO> ordList = new ArrayList<>();

        if(networkConnected()){
            retriveOrdTask = new OrderFragment.RetrieveOrdTask().execute(Common.ORD_URL, mem_ac);

            try {
                ordList = (List) retriveOrdTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        return ordList;
    }

    class RetrieveOrdTask extends AsyncTask<String, Void, List<OrdVO>> {

        @Override
        protected List<OrdVO> doInBackground(String... param) {
            String url = param[0];
            String mem_ac = param[1];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getOrdByMem_ac");
            jsonObject.addProperty("mem_ac", mem_ac);

            try{
                jsonIn = getRemoteData(url, jsonObject.toString());
            }catch (IOException e){
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<OrdVO>>(){}.getType();
            return gson.fromJson(jsonIn, listType);
        }
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                jsonIn.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonIn: " + jsonIn);
        return jsonIn.toString();
    }

    private class OrderDetailAdapter extends BaseAdapter{

        private Context context;
        private List<Ord_listVO> ord_listVOList;
        private String ord_no;

        public OrderDetailAdapter(Context context, String ord_no) {

            List<Ord_listVO> ord_listVOList = getOrderList(ord_no);
                this.ord_no = ord_no;
                this.context = context;
                this.ord_listVOList = ord_listVOList;
            }

        @Override
        public int getCount() {
            return ord_listVOList.size();
        }

        @Override
        public Object getItem(int position) {
            return ord_listVOList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ord_listVOList.get(position).getAmont();
        }

        @Override
        public View getView(int position, View prodItem, ViewGroup parent) {
            final Ord_listVO ordListItem = ord_listVOList.get(position);
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            final String prod_no = ordListItem.getProd_no();


            if(prodItem == null){
                prodItem = layoutInflater.inflate(R.layout.order_detail_fragment,parent,false);
            }

            ImageView prodItemIv;
            TextView prodNameTv, prodPriceCountTv;
            Button writeReviewBt;
            prodItemIv = (ImageView) prodItem.findViewById(R.id.prod_itme_iv);
            prodNameTv = (TextView) prodItem.findViewById(R.id.order_prod_name);
            prodPriceCountTv = (TextView) prodItem.findViewById(R.id.order_prod_conut);
            writeReviewBt =(Button)  prodItem.findViewById(R.id.order_prod_detail_bt);
            String ordProdVOString = "";
            String getIsProdPostedString = "";



            //**************取得商品名稱**************
            RetrieveProdTask getOrdProdVOString  =
                    (RetrieveProdTask) new RetrieveProdTask("getOneProd", ordListItem.getProd_no())
                            .execute(Common.PROD_URL);
            try {
                ordProdVOString = getOrdProdVOString.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ProdVO>(){}.getType();
            final ProdVO prodVO = gson.fromJson(ordProdVOString, listType);


            //*******取得是否已經發過評論*******
            CommonTask retrieveIsPosted  =
                    (CommonTask) new CommonTask().execute(Common.REVIEW_URL, "isPostedReview",
                            "ord_no", ordListItem.getOrd_no(), "prod_no", ordListItem.getProd_no());
            try {
                getIsProdPostedString = retrieveIsPosted.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Gson getIsPostedGson = new Gson();
            Type isPostedType = new TypeToken<Boolean>(){}.getType();
            final Boolean isPostedString = getIsPostedGson.fromJson(getIsProdPostedString, isPostedType);

            if (isPostedString){
                writeReviewBt.setVisibility(GONE);
            }

            prodNameTv.setText(prodVO.getProd_name());
            prodPriceCountTv.setText("數量 : " + ordListItem.getAmont().toString());
            new GetImageByPkTask(Common.PROD_URL, "prod_no", prod_no, 200, prodItemIv).execute();

            //********轉到寫商品評論*********
            writeReviewBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new ReviewWriteFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("prod_no", prod_no);
                    bundle.putSerializable("ord_no", ord_no);
                    bundle.putSerializable("prod_name", prodVO.getProd_name());
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            return prodItem;
        }

        //取得訂單列表
        List<Ord_listVO> getOrderList(String ord_no) {
            //List<Ord_listVO> ordList = new ArrayList<Ord_listVO>();
            String orderListString = "";
            if (networkConnected()) {
                CommonTask retrieveOrderList =
                        (CommonTask) new CommonTask().execute(Common.ORD_URL,
                                "getOrd_listByOrd", "ord_no", ord_no);
                try {
                    orderListString = retrieveOrderList.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Ord_listVO>>(){}.getType();
            return gson.fromJson(orderListString, listType);
            //return ordList;
        }

        private boolean networkConnected(){
            ConnectivityManager conManager = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

    }
}
