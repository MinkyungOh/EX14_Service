package com.example.omin.ex14_service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {
    private Thread mThread;
    private int mCount;

    private IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {

        public MyService getService() {
            return MyService.this;
        }
    }

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if("startForeground".equals(intent.getAction())) {
            startForegroundService();
        }
        else if(mThread == null) {
            mThread = new Thread("My Thread") {
                @Override
                public void run() {
                    for(int i=0; i<50; i++) {
                        try {
                            mCount ++;
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            break;
                        }
                        Log.d("My Service", "Service 동작 중 " + mCount);
                    }
                }
            };
            mThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("My Service", "onDestroy ");

        if(mThread != null) {
            mThread.interrupt();
            mThread = null;
            mCount = 0;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int getCount() {
        /*if(mThread != null) {
            mThread.interrupt();
            mThread = null;
            mCount = 0;
        }*/

        return mCount;
    }

    private void startForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("포그라운드 서비스");
        builder.setContentText("포그라운드 서비스 실행 중");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }

        startForeground(1, builder.build());
    }
}
