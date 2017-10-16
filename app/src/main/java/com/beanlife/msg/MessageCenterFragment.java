package com.beanlife.msg;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.act.ActVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vivienhuang on 2017/10/13.
 */

public class MessageCenterFragment extends Fragment {
    private static final String TAG = "Member Message Fragment";
    private MessageCenterFragment.MemPairCardAdapter adapter;
    private CommonTask retrieveMemTask;
    private ActVO actVO;
    private List<String> memAcList;
    private String myName;
    private TextView noMsgTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.message_center_fragment, container, false);
        // final List<ProdVO> prod = getProductList();
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        myName = loginState.getString("userAc", "noLogIn");

        addRow(view, R.id.msg_mem_pair_rv);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addRow(View view, int viewId){

        RecyclerView recyclerView = (RecyclerView) view.findViewById(viewId);
        noMsgTv = (TextView) view.findViewById(R.id.no_msg_tv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        memAcList = getMemList();
        if(memAcList.size() == 0){
            noMsgTv.setVisibility(View.VISIBLE);
        } else {
            noMsgTv.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(new MessageCenterFragment.MemPairCardAdapter(getActivity(), memAcList));

    }

    private class MemPairCardAdapter extends
            RecyclerView.Adapter<MessageCenterFragment.MemPairCardAdapter.MyViewHolder>{

        private Context context;
        private List<String> memList;

        MemPairCardAdapter(Context context, List<String> memList) {
            this.context = context;
            this.memList = memList;
        }

        @Override
        public MessageCenterFragment.MemPairCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.member_message_pair_card, parent, false);
            return new MessageCenterFragment.MemPairCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return memList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView msgMemPairIv;
            TextView msgMemAcTv;

            MyViewHolder(View itemView) {
                super(itemView);
                msgMemPairIv = (ImageView) itemView.findViewById(R.id.msg_mem_pair_iv);
                msgMemAcTv = (TextView) itemView.findViewById(R.id.msg_mem_ac_tv);
            }
        }

        @Override
        public void onBindViewHolder(MessageCenterFragment.MemPairCardAdapter.MyViewHolder viewHolder, int position) {
            final String memAc = memList.get(position);

            new GetImageByPkTask(Common.MEM_URL, "mem_ac", memAc, 150, viewHolder.msgMemPairIv).execute();
            viewHolder.msgMemAcTv.setText(memAc);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new MsgFragment(myName, memAc);
//                    Fragment pFragment = getParentFragment();
//                    FragmentManager fragmentManager = pFragment.getFragmentManager();
                    FragmentManager fragmentManager = getFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
    }

    List<String> getMemList(){
        memAcList = new ArrayList<String>();

        String memPairString = "";

        if (Common.networkConnected(getActivity())) {
            retrieveMemTask =
                    (CommonTask) new CommonTask().execute(Common.MSG_URL, "getAllPairByMem", "mem_ac", myName);
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
        Type listType = new TypeToken<List<String>>(){}.getType();

        return gson.fromJson(memPairString, listType);
    }
}
