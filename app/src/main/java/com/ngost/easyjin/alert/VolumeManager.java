package com.ngost.easyjin.alert;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Jinyoung on 2018-02-08.
 */

public class VolumeManager {
    private Context context;

    public VolumeManager(Context context){
        this.context = context;

    }

    public  void volumeUp(){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        // 현재 볼륨 가져오기
        int volume = am.getStreamVolume(AudioManager.STREAM_ALARM);
        // volume이 15보다 작을 때만 키우기 동작

        if(volume <= 15) {
            am.setStreamVolume(AudioManager.STREAM_ALARM, 15, AudioManager.STREAM_ALARM);
        }else {
        }

    }
}