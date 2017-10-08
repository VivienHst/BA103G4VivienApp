package com.beanlife.prod;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.act.ActivityPageWithTab;
import com.beanlife.ad.AdVO;
import com.beanlife.ad.AdvertFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Java on 2017/9/2.
 * 商品總覽頁面
 * 9/13新增頁面連結功能
 */

public class ProductTotalFragment extends Fragment {
    private final static String TAG = "SearchActivity";
    private CommonTask retrieveProdTask, retrieveHotProdTask, retrieveAdTask;
    private Menu menu;
    private View view;
    private ViewPager viewPager;
    private ProductTotalFragment.AdPagerAdapter adapter;
    private List<AdvertFragment> fragmentLists;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.product_fragment, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.prod_ad_viewpager);
        addHotProdRow(R.id.rv_prod_card);
        addRow(R.id.new_prod_card);
        addRow(R.id.recom_prod_card);
        fragmentLists = new ArrayList();
        fragmentLists = getAdList();
        adapter = new ProductTotalFragment.AdPagerAdapter(getChildFragmentManager(), fragmentLists);
        viewPager.setAdapter(adapter);

        return view;
    }

    private void addHotProdRow(int viewId){

        RecyclerView recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.HORIZONTAL));
        //final List<ProdVO> prod = getProductList();
        final List<ProdVO> prod = getHotProd();
        recyclerView.setAdapter(new ProductCardAdapter(getActivity(), prod));

    }

    private void addRow(int viewId){

        RecyclerView recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.HORIZONTAL));
        //final List<ProdVO> prod = getProductList();
        final List<ProdVO> prod = getProductList();
        recyclerView.setAdapter(new ProductCardAdapter(getActivity(), prod));

    }

    private boolean isLogIn(){
        boolean isLogIn;
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        isLogIn = loginState.getBoolean("login", false);
        return isLogIn;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        //顯示購物車,放大鏡
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_shopping_car).setVisible(isLogIn());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        private List<ProdVO> prodVOList;

        ProductCardAdapter(Context context, List<ProdVO> prodVOList) {
            this.context = context;
            this.prodVOList = prodVOList;
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
            return prodVOList.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.product_card_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {

            final ProdVO prodVO = prodVOList.get(position);
            Log.d("prodVOList",prodVOList.toString());
            String url = Common.PROD_URL;
            String prod_no = prodVO.getProd_no();
            String action = "prod_no";

            //取得圖片
            new GetImageByPkTask(url, action, prod_no, 200, viewHolder.cardImageView).execute();

            //把字卡大小限制在12字以內
            String cardProdName = prodVO.getProd_name();
            StringBuffer prodNameSim = new StringBuffer();
            int limitStr = 12;
            if(cardProdName.length() >= limitStr){
                for(int i = 0; i < limitStr; i++){
                    prodNameSim = prodNameSim.append(cardProdName.charAt(i));
                }
                prodNameSim.append("...");
                cardProdName = prodNameSim.toString();
            }

            viewHolder.cardProdName.setText(cardProdName);


            viewHolder.cardProdPrice.setText("$" + prodVO.getProd_price().toString());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ProductWithTab();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("prod", prodVO);
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

    List<ProdVO> getHotProd(){

        Set<ProdVO> hotProdVOSet = new HashSet<ProdVO>();
        String prodSetString = "";
        retrieveHotProdTask = (CommonTask) new CommonTask().execute(Common.PROD_URL,"getHotProd");
        try {
            prodSetString = retrieveHotProdTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ProdVO>>(){}.getType();
        return  gson.fromJson(prodSetString, listType);

    }

    List<ProdVO> getProductList(){
        List<ProdVO> prodList = new ArrayList<>();
        String prodListString = "";


        if (networkConnected()) {
            retrieveProdTask = (CommonTask) new CommonTask().execute(Common.PROD_URL, "getAll");
            try {
                prodListString = retrieveProdTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }else {
            ProdVO desCon = new ProdVO();
            desCon.setProd_no("P1000000000");
            desCon.setProd_name("無網路");
            desCon.setProd_price(404);
            prodList.add(desCon);
            prodList.add(desCon);
            prodList.add(desCon);
            prodList.add(desCon);
            prodList.add(desCon);
            prodList.add(desCon);
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ProdVO>>(){}.getType();
        prodList =  gson.fromJson(prodListString, listType);
        return prodList;
        
    }
    //廣告頁面
    private class AdPagerAdapter extends FragmentStatePagerAdapter {

        int nNumOfTabs;
        FragmentManager fm;

        List<AdvertFragment> fragmentLists;

//        fragmentLists = getAdList();
        public AdPagerAdapter(FragmentManager fm, List<AdvertFragment> fragmentLists)
        {
            super(fm);
            this.fragmentLists=fragmentLists;
        }
        @Override
        public Fragment getItem(int position) {

            return fragmentLists.get(position);
        }
        @Override
        public int getCount() {
            return fragmentLists.size();
        }
    }

    List<AdvertFragment> getAdList(){
        List<AdvertFragment> fragmentList = new ArrayList<>();
        List<AdVO> adVOList = new ArrayList<AdVO>();
        retrieveAdTask = (CommonTask) new CommonTask().execute(Common.AD_URL, "getAll");
        String adVOString = "";
        try {
            adVOString = retrieveAdTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<AdVO>>(){}.getType();
        adVOList =  gson.fromJson(adVOString, listType);

        for(AdVO adVO : adVOList){
            fragmentList.add(new AdvertFragment(adVO));
        }
        return fragmentList;
    }

    @Override
    public void onPause() {
        super.onPause();
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_shopping_car).setVisible(false);

    }

}
