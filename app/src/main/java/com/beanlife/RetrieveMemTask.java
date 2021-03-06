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
 * Created by vivienhuang on 2017/9/21.
 */

public class RetrieveMemTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "Mem Task";

    String mem_ac, mem_psw, action;
    RetrieveMemTask(String action, String mem_ac, String mem_psw){
        this.action = action;
        this.mem_ac = mem_ac;
        this.mem_psw = mem_psw;
    }
    @Override
    protected Boolean doInBackground(String... param) {
        String url = param[0];
        String jsonIn="";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);
        jsonObject.addProperty("mem_ac", mem_ac);
        jsonObject.addProperty("mem_psw", mem_psw);

        try {
            jsonIn = getRemoteData(url, jsonObject.toString());
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<Boolean>(){}.getType();
        return gson.fromJson(jsonIn,listType);
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder jsonIn = new StringBuilder();
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
            while ((line = br.readLine()) != null){
                jsonIn.append(line);
            }
        } else {
            Log.d(TAG, "response code : " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonIn : " + jsonIn);
        return jsonIn.toString();
    }
}

