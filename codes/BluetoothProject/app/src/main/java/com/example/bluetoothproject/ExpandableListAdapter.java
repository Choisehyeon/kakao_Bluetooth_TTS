package com.example.bluetoothproject;

import static android.content.Context.AUDIO_SERVICE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD_TS = 1;
    public static final int CHILD_TT = 2;
    public static final int CHILD_TI = 3;
    public static final int CHILD_TSB = 4;
    public static final int CHILD_TSP = 5;
    public static final int CHILD_STS = 6;
    public static final int CHILD_CTS = 7;
    public static final int CHILD_DTS = 8;

    Context context;
    BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    private String bDevice;

    private List<Item> data;

    public ExpandableListAdapter(List<Item> data) {
        this.data = data;
    }


    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                bDevice = device.getName().toString();
            }

        }//end onReceive
    };


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view;
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
                ListChildTSPViewHolder child_tsp = new ListChildTSPViewHolder(view);
                return child_tsp;
            case CHILD_STS:
                LayoutInflater inflater5 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater5.inflate(R.layout.child_ts, parent, false);
                ListChildSTSViewHolder child_sts = new ListChildSTSViewHolder(view);
                return child_sts;
            case CHILD_CTS:
                LayoutInflater inflater6 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater6.inflate(R.layout.child_ts, parent, false);
                ListChildCTSViewHolder child_cts = new ListChildCTSViewHolder(view);
                return child_cts;
            case CHILD_DTS:
                LayoutInflater inflater7 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater7.inflate(R.layout.child_ts, parent, false);
                ListChildDTSViewHolder child_dts = new ListChildDTSViewHolder(view);
                return child_dts;

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        switch(item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_title.setText(item.text1);
                if(item.invisibleChildren == null) {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.up);
                } else {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.down);
                }
                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<Item>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while (data.size() > pos + 1 && (data.get(pos + 1).type == CHILD_TS || data.get(pos +1 ).type == CHILD_TT || data.get(pos+1).type == CHILD_TI
                            || data.get(pos + 1).type == CHILD_TSB || data.get(pos+1).type == CHILD_TSP || data.get(pos+1).type == CHILD_STS
                            || data.get(pos + 1).type == CHILD_CTS || data.get(pos+1).type == CHILD_DTS)){
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.down);
                        } else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
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


                if (bAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    itemController1.aSwitch.setChecked(true);
                } else {
                    itemController1.aSwitch.setChecked(false);
                }

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


                break;
            case CHILD_TT:
                final ListChildTTViewHolder itemController2 = (ListChildTTViewHolder) holder;
                itemController2.refferalItem = item;
                itemController2.child_title.setText(item.text1);
                itemController2.child_content.setText(bDevice);


                break;
            case CHILD_TI:
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
                final ListChildTSPViewHolder itemController5 = (ListChildTSPViewHolder) holder;
                itemController5.refferalItem = item;
                itemController5.child_title.setText(item.text1);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.speed, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemController5.spinner.setAdapter(adapter);


                itemController5.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        float f = Float.parseFloat(itemController5.spinner.getSelectedItem().toString());
                        Intent intent = new Intent(context, NotificationListener.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("speed_value", f);

                        context.startService(intent);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                break;
            case CHILD_STS:
                final ListChildSTSViewHolder itemController6 = (ListChildSTSViewHolder) holder;
                itemController6.refferalItem = item;
                itemController6.child_title.setText(item.text1);

                itemController6.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        Intent intent = new Intent(context, NotificationListener.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if(isChecked) {
                            intent.putExtra("sender_check", true);
                        } else {
                            intent.putExtra("sender_check", false);
                        }
                        context.startService(intent);
                    }
                });

                break;

            case CHILD_CTS:
                final ListChildCTSViewHolder itemController7 = (ListChildCTSViewHolder) holder;
                itemController7.refferalItem = item;
                itemController7.child_title.setText(item.text1);

                itemController7.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        Intent intent = new Intent(context, NotificationListener.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if(isChecked) {
                            intent.putExtra("content_check", true);
                        } else {
                            intent.putExtra("content_check", false);
                        }
                        context.startService(intent);
                    }
                });
                break;
            case CHILD_DTS:
                final ListChildDTSViewHolder itemController8 = (ListChildDTSViewHolder) holder;
                itemController8.refferalItem = item;
                itemController8.child_title.setText(item.text1);

                itemController8.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        Intent intent = new Intent(context, NotificationListener.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if(isChecked) {
                            intent.putExtra("time_check", true);
                        } else {
                            intent.putExtra("time_check", false);
                        }
                        context.startService(intent);
                    }
                });
                break;

        }
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

    private static class ListChildTIViewHolder extends RecyclerView.ViewHolder{
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

    public static class ListChildSTSViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public Switch aSwitch;
        public Item refferalItem;
        public ListChildSTSViewHolder(@NonNull View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title_ts);
            aSwitch = itemView.findViewById(R.id.sw);
        }
    }

    public static class ListChildCTSViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public Switch aSwitch;
        public Item refferalItem;
        public ListChildCTSViewHolder(@NonNull View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title_ts);
            aSwitch = itemView.findViewById(R.id.sw);
        }
    }

    public static class ListChildDTSViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public Switch aSwitch;
        public Item refferalItem;
        public ListChildDTSViewHolder(@NonNull View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title_ts);
            aSwitch = itemView.findViewById(R.id.sw);
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
