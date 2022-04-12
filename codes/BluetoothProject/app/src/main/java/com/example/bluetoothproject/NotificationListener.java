package com.example.bluetoothproject;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationListener extends NotificationListenerService {

    private TextToSpeech tts;
    private Float speed_value;
    private boolean sender_check;
    private boolean content_check;
    private boolean time_check;

    @Override
    public void onCreate() {
        super.onCreate();

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                tts.setLanguage(Locale.KOREA);
            }
        });


    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

        Log.i("NotificationListener","Connected");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("NotificationListener","Started");
        speed_value = intent.getFloatExtra("speed_value", 0);
        sender_check = intent.getBooleanExtra("sender_check", false);
        content_check = intent.getBooleanExtra("content_check", false);
        time_check = intent.getBooleanExtra("time_check", false);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String result = "";
        String pack = sbn.getPackageName();

        if (pack.equals("com.kakao.talk")) {
            Notification notification = sbn.getNotification();
            Bundle bundle = notification.extras;
            try {
                String sender = bundle.getString(NotificationCompat.EXTRA_TITLE) + "님이 카톡을 보냈습니다.";
                Log.i("notificaion", sender);
                String message = bundle.getString(NotificationCompat.EXTRA_TEXT);
                Log.i("notificaion", message);
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String current = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분").format(date);
                Log.i("notificaion", current);
                tts.setSpeechRate(speed_value);

                if(sender_check == true) {
                    result += sender;
                }
                System.out.println(sender_check);
                if(content_check == true) {
                    result += message;
                }
                System.out.println(content_check);
                if(time_check == true) {
                    result += date;
                }
                System.out.println(time_check);
                Log.i("Noti", result);

                tts.speak(result, TextToSpeech.QUEUE_FLUSH, null, null);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
    }
}
