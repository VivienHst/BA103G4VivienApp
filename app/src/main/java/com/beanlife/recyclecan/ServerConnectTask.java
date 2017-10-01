package com.beanlife.recyclecan;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Java on 2017/9/13.
 */

public class ServerConnectTask extends AsyncTask<String, Integer, String> {
    private final static String TAG = "ServerConnectTask";
    private String url, outStr;

    public ServerConnectTask(String url, String outStr) {
        this.url = url;
        this.outStr = outStr;
    }

    @Override
    protected String doInBackground(String... strings) {
        String inStr = null;
        try {
            inStr = getRemoteData();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return inStr;
    }

    private String getRemoteData() throws IOException {
        StringBuilder inStr = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(outStr);
        Log.d(TAG, "outStr: " + outStr);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                inStr.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "inStr: " + inStr);
        return inStr.toString();
    }
}
