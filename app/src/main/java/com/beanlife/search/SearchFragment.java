package com.beanlife.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.beanlife.R;

/**
 * Created by Java on 2017/8/30.
 * 搜尋頁面　傳送請求條件
 */

public class SearchFragment extends Fragment {
    private View prodPage,view;
    private Button resultButton;
    private Spinner spProdProc, spProdContry, spProdRoast;
    private SearchView svProdName;
    private String bean_contry, prodProc, roast, prod_name;
    private String[] queryString;

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

                queryString = new String[]{bean_contry, prodProc, roast, prod_name};
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

        spProdContry = (Spinner)view.findViewById(R.id.spinner_prod_contry);
        bean_contry = "%%";
        spProdContry.setSelection(0, true);
        spProdContry.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bean_contry = adapterView.getItemAtPosition(i).toString();
                if(bean_contry.equals("請選擇")){
                    bean_contry = "%%";
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

}
