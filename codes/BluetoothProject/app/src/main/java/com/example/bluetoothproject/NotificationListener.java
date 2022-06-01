package com.example.bluetoothproject;

import static android.speech.tts.TextToSpeech.ERROR;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Switch;


import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NotificationListener extends NotificationListenerService {

    private TextToSpeech tts;
    private float speed = 1.0F;
    SharedPreferences spf;

    @Override
    public void onCreate() {
        super.onCreate();

        //TTS api 사용
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate(1);
                }
            }
        });
        spf = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i("NotificationListener", "Connected");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("NotificationListener", "Started");
        processCommand(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void processCommand(Intent intent) {
        //intent로 보내진 값들을 받아옴
        speed = intent.getFloatExtra("speed_value", 0);
        tts.setSpeechRate(speed);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        String result = "";
        String pack = sbn.getPackageName();
        //카카오톡으로 오는 알림을 읽도록 함.
        if (pack.equals("com.kakao.talk")) {
            Notification notification = sbn.getNotification();
            Bundle bundle = notification.extras;
            try {
                String sender = bundle.getString(NotificationCompat.EXTRA_TITLE) + "님이 카톡을 보냈습니다.";
                String message = bundle.getString(NotificationCompat.EXTRA_TEXT);
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String current = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분").format(date);
                Log.i("sender", sender);
                System.out.println(spf.getBoolean("sender", false));
                Log.i("speed_value", String.valueOf(speed));

                //message가 null이 아니거나 기능을 사용할 때 tts.speak 사용
                if(message != null && spf.getBoolean("func", true)) {
                    if(spf.getBoolean("sender",false) ) {
                        result += sender;
                    }

                    if(message.length() >= 20) {
                        result += "장문의 메세지이니 나중에 확인하시기 바랍니다.";
                    }
                    else {
                        result += message;
                    }
                    if(spf.getBoolean("time",false) ) {
                        result += current;
                    }

                    tts.speak(result, 0, null, null);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(tts!=null){ // 사용한 TTS객체 제거
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
