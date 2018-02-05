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
package eu.kudan.ar.preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.kudan.ar.Alarm;
import eu.kudan.ar.preferences.AlarmPreference.Type;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class AlarmPreferenceListAdapter extends BaseAdapter implements Serializable,Parcelable {

	private Context context;
	private Alarm alarm;
	private List<AlarmPreference> preferences = new ArrayList<AlarmPreference>();
	private final String[] repeatDays = {"일요일","월요일","화요일","수요일","목요일","금요일","토요일"};
	private final String[] alarmDifficulties = {"쉬움","보통","어려움"};
	
	private String[] alarmTones;
	private String[] alarmTonePaths;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

	public AlarmPreferenceListAdapter(Context context, Alarm alarm) {
		setContext(context);
		
		
//		(new Runnable(){
//
//			@Override
//			public void run() {
				Log.d("AlarmPreferenceListAdapter", "Loading Ringtones...");
				
				RingtoneManager ringtoneMgr = new RingtoneManager(getContext());
				
				ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
				
				Cursor alarmsCursor = ringtoneMgr.getCursor();
				
				alarmTones = new String[alarmsCursor.getCount()+1];
				alarmTones[0] = "Silent"; 
				alarmTonePaths = new String[alarmsCursor.getCount()+1];
				alarmTonePaths[0] = "";
				
				if (alarmsCursor.moveToFirst()) {		    			
					do {
						alarmTones[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtone(alarmsCursor.getPosition()).getTitle(getContext());
						alarmTonePaths[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()).toString();
					}while(alarmsCursor.moveToNext());					
				}
				Log.d("AlarmPreferenceListAdapter", "Finished Loading " + alarmTones.length + " Ringtones.");
				alarmsCursor.close();
//				
//			}
//			
//		}).run();
//		
	    setMathAlarm(alarm);		
	}

	@Override
	public int getCount() {
		return preferences.size();
	}

	@Override
	public Object getItem(int position) {
		return preferences.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlarmPreference alarmPreference = (AlarmPreference) getItem(position);
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		switch (alarmPreference.getType()) {
		case BOOLEAN:
			if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_checked)
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_checked, null);

			CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
			checkedTextView.setText(alarmPreference.getTitle());
			checkedTextView.setChecked((Boolean) alarmPreference.getValue());
			break;
		case INTEGER:
		case STRING:
		case LIST:
		case MULTIPLE_LIST:
		case TIME:
		default:
			if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_2)
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
			
			TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
			text1.setTextSize(18);
			text1.setText(alarmPreference.getTitle());
			
			TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
			text2.setText(alarmPreference.getSummary());
			break;
		}

		return convertView;
	}

	public Alarm getMathAlarm() {		
		for(AlarmPreference preference : preferences){
			switch(preference.getKey()){
				case ALARM_ACTIVE:
					alarm.setAlarmActive((Boolean) preference.getValue());
					break;
				case ALARM_NAME:
					alarm.setAlarmName((String) preference.getValue());
					break;
				case ALARM_TIME:
					alarm.setAlarmTime((String) preference.getValue());
					break;
				case ALARM_DIFFICULTY:
					alarm.setDifficulty(Alarm.Difficulty.valueOf((String)preference.getValue()));
					break;
				case ALARM_TONE:
					alarm.setAlarmTonePath((String) preference.getValue());
					break;
				case ALARM_VIBRATE:
					alarm.setVibrate((Boolean) preference.getValue());
					break;
				case ALARM_REPEAT:
					alarm.setDays((Alarm.Day[]) preference.getValue());
					break;
			}
		}
				
		return alarm;
	}

	public void setMathAlarm(Alarm alarm) {
		this.alarm = alarm;
		preferences.clear();
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_ACTIVE,"활동", null, null, alarm.getAlarmActive(),Type.BOOLEAN));
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_NAME, "라벨",alarm.getAlarmName(), null, alarm.getAlarmName(), Type.STRING));
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TIME, "설정 시간",alarm.getAlarmTimeString(), null, alarm.getAlarmTime(), Type.TIME));
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_REPEAT, "반복",alarm.getRepeatDaysString(), repeatDays, alarm.getDays(),Type.MULTIPLE_LIST));
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_DIFFICULTY,"난이도", alarm.getDifficulty().toString(), alarmDifficulties, alarm.getDifficulty(), Type.LIST));
		
			Uri alarmToneUri = Uri.parse(alarm.getAlarmTonePath());
			Ringtone alarmTone = RingtoneManager.getRingtone(getContext(), alarmToneUri);
		
		if(alarmTone instanceof Ringtone && !alarm.getAlarmTonePath().equalsIgnoreCase("")){
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TONE, "벨소리", alarmTone.getTitle(getContext()),alarmTones, alarm.getAlarmTonePath(), Type.LIST));
		}else{
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TONE, "벨소리", getAlarmTones()[0],alarmTones, null, Type.LIST));
		}
		
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_VIBRATE, "진동",null, null, alarm.getVibrate(), Type.BOOLEAN));
	}

	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String[] getRepeatDays() {
		return repeatDays;
	}

	public String[] getAlarmDifficulties() {
		return alarmDifficulties;
	}

	public String[] getAlarmTones() {
		return alarmTones;
	}

	public String[] getAlarmTonePaths() {
		return alarmTonePaths;
	}

}
