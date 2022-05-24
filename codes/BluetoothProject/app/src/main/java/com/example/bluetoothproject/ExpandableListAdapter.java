package com.example.bluetoothproject;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.appsearch.GetByDocumentIdRequest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int HEADER = 0;
    public static final int CHILD_TS = 1;
    public static final int CHILD_TT = 2;
    public static final int CHILD_TI = 3;
    public static final int CHILD_TSB = 4;
    public static final int CHILD_TSP = 5;


    Context context;
    BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    String bDevice = "없음";
    Intent intent;
    View view;
    BroadcastReceiver blReceiver;

    private final List<Item> data;


    public ExpandableListAdapter(List<Item> data) {
        this.data = data;
    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        context = parent.getContext();
        switch (type) {
            case HEADER:
                LayoutInflater inflaterHeader = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterHeader.inflate(R.layout.parent, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD_TS:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.child_ts, parent, false);
                intent = new Intent(view.getContext(), NotificationListener.class);
                ListChildTSViewHolder child_ts = new ListChildTSViewHolder(view);
                return child_ts;
            case CHILD_TT:
                LayoutInflater inflater1 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater1.inflate(R.layout.child_tt, parent, false);
                ListChildTTViewHolder child_tt = new ListChildTTViewHolder(view);
                return child_tt;
            case CHILD_TI:
                LayoutInflater inflater2 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater2.inflate(R.layout.child_ti, parent, false);
                ListChildTIViewHolder child_ti = new ListChildTIViewHolder(view);
                return child_ti;
            case CHILD_TSB:
                LayoutInflater inflater3 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater3.inflate(R.layout.child_tsb, parent, false);
                ListChildTSBViewHolder child_tsb = new ListChildTSBViewHolder(view);
                return child_tsb;
            case CHILD_TSP:
                LayoutInflater inflater4 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater4.inflate(R.layout.child_tsp, parent, false);
                intent = new Intent(view.getContext(), NotificationListener.class);
                ListChildTSPViewHolder child_tsp = new ListChildTSPViewHolder(view);
                return child_tsp;



        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        start();

        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_title.setText(item.text1);
                if (item.invisibleChildren == null) {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.up);
                } else {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.down);
                }
                //리사이클러뷰를 열고 접을 수 있게
                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<Item>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while (data.size() > pos + 1 && (data.get(pos + 1).type == CHILD_TS || data.get(pos + 1).type == CHILD_TT || data.get(pos + 1).type == CHILD_TI
                                    || data.get(pos + 1).type == CHILD_TSB || data.get(pos + 1).type == CHILD_TSP)) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            notifyDataSetChanged();
                            itemController.btn_expand_toggle.setImageResource(R.drawable.down);
                        } else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            notifyDataSetChanged();
                            itemController.btn_expand_toggle.setImageResource(R.drawable.up);
                            item.invisibleChildren = null;
                        }
                    }
                });
                break;
            case CHILD_TS:

                final ListChildTSViewHolder itemController1 = (ListChildTSViewHolder) holder;
                itemController1.refferalItem = item;
                itemController1.child_title.setText(item.text1);

                SharedPreferences spf = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spf.edit();
                SharedPreferences sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);


                //블루투스 on/off
                if (item.text1.equals("현재 상태")) {

                    //블루투스 현재 상태에 따라 버튼이 세팅됨
                   if (bAdapter.getState() == BluetoothAdapter.STATE_ON) {
                        itemController1.aSwitch.setChecked(true);
                    } else {
                        itemController1.aSwitch.setChecked(false);
                    }

                   //버튼 on/off에 따라 블루투스 사용 설정됨
                    itemController1.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked) {
                                if (bAdapter == null) {
                                } else {
                                    if (bAdapter.isEnabled()) {
                                    } else {
                                        bAdapter.enable();
                                    }
                                }
                            } else {
                                if (bAdapter.isEnabled()) {
                                    bAdapter.disable();
                                } else {
                                }
                            }
                        }
                    });
                }
                //진동알림과 소리 알림 설정
                if(item.text1.equals("진동 알림")) {
                    AudioManager audioManager;
                    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    //현재 모드에 따라 버튼이 세팅됨
                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                        itemController1.aSwitch.setChecked(true);
                    } else {
                        itemController1.aSwitch.setChecked(false);
                    }
                    //버튼에 따라 진동과 소리 설정 가능
                    itemController1.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked) {
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            } else {
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            }
                        }
                    });

                }
                //발신자 버튼 on/off
                if(item.text1.equals("발신자")) {
                    boolean check = false;
                    check = sharedPreferences.getBoolean("sender", false);
                    itemController1.aSwitch.setChecked(check);

                    itemController1.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            //버튼이 on되면 NotificationListner class로 true가 보내짐
                            editor.putBoolean("sender", isChecked);
                            editor.commit();
                            intent.putExtra("sender_check", isChecked);
                            view.getContext().startService(intent);
                        }
                    });
                }
                //발신시간 버튼 on/off
                if(item.text1.equals("발신시간")) {
                    boolean check = false;
                    check = sharedPreferences.getBoolean("time", false);
                    itemController1.aSwitch.setChecked(check);

                    itemController1.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            //버튼이 on되면 NotificationListner class로 true가 보내짐
                            editor.putBoolean("time", isChecked);
                            editor.commit();
                            intent.putExtra("time_check", isChecked);
                            view.getContext().startService(intent);
                        }
                    });
                }

                break;
            case CHILD_TT:
                //연결된 기기
                final ListChildTTViewHolder itemController2 = (ListChildTTViewHolder) holder;
                itemController2.refferalItem = item;
                itemController2.child_title.setText(item.text1);
                itemController2.child_content.setText(bDevice);

                //브로드캐스트 리시버를 통해 블루투스에 연결된 기기 이름 가져옴
                blReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                        if ("android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                            try {
                                bDevice = device.getName().toString();
                                itemController2.child_content.setText(bDevice);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }

                        } else if ("android.bluetooth.device.action.ACL_DISCONNECTED".equals(action)) {
                            bDevice = "없음";
                            itemController2.child_content.setText(bDevice);
                        }
                    }
                };



                break;
            case CHILD_TI:
                //블루투스 설정 들어가는 부분
                final ListChildTIViewHolder itemController3 = (ListChildTIViewHolder) holder;
                itemController3.refferalItem = item;
                itemController3.child_title.setText(item.text1);

                itemController3.btn_setting_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        context.startActivity(intent);
                    }
                });
                break;
            case CHILD_TSB:
                //음량 조절하는 부분
                final ListChildTSBViewHolder itemController4 = (ListChildTSBViewHolder) holder;
                itemController4.refferalItem = item;
                itemController4.child_title.setText(item.text1);

                final AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int nCurrentVolumn = audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                itemController4.seekBar.setMax(nMax);
                itemController4.seekBar.setProgress(nCurrentVolumn);

                itemController4.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                progress, 0);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                break;
            case CHILD_TSP:
                //음성 속도 조절하는 부분
                final ListChildTSPViewHolder itemController5 = (ListChildTSPViewHolder) holder;
                itemController5.refferalItem = item;
                itemController5.child_title.setText(item.text1);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.speed, R.layout.spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemController5.spinner.setAdapter(adapter);



                sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
                int posi = 0;
                float speed = sharedPreferences.getFloat("speed", 0);
                Log.i("speed_value", String.valueOf(speed));
                if (speed == 0.5) posi = 0;
                else if (speed == 1.0) posi = 1;
                else if (speed == 1.5) posi = 2;
                else if (speed == 2.0) posi = 3;

                itemController5.spinner.setSelection(posi);

                itemController5.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        //sharedPreferences에 값을 저장하여 리사이클러뷰가 접었다 열려도 이전에 저장한 값이 그대로 적용되도록 함.
                        SharedPreferences spf = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = spf.edit();
                        float speed = Float.parseFloat(itemController5.spinner.getSelectedItem().toString());

                        editor.putFloat("speed", speed);
                        editor.commit();
                        //설정한 음성속도를 NotificationService에 보냄
                        intent.putExtra("speed_value", speed);
                        view.getContext().startService(intent);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) { }
                });

                break;

        }

    }

    public void start() {
        context.registerReceiver(blReceiver, new IntentFilter("android.bluetooth.device.action.ACL_CONNECTED"));
        context.registerReceiver(this.blReceiver, new IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED"));
    }



    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    private static class ListChildTSViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public Switch aSwitch;
        public Item refferalItem;

        public ListChildTSViewHolder(View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title_ts);
            aSwitch = itemView.findViewById(R.id.sw);
        }
    }

    private static class ListChildTTViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public TextView child_content;
        public Item refferalItem;

        public ListChildTTViewHolder(View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title_tt);
            child_content = (TextView) itemView.findViewById(R.id.child_content);
        }
    }

    private static class ListChildTIViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public ImageView btn_setting_toggle;
        public Item refferalItem;

        public ListChildTIViewHolder(View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title_ti);
            btn_setting_toggle = itemView.findViewById(R.id.btn_setting_toggle);
        }
    }

    public static class ListChildTSBViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public SeekBar seekBar;
        public Item refferalItem;

        public ListChildTSBViewHolder(View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title_tsb);
            seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
        }
    }

    public static class ListChildTSPViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public Spinner spinner;
        public Item refferalItem;

        public ListChildTSPViewHolder(View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title_tsp);
            spinner = (Spinner) itemView.findViewById(R.id.spinner);
        }
    }

    public static class Item {
        public int type;
        public String text1;
        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, String text1) {
            this.type = type;
            this.text1 = text1;
        }
    }



}
