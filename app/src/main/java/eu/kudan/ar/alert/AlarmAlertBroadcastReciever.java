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
package eu.kudan.ar.alert;

import eu.kudan.ar.Alarm;
import eu.kudan.ar.service.AlarmServiceBroadcastReciever;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmAlertBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mathAlarmServiceIntent = new Intent(
				context,
				AlarmServiceBroadcastReciever.class);
		context.sendBroadcast(mathAlarmServiceIntent, null);
		
		StaticWakeLock.lockOn(context);
		//Bundle bundle = intent.getExtras();
		//Log.d("intent",bundle.getSerializable("alarm"))

		//조심할것... Alarm class를 넘길때 nouget에서는 bundle에 담아서 넘겨야함..
		Bundle bundle =  intent.getBundleExtra("bundle");
		if(bundle != null){
			final Alarm alarm = (Alarm) bundle.getSerializable("alarm");
			Intent mathAlarmAlertActivityIntent;

			mathAlarmAlertActivityIntent = new Intent(context, AlarmAlertActivity.class);

			mathAlarmAlertActivityIntent.putExtra("alarm", alarm);

			mathAlarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			context.startActivity(mathAlarmAlertActivityIntent);
		}else{
			Log.d("msg","bundle null");
		}
	}

}