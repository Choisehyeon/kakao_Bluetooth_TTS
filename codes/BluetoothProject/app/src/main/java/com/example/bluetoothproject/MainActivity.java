package com.example.bluetoothproject;

import static android.util.Log.ERROR;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener{

    private RecyclerView recyclerView;
    private static TextToSpeech tts;
    private boolean sender_check;
    private boolean content_check;
    private boolean time_check;

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AutoPermissions.Companion.loadAllPermissions(this, 101);


        if (!permissionGranted()) {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Toast.makeText(getApplicationContext(), "권한을 허용해주세요", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<ExpandableListAdapter.Item> data = new ArrayList<>();

        ExpandableListAdapter.Item ble = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "블루투스 연결");
        ble.invisibleChildren = new ArrayList<>();
        ble.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD_TS, "현재 상태"));
        ble.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD_TT, "연결된 기기"));
        ble.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD_TI, "블루투스 설정"));
        data.add(ble);

        ExpandableListAdapter.Item sound = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "사운드 설정");
        sound.invisibleChildren = new ArrayList<>();
        sound.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD_TSB, "볼륨"));
        sound.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD_TSP, "읽기 속도"));
        sound.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD_TS, "진동 알림"));
        data.add(sound);

        ExpandableListAdapter.Item text = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "텍스트 내용 설정");
        text.invisibleChildren = new ArrayList<>();
        text.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD_TS, "발신자"));
        text.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD_TS, "발신시간"));
        data.add(text);
        recyclerView.setAdapter(new ExpandableListAdapter(data));
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    private boolean permissionGranted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager.isNotificationListenerAccessGranted(new ComponentName(getApplication(), NotificationListener.class));

        }
        else {
            return NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext()).contains(getApplicationContext().getPackageName());
        }
    }

    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {
    }

}