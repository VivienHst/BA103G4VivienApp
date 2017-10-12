package com.beanlife;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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

public class MessageTestActivity extends AppCompatActivity {
    private static final String TAG = "Msg Fragment";
    private MyWebSocketClient myWebSocketClient;
    private TextView msgTextTv;
    private EditText msgWriteEt;
    private Button msgSendBt;
    private View view;
    private String myName, urName;


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


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_test);
        SharedPreferences loginState = getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        findView();
        myName = loginState.getString("userAc", "noLogIn");
       // myName = "mrbrown";

        //************for test*************
        urName = "mamabeak";
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
        msgWriteEt = (EditText) findViewById(R.id.msg_write_et);
        msgSendBt =(Button) findViewById(R.id.msg_send_bt);

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (myWebSocketClient != null) {
                myWebSocketClient.close();
                //showToast(R.string.text_LeftChatRoom);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showToast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }
}
