package com.example.myapplication;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

public class ASRapplication extends Application {
    @Override
    public void onCreate() {
        SpeechUtility.createUtility(this,"appid=ee7831c0");
        super.onCreate();
    }
}
