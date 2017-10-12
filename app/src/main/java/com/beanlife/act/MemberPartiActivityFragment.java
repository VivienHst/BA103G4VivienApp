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
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Java on 2017/8/27.
 * 會員活動頁面，產生活動頁面
 * 已連結資料庫，狀態未設定
 */

public class MemberPartiActivityFragment extends Fragment {

    private MemberPartiActivityFragment.ActivityCardAdapter adapter;
    private CommonTask retrieveActTask, retrievePartiActTask;
    private final static String TAG = "Member Activity";
    private String mem_ac;
    private View view;
    private SearchView actSv;
    private RecyclerView recyclerView;
    private List<ActVO> act;

    public static MemberPartiActivityFragment newInstance() {
        MemberPartiActivityFragment f = new MemberPartiActivityFragment();
        return f;
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
                recyclerView.setAdapter(new MemberPartiActivityFragment.ActivityCardAdapter(getActivity(), list));
                return false;
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_fragment_layout, container, false);
        addRow(view, R.id.rv_act_card);
        getSearchResult(act);
        return view;
    }

    private void addRow(View view, int viewId){
        recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));
        act = getActivityList();
        for(ActVO actVO : act){
            if(actVO.getMem_ac().equals(mem_ac)){
                act.remove(actVO);
            }
        }
        recyclerView.setAdapter(new MemberPartiActivityFragment.ActivityCardAdapter(getActivity(), act));

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

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class ActivityCardAdapter extends
            RecyclerView.Adapter<MemberPartiActivityFragment.ActivityCardAdapter.MyViewHolder> {
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
        public MemberPartiActivityFragment.ActivityCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.activity_card, viewGroup, false);
            return new MemberPartiActivityFragment.ActivityCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MemberPartiActivityFragment.ActivityCardAdapter.MyViewHolder viewHolder, int position) {
            final ActVO actVO = actList.get(position);
//            viewHolder.cardImageView.setImageResource(ActivityCard.getActImg());
//            viewHolder.cardImageView.setImageResource(R.drawable.activity01);
            String action = "act_no";
            new GetImageByPkTask(Common.ACT_URL, action, actVO.getAct_no(), 256, viewHolder.cardImageView).execute();

            viewHolder.cardMemName.setText(actVO.getAct_name());

            DateFormat inputDate = new SimpleDateFormat("MM月 dd, yyyy");
            DateFormat outDate  = new SimpleDateFormat("yyyy-MM-dd");
            String opDate="";
            try {
                Date convertDate = (Date) inputDate.parse(actVO.getAct_op_date());
                opDate = outDate.format(convertDate).toString()+"++";
            } catch (ParseException e) {
                e.printStackTrace();
            }

            viewHolder.cardMemLv.setText(actVO.getAct_op_date());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new MemberPartiActivityContFragment();
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
        List<Act_pairVO> partiList = new ArrayList<Act_pairVO>();
        partiList = getPartiActivityList();

        for(Act_pairVO parti_actVO : partiList) {
            ActVO actVO = new ActVO();
            String act_no;
            String actListString = "";
            if (networkConnected()) {
                retrieveActTask = (CommonTask) new CommonTask().execute(Common.ACT_URL, "getOne", "act_no", parti_actVO.getAct_no());

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

    List <Act_pairVO> getPartiActivityList(){
        //List<Fo_actVO> actFoList = new ArrayList<>();
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        mem_ac = loginState.getString("userAc", "noLogIn");

        String actListPartiString = "";
        if(networkConnected()){
            retrievePartiActTask = (CommonTask) new CommonTask().execute(Common.ACT_URL, "getPartiAct" ,
                    "mem_ac", mem_ac);
            try {
                actListPartiString = retrievePartiActTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Act_pairVO>>(){}.getType();
        return gson.fromJson(actListPartiString, listType);
    }


}

