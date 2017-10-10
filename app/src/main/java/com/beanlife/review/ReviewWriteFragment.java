package com.beanlife.review;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.ord.MemberOrderFragment;
import com.beanlife.ord.OrderFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by vivienhuang on 2017/9/23.
 */

public class ReviewWriteFragment extends Fragment {
    View view;
    TextView prodNameTv;
    EditText reviewWeightEt, reviewWaterEt, reviewTempEt, reviewTimeEt, reviewContEt;
    RatingBar prodScoreRb;
    ReviewVO reviewVO;
    Button submitReviewBt, magicBt;
    String useWay, revCont, reviewWeight, reviewWater, reviewTemp, reviewTime, reviewCont;
    Integer score;
    ImageView prodImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.review_add_fragment, container, false);

        findView();

        submitReviewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getTextCont();
                if(checkCont()){
                    Log.d("ReviewVo", reviewVO.getOrd_no().toString());
                    if(networkConnected()){
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action","insertReview");
                        jsonObject.addProperty("reviewVO", new Gson().toJson(reviewVO));

                        try {
                            String result =
                                    new CommonTask()
                                            .execute(Common.REVIEW_URL,"insertReview","reviewVO",
                                                    new Gson().toJson(reviewVO)).get();

                            Log.d("ReviewVo", result);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(view.getContext(), "已新增成功", Toast.LENGTH_SHORT).show();
                    Fragment fragment = new MemberOrderFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void findView(){
        prodNameTv = (TextView) view.findViewById(R.id.review_prod_name_tv);
        reviewWeightEt = (EditText) view.findViewById(R.id.review_weight_cont);
        reviewWaterEt = (EditText) view.findViewById(R.id.review_water_cont);
        reviewTempEt = (EditText) view.findViewById(R.id.review_temp_cont);
        reviewTimeEt = (EditText) view.findViewById(R.id.review_time_cont);
        reviewContEt = (EditText) view.findViewById(R.id.review_cont_et);
        prodImageView = (ImageView) view.findViewById(R.id.review_prod_itme_iv);

        prodScoreRb = (RatingBar) view.findViewById(R.id.review_score_rb);

        submitReviewBt = (Button) view.findViewById(R.id.submit_review_button);
        magicBt = (Button) view.findViewById(R.id.magic_review_button);

        prodNameTv.setText(getArguments().getSerializable("prod_name").toString());
        new GetImageByPkTask(Common.PROD_URL, "prod_no",
                getArguments().getSerializable("prod_no").toString(), 200, prodImageView).execute();

        magicBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reviewWeightEt.setText("20");
                reviewWaterEt.setText("220");
                reviewTempEt.setText("120");
                reviewTimeEt.setText("300");
                reviewContEt.setText("好好喝");
            }
        });
    }

    private void getTextCont(){

        reviewWeight = reviewWeightEt.getText().toString().trim();
        reviewWater = reviewWaterEt.getText().toString().trim();
        reviewTemp =  reviewTempEt.getText().toString().trim();
        reviewTime = reviewTimeEt.getText().toString().trim();
        reviewCont = reviewContEt.getText().toString().trim();

        useWay = reviewWeight + "," + reviewWater
                + "," + reviewTemp + "," + reviewTime;

        reviewVO = new ReviewVO();
        //取得現在時間
        Calendar addDate = Calendar.getInstance();
        int addYear = addDate.get(Calendar.YEAR);
        int addMonth = addDate.get(Calendar.MONTH) + 1;
        int addDay = addDate.get(Calendar.DATE);

        score = (int)prodScoreRb.getRating();
        reviewVO.setProd_score(score);
        reviewVO.setUse_way(useWay);
        reviewVO.setRev_cont(reviewCont);
        reviewVO.setRev_date(addYear + "-" + addMonth + "-" + addDay);
        reviewVO.setProd_no(getArguments().getSerializable("prod_no").toString());
        reviewVO.setOrd_no(getArguments().getSerializable("ord_no").toString());
    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean checkCont(){

        boolean checkResult = false;

        if(score < 1){
            Toast.makeText(view.getContext(), "請點選星星給予評分", Toast.LENGTH_SHORT).show();
        } else if(reviewWeight.isEmpty()){
            Toast.makeText(view.getContext(), "請輸入沖泡的重量", Toast.LENGTH_SHORT).show();
        } else if(reviewWater.isEmpty()){
            Toast.makeText(view.getContext(), "請輸入沖泡的水量", Toast.LENGTH_SHORT).show();
        } else if(reviewTemp.isEmpty()){
            Toast.makeText(view.getContext(), "請輸入沖泡的溫度", Toast.LENGTH_SHORT).show();
        } else if(reviewTime.isEmpty()){
            Toast.makeText(view.getContext(), "請輸入沖泡的時間", Toast.LENGTH_SHORT).show();
        } else if(reviewCont.isEmpty()){
            Toast.makeText(view.getContext(), "請輸入品嚐心得", Toast.LENGTH_SHORT).show();
        } else {
            checkResult = true;
        }

        return checkResult;
    }
}
