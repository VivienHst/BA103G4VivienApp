package com.beanlife.act;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vivienhuang on 2017/9/30.
 */

public class MemberFollowActivityFragment extends Fragment {

    private MemberFollowActivityFragment.ActivityCardAdapter adapter;
    private CommonTask retrieveActTask, retrieveFoActTask;
    private final static String TAG = "mem";
    private String mem_ac;
    private List<ActVO> act;
    private SearchView actFoSv;
    private View view;
    private RecyclerView recyclerView;
    private TextView noActTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_fragment_layout, container, false);

        addRow(view, R.id.rv_act_card);
        getSearchResult(act);
        return view;
    }

    void getSearchResult(final List<ActVO> act){
        actFoSv = (SearchView) view.findViewById(R.id.act_search_sv);

        actFoSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CharSequence keyWord = actFoSv.getQuery();
                List<ActVO> list = new ArrayList<ActVO>();
                for (ActVO actVO : act) {
                    if (actVO.getAct_name().contains(keyWord)) {
                        list.add(actVO);
                        noActTv.setVisibility(View.GONE);
                    }else {
                        noActTv.setText("找不到您搜尋的活動");
                        noActTv.setVisibility(View.VISIBLE);
                    }
                }
                recyclerView.setAdapter(new MemberFollowActivityFragment.ActivityCardAdapter(getActivity(), list));
                return false;
            }
        });
    }

    private void addRow(View view, int viewId){
        recyclerView  = (RecyclerView) view.findViewById(viewId);
        noActTv = (TextView) view.findViewById(R.id.no_my_act_tv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        2, StaggeredGridLayoutManager.VERTICAL));
        act = getActivityList();

        if(act.size() == 0){
            noActTv.setText("您沒有收藏的活動");
            noActTv.setVisibility(View.VISIBLE);
        } else {
            noActTv.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(new MemberFollowActivityFragment.ActivityCardAdapter(getActivity(), act));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu, menu);
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

    private class ActivityCardAdapter extends
            RecyclerView.Adapter<MemberFollowActivityFragment.ActivityCardAdapter.MyViewHolder> {
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
        public MemberFollowActivityFragment.ActivityCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.activity_card, viewGroup, false);
            return new MemberFollowActivityFragment.ActivityCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MemberFollowActivityFragment.ActivityCardAdapter.MyViewHolder viewHolder, int position) {
            final ActVO actVO = actList.get(position);
//            viewHolder.cardImageView.setImageResource(ActivityCard.getActImg());
//            viewHolder.cardImageView.setImageResource(R.drawable.activity01);
            String action = "act_no";
            new GetImageByPkTask(Common.ACT_URL, action, actVO.getAct_no(), 256, viewHolder.cardImageView).execute();

            viewHolder.cardMemName.setText(actVO.getAct_name());

            viewHolder.cardMemLv.setText(actVO.getAct_op_date());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ActivityPageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("actVO", actVO);
                    Log.d("actVO", actVO.getAct_name());
                    fragment.setArguments(bundle);
                    Fragment pFragment = getParentFragment();
                    FragmentManager fragmentManager = pFragment.getFragmentManager();
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
        List<ActVO> actList = new ArrayList<>();
        List<Fo_actVO> foList = new ArrayList<Fo_actVO>();
        foList = getFoActivityList();

        for(Fo_actVO fo_actVO : foList) {
            ActVO actVO = new ActVO();
            String actListString = "";
            if (Common.networkConnected(getActivity())) {
                retrieveActTask = (CommonTask) new CommonTask().execute(Common.ACT_URL, "getOne", "act_no", fo_actVO.getAct_no());

                try {
                    actListString = retrieveActTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ActVO>() {
            }.getType();
            actVO = gson.fromJson(actListString, listType);
            actList.add(actVO);
        }
        return actList;
    }

    List <Fo_actVO> getFoActivityList(){
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        mem_ac = loginState.getString("userAc", "noLogIn");

        String actListFoString = "";
        if(Common.networkConnected(getActivity())){
            retrieveFoActTask = (CommonTask) new CommonTask().execute(Common.ACT_URL, "getFoAct" ,
                    "mem_ac", mem_ac);
            try {
                actListFoString = retrieveFoActTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Fo_actVO>>(){}.getType();
        return gson.fromJson(actListFoString, listType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

