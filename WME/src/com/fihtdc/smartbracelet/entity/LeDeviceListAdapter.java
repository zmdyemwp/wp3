package com.fihtdc.smartbracelet.entity;

import java.util.ArrayList;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.PairListActivity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LeDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;
    private int mConnectingPosition = -1;
    private boolean mIsConnected = false;
    public LeDeviceListAdapter(Context context) {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();
        mInflator = ((Activity) context).getLayoutInflater();
    }

    public void setConnectingPosition(int position,boolean isConnected){
        mConnectingPosition = position;
        mIsConnected = isConnected;
    }
    
    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.paring_item_list, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progress_connecting);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.selection);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0){
            viewHolder.deviceName.setText(deviceName);
        }else{
            viewHolder.deviceName.setText("unknow");
        }
        if(mConnectingPosition == i){
            if(mIsConnected){
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.imageView.setVisibility(View.VISIBLE);
            }else{
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.imageView.setVisibility(View.GONE);
            }
        }else{
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.GONE);
        }
            

        return view;
    }
}

class ViewHolder {
    TextView deviceName;
    ProgressBar progressBar;
    ImageView imageView;
}
