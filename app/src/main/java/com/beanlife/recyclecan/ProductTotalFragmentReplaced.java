package com.beanlife.recyclecan;

import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.prod.ProdVO;
import com.beanlife.prod.ProductCardItem;
import com.beanlife.prod.ProductWithTab;
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

/**
 * Created by Java on 2017/9/2.
 * 商品總覽頁面
 * 保留為無網路狀態測試用
 */

public class ProductTotalFragmentReplaced extends Fragment {
    private final static String TAG = "SearchActivity";
    private ListView prodList;
    private CommonTask retrieveProdTask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view;
        view = inflater.inflate(R.layout.product_fragment, container, false);

        addRow(view, R.id.rv_prod_card);
        addRow(view, R.id.new_prod_card);
        addRow(view, R.id.recom_prod_card);

        return view;
    }

    private void addRow(View view,int viewId){

        RecyclerView recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.HORIZONTAL));
        final List<ProductCardItem> prod = getProductList();
        recyclerView.setAdapter(new ProductCardAdapter(getActivity(), prod));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //確認網路是否連接
    private boolean networkConnected() {
        ConnectivityManager conManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class ProductCardAdapter extends
            RecyclerView.Adapter<ProductCardAdapter.MyViewHolder> {
        private Context context;
        private List<ProductCardItem> prodCardList;

        ProductCardAdapter(Context context, List<ProductCardItem> prodCardList) {
            this.context = context;
            this.prodCardList = prodCardList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView cardImageView;
            TextView cardProdName, cardProdPrice;

            MyViewHolder(View itemView) {
                super(itemView);
                cardImageView = (ImageView) itemView.findViewById(R.id.card_imageView);
                cardProdName = (TextView) itemView.findViewById(R.id.card_prodName);
                cardProdPrice = (TextView) itemView.findViewById(R.id.card_prodPrice);
            }
        }

        @Override
        public int getItemCount() {
            return prodCardList.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.product_card_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            final ProductCardItem prodCard = prodCardList.get(position);
            String url = Common.PROD_URL;
            String prod_no = prodCard.getProdNo();
            String action = "prod_no";
            new GetImageByPkTask(url, action, prod_no, 256, viewHolder.cardImageView).execute();

            viewHolder.cardImageView.setImageResource(prodCard.getProdPic());
            viewHolder.cardProdName.setText(prodCard.getProdName());
            viewHolder.cardProdPrice.setText(prodCard.getProdPrice());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ProductWithTab();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("prod", prodCard);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    System.out.println("Product onclick");
                }
            });
        }
    }

    List<ProductCardItem> getProductList(){
        List<ProdVO> prodList = null;
        List<ProductCardItem> productLists = new ArrayList<>();
        String prodListString = "";

        if (networkConnected()) {
            retrieveProdTask = (CommonTask) new CommonTask().execute(Common.PROD_URL, "getAll");
            try {
                prodListString = retrieveProdTask.get();

                //prodList = (List) retrieveProdTask.get();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<ProdVO>>(){}.getType();
                prodList =  gson.fromJson(prodListString, listType);

                for(ProdVO prodlist: prodList){

                    String prodName = prodlist.getProd_name();
                    StringBuffer prodNameSim = new StringBuffer();
                    int limitStr = 12;

                    if(prodName.length() >= limitStr){
                        for(int i = 0; i < limitStr; i++){
                            prodNameSim = prodNameSim.append(prodName.charAt(i));
                        }
                        prodNameSim.append("...");
                        prodName = prodNameSim.toString();
                    }

                    productLists.add(new ProductCardItem(R.drawable.prod01,prodlist.getProd_no(),
                            prodName, "$"+prodlist.getProd_no()));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }else {
            //沒有網路時的做法
            productLists.add(new ProductCardItem(R.drawable.prod01, "無網路", "無網路", "無網路"));
            productLists.add(new ProductCardItem(R.drawable.prod01, "無網路", "無網路", "無網路"));
            productLists.add(new ProductCardItem(R.drawable.prod01, "無網路", "無網路", "無網路"));
            productLists.add(new ProductCardItem(R.drawable.prod01, "無網路", "無網路", "無網路"));
            productLists.add(new ProductCardItem(R.drawable.prod01, "無網路", "無網路", "無網路"));
            productLists.add(new ProductCardItem(R.drawable.prod01, "無網路", "無網路", "無網路"));
        }
        return productLists;
    }

//    class RetrieveProdTask extends AsyncTask<String, Void, List<ProdVO>>{
//
//        @Override
//        protected List<ProdVO> doInBackground(String... param) {
//            String url = param[0];
//
//            String jsonIn;
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAll");
//            try{
//                jsonIn = getRemoteData(url, jsonObject.toString());
//            }catch (IOException e){
//                Log.e(TAG, e.toString());
//                return null;
//            }
//
//            Gson gson = new Gson();
//            Type listType = new TypeToken<List<ProdVO>>(){}.getType();
//            return gson.fromJson(jsonIn, listType);
//        }
//    }
//
//    private String getRemoteData(String url, String jsonOut) throws IOException {
//        StringBuilder jsonIn = new StringBuilder();
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setDoInput(true); // allow inputs
//        connection.setDoOutput(true); // allow outputs
//        connection.setUseCaches(false); // do not use a cached copy
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("charset", "UTF-8");
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
//        bw.write(jsonOut);
//        Log.d(TAG, "jsonOut: " + jsonOut);
//        bw.close();
//
//        int responseCode = connection.getResponseCode();
//
//        if (responseCode == 200) {
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line;
//            while ((line = br.readLine()) != null) {
//                jsonIn.append(line);
//            }
//        } else {
//            Log.d(TAG, "response code: " + responseCode);
//        }
//        connection.disconnect();
//        Log.d(TAG, "jsonIn: " + jsonIn);
//        return jsonIn.toString();
//    }

}
