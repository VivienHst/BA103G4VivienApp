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

/**
 * Created by vivienhuang on 2017/9/16.
 */

public class CommonTask extends AsyncTask<String, Integer, String>{
    private final static String TAG = "CommonTask";
    private String url, outStr;
//
//    CommonTask(String url, String outStr){
//        this.url = url;
//        this.outStr = outStr;
//    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String action = params[1];



        String jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);

        if(params.length >= 4) {
            for (int i = 1 ;i < params.length/2; i++){
                String atrNameKey = params[i * 2];
                String atrNameValue = params[i * 2 + 1];
                jsonObject.addProperty(atrNameKey, atrNameValue);
            }
        }


//        if(params.length == 4) {
//            String atrNameKey = params[2];
//            String atrNameValue = params[3];
//            jsonObject.addProperty(atrNameKey, atrNameValue);
//
//        }else if(params.length == 6){
//            String atrNameKey = params[2];
//            String atrNameValue = params[3];
//            jsonObject.addProperty(atrNameKey, atrNameValue);
//            String atrNameKey2 = params[4];
//            String atrNameValue2 = params[5];
//            jsonObject.addProperty(atrNameKey2, atrNameValue2);
//
//        } else if(params.length == 8){
//            String atrNameKey = params[2];
//            String atrNameValue = params[3];
//            jsonObject.addProperty(atrNameKey, atrNameValue);
//            String atrNameKey2 = params[4];
//            String atrNameValue2 = params[5];
//            jsonObject.addProperty(atrNameKey2, atrNameValue2);
//            String atrNameKey3 = params[6];
//            String atrNameValue3 = params[7];
//            jsonObject.addProperty(atrNameKey3, atrNameValue3);
//        }

        try{
            jsonIn = getRemoteData(url, jsonObject.toString());
        }catch (IOException e){
            Log.e(TAG, e.toString());
            return null;
        }

        return jsonIn;
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder inStr = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut : " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();
        if(responseCode == 200){
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = br.readLine()) != null){
                inStr.append(line);
            }
        } else{
            Log.d(TAG, "response code : " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "inStr : " + inStr);
        return inStr.toString();
    }


}
