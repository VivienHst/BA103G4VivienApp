package com.beanlife;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Java on 2017/9/3.
 * 商品頁面中的評論頁面
 * 需要改檔名
 */

public class CommentFragment extends Fragment {

    private List<ReviewVO> reviewVOList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.comm_fragment, container, false);

        addRow(view, R.id.rv_comm_card);

        return view;
    }

    public void getReviewVOList(List<ReviewVO> prodReviewVOList){
        this.reviewVOList = new ArrayList<>();
        this.reviewVOList = prodReviewVOList;
        Log.d("this.reviewVOList",this.reviewVOList.toString());
    }

    private void addRow(View view,int viewId){
        RecyclerView recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));
        final List<ReviewVO> comm = reviewVOList;
        recyclerView.setAdapter(new CommentFragment.CommentCardAdapter(getActivity(), comm));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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

    private class CommentCardAdapter extends
            RecyclerView.Adapter<CommentCardAdapter.MyViewHolder> {
        private Context context;
        private List<ReviewVO> reviewVOList;

        CommentCardAdapter(Context context, List<ReviewVO> reviewVOList) {
            this.context = context;
            this.reviewVOList = reviewVOList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView memImageIv;
            TextView memNameTv, memLvTv, commDateTv, commLikeTv,
                    commWeightTv, commWaterTv, commDegreeTv, commTimeTv;

            MyViewHolder(View itemView) {
                super(itemView);
                memImageIv = (ImageView) itemView.findViewById(R.id.commMemIv);
                memNameTv = (TextView) itemView.findViewById(R.id.comMemTv);
                memLvTv = (TextView) itemView.findViewById(R.id.memLvTv);
                commDateTv = (TextView) itemView.findViewById(R.id.commDateTv);
                commLikeTv = (TextView) itemView.findViewById(R.id.commLikeTv);
                commWeightTv = (TextView) itemView.findViewById(R.id.commWeightTv);
                commWaterTv = (TextView) itemView.findViewById(R.id.commWaterTv);
                commDegreeTv = (TextView) itemView.findViewById(R.id.commDegreeTv);
                commTimeTv = (TextView) itemView.findViewById(R.id.commTimeTv);
            }
        }

        @Override
        public int getItemCount() {
            return reviewVOList.size();
        }

        @Override
        public CommentFragment.CommentCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.product_comm_fragment, viewGroup, false);
            return new CommentFragment.CommentCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            final ReviewVO reviewVO = reviewVOList.get(position);

            String useWay = reviewVO.getUse_way();
            String[] useWayToken = useWay.split(",");
            int useSecs = Integer.parseInt(useWayToken[3]);
            int useMins = useSecs/60;
            useSecs = useSecs%60;
            viewHolder.memImageIv.setImageResource(R.drawable.mem01);
            viewHolder.memNameTv.setText("TestName");
            viewHolder.memLvTv.setText("TestLv");
            viewHolder.commDateTv.setText(reviewVO.getRev_date());
            viewHolder.commLikeTv.setText(getCount(reviewVO.getRev_no()));
            viewHolder.commWeightTv.setText(useWayToken[0] + " g");
            viewHolder.commWaterTv.setText(useWayToken[1] + " ml");
            viewHolder.commDegreeTv.setText(useWayToken[2] + " ℃");
            //viewHolder.commTimeTv.setText(useWayToken[3] + " s");
            viewHolder.commTimeTv.setText(useMins + " m " + useSecs + " s");

        }
    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getCount(String rev_no){
        String count = null;
        if(networkConnected()){

            try {
                count = new RetrieveCountTask("rev_no", rev_no).execute(Common.LIKE_REV_URL).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("get Review Count", "Network Not Connect");
        }
        return count;

    }
}
