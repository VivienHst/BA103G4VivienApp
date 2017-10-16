package com.beanlife.act;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.msg.MsgFragment;
import com.beanlife.prod.ProdGetQueryTask;
import com.beanlife.prod.ProdVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by vivienhuang on 2017/10/2.
 * 舉辦活動參加活動人員列表
 */

public class MemberHostActivityListFragment  extends Fragment {
    private static final String TAG = "Member List Fragment";
    private MemberHostActivityListFragment.MemPairCardAdapter adapter;
    private CommonTask retrieveMemTask;
    private ActVO actVO;
    private String mem_ac;
    private RecyclerView recyclerView;

    MemberHostActivityListFragment(String mem_ac){
        this.mem_ac = mem_ac;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        actVO = new ActVO();
        actVO = (ActVO) getArguments().getSerializable("actVO");
        View view = inflater.inflate(R.layout.member_activity_host_list_fargment, container, false);
        addRow(view, R.id.act_mem_pair_rv);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addRow(View view, int viewId){

        recyclerView = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        final List<Act_pairVO> act_pairVOList = getMemPairList();

        recyclerView.setAdapter(new MemberHostActivityListFragment.MemPairCardAdapter(getActivity(), act_pairVOList));
    }

    private class MemPairCardAdapter extends
            RecyclerView.Adapter<MemberHostActivityListFragment.MemPairCardAdapter.MyViewHolder>{

        private Context context;
        private List<Act_pairVO> act_pairVOs;

        MemPairCardAdapter(Context context, List<Act_pairVO> act_pairVOs) {
            this.context = context;
            this.act_pairVOs = act_pairVOs;
        }

        @Override
        public MemberHostActivityListFragment.MemPairCardAdapter.MyViewHolder onCreateViewHolder
                (ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.member_activity_pair_card, parent, false);
            return new MemberHostActivityListFragment.MemPairCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return act_pairVOs.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView actMemPairIv;
            TextView actMemAcTv, actMemPayStateTV;
            CardView pairCv;

            MyViewHolder(View itemView) {
                super(itemView);
                actMemPairIv = (ImageView) itemView.findViewById(R.id.act_mem_pair_iv);
                actMemAcTv = (TextView) itemView.findViewById(R.id.act_mem_ac_tv);
                actMemPayStateTV = (TextView) itemView.findViewById(R.id.act_mem_pay_state_tv);
                pairCv = (CardView) itemView.findViewById(R.id.act_mem_pair_card);
            }
        }

        @Override
        public void onBindViewHolder(MemberHostActivityListFragment.MemPairCardAdapter.MyViewHolder viewHolder, int position) {
            final Act_pairVO act_pairVO = act_pairVOs.get(position);

            if(Common.networkConnected(getActivity())){
                new GetImageByPkTask(Common.MEM_URL, "mem_ac", act_pairVO.getMem_ac(), 150, viewHolder.actMemPairIv).execute();
            }
            viewHolder.actMemAcTv.setText(act_pairVO.getMem_ac());
            viewHolder.actMemPayStateTV.setText(act_pairVO.getPay_state() + " / " + act_pairVO.getChk_state());
            if(act_pairVO.getChk_state().equals("已報到")){
                viewHolder.pairCv.setBackgroundColor(Color.parseColor("#fff5c4"));
            } else {
                viewHolder.pairCv.setBackgroundColor(Color.parseColor("#efdeca"));
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new MsgFragment(actVO.getMem_ac(), act_pairVO.getMem_ac());
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

    List<Act_pairVO> getMemPairList(){
        String memPairString = "";

        if (Common.networkConnected(getActivity())) {
            retrieveMemTask =
                    (CommonTask) new CommonTask().execute(Common.ACT_URL, "getMemPair", "act_no",
                            actVO.getAct_no(), "mem_ac", mem_ac);
            try {
                memPairString = retrieveMemTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }else {
            Log.d(TAG, "net not connect");
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Act_pairVO>>(){}.getType();

        return gson.fromJson(memPairString, listType);
    }
}
