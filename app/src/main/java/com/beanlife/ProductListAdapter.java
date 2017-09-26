package com.beanlife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vivien on 2017/8/25.
 */
//要放在結果頁面裡面，不要獨立出來
//改成RecyclerView比較不會有圖片載入的問題


public class ProductListAdapter extends BaseAdapter {
    private Context context;
    private List<ProdVO> prodVOList;


    public ProductListAdapter(Context context, List prodVOList) {
        this.context = context;
        this.prodVOList = prodVOList;
    }

    @Override
    public int getCount() {
        return prodVOList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View prodView, ViewGroup parent) {
        ProdVO prodVOLists = prodVOList.get(position);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if(prodView == null){
            prodView = layoutInflater.inflate(R.layout.prod_list,parent,false);
        }

        String rtProdName = prodVOLists.getProd_name();
        StringBuffer prodNameSim = new StringBuffer();
        int limitStr = 12;
        if(rtProdName.length() >= limitStr){
            for(int i = 0; i < limitStr; i++){
                prodNameSim = prodNameSim.append(rtProdName.charAt(i));
            }
            prodNameSim.append("...");
            rtProdName = prodNameSim.toString();
        }

        ImageView productIv = (ImageView) prodView.findViewById(R.id.ivProdSearchResult);
        String action = "prod_no";
        new GetImageByPkTask(Common.PROD_URL, action, prodVOLists.getProd_no(), 150, productIv).execute();

        TextView prodName = (TextView) prodView.findViewById(R.id.prodName);
        prodName.setText(rtProdName);

        TextView prodPrice = (TextView) prodView.findViewById(R.id.prodPrice);
        prodPrice.setText(prodVOLists.getProd_price().toString());

        TextView prodDesc = (TextView) prodView.findViewById(R.id.prodDesc);
        prodDesc.setText(prodVOLists.getProc());

        return prodView;
    }
}
