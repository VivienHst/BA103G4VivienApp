package com.beanlife.store;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.prod.ProdVO;
import com.beanlife.prod.ProductTotalFragment;
import com.beanlife.prod.ProductWithTab;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by Java on 2017/9/10.
 */

public class StorePageFragment extends Fragment{
    private static final String TAG = "Store fragment";
    private View view;
    private StoreVO storeVO;
    private ImageView storeImg;
    private TextView storeNameTv, storePhoneTv, storeAddTv, storeDescConTv;
    MapView storeMv;
    private GoogleMap googleMap;
    private LatLng myLocation;
    private Marker marker_myLocation;
    private CommonTask retrieveProdTask;

    public void getStoreVO(StoreVO storeVO){
        this.storeVO = storeVO;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.store_fragment, container, false);
        Log.d(TAG, "storeVO.getStore_no() : "+ storeVO.getStore_no());
        findView(storeVO);
        storeMv = (MapView) view.findViewById(R.id.store_mapView);
        storeMv.onCreate(savedInstanceState);
        storeMv.onResume();
        MapsInitializer.initialize(getActivity());
        googleMap = storeMv.getMap();
        setUpMap();
        addRow(R.id.store_prods_rv);
        //android:id="@+id/store_mapView"
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(StoreVO storeVO){

        storeNameTv = (TextView) view.findViewById(R.id.store_name);
        storePhoneTv = (TextView) view.findViewById(R.id.store_phone);
        storeAddTv = (TextView) view.findViewById(R.id.store_add);
        storeDescConTv = (TextView) view.findViewById(R.id.store_dsc_con);

        storeImg = (ImageView) view.findViewById(R.id.store_img);
        new GetImageByPkTask(Common.STORE_URL, "store_no", storeVO.getStore_no(), 500, storeImg).execute();

        storeNameTv.setText(storeVO.getStore_name());
        storePhoneTv.setText("電話：" + storeVO.getStore_phone());
        storeAddTv.setText("地址：" + storeVO.getStore_add());
        storeDescConTv.setText(storeVO.getStore_cont());

    }

    private void setUpMap(){
        myLocation = new LatLng(Double.parseDouble(storeVO.getStore_add_lat()),Double.parseDouble(storeVO.getStore_add_lon()));


        if(ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        googleMap.animateCamera(cameraUpdate);

        googleMap.addMarker(new MarkerOptions()
                        .position(myLocation )
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker)));
        googleMap.moveCamera(cameraUpdate);
    }

    private void addRow(int viewId){

        RecyclerView recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.HORIZONTAL));
        //final List<ProdVO> prod = getProductList();
        final List<ProdVO> prod = getProductList();
//        List<ProdVO> prodOnList = new ArrayList<ProdVO>();
//        for(ProdVO prodVO : prod){
//            if(prodVO.getProd_stat().equals("上架")){
//                prodOnList.add(prodVO);
//            }
//        }
        recyclerView.setAdapter(new StorePageFragment.ProductCardAdapter(getActivity(), prod));

    }

    private class ProductCardAdapter extends
            RecyclerView.Adapter<StorePageFragment.ProductCardAdapter.MyViewHolder> {
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
                cardImageView = (ImageView) itemView.findViewById(R.id.store_card_imageView);
                cardProdName = (TextView) itemView.findViewById(R.id.store_card_prodName);
                cardProdPrice = (TextView) itemView.findViewById(R.id.store_card_prodPrice);
            }
        }

        @Override
        public int getItemCount() {
            return prodVOList.size();
        }

        @Override
        public StorePageFragment.ProductCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.store_card_item, viewGroup, false);
            return new StorePageFragment.ProductCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(StorePageFragment.ProductCardAdapter.MyViewHolder viewHolder, int position) {

            final ProdVO prodVO = prodVOList.get(position);
            Log.d("prodVOList",prodVOList.toString());
            String url = Common.PROD_URL;
            String prod_no = prodVO.getProd_no();
            String action = "prod_no";

            //取得圖片
            new GetImageByPkTask(url, action, prod_no, 150, viewHolder.cardImageView).execute();

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
                    Fragment pFragment = getParentFragment();
                    FragmentManager fragmentManager = pFragment.getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

    }

    List<ProdVO> getProductList() {
        List<ProdVO> prodList = new ArrayList<>();
        String prodListString = "";

        retrieveProdTask = (CommonTask) new CommonTask().execute(Common.STORE_URL,
                "getProdByStore", "store_no", storeVO.getStore_no());
        try {
            prodListString = retrieveProdTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<ProdVO>>() {
        }.getType();
        prodList = gson.fromJson(prodListString, listType);
        return prodList;

    }

}
