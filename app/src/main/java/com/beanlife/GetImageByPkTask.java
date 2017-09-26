package com.beanlife;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Java on 2017/9/13.
 */

public class GetImageByPkTask extends AsyncTask<Object, Integer, Bitmap> {

    private final static String TAG = "GetImageTask";
    private String url , pk_no, action;
    private int imageSize;
    private ImageView imageView;

    GetImageByPkTask(String url, String action, String pk_no, int imageSize) {
        this(url, action, pk_no, imageSize, null);
    }

    public GetImageByPkTask(String url, String action, String pk_no, int imageSize, ImageView imageView) {
        this.url = url;
        this.action = action;
        this.pk_no = pk_no;
        this.imageSize = imageSize;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        //設定向伺服器請求的參數
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty(action, pk_no);
        jsonObject.addProperty("imageSize", imageSize);

        Bitmap bitmap;
        try {
            bitmap = getRemoteImage(url, jsonObject.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.search01);
        }
    }

    private Bitmap getRemoteImage(String url, String jsonOut) throws IOException {
        Bitmap bitmap = null;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        return bitmap;
    }
}
