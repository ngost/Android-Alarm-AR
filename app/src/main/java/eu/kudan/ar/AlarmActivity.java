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
package eu.kudan.ar;

import java.io.File;
import java.util.List;

import eu.kudan.ar.database.Database;
import eu.kudan.ar.preferences.AlarmPreferencesActivity;
import eu.kudan.kudan.ARAPIKey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmActivity extends BaseActivity {
	final static int MY_PERMISSIONS_REQUEST_CODE = 0;
	final static int PICK_FROM_CAMERA=1;
	final static int PICK_FROM_ALBUM=2;
	final static String MARKER_PATH = "marker";
	PermissionManager permissionManager = new PermissionManager(this);
	ImageButton newButton;

	String absoultePath;
	ListView mathAlarmListView;
	AlarmListAdapter alarmListAdapter;
	String imgUrlCamera;
	Uri mlmageCaptureUri;
	ImageButton markerBtn;
	ImageView preview_img;
	String img_path;
	TextView status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_activity);
		//permission check
		permissionManager.permissionCheck();
		ARAPIKey key = ARAPIKey.getInstance();
		key.setAPIKey("agWZcpYLYjBxCbWf2qZx6k+PWISqeGtFCqKaZwYtwS+kdn1HKiQAmsJ55STRBe9BqCw3VwG6qL+ESI5ntTF/iV/uekLG3PCokaUE0/uTzqhaYlxRdmuNBIduzBCjq3mV2na+gy3ffHH9Ipc7eIN0geTj3p+ppsmK0U399iGmN38ndIh6k2y16cByWIecMSU3yw3Ztw7gHRqf83hVhZ5T2ACGK4SNkQhhdKp+CTaR5W3amYCJBgwumqFqNFyI9UniuMk70T/cQObRQum2U51OjjbMfmEAwIBt8Q8jD2yACzye6K4/1O4pZhbGEbiDeLrAfxqMwBAe5o6vnYIilGNnpDhfi3wOHhRaqtLOVvB58GUIFTnAPvmYFVnLWRJmCUZ9FJNDyX3ALCl/alFEWh+A/a6NFjcwLGKI9drPuGG4ONFg4p0l+p3b9DZoLzszlmWAflI/UFzQa++kQn3/sclO9i0vPnpi0LWoABm5vGswLVAIX/0k6384GXxfkADI6fjGtf62XJ5ImaVDiiREa9mabWEQGoifghQG1sGNDYgBIYEpiaLsVzOfTALpe20Q7kFCMjedJImQhhuLtEK1BXfXJEed1QqUOsG9IeKxKk28GbOtOF9w3yrSF3gnJslzZxF2kEF3C6ckog8byagS+4p37FJmbpPsiKNH1Qm0LuouGcQ=");

		status = (TextView) findViewById(R.id.marker_status_text);
		markerBtn = (ImageButton) findViewById(R.id.marker_generate_btn);
		SharedPreferences save = getSharedPreferences(MARKER_PATH,0);
		img_path = save.getString("path","0");
		if(!img_path.equals("0")){
			//Bitmap bitmap = BitmapFactory.decodeFile(img_path);
			//preview_img.setImageBitmap(bitmap);
			status.setText(R.string.marker_generate_msg2);
		}

		mathAlarmListView = (ListView) findViewById(android.R.id.list);
		mathAlarmListView.setLongClickable(true);
		mathAlarmListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
				final Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				Builder dialog = new Builder(AlarmActivity.this);
				dialog.setTitle("Delete");
				dialog.setMessage("Delete this alarm?");
				dialog.setPositiveButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Database.init(AlarmActivity.this);
						Database.deleteEntry(alarm);
						AlarmActivity.this.callMathAlarmScheduleService();
						
						updateAlarmList();
					}
				});
				dialog.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				dialog.show();

				return true;
			}
		});

		callMathAlarmScheduleService();

		alarmListAdapter = new AlarmListAdapter(this);
		this.mathAlarmListView.setAdapter(alarmListAdapter);
		mathAlarmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				Intent intent = new Intent(AlarmActivity.this, AlarmPreferencesActivity.class);
				intent.putExtra("alarm", alarm);
				startActivity(intent);
			}

		});

		final String[] stringArray = new String[] {"앨범에서가져오기","사진 촬영"};
		final AlertDialog.Builder dlg = new AlertDialog.Builder(this);

		dlg.setItems(stringArray, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0)
				{
					doTakeAlbumAction();
				}else if(which ==1){
					doTakePhotoAction();
				}
			}
		});

		//marker 등록
		markerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.show();
			}
		});

		//마커 프리뷰 다이얼로그
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.marker_preview_layout);
		dialog.setTitle("미리보기");
		preview_img = (ImageView) dialog.findViewById(R.id.marker_preview);

		status.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("AlarmActivity, image path check",""+img_path);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				Bitmap bitmap = BitmapFactory.decodeFile(img_path,options);
				preview_img.setImageBitmap(bitmap);
				dialog.show();
			}
		});
	}
	//onCreate end..

	public void doTakePhotoAction(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imgUrlCamera=String.valueOf(System.currentTimeMillis());
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "marker");

		mlmageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//카메라 가로고정
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mlmageCaptureUri);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}
	public void doTakeAlbumAction(){
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);		
		menu.findItem(R.id.menu_item_save).setVisible(false);
		menu.findItem(R.id.menu_item_delete).setVisible(false);
	    return result;
	}
		
	@Override
	protected void onPause() {
		// setListAdapter(null);
		Database.deactivate();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateAlarmList();
	}
	
	public void updateAlarmList(){
		Database.init(AlarmActivity.this);
		final List<Alarm> alarms = Database.getAll();
		alarmListAdapter.setMathAlarms(alarms);
		
		runOnUiThread(new Runnable() {
			public void run() {
				// reload content			
				AlarmActivity.this.alarmListAdapter.notifyDataSetChanged();				
				if(alarms.size() > 0){
					findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
				}else{
					findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.checkBox_alarm_active) {
			CheckBox checkBox = (CheckBox) v;
			Alarm alarm = (Alarm) alarmListAdapter.getItem((Integer) checkBox.getTag());
			alarm.setAlarmActive(checkBox.isChecked());
			Database.update(alarm);
			AlarmActivity.this.callMathAlarmScheduleService();
			if (checkBox.isChecked()) {
				Toast.makeText(AlarmActivity.this, alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
			}
		}


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_FROM_CAMERA) {
			try {
				absoultePath = getPathFromUri(mlmageCaptureUri);
				File tempFile = new File(absoultePath);
				if(tempFile.length()==0){
					getContentResolver().delete(mlmageCaptureUri,null,null);
				}
			} catch (NullPointerException e) {
			}

		} else if (requestCode == PICK_FROM_ALBUM) {
			try {
				mlmageCaptureUri = data.getData();
				absoultePath = getPathFromUri(mlmageCaptureUri);
			} catch (NullPointerException e) {
			}
		}

		if(absoultePath!=null){
			if(absoultePath.length()>0){
				SharedPreferences save = getSharedPreferences(MARKER_PATH, 0);
				SharedPreferences.Editor editor = save.edit();
				editor.putString("path",absoultePath);
				editor.commit();
				img_path = absoultePath;
				status.setText(R.string.marker_generate_msg2);
			}
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_CODE: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, getResources().getString(R.string.permissionAccepted),Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, getResources().getString(R.string.permissionDeninedMsg),Toast.LENGTH_SHORT).show();
				}
				return;
			}
		}
	}

	public String getPathFromUri(Uri uri){
		Cursor cursor = getContentResolver().query(uri, null, null, null, null );
		cursor.moveToNext();
		String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
		cursor.close();
		return path;
	}

	@Override
	protected void onStop() {
		if(!img_path.equals("0")){
			SharedPreferences save = getSharedPreferences(MARKER_PATH, 0);
			SharedPreferences.Editor editor = save.edit();
			editor.putString("path",img_path);
			editor.commit();
		}
		super.onStop();
	}
}