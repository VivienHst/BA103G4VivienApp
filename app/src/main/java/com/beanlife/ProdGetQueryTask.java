package com.beanlife;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.List;

/**
 * Created by Java on 2017/9/14.
 */

public class ProdGetQueryTask extends AsyncTask<Object, Integer, List<ProdVO>> {

    private final static String TAG = "ProdGetImageTask";
    private String url,bean_contry, proc, roast, prod_name;

//    ProdGetQueryTask(String url, String bean_contry, String proc, String roast, String prod_name) {
//        this(url, prod_no, imageSize, null);
//        }

    ProdGetQueryTask(String url, String bean_contry, String proc, String roast, String prod_name) {
        this.url = url;
        this.bean_contry = bean_contry;
        this.proc = proc;
        this.roast = roast;
        this.prod_name = prod_name;
    }

    @Override
    protected List<ProdVO> doInBackground(Object... params) {

        //設定向伺服器請求的參數
//        jsonObject.addProperty("action", "getImage");
//        jsonObject.addProperty("prod_no", prod_no);
//        jsonObject.addProperty("imageSize", imageSize);
//        String url = param[0];
        String jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getQueryResult");
        jsonObject.addProperty("bean_contry", bean_contry);
        jsonObject.addProperty("proc", proc);
        jsonObject.addProperty("roast", roast);
        jsonObject.addProperty("prod_name", prod_name);
        try {
            jsonIn = getRemoteData(url, jsonObject.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ProdVO>>(){}.getType();
        return gson.fromJson(jsonIn, listType);
    }

//    @Override
//    protected void onPostExecute(ProdVO prodVO) {
//        if (isCancelled() || imageView == null) {
//            return;
//        }
//        if (prodVO != null) {
//            imageView.setImageBitmap(prodVO);
//        } else {
//            imageView.setImageResource(R.drawable.search01);
//        }
//    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                jsonIn.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonIn: " + jsonIn);
        return jsonIn.toString();
    }
}
