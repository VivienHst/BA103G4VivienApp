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
 * Created by vivienhuang on 2017/9/19.
 */

public class CommonRetrieveTask extends AsyncTask<String, Void, List<ReviewVO>> {
    String TAG;
    String action, sendKey,sendValue;



    CommonRetrieveTask(String action, String sendKey, String sendValue){

        this.action = action;
        this.sendKey = sendKey;
        this.sendValue = sendValue;
    }

    @Override
    protected List<ReviewVO> doInBackground(String... param) {
        String url = param[0];
        String jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);
        jsonObject.addProperty(sendKey, sendValue);
        try{
            jsonIn = getRemoteData(url, jsonObject.toString());
        }catch (IOException e){
            Log.e(TAG, e.toString());
            return null;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<ReviewVO>>(){}.getType();
        return gson.fromJson(jsonIn, listType);
    }

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
