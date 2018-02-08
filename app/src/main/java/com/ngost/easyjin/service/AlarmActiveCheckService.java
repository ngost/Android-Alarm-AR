package com.ngost.easyjin.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ngost.easyjin.Alarm;
import com.ngost.easyjin.alert.AlarmAlertActivity;
import com.ngost.easyjin.alert.AlarmAlertActivityAR;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jinyoung on 2018-02-08.
 */

public class AlarmActiveCheckService extends Service{
    Alarm alarm = null;
    Timer timer = null;
    Bundle bundle;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("checkService","start");
        bundle = intent.getBundleExtra("bundle");
        alarm = (Alarm) bundle.getSerializable("alarm");
        if(alarm==null){
            Log.e("checkService","alarm is null");
            timer.cancel();
            stopSelf();
        }


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(isActivityLive()){
                    Log.d("check","activity live");
                }else {
                    Log.d("check","activity dead");
                    Intent intent;
                    //start activity
                    String way = alarm.getAlertway().toString();
                    switch (way){
                        case "수학문제":
                            intent = new Intent(getApplicationContext(), AlarmAlertActivity.class);
                            intent.putExtra("alarm", alarm);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            break;
                        case "증강현실":
                            intent = new Intent(getApplicationContext(), AlarmAlertActivityAR.class);
                            intent.putExtra("alarm", alarm);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            break;
                        default:
                            Log.d("can't start activity","error");
                            break;
                    }

                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,1000,2500);



        return START_NOT_STICKY;
    }
    public boolean isActivityLive() { //if activity is dead, return false
        try {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
            ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
            Log.d("classname",componentInfo.getClassName());
            Log.d("classname",componentInfo.getShortClassName());
            if(componentInfo.getShortClassName().equals(".alert.AlarmAlertActivity")||componentInfo.getShortClassName().equals(".alert.AlarmAlertActivityAR")){

                return true;
            }else {
                return false;
            }

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Log.e("CheckService","dead!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("checkServcie","onTaskRemoved");
        if(bundle!=null){
            Intent intent2 = new Intent(this.getApplicationContext(), this.getClass());
            intent2.setPackage(this.getPackageName());
            intent2.putExtra("bundle",bundle);
            PendingIntent pendingIntent = PendingIntent.getService((Context)this.getApplicationContext(), (int)1, (Intent)intent2,0);
            ((AlarmManager)this.getApplicationContext().getSystemService(ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME, 1000 + SystemClock.elapsedRealtime(), pendingIntent);
        }
        super.onTaskRemoved(rootIntent);
    }
}
