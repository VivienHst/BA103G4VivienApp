package com.beanlife.review;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.RetrieveCountTask;
import com.beanlife.mem.MemVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Java on 2017/9/3.
 * 商品頁面中的評論頁面
 */

public class ReviewFragment extends Fragment {
    private CommonTask retrieveMemTask;
    String memGrade;

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
        recyclerView.setAdapter(new ReviewFragment.CommentCardAdapter(getActivity(), comm));
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
            RatingBar memReviewRb;

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
                memReviewRb = (RatingBar) itemView.findViewById(R.id.mem_prod_review_rb);
            }
        }

        @Override
        public int getItemCount() {
            return reviewVOList.size();
        }

        @Override
        public ReviewFragment.CommentCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.product_comm_fragment, viewGroup, false);
            return new ReviewFragment.CommentCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            final ReviewVO reviewVO = reviewVOList.get(position);

            String useWay = reviewVO.getUse_way();
            String[] useWayToken = useWay.split(",");
            int useSecs = Integer.parseInt(useWayToken[3]);
            int useMins = useSecs/60;
            useSecs = useSecs%60;
            //viewHolder.memImageIv.setImageResource(R.drawable.mem01);
            viewHolder.memNameTv.setText(getMemVO(reviewVO.getOrd_no()).getMem_ac());
            viewHolder.memLvTv.setText(memGrade);


            new GetImageByPkTask(Common.MEM_URL, "mem_ac", getMemVO(reviewVO.getOrd_no()).getMem_ac(), 150, viewHolder.memImageIv).execute();
            viewHolder.commDateTv.setText(reviewVO.getRev_date());
            viewHolder.memReviewRb.setRating(reviewVO.getProd_score());
            viewHolder.commLikeTv.setText(getCount(reviewVO.getRev_no()));
            viewHolder.commWeightTv.setText(useWayToken[0] + " g");
            viewHolder.commWaterTv.setText(useWayToken[1] + " ml");
            viewHolder.commDegreeTv.setText(useWayToken[2] + " ℃");
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

    private MemVO getMemVO(String ord_no){
        MemVO memVOl = new MemVO();
        Map<String,Object> memWithGrade = new HashMap<String,Object>();

        String getMemString = "";
        retrieveMemTask = (CommonTask) new CommonTask().execute(Common.MEM_URL, "getMemByOrd", "ord_no" ,ord_no);
        try {
            getMemString = retrieveMemTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<HashMap<String,Object>>(){}.getType();
        Type listTypeMemVO = new TypeToken<MemVO>(){}.getType();
        memWithGrade = gson.fromJson(getMemString, listType);
        memVOl = gson.fromJson(gson.toJson(memWithGrade.get("memVO")), listTypeMemVO);

        //取得對應等級稱號
        memGrade = memWithGrade.get("gradeTitle").toString();

        return memVOl;

    }

}
