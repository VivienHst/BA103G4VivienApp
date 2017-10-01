package com.beanlife.prod;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.beanlife.Common;
import com.beanlife.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Java on 2017/8/31.
 */

public class ProductSearchResultFragment extends ListFragment {
    private ListView prodList;
    private ProductListAdapter adapter;
    private final static String TAG = "SearchActivity";
    private AsyncTask retrieveProdTask;
    private List<ProdVO> prodResultList;
    private String prodProc;
    String[] queryString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        queryString = (String[]) getArguments().getSerializable("queryString");
        View view = inflater.inflate(R.layout.search_result_fragment, container, false);
        final List<ProdVO> prod = getProductList();
        prodList = view.findViewById(android.R.id.list);
        prodList.setAdapter(new ProductListAdapter(getActivity(), prod));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setHasOptionsMenu(false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ProdVO prodVO = prodResultList.get(position);
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

//    class RetrieveProdTask extends AsyncTask<String, Void, List<ProdVO>> {
//
//        @Override
//        protected List<ProdVO> doInBackground(String... param) {
//            String url = param[0];
//            String jsonIn;
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAll");
//            try{
//                jsonIn = getRemoteData(url, jsonObject.toString());
//            }catch (IOException e){
//                Log.e(TAG, e.toString());
//                return null;
//            }
//
//            Gson gson = new Gson();
//            Type listType = new TypeToken<List<ProdVO>>(){}.getType();
//            return gson.fromJson(jsonIn, listType);
//        }
//    }
//
//    private String getRemoteData(String url, String jsonOut) throws IOException {
//        StringBuilder jsonIn = new StringBuilder();
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setDoInput(true); // allow inputs
//        connection.setDoOutput(true); // allow outputs
//        connection.setUseCaches(false); // do not use a cached copy
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("charset", "UTF-8");
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
//        bw.write(jsonOut);
//        Log.d(TAG, "jsonOut: " + jsonOut);
//        bw.close();
//
//        int responseCode = connection.getResponseCode();
//
//        if (responseCode == 200) {
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line;
//            while ((line = br.readLine()) != null) {
//                jsonIn.append(line);
//            }
//        } else {
//            Log.d(TAG, "response code: " + responseCode);
//        }
//        connection.disconnect();
//        Log.d(TAG, "jsonIn: " + jsonIn);
//        return jsonIn.toString();
//    }

}

