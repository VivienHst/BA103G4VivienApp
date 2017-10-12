package com.beanlife;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Vivien on 2017/10/12.
 */

public class MsgTestActivity extends AppCompatActivity {
    private static final String TAG = "Msg Fragment";
    public MyWebSocketClient myWebSocketClient;
    private TextView msgTextTv;
    private EditText msgWriteEt;
    private Button msgSendBt;
    private View view;
    private String myName, urName;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_fragment);
        SharedPreferences loginState = getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        findView();
        //myName = loginState.getString("userAc", "noLogIn");
        myName = "mrbrown";

        //************for test*************
        urName = "amy39";
        URI uri = null;
        try {
            uri = new URI(Common.SERVER_URL + myName + "/" + urName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        myWebSocketClient = new MyWebSocketClient(uri);
        myWebSocketClient.connect();

    }

    private void findView(){
        msgWriteEt = (EditText) view.findViewById(R.id.msg_write_et);
        msgSendBt =(Button) view.findViewById(R.id.msg_send_bt);

        msgSendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendClick(view);
            }
        });
    }

    public void onSendClick(View view) {

        String message = msgWriteEt.getText().toString();
        if (message.trim().isEmpty()) {
            showToast("String is empty");
            return;
        }
        Map<String, String> map = new HashMap<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("myName", myName);
        jsonObject.addProperty("urName", urName);
        jsonObject.addProperty("message", message);
        if (myWebSocketClient != null) {
            myWebSocketClient.send(jsonObject.toString());
        }
        Log.d(TAG, "output: " + jsonObject.toString());
    }

    private void showToast(String  message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (myWebSocketClient != null) {
//                myWebSocketClient.close();
//                showToast(R.string.text_LeftChatRoom);
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void showToast(int messageId) {
//        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
//    }


    class MyWebSocketClient extends WebSocketClient {

        MyWebSocketClient(URI serverURI) {
            // Draft_17是連接協議，就是標準的RFC 6455（JSR256）
            super(serverURI, new Draft_17());
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d(TAG, "onOpen: handshakedata.toString() = " + handshakedata.toString());
        }

        @Override
        public void onMessage(final String message) {
            Log.d(TAG, "onMessage: " + message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String userName = jsonObject.get("urName").toString();

                        String message = jsonObject.get("message").toString();
                        String text = userName + ": " + message + "\n";
                        msgTextTv.append(text);
                        //scrollView.fullScroll(View.FOCUS_DOWN);
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {

        }

        @Override
        public void onError(Exception ex) {

        }
    }
}
