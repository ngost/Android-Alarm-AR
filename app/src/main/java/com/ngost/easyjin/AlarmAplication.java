package com.ngost.easyjin;

import android.app.Application;

import eu.kudan.kudan.ARAPIKey;

/**
 * Created by Jinyoung on 2018-02-07.
 */

public class AlarmAplication extends Application {
    public AlarmAplication(){
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey(ApiSingletone.getInstance().getKey());
    }
}
