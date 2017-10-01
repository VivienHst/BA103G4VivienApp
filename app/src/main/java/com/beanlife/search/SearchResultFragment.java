package com.beanlife.search;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.prod.ProdGetQueryTask;
import com.beanlife.prod.ProdVO;
import com.beanlife.prod.ProductWithTab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by vivienhuang on 2017/9/20.
 */

public class SearchResultFragment extends Fragment {
    private static final String TAG = "Search Result Fragment";
    private ListView prodList;
    private SearchResultFragment.ProductCardAdapter adapter;
    private AsyncTask retrieveProdTask;
    private List<ProdVO> prodResultList;
    private String prodProc;
    String[] queryString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        queryString = (String[]) getArguments().getSerializable("queryString");
        View view = inflater.inflate(R.layout.search_result_fragment, container, false);
       // final List<ProdVO> prod = getProductList();
        addRow(view, R.id.rv_search_result_card);
        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addRow(View view, int viewId){

        RecyclerView recyclerView = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        final List<ProdVO> prod = getProductList();
        recyclerView.setAdapter(new SearchResultFragment.ProductCardAdapter(getActivity(), prod));

    }

    private class ProductCardAdapter extends
            RecyclerView.Adapter<SearchResultFragment.ProductCardAdapter.MyViewHolder>{

        private Context context;
        private List<ProdVO> prodVOList;

        ProductCardAdapter(Context context, List<ProdVO> prodVOList) {
            this.context = context;
            this.prodVOList = prodVOList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.search_result_card, parent, false);
            return new SearchResultFragment.ProductCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return prodVOList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView prodSearchResultIv;
            TextView prodNameTv, prodPriceTv;

            MyViewHolder(View itemView) {
                super(itemView);
                prodSearchResultIv = (ImageView) itemView.findViewById(R.id.prod_search_result_iv);
                prodNameTv = (TextView) itemView.findViewById(R.id.search_result_prod_name);
                prodPriceTv = (TextView) itemView.findViewById(R.id.search_result_prod_price);
            }
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            final ProdVO prodVO = prodVOList.get(position);
            //viewHolder.cardImageView.setImageResource(ActivityCard.getActImg());

            //****有網路再開
            String action = "prod_no";
            new GetImageByPkTask(Common.PROD_URL, action, prodVO.getProd_no(), 150, viewHolder.prodSearchResultIv).execute();
            viewHolder.prodNameTv.setText(prodVO.getProd_name());
            viewHolder.prodPriceTv.setText(prodVO.getProd_price().toString());


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
                    System.out.println("Product onclick");
                }
            });
        }

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        setHasOptionsMenu(true);
//    }

    @Override
    public void onPause() {
        super.onPause();
        setHasOptionsMenu(false);
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        ProdVO prodVO = prodResultList.get(position);
//        Fragment fragment = new ProductWithTab();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("prod", prodVO);
//        fragment.setArguments(bundle);
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.body, fragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//        System.out.println("Product onclick");
//    }


    //確認網路是否連接
    private boolean networkConnected() {
        ConnectivityManager conManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    List<ProdVO> getProductList(){
        prodResultList = new ArrayList<>();
        List<ProdVO> prodFilterList;

        if (networkConnected()) {
            retrieveProdTask =
                    new ProdGetQueryTask(Common.PROD_URL, queryString[0], queryString[1], queryString[2], queryString[3])
                            .execute(Common.PROD_URL);
            try {
                prodFilterList = (List) retrieveProdTask.get();
                for(ProdVO list : prodFilterList){
                    prodResultList.add(list);
                }
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
            prodResultList.add(desCon);
            prodResultList.add(desCon);
            prodResultList.add(desCon);
            prodResultList.add(desCon);
            prodResultList.add(desCon);
            prodResultList.add(desCon);
        }
        return prodResultList;
    }


}
