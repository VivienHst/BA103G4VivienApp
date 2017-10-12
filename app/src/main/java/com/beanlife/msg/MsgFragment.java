package com.beanlife.msg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.R;
import com.beanlife.act.ActVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Vivien on 2017/10/12.
 */

public class MsgFragment extends Fragment {
    private static final String TAG = "Msg Fragment";
    public MyWebSocketClient myWebSocketClient;
    private EditText msgWriteEt;
    private Button msgSendBt;
    private View view;
    private String myName, urName;
    private RecyclerView recyclerView;
    private List<MsgVO> msgVOList;
    private CommonTask retrieveOldMsg;
    private ScrollView msgContSv;

    public MsgFragment(String myName, String urName){
        this.myName = myName;
        this.urName = urName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.msg_fragment, container, false);
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);

        //myName = loginState.getString("userAc", "noLogIn");

        //************for test*************
        //urName = "mamabeak";
        findView();
        URI uri = null;
        try {
            uri = new URI(Common.SERVER_URL + myName + "/" + urName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        myWebSocketClient = new MyWebSocketClient(uri);
        myWebSocketClient.connect();

        return view;
    }

    private void findView(){
        msgWriteEt = (EditText) view.findViewById(R.id.msg_write_et);
        msgSendBt = (Button) view.findViewById(R.id.msg_send_bt);
        msgContSv = (ScrollView) view.findViewById(R.id.msg_cont_sv);
        msgVOList= new ArrayList<MsgVO>();
        Set<MsgVO> oldMsgVOSet = new LinkedHashSet<MsgVO>();

        oldMsgVOSet = getMsgSendList(myName,urName);
        if(!(oldMsgVOSet == null)){
            Iterator<MsgVO> msgIt = oldMsgVOSet.iterator();
            while(msgIt.hasNext()){
                MsgVO msgVO = new MsgVO();
                msgVO = msgIt.next();
                msgVOList.add(msgVO);
                addRow(R.id.msg_cont_rv);
            }
        }


        msgSendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendClick(view);
                msgContSv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void onSendClick(View view) {

        String message = msgWriteEt.getText().toString();
        if (message.trim().isEmpty()) {
            showToast("請輸入訊息");
            return;
        }
        Map<String, String> map = new HashMap<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userName", myName);
        //jsonObject.addProperty("urName", urName);
        jsonObject.addProperty("message", message);
        if (myWebSocketClient != null) {
            myWebSocketClient.send(jsonObject.toString());
            msgWriteEt.setText("");
        }

        Log.d(TAG, "output: " + jsonObject.toString());
    }

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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String userName = jsonObject.get("userName").toString();

                        String message = jsonObject.get("message").toString();
                        String text = message ;

                        MsgVO msgVO = new MsgVO();
                        msgVO.setMem_sen(myName);
                        msgVO.setMsg_cont(text);
                        msgVOList.add(msgVO);
                        addRow(R.id.msg_cont_rv);

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
    public void onDetach() {
        super.onDetach();
        if (myWebSocketClient != null) {
            myWebSocketClient.close();
            //showToast("關閉私訊");
        }
    }

    private void showToast(String  message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }



    private void addRow(int viewId){
        recyclerView  = (RecyclerView) view.findViewById(viewId);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MsgFragment.MsgCardAdapter(getActivity(), msgVOList));
    }

    private class MsgCardAdapter extends
            RecyclerView.Adapter<MsgFragment.MsgCardAdapter.MyViewHolder> {
        private Context context;

        private List<MsgVO> msgVOList;
        private View itemView;

        MsgCardAdapter(Context context, List<MsgVO> msgVOList) {
            this.context = context;
            this.msgVOList = msgVOList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView msgSendTv, msgSendNameTv;
            CardView msgContCv;
            LinearLayout msgContLl;

            MyViewHolder(View itemView) {
                super(itemView);
                msgSendTv = (TextView) itemView.findViewById(R.id.msg_send_tv);
                msgSendNameTv = (TextView) itemView.findViewById(R.id.msg_send_name_tv);
                msgContCv = (CardView) itemView.findViewById(R.id.msg_send_card);
                msgContLl = (LinearLayout) itemView.findViewById(R.id.msg_send_layout);
            }
        }

        @Override
        public int getItemCount() {
            return msgVOList.size();
        }

        @Override
        public MsgFragment.MsgCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            itemView = layoutInflater.inflate(R.layout.msg_send_card, viewGroup, false);
            return new MsgFragment.MsgCardAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MsgFragment.MsgCardAdapter.MyViewHolder viewHolder, int position) {
//            String msgSend = msgSendList.get(position);
            MsgVO msgVO = msgVOList.get(position);
            if(!msgVO.getMem_sen().equals(myName)){
                viewHolder.msgContCv.setBackgroundColor(Color.parseColor("#EEEEEE"));
                viewHolder.msgContLl.setPadding(10,10,240,10);
            }else {
                viewHolder.msgContCv.setBackgroundColor(Color.parseColor("#fffae3"));
                viewHolder.msgContLl.setPadding(240,10,10,10);
            }

            final String msgSend = msgVO.getMsg_cont();
            viewHolder.msgSendTv.setText(msgSend);
            viewHolder.msgSendNameTv.setText(msgVO.getMem_sen());
        }
    }

    Set<MsgVO> getMsgSendList(String  memAc1, String memAc2){
        retrieveOldMsg = (CommonTask) new CommonTask().execute(Common.MSG_URL, "getAllByPair", "mem_ac1", memAc1
                , "mem_ac2",memAc2);

        Set<MsgVO> oldMsgVOSet = new LinkedHashSet<MsgVO>();
        String oldMsgSetString = "";
        try {
            oldMsgSetString = retrieveOldMsg.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<LinkedHashSet<MsgVO>>(){}.getType();
        oldMsgVOSet =  gson.fromJson(oldMsgSetString, listType);

        return oldMsgVOSet;
    }
}