/* Copyright 2014 Sheldon Neilson www.neilson.co.za
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.ngost.easyjin.alert;

import com.ngost.easyjin.Alarm;
import com.ngost.easyjin.service.AlarmActiveCheckService;
import com.ngost.easyjin.service.AlarmServiceBroadcastReciever;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmAlertBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		StaticWakeLock wakeLock = new StaticWakeLock();
		wakeLock.lockOn(context);

		Intent mathAlarmServiceIntent = new Intent(
				context,
				AlarmServiceBroadcastReciever.class);
		context.sendBroadcast(mathAlarmServiceIntent, null);
//

		//Bundle bundle = intent.getExtras();
		//Log.d("intent",bundle.getSerializable("alarm"))

		//조심할것... Alarm class를 넘길때 nouget에서는 bundle에 담아서 넘겨야함..
		Bundle bundle =  intent.getBundleExtra("bundle");
		if(bundle != null){
			final Alarm alarm = (Alarm) bundle.getSerializable("alarm");
			Intent mathAlarmAlertActivityIntent;

			//alarm active check
			//1. service create
			Intent activeCheckIntent = new Intent(context, AlarmActiveCheckService.class);
			//bundle create
			//put alarm instants and active status into bundle
			bundle.putBoolean("active",true);
			//put bundle into intent
			activeCheckIntent.putExtra("bundle",bundle);
			//service start
			context.startService(activeCheckIntent);
			Log.e("statCheckService","ok");

			//switch ()
			switch (alarm.getAlertway().toString()){
				case "수학문제":
					Log.e("alertReciver","come");
					mathAlarmAlertActivityIntent = new Intent(context, AlarmAlertActivity.class);

					mathAlarmAlertActivityIntent.putExtra("alarm", alarm);

					mathAlarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

					context.startActivity(mathAlarmAlertActivityIntent);
					break;
				case "증강현실":
					mathAlarmAlertActivityIntent = new Intent(context, AlarmAlertActivityAR.class);

					mathAlarmAlertActivityIntent.putExtra("alarm", alarm);

					mathAlarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

					context.startActivity(mathAlarmAlertActivityIntent);
					break;
			}
		}else{
			Log.d("msg","bundle null");
		}
	}

}
