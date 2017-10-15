package com.beanlife.act;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Java on 2017/8/27.
 * 主要活動頁面，產生活動頁面
 */

public class ActivityFragment extends Fragment {

    private View view;
    private ActivityFragment.ActivityCardAdapter adapter;
    private CommonTask retrieveActTask;
    private final static String TAG = "SearchActivity";
    private SearchView actSv;
    private RecyclerView recyclerView;
    private List<ActVO> act;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_fragment_layout, container, false);
        addRow(R.id.rv_act_card);
        getSearchResult(act);
        return view;
    }

    void getSearchResult(final List<ActVO> act){
        actSv = (SearchView) view.findViewById(R.id.act_search_sv);

        actSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CharSequence keyWord = actSv.getQuery();
                List<ActVO> list = new ArrayList<ActVO>();
                for (ActVO actVO : act) {
                    if (actVO.getAct_name().contains(keyWord)) {
                        list.add(actVO);
                    }
                }
                recyclerView.setAdapter(new ActivityFragment.ActivityCardAdapter(getActivity(), list));
                return false;
            }
        });
    }

    private void addRow(int viewId){
        recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        2, StaggeredGridLayoutManager.VERTICAL));
        act = getActivityList();
        recyclerView.setAdapter(new ActivityFragment.ActivityCardAdapter(getActivity(), act));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class ActivityCardAdapter extends
            RecyclerView.Adapter<ActivityFragment.ActivityCardAdapter.MyViewHolder> {
        private Context context;
        private List<ActVO> actList;

        ActivityCardAdapter(Context context, List<ActVO> actList) {
            this.context = context;
            this.actList = actList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView cardImageView;
            TextView cardMemName, cardMemLv;

            MyViewHolder(View itemView) {
                super(itemView);
                cardImageView = (ImageView) itemView.findViewById(R.id.actImg);
                cardMemName = (TextView) itemView.findViewById(R.id.actName);
                cardMemLv = (TextView) itemView.findViewById(R.id.actDate);
            }
        }

        @Override
        public int getItemCount() {
            return actList.size();
        }

        @Override
        public ActivityFragment.ActivityCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.activity_card, viewGroup, false);
            return new ActivityFragment.ActivityCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ActivityFragment.ActivityCardAdapter.MyViewHolder viewHolder, int position) {
            final ActVO actVO = actList.get(position);

            String action = "act_no";
            new GetImageByPkTask(Common.ACT_URL, action, actVO.getAct_no(), 256, viewHolder.cardImageView).execute();

            viewHolder.cardMemName.setText(actVO.getAct_name());

//            viewHolder.cardMemLv.setText(opDate);
            viewHolder.cardMemLv.setText(actVO.getAct_op_date());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ActivityPageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("actVO", actVO);
                    Log.d("actVO", actVO.getAct_name());
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    System.out.println("Activity onclick");
                }
            });
        }
    }

    List<ActVO> getActivityList(){
        String actListString = "";
        if(Common.networkConnected(getActivity())){
            retrieveActTask = (CommonTask) new CommonTask().execute(Common.ACT_URL, "getAllAct",
                    "act_stat", "可報名'or act_stat='已成團");
            try {
                actListString = retrieveActTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ActVO>>(){}.getType();
        return gson.fromJson(actListString, listType);
    }
}
