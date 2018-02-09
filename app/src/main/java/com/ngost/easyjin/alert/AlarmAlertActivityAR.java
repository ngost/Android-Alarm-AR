package com.ngost.easyjin.alert;

import com.ngost.easyjin.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import com.ngost.easyjin.Alarm;
import com.ngost.easyjin.service.AlarmActiveCheckService;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTrackableListener;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;

public class AlarmAlertActivityAR extends ARActivity implements View.OnClickListener {
    public Boolean firstRun = true;
    String path;
    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean alarmActive;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Bundle bundle = this.getIntent().getExtras();
        alarm = (Alarm) bundle.getSerializable("alarm");
        if(alarm==null){
            this.setTitle("Alert");
        }else {
            this.setTitle(alarm.getAlarmName());
        }

        setContentView(R.layout.activity_alarm_alert_ar);
        linearLayout = (LinearLayout) findViewById(R.id.examLin);


        SharedPreferences preferences = getSharedPreferences("marker",0);
        path = preferences.getString("path",null);


        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(getClass().getSimpleName(), "Incoming call: "
                                + incomingNumber);
                        try {
                            mediaPlayer.pause();
                        } catch (IllegalStateException e) {

                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(getClass().getSimpleName(), "Call State Idle");
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException e) {

                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

        // Toast.makeText(this, answerString, Toast.LENGTH_LONG).show();

        startAlarm();

        TextView exam1 = (TextView) findViewById(R.id.example1);
        TextView exam2 = (TextView) findViewById(R.id.example2);
        TextView exam3 = (TextView) findViewById(R.id.example3);
        exam1.setOnClickListener(this);
        exam2.setOnClickListener(this);
        exam3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!alarmActive)
            return;

        switch (v.getId()){
            case R.id.example1:
                endAlarm();
                break;
            case R.id.example2:
                Toast.makeText(getApplicationContext(),"틀렸어",Toast.LENGTH_SHORT).show();
                break;
            case R.id.example3:
                Toast.makeText(getApplicationContext(),"틀렸어",Toast.LENGTH_SHORT).show();
                break;

        }

    }

    @Override
    public void setup()
    {

        switch (alarm.getDifficulty()) {
            //do 난이도체크
            case EASY:
                break;
            case MEDIUM:
                break;
            case HARD:
                break;
        }

        //step1, 추적 가능한 이미지를 등록해라. 여기서 해야 할 일은 Trackable 객체 만들기, Tracker 생성
        ARImageTrackable imageTrackable;
        //이름지정
        imageTrackable = new ARImageTrackable("Lego Marker");
        //에셋에서 이미지 로딩
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"kudan");
        Log.d("123123",file.getAbsolutePath());
        if(path!=null){
            imageTrackable.loadFromPath(path);
        }else {
            Toast.makeText(getApplicationContext(),"마커를 먼저 등록해주세요.",Toast.LENGTH_LONG).show();
            finish();
        }

        // Get the single instance of the image tracker.
        ARImageTracker imageTracker;
        //인스턴스생성
        imageTracker = ARImageTracker.getInstance();
        //트래커 초기화
        imageTracker.initialise();
        imageTracker.addTrackable(imageTrackable);

        //트래커 객체생성

        /* 모델 파일 테스트 */
        ARModelImporter arModelImporter = new ARModelImporter();
        arModelImporter.loadFromAsset("moon.jet");
        ARModelNode node3d = arModelImporter.getNode();
        node3d.setName("Cow");
        node3d.rotateByDegrees(90.0f,1.0f,0.0f,0.0f);
        node3d.rotateByDegrees(180.0f,1.0f,100.0f,0.0f);
        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset("moon.jpg");
        ARLightMaterial material = new ARLightMaterial();
        material.setTexture(texture2D);
        material.setColour(1,1,1);
        material.setAmbient(0.8f,0.8f,0.8f);//조명

        for(ARMeshNode meshNode : arModelImporter.getMeshNodes()){
            meshNode.setMaterial(material);
        }
        node3d.scaleByUniform(3);

        imageTrackable.getWorld().addChild(node3d);
        imageTrackable.addListener(new ARImageTrackableListener() {
            @Override
            public void didDetect(ARImageTrackable arImageTrackable) {
            }

            @Override
            public void didTrack(ARImageTrackable arImageTrackable) {
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void didLose(ARImageTrackable arImageTrackable) {
                linearLayout.setVisibility(View.GONE);
            }
        });
    }


    private void startAlarm() {

        if (alarm.getAlarmTonePath() != "") {
            mediaPlayer = new MediaPlayer();
            if (alarm.getVibrate()) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = { 1000, 200, 200, 200 };
                vibrator.vibrate(pattern, 0);
            }
            try {
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.setDataSource(this,
                        Uri.parse(alarm.getAlarmTonePath()));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (Exception e) {
                mediaPlayer.release();
                alarmActive = false;
            }
        }

    }
    @Override
    public void onBackPressed() {
        if (!alarmActive)
            super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        StaticWakeLock.lockOff();
    }

    @Override
    protected void onDestroy() {

        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.stop();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    public void endAlarm(){
        if (isAnswerCorrect()) {
            alarmActive = false;
            Intent activeCheckIntent = new Intent(this, AlarmActiveCheckService.class);
            stopService(activeCheckIntent);

            if (vibrator != null)
                vibrator.cancel();
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException ise) {

            }
            try {
                mediaPlayer.release();
            } catch (Exception e) {

            }
            StaticWakeLock.lockOff();
            this.finish();
            System.runFinalizersOnExit(true);
            System.exit(0);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                VolumeManager volumeManager = new VolumeManager(this);
                volumeManager.volumeUp();
                Log.d("key","VOLUME CODE");
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean isAnswerCorrect(){
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        alarmActive = true;
    }

}
