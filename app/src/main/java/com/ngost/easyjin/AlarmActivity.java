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
package com.ngost.easyjin;

import java.io.File;
import java.util.List;

import com.ngost.easyjin.database.Database;
import com.ngost.easyjin.preferences.AlarmPreferencesActivity;
import eu.kudan.kudan.ARAPIKey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
	Dialog dialog_pre;
	String absoultePath;
	ListView mathAlarmListView;
	AlarmListAdapter alarmListAdapter;
	String imgUrlCamera;
	Uri mlmageCaptureUri;
	ImageButton markerBtn;
	ImageView preview_img;
	String img_path;
	TextView status;
	AlertDialog dlg_origin = null;
	Builder dialog_builder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_activity);
		//permission check
		permissionManager.permissionCheck();
		dialog_pre = new Dialog(this);


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.System.canWrite(this)) {
				Log.d("AlarmActivity","onCreate: Already Granted");
			} else {
				Log.d("AlarmActivity","onCreate: Not Granted. Permission Requested");
				Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
				intent.setData(Uri.parse("package:" + this.getPackageName()));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}

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
				dialog_builder = new Builder(AlarmActivity.this);
				dialog_builder.setTitle("삭제");
				dialog_builder.setMessage("알람을 삭제하시겠습니까?");
				dialog_builder.setPositiveButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Database.init(AlarmActivity.this);
						Database.deleteEntry(alarm);
						AlarmActivity.this.callMathAlarmScheduleService();
						
						updateAlarmList();
					}
				});
				dialog_builder.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				dialog_builder.show();

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

		final String[] stringArray = new String[] {"앨범에서 가져오기","사진 촬영","마커 삭제"};

		final Builder dlg = new Builder(this);
		TextView tv = new TextView(this);
		tv.setText("마커 관리");
		tv.setTextSize(30);
		tv.setTextColor(Color.WHITE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		}else{
			tv.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
		}
		tv.setBackgroundColor(getResources().getColor(R.color.holo_blue_light));
		LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,40);
		params.gravity = Gravity.CENTER;


		tv.setLayoutParams(params);
		dlg.setCustomTitle(tv);
		dlg_origin = dlg.setItems(stringArray, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0)
				{
					doTakeAlbumAction();
					dialog.dismiss();
					dialog.cancel();
				}else if(which ==1){
					doTakePhotoAction();
					dialog.dismiss();
					dialog.cancel();
				}else {
					Log.d("AlarmActivity","camera backed");
					SharedPreferences save = getSharedPreferences(MARKER_PATH, 0);
					SharedPreferences.Editor editor = save.edit();
					editor.putString("path","0");
					editor.commit();
					status.setText(R.string.marker_generate_msg1);
					img_path = "0";
					dialog.dismiss();
					dialog.cancel();
				}
			}
		}).create();


		//marker 등록
		markerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					dlg_origin.show();
				}catch (IllegalStateException e){
					Log.d("dlg","child already");

				}

			}
		});

		//마커 프리뷰 다이얼로그

		dialog_pre.setContentView(R.layout.marker_preview_dialog);
		preview_img = (ImageView) dialog_pre.findViewById(R.id.marker_preview);

		status.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(img_path.equals("0")||img_path==null){
					status.setText(getResources().getText(R.string.marker_generate_msg1));
					SharedPreferences save = getSharedPreferences(MARKER_PATH, 0);
					SharedPreferences.Editor editor = save.edit();
					editor.putString("path","0");
					editor.commit();
					return;
				}
				try{
					Log.d("pathcheck",""+img_path);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;

					Matrix rotateMatrix = new Matrix();
					rotateMatrix.postRotate(90);
					Bitmap bitmap = BitmapFactory.decodeFile(img_path,options);

					if(bitmap.getWidth()>bitmap.getHeight()){
						Bitmap sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false);
						preview_img.setImageBitmap(sideInversionImg);
					}else {
						preview_img.setImageBitmap(bitmap);
					}
					dialog_pre.show();
				}catch (Exception e){
					status.setText(getResources().getText(R.string.marker_generate_msg1));
					return;
				}
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
		intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//카메라 가로고정
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
		dlg_origin.dismiss();
		dialog_pre.dismiss();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.System.canWrite(this)) {
				Log.d("AlarmActivity","onResume: Granted");
			}
		}
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
					findViewById(android.R.id.empty).setVisibility(View.GONE);
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
				Toast.makeText(getApplicationContext(),"마커 생성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
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
				try{
					File files = new File(absoultePath);
					if(!files.exists()){
						throw new Exception();
					}
					SharedPreferences save = getSharedPreferences(MARKER_PATH, 0);
					SharedPreferences.Editor editor = save.edit();
					editor.putString("path",absoultePath);
					editor.commit();
					img_path = absoultePath;
					status.setText(R.string.marker_generate_msg2);
				}catch (Exception e){
					Log.d("AlarmActivity","camera backed");
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					sp.edit().remove("MARKER_PATH").commit();
				}
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
		if (cursor !=null && cursor.getCount() !=0){
			try{
				cursor.moveToNext();
				String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
				cursor.close();
				return path;
			}catch (CursorIndexOutOfBoundsException e){

			}

		}
		return null;
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