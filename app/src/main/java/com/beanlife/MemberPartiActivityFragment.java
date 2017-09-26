package com.beanlife;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

/**
 * Created by Java on 2017/8/27.
 * 會員活動頁面，產生活動頁面
 * 尚未連結資料庫
 */

public class MemberPartiActivityFragment extends Fragment {

    private ListView actList;
    private MemberPartiActivityFragment.ActivityCardAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_fragment_layout, container, false);

        addRow(view, R.id.rv_act_card);

        return view;
    }

    private void addRow(View view,int viewId){
        RecyclerView recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));
        final List<ActivityCardItem> act = getActivityList();
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

    private class ActivityCardAdapter extends
            RecyclerView.Adapter<MemberPartiActivityFragment.ActivityCardAdapter.MyViewHolder> {
        private Context context;
        private List<ActivityCardItem> ActivityCardList;

        ActivityCardAdapter(Context context, List<ActivityCardItem> ActivityCardList) {
            this.context = context;
            this.ActivityCardList = ActivityCardList;
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
            return ActivityCardList.size();
        }

        @Override
        public MemberPartiActivityFragment.ActivityCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.activity_card, viewGroup, false);
            return new MemberPartiActivityFragment.ActivityCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MemberPartiActivityFragment.ActivityCardAdapter.MyViewHolder viewHolder, int position) {
            final ActivityCardItem ActivityCard = ActivityCardList.get(position);
            viewHolder.cardImageView.setImageResource(ActivityCard.getActImg());
            viewHolder.cardMemName.setText(ActivityCard.getActName());
            viewHolder.cardMemLv.setText(ActivityCard.getActDate());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Fragment fragment = new ProductPageWithTab(); //切換成有TAB的形式 >> 未完成
                    Fragment fragment = new ActivityPageFragment();
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

    List<ActivityCardItem> getActivityList(){
        List<ActivityCardItem> ActivityLists = new ArrayList<>();
        ActivityLists.add(new ActivityCardItem(R.drawable.activity01, "拉花教學", "2017-7-20"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity03, "烘豆教學", "2017-7-30"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity02, "手沖達人分享", "2017-2-30"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity03, "Java教學", "2017-4-31"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity01, "專題展示", "2017-10-20"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity02, "拉花教學", "2017-7-20"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity03, "烘豆教學", "2017-7-30"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity01, "手沖達人分享", "2017-2-30"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity02, "Java教學", "2017-4-31"));
        ActivityLists.add(new ActivityCardItem(R.drawable.activity03, "專題展示", "2017-10-20"));
        return ActivityLists;
    }

}

