package com.beanlife.search;

import android.companion.CompanionDeviceManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.R;
import com.beanlife.act.Fo_actVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by Java on 2017/8/30.
 * 搜尋頁面　傳送請求條件
 */

public class SearchFragment extends Fragment {
    private View prodPage,view;
    private Button resultButton;
    private Spinner spProdProc, spProdCountry, spProdRoast;
    private SearchView svProdName;
    private String bean_country, prodProc, roast, prod_name;
    private String[] queryString;
    private CommonTask getCountryTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.search_fragment, container, false);
        resultButton = view.findViewById(R.id.search_result);
        findView();
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prod_name = svProdName.getQuery().toString();
                prod_name = "%" + prod_name + "%";

                Log.d("Product Key Word", prod_name);

                queryString = new String[]{bean_country, prodProc, roast, prod_name};
                Fragment fragment = new SearchResultFragment();
                FragmentManager fragmentManager = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putSerializable("queryString", queryString);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        svProdName = (SearchView) view.findViewById(R.id.searchView_prodName);

        spProdProc = (Spinner)view.findViewById(R.id.spinner_prod_proc);
        prodProc = "%%";
        spProdProc.setSelection(0, true);
        spProdProc.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prodProc = adapterView.getItemAtPosition(i).toString();
                if(prodProc.equals("請選擇")){
                    prodProc = "%%";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spProdCountry = (Spinner)view.findViewById(R.id.spinner_prod_country);
        String[] countryString = getCountryString();
        Array.set(countryString, 0, "請選擇");

        ArrayAdapter<String> adapterCon = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, countryString);
        adapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProdCountry.setAdapter(adapterCon);
        bean_country = "%%";
        spProdCountry.setSelection(0, true);
        spProdCountry.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bean_country = adapterView.getItemAtPosition(i).toString();
                if(bean_country.equals("請選擇")){
                    bean_country = "%%";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spProdRoast = (Spinner)view.findViewById(R.id.spinner_prod_roast);
        roast = "%%";
        spProdRoast.setSelection(0, true);
        spProdRoast.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                roast = adapterView.getItemAtPosition(i).toString();
                if(roast.equals("請選擇")){
                    roast = "%%";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    String[] getCountryString(){
        Set<String> conSet = new HashSet<String>();
        String getConSetString = "";

        getCountryTask = (CommonTask) new CommonTask().execute(Common.PROD_URL,"getProdAtr");
        try {
            getConSetString = getCountryTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();

        Type listType = new TypeToken<String[]>(){}.getType();
        return gson.fromJson(getConSetString, listType);
    }


}
