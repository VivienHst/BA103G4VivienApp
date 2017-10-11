package com.beanlife.ord;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.beanlife.MainActivity;
import com.beanlife.R;
import com.beanlife.checkout.CheckoutFragment;

/**
 * Created by vivienhuang on 2017/10/11.
 */

public class NotifyOrderService extends Service {
//    private final IBinder binder = new ServiceBinder();
    NotificationManager notificationManager;

    private final static int NOTIFICATION_ID = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public class ServiceBinder extends Binder{
//        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//        public NotifyOrderService getService(){
//            Intent intent = new Intent(getApplicationContext(), NotifyOrderService.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            Notification notification = new Notification.Builder(getService())
//                    .setContentTitle("Title")
//                    .setContentText("您有一筆未付款資訊")
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent)
//                    .build();
//
//
//            MainActivity.notificationManager.notify(NOTIFICATION_ID, notification);
//
//            return NotifyOrderService.this;
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotify(){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Title")
                .setContentText("您有一筆未付款資訊")
                .setSmallIcon(android.R.drawable.arrow_down_float)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotify();
        return super.onStartCommand(intent, flags, startId);

    }
}
