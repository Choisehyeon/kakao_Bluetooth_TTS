package com.example.bluetoothproject;

import static android.bluetooth.BluetoothProfile.GATT;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.bluetoothproject.databinding.ActivityMain1Binding;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ActivityMain1Binding binding;
    private Intent intent;
    private String bDevice;

    public static final String[] ANDROID_12_BLUETOOTH_PERMISSIONS = { Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT} ;




    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //세로방향 유지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = ActivityMain1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SlidingUpPanelLayout sliding = binding.mainFrame;
        intent = new Intent(MainActivity.this, NotificationListener.class);


        //permissionCheck();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!EasyPermissions.hasPermissions(this, ANDROID_12_BLUETOOTH_PERMISSIONS)) {
                EasyPermissions.requestPermissions(this, "please give me bluetooth permissions", 2,ANDROID_12_BLUETOOTH_PERMISSIONS);
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        }


        //notification 권한 설정
        if (!permissionGranted()) {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Toast.makeText(getApplicationContext(), "권한을 허용해주세요", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }

        binding.speakingOnoff.setImageResource(R.drawable.speaking_on);
        binding.imageOn.setImageResource(R.drawable.on);
        binding.imageOff.setImageResource(R.drawable.off_trans);

        //메인이미지 on일 때
        binding.imageOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sliding.setTouchEnabled(true);
                binding.lockview.setImageResource(R.drawable.unlock);
                binding.imageOn.setImageResource(R.drawable.on);
                binding.imageOff.setImageResource(R.drawable.off_trans);
                binding.speakingOnoff.setImageResource(R.drawable.speaking_on);
                //기능을 on하면 NotificationService에 true 보냄.
                intent.putExtra("fuc_check", true);
                startService(intent);
            }
        });

        //메인이미지 off일 때
        binding.imageOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sliding.setTouchEnabled(false);
                binding.lockview.setImageResource(R.drawable.lock);
                binding.imageOn.setImageResource(R.drawable.on_trans);
                binding.imageOff.setImageResource(R.drawable.off);
                binding.speakingOnoff.setImageResource(R.drawable.speaking_off);
                //기능을 off하면 NotificationService에 false 보냄.
                intent.putExtra("fuc_check", false);
                startService(intent);
            }
        });


        //리사이클러뷰
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
        binding.recyclerview.setAdapter(new ExpandableListAdapter(data));


        //registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));


    }



/*
    //sms receive 권한 확인
    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    private  void checksmsReceivePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.Receive_SMS");
            if(permissionCheck!=0){
                this.requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},1001);
            }
            else{
                Log.d("checkPermission", "No need to check permissions. SDK version < O");
            }
        }
    }*/

    //Notification 권한 확인
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


}