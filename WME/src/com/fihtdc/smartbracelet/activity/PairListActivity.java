package com.fihtdc.smartbracelet.activity;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.entity.LeDeviceListAdapter;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;

public class PairListActivity extends ListActivity implements View.OnClickListener{
    
    //Views
    private ProgressBar mProgressBar;
    private ListView mListView;
    private ImageView mScanView;
    
    private LinearLayout mWifiLayout;
    private Button mWifiSwitchBtn;
    
    private BLEService mBluetoothLeService;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    
    private int mState = IDLE;
    private static final int ENABLEING = 1;
    private static final int SCANEDING = 2;
    private static final int CONNECTING = 3;
    private static final int IDLE = 0;
    private static final long SCAN_PERIOD = 60*1000;
    private static final int MSG_FINISH = 21233;
    private static final int MSG_STOP_SCAN = 21244;
    private static final int MSG_HIDE_VIEW = 21255;
    
    // Add by Galvin.q.wang 2013/11/14 14.46 begin (20131105 wenyue)
    private WifiManager mWifiSwitch;
//    private boolean OrgWifiState = false;
    // Add by Galvin.q.wang 2013/11/14 14.46 end

    BluetoothDevice mBluetoothDevice;
    private int mPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pairinglist);
        
        initViews();
      
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        setListAdapter(mLeDeviceListAdapter);
        initBTAdapter();
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    
    private WifiManager getWifiManager(){
        if(null == mWifiSwitch){
            mWifiSwitch = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        }
        return mWifiSwitch;
    }
    
    @Override
    protected void onResume() {
        Log.v("PairListActivity", "SDK_INT="+Build.VERSION.SDK_INT);
        Log.v("PairListActivity", "MANUFACTURER="+Build.MANUFACTURER);
        if(Utility.wifiCondition(getWifiManager())){
            mWifiLayout.setVisibility(View.VISIBLE);
        }
        
        if (!mBluetoothAdapter.isEnabled()) {
            mState = ENABLEING;
            mBluetoothAdapter.enable();
        }else{
            //scan LE devices
            scanLeDevice(true);
        }
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        
        if(mState == SCANEDING){
            scanLeDevice(false);
        }
        
        this.unregisterReceiver(mReceiver);
        this.unbindService(mServiceConnection);
        mHandler = null; 
        super.onDestroy();
    }
    
    
    private void initViews(){
        mProgressBar = (ProgressBar)this.findViewById(R.id.progress_scan);
        mListView = this.getListView();
        mScanView = (ImageView)this.findViewById(R.id.refresh);
        mScanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                scanLeDevice(true);
            }
        });
        
        mWifiLayout = (LinearLayout)this.findViewById(R.id.wifilayout);
        mWifiSwitchBtn = (Button)this.findViewById(R.id.switch1);
        mWifiSwitchBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // Add by Galvin.q.wang 2013/11/14 14.46 begin (20131105 wenyue)
              if (getWifiManager().isWifiEnabled()) {
                  mWifiSwitch.setWifiEnabled(false);
              }
              mWifiSwitchBtn.setEnabled(false);
              mHandler.sendEmptyMessageDelayed(MSG_HIDE_VIEW, 1000);
              // Add by Galvin.q.wang 2013/11/14 14.46 end
            }
        });
    }
    
    
    private void showListView(){
        if(!mListView.isShown()){
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }
    /**
     * @author F3060326 at 2013/8/2
     * @return void
     * @param @param enable true is scan
     * @Description: start to scan devices
     */
    private void scanLeDevice(final boolean enable) {
        LogApp.Logd("scanLeDevice="+enable);
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.removeMessages(MSG_STOP_SCAN);
            mHandler.sendEmptyMessageDelayed(MSG_STOP_SCAN, SCAN_PERIOD);
            mState = SCANEDING;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mState = IDLE;
        }
        
    }
    
    // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
    // BluetoothAdapter through BluetoothManager.
    private void initBTAdapter(){
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
    }
    
    
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        
        if(mState != CONNECTING){
            mScanView.setEnabled(false);
            mHandler.removeMessages(MSG_STOP_SCAN);
            mPosition = position;
            mBluetoothDevice = mLeDeviceListAdapter.getDevice(mPosition);
            if (mBluetoothDevice != null){
                mLeDeviceListAdapter.setConnectingPosition(mPosition,false);
                mLeDeviceListAdapter.notifyDataSetChanged();
                
                if (SCANEDING == mState) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mState = IDLE;
                }
                
                LogApp.Logd("onListItemClick"+mBluetoothDevice.getName());
                //start to connect the LE devices 
                if(mBluetoothLeService.connect(mBluetoothDevice.getAddress())){
                    LogApp.Logd("connecting!!!");
                    mState = CONNECTING;
                }else{
                    //Connect  failed
                    mState = IDLE;
                }
            }
        }
    }
    
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    for(int i = 0;i<scanRecord.length;i++){
//                        LogApp.Logd(String.format("%1$02d", scanRecord[i]));
//                    }
//                    LogApp.Logd(device.getName()+"="+scanRecord.length);
                    LogApp.Logd("device.type+"+device.getType());
                    if(null!=device && device.getType()==BluetoothDevice.DEVICE_TYPE_LE){
                        String name = device.getName();
                        if(null != name && name.startsWith("With")){
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                            //refresh UI
                            showListView();
                        }
                    }
                }
            });
        }
    };
    
    /**
     * The BroadcastReceiver that listens for discovered devices and changes the
     * title when discovery is finished.
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_ON) ;
                if(BluetoothAdapter.STATE_ON == state){
                    //turn on
                    if(mState == ENABLEING){
                        //scan LE devices
                        scanLeDevice(true);
                    }
                }else if(BluetoothAdapter.STATE_OFF == state){
                    //turn off
                    if(mState == ENABLEING){
                        //notify APP 
                        scanLeDevice(false);
                    }
                }
                
            }
        }
    };
    
    
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //mBluetoothLeService.connect(mDeviceAddress);
            mBluetoothLeService.setCallbackHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            mHandler = null;
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(v.getId() == R.id.refresh){
            if(mState == SCANEDING){
                //scan LE devices
                scanLeDevice(true);
            }
        }
    }
   
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(PairListActivity.this.isFinishing()||PairListActivity.this.isDestroyed()){
                return;
            }
            switch (msg.what) {
            case BLEService.MSG_CONNECT_RESULT:
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mState == CONNECTING){
                            mState = IDLE;
                            mLeDeviceListAdapter.setConnectingPosition(mPosition,true);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                            mHandler.sendEmptyMessageDelayed(MSG_FINISH, 500);
                        }
                    }
                });
                break;
            case BLEService.MSG_DISCONNECT_RESULT:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       //do nothing
                    }
                });
                break;
            case MSG_FINISH: {
                Intent intent = new Intent();
                intent.putExtra(BLEService.BLE_DEVICE, mBluetoothDevice);
                setResult(RESULT_OK,intent);
                finish();
                }
                break;  
            case MSG_STOP_SCAN:
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mState = IDLE;
                break;
                
            case MSG_HIDE_VIEW:
                mWifiLayout.setVisibility(View.GONE);
                break;
            default:
                super.handleMessage(msg);
            }
        }
    };
}
