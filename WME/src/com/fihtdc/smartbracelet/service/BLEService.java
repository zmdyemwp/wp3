/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fihtdc.smartbracelet.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.SettingsFragment;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;
import com.yl.ekgrr.EkgRR;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

public class BLEService extends Service {
    public static final String TAG = "BLEService";

    private static final int WME_SERVICE_STATUS = 1000;
    public static final String BLE_DEVICE = "ble_device";
    public static final String BLE_DEVICE_NAME = "ble_device_name";
    public static final String BLE_DEVICE_VERSION = "ble_device_version";

    /*-----Control with Activity begin-----------*/
    // MSG TO ACTIVITY
    // Interrupt message send
    public static final int MSG_IT_DISCONNECT = 1;
    public static final int MSG_IT_PHONE_EVENT = 2;
    public static final int MSG_IT_RECONNECT = 3;
    public static final int MSG_IT_SHUTDOWN = 4;

    // BT connect
    public static final int MSG_SCAN_RESULT = 10;
    public static final int MSG_CONNECT_RESULT = 11;
    public static final int MSG_DISCONNECT_RESULT = 12;
    public static final int MSG_DEVICENAME_CHANGE_RESULT = 13;
    public static final int MSG_VERSION_CHANGE_RESULT = 14;

    // Measure date message
    public static final int MSG_ECG_MEASURE_SCORE = 20;
    public static final int MSG_ECG_COACHE_SCORE = 21;
    /*-------Control with Activity end-------------*/

    //BT 
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mConnectedBD = null;
    
    //BT STATE
//    private int mConnectionState = STATE_DISCONNECTED;
//    private static final int STATE_DISCONNECTED = 0;
//    private static final int STATE_CONNECTING = 1;
//    private static final int STATE_CONNECTED = 2;

    private final IBinder mBinder = new LocalBinder();
    private Handler mCurrenCallbackHandler = null;

    // Find me
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private int mVolumeLevel;
    private boolean mPlayFindMe = false;

    // MSG
    private static final int FACEB00K_DELAY = 30;
    private static final int EMAIL_DELAY = 31;
    private static final int UPDATE_TIME_DELAY = 32;
    private static final int GETVERSION_DELAY = 33;
    private static final int GETNAME_DELAY = 34;
    private static final int AUTO_CONNECT_MSG = 35;
    private static final int ECG_START_MSG = 36;
    private static final int ECG_STOP_MSG = 37;
    private static final int LNF_LISE_MSG = 38;
    private static final int CAMERA_ON_MSG = 39;
    private static final int CAMERA_OFF_MSG = 40;
    private static final int CAMERA_ON_RESET_MSG = 41;
    
    //TIME
    //MSG delay time in BLE command 
    private static final int MSG_DELAY_TIME = 3 * 1000;
    private static final long GMAIL_CHECK_INTEVAL = 15 * 60 * 1000;
    

    private boolean mUserFergot = false;
    private boolean mECGMode = false;
    // private long mPreStartTime = 0;

    // measure data
    private long mPreStartTime = 0;
    private int mMeasureTotalTime = 3 * 60;
    private EkgRR eRR;

    Timer mTimerChecker;

    private boolean mIsCameraMode = false;
    
    //Fixed stop issue
    private boolean mCmdStopSend = false;
    @Override
    public void onCreate() {
        LogApp.Logd(TAG, "BLEService onCreate()");
        setAsForeground();
        setBluetoothStateChange();
        addCameraStateChange();
        initTimer();
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that
        // BluetoothGatt.close() is called
        // such that resources are cleaned up properly. In this particular
        // example, close() is
        // invoked when the UI is disconnected from the Service.
        // close();
        // FIXME will close after dis-connect
        return super.onUnbind(intent);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogApp.Logd(TAG, "BLEService onStartCommand()" + flags + "startId=" + startId);
        handleCommand(intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogApp.Logd(TAG, "BLEService onDestroy()");
        mTimerChecker.cancel();
        myHandler.removeMessages(FACEB00K_DELAY);
        myHandler.removeMessages(EMAIL_DELAY);
        myHandler.removeMessages(UPDATE_TIME_DELAY);
        myHandler.removeMessages(GETVERSION_DELAY);
        myHandler.removeMessages(GETNAME_DELAY);
        myHandler.removeMessages(AUTO_CONNECT_MSG);
        myHandler.removeMessages(ECG_START_MSG);
        myHandler.removeMessages(ECG_STOP_MSG);
        myHandler.removeMessages(LNF_LISE_MSG);
        myHandler.removeMessages(CAMERA_ON_MSG);
        myHandler.removeMessages(CAMERA_OFF_MSG);
        myHandler.removeMessages(CAMERA_ON_RESET_MSG);
        this.unregisterReceiver(mBTState);
        this.unregisterReceiver(mCameraState);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    /**
     * @author F3060326 at 2013/9/4
     * @return void
     * @param
     * @Description: init timer
     */
    private void initTimer() {
        mTimerChecker = new Timer();
        mTimerChecker.schedule(new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent(Constants.CHECK_GMAIL_ACTION);
                BLEService.this.sendBroadcast(intent);
            }
        }, 1000, GMAIL_CHECK_INTEVAL);
    }

    /**
     * @author F3060326 at 2013/8/7
     * @return void
     * @param
     * @Description: Set as for Foreground avoid to killed by system
     */
    private void setAsForeground() {
        Notification status = new Notification();
        status.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        status.priority = Notification.PRIORITY_MIN;

        startForeground(WME_SERVICE_STATUS, status);
    }


    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case FACEB00K_DELAY:
                alertNotificationWithFacebook();
                break;
            case EMAIL_DELAY:
                alertNotificationWithEmail();
                break;
            case UPDATE_TIME_DELAY:
                NotifySBUpdateTime();
                break;
            case GETVERSION_DELAY:
                NotifySBGetVersion();
                break;
            case GETNAME_DELAY:
                geteFriendlyName();
                break;
            case AUTO_CONNECT_MSG:
                autoConnect();
                break;
            case ECG_START_MSG:
                NotifyECGStart(true, (Integer) (msg.obj));
                break;
            case ECG_STOP_MSG:
                NotifyECGStart(false, (Integer) (0));
                break;
            case LNF_LISE_MSG:
                setListenerNotification(getWeLNFCharacteristic(), true);
                break;
            case CAMERA_ON_MSG:
                NotifySBCameraMode(true);
                break;
            case CAMERA_OFF_MSG:
                NotifySBCameraMode(false);
                break;
            case CAMERA_ON_RESET_MSG:
                if(mIsCameraMode){
                    NotifySBCameraMode(true);
                }
                break;    
            default:
                break;
            }
            super.handleMessage(msg);
        }

    };

    private void setBluetoothStateChange() {
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mBTState, intentfilter);
    }

    
    BroadcastReceiver mBTState = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (null == arg1)
                return;
            if (BluetoothAdapter.STATE_OFF == arg1.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
                mBluetoothDeviceAddress = "";
            }
        }
    };

    private void addCameraStateChange() {
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Constants.COMMAND_CAMERA_STATE_ON);
        intentfilter.addAction(Constants.COMMAND_CAMERA_STATE_OFF);
        this.registerReceiver(mCameraState, intentfilter);
    }
    
    BroadcastReceiver mCameraState = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            myHandler.removeMessages(CAMERA_ON_MSG);
            myHandler.removeMessages(CAMERA_OFF_MSG);
            if (Constants.COMMAND_CAMERA_STATE_ON.equalsIgnoreCase(action)){
                myHandler.sendEmptyMessageDelayed(CAMERA_ON_MSG, 1500);
//                NotifySBCameraMode(true);
            }else if(Constants.COMMAND_CAMERA_STATE_OFF.equalsIgnoreCase(action)){
//                NotifySBCameraMode(false);
                myHandler.sendEmptyMessageDelayed(CAMERA_OFF_MSG, 1500);
            }
        }
    };
    /**
     * @author F3060326 at 2013/7/30
     * @return void
     * @param @param intent
     * @Description: handle command form intent
     */
    private void handleCommand(Intent intent) {
        if (null == intent) {
            LogApp.Loge("handleCommand null == intent");
            return;
        } else {
            String action = intent.getAction();
            // Add ECG mode
            if (Constants.COMMAND_ACTION.equals(action) && !mECGMode) {
                String extra = intent.getStringExtra(Constants.COMMAND_EXTRA);
                if(mIsCameraMode){
                    myHandler.sendEmptyMessageDelayed(CAMERA_ON_RESET_MSG, 6000);
                }
                if (Constants.COMMAND_EXTRA_ALARM.equals(extra)) {
                    LogApp.Logd("Alarm command");
                    alertNotificationWithAlarm();
                } else if (Constants.COMMAND_EXTRA_EMAIL.equals(extra)) {
                    int num = intent.getIntExtra(
                            Constants.COMMAND_EXTRA_EMAIL_COUNT, 0);
                    LogApp.Logd("Email command notification num=" + num);
                    NotifySBupdateUnreadMail(num);
                    if (num > 0) {
                        myHandler.sendEmptyMessageDelayed(EMAIL_DELAY,
                                MSG_DELAY_TIME);
                    }
                } else if (Constants.COMMAND_EXTRA_FB_NF.equals(extra)) {
                    int num = intent.getIntExtra(
                            Constants.COMMAND_EXTRA_FB_NF_COUNT, 0);
                    LogApp.Logd("Facebook command notification num=" + num);
                    NotifySBUnreadFBNotification(num);
                    if (num > 0) {
                        myHandler.sendEmptyMessageDelayed(FACEB00K_DELAY,
                                MSG_DELAY_TIME);
                    }
                } else if (Constants.COMMAND_EXTRA_INCOMING_CALL.equals(extra)) {
                    LogApp.Logd("Incoming call command");
                    alertNotificationWithCall();
                } else if (Constants.COMMAND_EXTRA_STOP_INCOMING_CALL.equals(extra)) {
                    LogApp.Logd("Clear Incoming call command");
                    alertClearNotificationWithCall();
                } else if (Constants.COMMAND_EXTRA_FB_CHECKIN_FAIL
                        .equals(extra)) {
                    LogApp.Logd("Facebook command check in fail");
                    alertNotificationWithFacebookCheckin(false);
                } else if (Constants.COMMAND_EXTRA_FB_CHECKIN_OK.equals(extra)) {
                    LogApp.Logd("Facebook command check in ok");
                    alertNotificationWithFacebookCheckin(true);
                }
            } else if (Constants.COMMAND_RESET.equals(action)) {
                LogApp.Logv("Constants.COMMAND_RESET!!!");
                mECGMode = false;
                disconnect();
            } else if (Constants.COMMAND_REBOOT_ACTION.equals(action)) {
                LogApp.Logv("COMMAND_REBOOT_ACTION");
                autoConnect();
            } else {

            }
        }
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param @param boundle
     * @param @param what
     * @Description: send2Target will callback MSG to current activity
     */
    private void send2Target(Bundle boundle, int what) {
        if (null != mCurrenCallbackHandler) {
            LogApp.Logd("send2Target" + mCurrenCallbackHandler);
            Message msg = Message.obtain(mCurrenCallbackHandler, what);
            msg.setData(boundle);
            msg.sendToTarget();
        }
    }

    private void dealwithConnected(){
        mPreStartTime = 0;
        // intentAction = ACTION_GATT_CONNECTED;
//        mConnectionState = STATE_CONNECTED;
        // broadcastUpdate(intentAction);

        myHandler.removeMessages(AUTO_CONNECT_MSG);

        // Attempts to discover services after successful connection.
        boolean disS = mBluetoothGatt.discoverServices();
        LogApp.Logd(TAG, "Attempting to start service discovery:"
                + disS);

        // saveConnecttedBLEDevices(gatt.getDevice());

        // send to target activity
        // send2Target(new Bundle(), MSG_CONNECT_RESULT);
    }
    
    /**
     * @author F3060326 at 2013/10/23
     * @return void
     * @param 
     * @Description: fixed APPG2-5489 
     */
    private void dealwithDisConnected(){
        mECGMode = false;
        stopNotifyEventFromWME();
        // intentAction = ACTION_GATT_DISCONNECTED;
//        mConnectionState = STATE_DISCONNECTED;
        LogApp.Logd(TAG,
                "Disconnected from GATT server. STATE_DISCONNECTED");
        // broadcastUpdate(intentAction);
        // send to target activity
        send2Target(new Bundle(), MSG_DISCONNECT_RESULT);
        mConnectedBD = null;
        saveConnecttedBLEDevices(null);
        // notify facebook service BLE connected
        NotifyFBNFService(false);
        notifyLost();
    }
    
    private void notifyLost(){
        Intent intent = new Intent(Constants.COMMAND_LOST_CAMERA);
        this.sendBroadcast(intent);
    }
    
    // Implements callback methods for GATT events that the app cares about. For
    // example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                int newState) {
            LogApp.Logd(TAG, "onConnectionStateChange");
            // String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogApp.Logd(TAG, "Connected to GATT server. STATE_CONNECTED");
                dealwithConnected();
                
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                dealwithDisConnected();

                if (needScanAgain()) {
                    if (!mUserFergot) {
                        // try to reconnect WP1 device again
                        // myHandler.sendEmptyMessageDelayed(AUTO_CONNECT_MSG,
                        // DELAY_TIME);
                        myHandler.sendEmptyMessageDelayed(AUTO_CONNECT_MSG,
                                5 * MSG_DELAY_TIME);
                    }
                }
                
                mUserFergot = false;
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                LogApp.Logd(TAG, "GATT server. STATE_CONNECTING");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                LogApp.Logd(TAG, "GATT server. STATE_DISCONNECTING");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LogApp.Logd(TAG, "onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                // we need do list 5 action after service discover.
                // 1.Save current connected devices
                // 2.listener notification from WME(LNF&SNF)
                // 4.send update time command (delay 3s))
                // 5.start FACEBOOK notification service
                
                //Fixed JimmyChen !!!!
                mBluetoothGatt = gatt;
                
                mConnectedBD = gatt.getDevice();
                saveConnecttedBLEDevices(mConnectedBD);
                // send to target activity
                send2Target(new Bundle(), MSG_CONNECT_RESULT);

//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    // Auto-generated catch block
//                    e.printStackTrace();
//                }
                
//                setListenerNotification(getTest1Characteristic(), false);
//                setListenerNotification(getTest2Characteristic(), false);
                
                /*
                BluetoothGattCharacteristic bc = getWeLNFCharacteristic();
                BluetoothGattDescriptor descriptor = bc.getDescriptor(
                        WMEGattAttributes.Descriptor_ID2);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                
                
                BluetoothGattDescriptor descriptor2 = bc.getDescriptor(
                        WMEGattAttributes.SERVICE2_CHARACTERISTIC_ID);
                descriptor2.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor2);
                
                
                
                BluetoothGattDescriptor descriptor = getWeLNFCharacteristic().getDescriptor(
                        WMEGattAttributes.Descriptor_ID1);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                
                BluetoothGattDescriptor descriptor2 = getWeLNFCharacteristic().getDescriptor(
                        WMEGattAttributes.Descriptor_ID2);
                descriptor2.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor2);
                setListenerNotification(getWeLNFCharacteristic(), true);*/
                
                boolean isSucess = setListenerNotification(getWeLNFCharacteristic(), true);
                LogApp.Logd(TAG, "setListenerNotification getWeLNFCharacteristic="+isSucess);
                isSucess = setListenerNotification(getWeSNFCharacteristic(), true);
                LogApp.Logd(TAG, "setListenerNotification getWeSNFCharacteristic="+isSucess);
                
                myHandler.sendEmptyMessage(GETNAME_DELAY);
                myHandler.sendEmptyMessageDelayed(UPDATE_TIME_DELAY,MSG_DELAY_TIME);


                NotifyFBNFService(true);
            } else {
                LogApp.Logd(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status) {
            LogApp.Logd(TAG, "onCharacteristicRead!!!");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                if (characteristic.getUuid().equals(
                        WMEGattAttributes.NAME_CHARACTERISTIC_ID)) {
                    LogApp.Logd(TAG,
                            "onCharacteristicRead-->NAME_CHARACTERISTIC_ID");
                    final byte[] data = characteristic.getValue();
                    LogApp.Logd(TAG, "onCharacteristicRead-->data"
                            + data.length);
                    final StringBuilder stringBuilder = new StringBuilder(
                            data.length);
                    if (data != null && data.length > 0) {
                        for (byte byteChar : data)
                            stringBuilder.append(String.format("%s", byteChar));
                        String s = "";
                        try {
                            s = new String(data, "US-ASCII");
                            LogApp.Logd(TAG, "onCharacteristicRead-->s=" + s);

                            Utility.setSharedPreferenceValue(BLEService.this,
                                    Utility.BLE_NAME, s);

                            Bundle bd = new Bundle();
                            bd.putString(BLE_DEVICE_NAME, s);
                            send2Target(bd, MSG_DEVICENAME_CHANGE_RESULT);
                        } catch (UnsupportedEncodingException e) {
                            LogApp.Logd(TAG,
                                    "onCharacteristicRead-->UnsupportedEncodingException");
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            // LogApp.Logd(TAG, "onCharacteristicChanged");
            // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            LogApp.Logd(TAG, "onCharacteristicChanged gatt="+gatt+"characteristic="+characteristic);
            UUID uuid = characteristic.getUuid();
            
            if (WMEGattAttributes.SNF_CHARACTERISTIC_ID.equals(uuid)) {

                LogApp.Logd(TAG,
                        "onCharacteristicChanged SNF_CHARACTERISTIC_ID");
                byte[] value = characteristic.getValue();

                if (null != value && value.length > 1) {
                    switch (value[0]) {
                    case WMEGattAttributes.DEV_USER_ACTION:
                        switch (value[1]) {
                        case WMEGattAttributes.DEV_USER_ACTION_FB_CHECK_IN:
                            devCheckIn();
                            break;
                        case WMEGattAttributes.DEV_USER_ACTION_START_FIND_ME:
                            if (Utility.getSharedPreferenceValue(
                                    BLEService.this,
                                    SettingsFragment.KEY_FINDME_ALERT_BRACELET,
                                    true)) {
                                if (!mPlayFindMe) {
                                    devFineMe(true);
                                }
                            }
                            break;
                        case WMEGattAttributes.DEV_USER_ACTION_STOP_FIND_ME:
                            devFineMe(false);
                            break;
                        case WMEGattAttributes.DEV_USER_ACTION_ECG_START:
                            devECGStart(true);
                            break;
                        case WMEGattAttributes.DEV_USER_ACTION_ECG_STOP:
                            devECGStart(false);
                            break;
                        default:
                            break;
                        }
                        break;
                    case (byte) WMEGattAttributes.DEV_TRIG:
                        devTrig();
                        break;
                    case (byte) WMEGattAttributes.DEV_CONN_PARAM_CHG:
                        devConnParamChange();
                        break;
                    case WMEGattAttributes.DEV_REMOTE_CONTROL:
                        devCameraControl();
                        break;
                    default:
                        break;
                    }
                }
            } else if (WMEGattAttributes.LNF_CHARACTERISTIC_ID.equals(uuid)) {
//                LogApp.Logd(TAG,
//                        "onCharacteristicChanged LNF_CHARACTERISTIC_ID");
                byte[] value = characteristic.getValue();
                if (null != value && value.length > 1) {
                    switch (value[0]) {
                    case WMEGattAttributes.DEV_USER_ACTION_ECG_START:
                        if(mBluetoothGatt == gatt){
                            devUserActionECGStart(value);
                        }
                        break;
                    case WMEGattAttributes.DEV_STAT_NOTIF:
                        devStateNotify(value);
                        break;
                    case WMEGattAttributes.APP_GET_VERSION:
                        devGetVersion(value);
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status) {
            LogApp.Logd(TAG, "onCharacteristicWrite");
            
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                BluetoothGattDescriptor descriptor, int status) {
            LogApp.Logd(TAG, "onDescriptorRead");
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                BluetoothGattDescriptor descriptor, int status) {
            LogApp.Logd(TAG, "onDescriptorWrite");
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            LogApp.Logd(TAG, "onReadRemoteRssi");
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            LogApp.Logd(TAG, "onReliableWriteCompleted");
            super.onReliableWriteCompleted(gatt, status);
        }
        
    };

    private AudioManager getAudioManager() {
        if (null == mAudioManager) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        return mAudioManager;
    }
    
    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param @param device
     * @Description: save the connect BLE device's MAC address & name
     */
    private void saveConnecttedBLEDevices(BluetoothDevice device) {
        String mac = "";
        String name = "";
        if (null != device) {
            mac = device.getAddress();
            name = device.getName();

            // do not save the mac to null except have new mac link again
            Utility.setSharedPreferenceValue(this, Utility.BLE_MAC, mac);
            if (!TextUtils.isEmpty(name)) {
                Utility.setSharedPreferenceValue(this, Utility.BLE_NAME, name);
            }
        }

        LogApp.Logd(TAG, "saveConnecttedBLEDevices mac=" + mac + "+name" + name);
    }

    /**
     * @author F3060326 at 2013/8/15
     * @return boolean
     * @param @return
     * @Description: when close the BT in Phone,we need rescan again
     */
    private boolean needScanAgain() {
        boolean isNeed = true;
        BluetoothAdapter ba = getBluetoothManager().getAdapter();
        if (BluetoothAdapter.STATE_TURNING_OFF == ba.getState()) {
            isNeed = false;
        }
        LogApp.Logd(TAG, "needScanAgain isNeed=" + isNeed);
        return isNeed;
    }

    /**
     * @author F3060326 at 2013/8/15
     * @return BluetoothManager
     * @param @return
     * @Description: instance of BluetoothManager
     */
    private BluetoothManager getBluetoothManager() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return mBluetoothManager;
    }

    /**
     * @author F3060326 at 2013/8/15
     * @return BluetoothManager
     * @param @return
     * @Description: instance of BluetoothAdapter
     */
    private BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = getBluetoothManager().getAdapter();
        }
        return mBluetoothAdapter;
    }

    /**
     * @author F3060326 at 2013/8/8
     * @return void
     * @param
     * @Description:
     */
    private void stopAllRunning() {
        LogApp.Logd(TAG, "stopAllRunningWME!!!@@");
        close();
        NotifyFBNFService(false);
        stopSelf();
    }

    /**
     * @author F3060326 at 2013/8/8
     * @return void
     * @param @param connect
     * @Description: start FACEBOOK notify service
     */
    private void NotifyFBNFService(boolean connect) {
        LogApp.Logd(TAG, "NotifyFBNFService connect=" + connect);
        Intent intent = new Intent();
        intent.setAction(Constants.FACEBOOK_NOTIFICATION_ACTION);
        if (!connect) {
            intent.putExtra(Constants.FACEBOOK_NOTIFICATION_EXTRA,
                    Constants.FACEBOOK_SERVICE_STOP);
        }
        startService(intent);
    }

    /**
     * @author F3060326 at 2013/8/28
     * @return void
     * @param
     * @Description: Need stop control form WME when WME DISCONNECTED
     */
    private void stopNotifyEventFromWME() {
        // stop find me play music
        devFineMe(false);
    }
    
    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     * 
     * @param characteristic
     *            The characteristic to read from.
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogApp.Logd(TAG, "BluetoothAdapter not initialized");
            return;
        }
        try {
            mBluetoothGatt.readCharacteristic(characteristic);
        } catch (Exception e) {
            LogApp.Loge(TAG, "readCharacteristic Exception" + e.getMessage());
        }

    }

    /**
     * Enables or disables notification on a give characteristic.
     * 
     * @param characteristic
     *            Characteristic to act on.
     * @param enabled
     *            If true, enable notification. False otherwise.
     */
    private boolean setListenerNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        LogApp.Logd(TAG, "setListenerNotification characteristic"+characteristic.getUuid());
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogApp.Logd(TAG, "BluetoothAdapter not initialized");
            return false;
        }

        try {
            return mBluetoothGatt.setCharacteristicNotification(characteristic,
                    enabled);
        } catch (Exception e) {
            LogApp.Loge(TAG,
                    "setCharacteristicNotification Exception" + e.getMessage());
            return false;
        }
    }
    


    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     * 
     * @return A {@code List} of supported services.
     */
//    private List<BluetoothGattService> getSupportedGattServices() {
//        LogApp.Logd(TAG, "getSupportedGattServices");
//        if (mBluetoothGatt == null)
//            return null;
//        return mBluetoothGatt.getServices();
//    }

    /**
     * @author F3060326 at 2013/8/6
     * @return BluetoothGattService
     * @param @return
     * @Description: SERVICE_ID (0x1800)
     */
    private BluetoothGattService getDeviceNameService() {
        if (null == mBluetoothGatt) {
            return null;
        }
        return mBluetoothGatt
                .getService(WMEGattAttributes.SERVICE_DEVICESNAME_ID);
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return BluetoothGattCharacteristic
     * @param @return
     * @Description: WRITE_CHARACTERISTIC_ID(0xFFF2)
     */
    private BluetoothGattCharacteristic getWeNameReadCharacteristic() {
        BluetoothGattService bgs = getDeviceNameService();
        if (null == bgs) {
            return null;
        } else {
            return bgs
                    .getCharacteristic(WMEGattAttributes.NAME_CHARACTERISTIC_ID);
        }
    }

    /**
     * @author F3060326 at 2013/8/10
     * @return BluetoothGattService
     * @param @return
     * @Description: SERVICE_ID (0xfff0)
     */
    private BluetoothGattService getWeService() {
        if (null == mBluetoothGatt) {
            return null;
        }
        return mBluetoothGatt.getService(WMEGattAttributes.SERVICE_ID);
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return BluetoothGattCharacteristic
     * @param @return
     * @Description: WRITE_CHARACTERISTIC_ID(0xFFF2)
     */
    private BluetoothGattCharacteristic getWeWriteCharacteristic() {
        BluetoothGattService bgs = getWeService();
        if (null == bgs) {
            return null;
        } else {
            return bgs
                    .getCharacteristic(WMEGattAttributes.WRITE_CHARACTERISTIC_ID);
        }
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return BluetoothGattCharacteristic
     * @param @return
     * @Description: SHORT_NOTIFICATION_CHARACTERISTIC (0xFFF1)
     */
//    private BluetoothGattCharacteristic getTest1Characteristic() {
//        if (null == mBluetoothGatt) {
//            return null;
//        }
//        BluetoothGattService bgs = mBluetoothGatt.getService(WMEGattAttributes.SERVICE2_ID);
//        if (null == bgs) {
//            return null;
//        } else {
//            return bgs
//                    .getCharacteristic(WMEGattAttributes.SERVICE2_CHARACTERISTIC_ID);
//        }
//    }
    
    /**
     * @author F3060326 at 2013/8/6
     * @return BluetoothGattCharacteristic
     * @param @return
     * @Description: SHORT_NOTIFICATION_CHARACTERISTIC (0xFFF1)
     */
//    private BluetoothGattCharacteristic getTest2Characteristic() {
//        if (null == mBluetoothGatt) {
//            return null;
//        }
//        BluetoothGattService bgs = mBluetoothGatt.getService(WMEGattAttributes.SERVICE4_ID);
//        if (null == bgs) {
//            return null;
//        } else {
//            return bgs
//                    .getCharacteristic(WMEGattAttributes.SERVICE4_CHARACTERISTIC_ID);
//        }
//    }
    
    /**
     * @author F3060326 at 2013/8/6
     * @return BluetoothGattCharacteristic
     * @param @return
     * @Description: SHORT_NOTIFICATION_CHARACTERISTIC (0xFFF1)
     */
    private BluetoothGattCharacteristic getWeSNFCharacteristic() {
        BluetoothGattService bgs = getWeService();
        if (null == bgs) {
            return null;
        } else {
            return bgs
                    .getCharacteristic(WMEGattAttributes.SNF_CHARACTERISTIC_ID);
        }
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return BluetoothGattCharacteristic
     * @param @return
     * @Description: LONG_NOTIFICATION_CHARACTERISTIC(0xFFF3)
     */
    private BluetoothGattCharacteristic getWeLNFCharacteristic() {
        BluetoothGattService bgs = getWeService();
        if (null == bgs) {
            return null;
        } else {
            return bgs
                    .getCharacteristic(WMEGattAttributes.LNF_CHARACTERISTIC_ID);
        }
    }

    // **************************************************************************************
    // -------------------------Start to get command from
    // DEV------------------------------**
    // **************************************************************************************

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Find me command from WME and play find me music
     */
    private void devFineMe(boolean start) {
        LogApp.Logd("devFineMe=" + start);
        if (start) {
            mVolumeLevel = getAudioManager().getStreamVolume(
                    AudioManager.STREAM_MUSIC);
            getAudioManager().setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    getAudioManager().getStreamMaxVolume(
                            AudioManager.STREAM_MUSIC),
                    AudioManager.FLAG_SHOW_UI);
            mMediaPlayer = MediaPlayer.create(this, R.raw.findme);
            if (null != mMediaPlayer) {
                mPlayFindMe = true;
                mMediaPlayer.start();
                mMediaPlayer
                        .setOnCompletionListener(new OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer arg0) {
                                // devFineMe(false);
                                getAudioManager()
                                        .setStreamVolume(
                                                AudioManager.STREAM_MUSIC,
                                                mVolumeLevel,
                                                AudioManager.FLAG_SHOW_UI);
                                mPlayFindMe = false;
                            }
                        });
                mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mPlayFindMe = false;
                        return false;
                    }
                });
            }
        } else {
            if (mPlayFindMe) {
                getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC,
                        mVolumeLevel, AudioManager.FLAG_SHOW_UI);
                if (null != mMediaPlayer) {
                    mMediaPlayer.pause();
                    mMediaPlayer.stop();
                    mPlayFindMe = false;
                }
            }
        }
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Check in Command from WME and AP will check in FACEBOOK
     */
    private void devCheckIn() {
        LogApp.Logd("devCheckIn");
        Intent service = new Intent(Constants.FACEBOOK_CHECKIN_ACTION);
        this.startService(service);
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param false is stop ECG
     * @Description: Start ECG
     */
    private void devECGStart(boolean start) {
        LogApp.Logd(TAG, "devECGStart=" + start);
        mECGMode = start;
        
        //fixed STOP issue in Galaxy devices
        if(!mCmdStopSend && !mECGMode){
            LogApp.Loge(TAG, "WP1 Unknow STOP!!!");
            send2Target(new Bundle(), MSG_IT_SHUTDOWN);
            //reSetWMEState(true);
        }
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: dev start to remote Camera control to capture command
     */
    private void devCameraControl() {
        LogApp.Logd("devCameraControl");
        RemoteControlCameraAP();

    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: //FIXME
     */
    private void devTrig() {
        LogApp.Logd("devTrig");
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: 
     */
    private void devConnParamChange() {
        LogApp.Logd("devConnParamChange");
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: 
     */
    private void devUserActionECGStart(byte[] value) {
        //final StringBuilder stringBuilder = new StringBuilder(value.length);
        //if (value != null && value.length > 0) {
        //    for (byte byteChar : value){
        //        stringBuilder.append(String.format("%s", byteChar));
        //    }
        //}
        //LogApp.Logd(TAG,
        //        "devUserActionECGStart-->" + stringBuilder.toString());

        LogApp.Logd("devUserActionECGStart");
        for (int i = 2; i < value.length; i++) {
            // YL 130913
            if (EkgRR.isMeasuring || EkgRR.isCoaching) {
                int iValue = (int) (0x000000FF & ((int) value[i]));
                // measure and coaching have the same behavior
                if (EkgRR.isECGStarted == false) {
                    //LogApp.Logd(TAG, "EkgRR.isECGStarted == false"
                     //       + "mMeasureTotalTime=" + mMeasureTotalTime);
                    int measureTime = mMeasureTotalTime; // sec
                    eRR = new EkgRR(measureTime);
                    EkgRR.isECGStarted = true;
                }
                eRR.an_inputECGData(iValue);
            }
        }
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: 
     */
    private void devStateNotify(byte[] value) {
        LogApp.Logd("devStateNotify");
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: get WME's version
     */
    private void devGetVersion(byte version[]) {
        LogApp.Logd("devGetVersion=" + version.length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            sb.append(String.format("%1$02x", version[i + 1]));
        }
        LogApp.Logd("string=" + sb.toString());
        Utility.setSharedPreferenceValue(this, Constants.KEY_VERSION,
                sb.toString());
        
        Bundle bd = new Bundle();
        bd.putString(BLE_DEVICE_VERSION, sb.toString());
        send2Target(bd, MSG_VERSION_CHANGE_RESULT); 
        
    }

    // **************************************************************************************
    // -------------------------Start to send command to
    // DEV-------------------------------**
    // **************************************************************************************

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Send command to Characteristic UUID(0000fff2)
     */
    private boolean sendCommand2Dev(byte[] value) {
        boolean isSendSuccess = false;
        BluetoothGattCharacteristic bgc = getWeWriteCharacteristic();
        if (null != bgc && null != mBluetoothGatt) {
            bgc.setValue(value);
            mBluetoothGatt.writeCharacteristic(bgc);
            isSendSuccess = true;
        }
        LogApp.Logv(TAG, "sendCommand2Dev==>" + isSendSuccess + "bgc=" + bgc
                + "mBluetoothGatt=" + mBluetoothGatt);
        return isSendSuccess;
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param true is start,3m = 180
     * @Description: ECGStart command
     */
    public void NotifyECGStart(boolean start, int totalTime) {
        // LogApp.Logd(TAG, "NotifyECG Start="+start);
        try{
            int gatt = getBluetoothAdapter().getProfileConnectionState(BluetoothProfile.GATT);
            int gatt_server = getBluetoothAdapter().getProfileConnectionState(BluetoothProfile.GATT_SERVER);
            int gatt2 =  getBluetoothManager().getConnectionState(mConnectedBD, BluetoothProfile.GATT);
            int gatt_server2 =  getBluetoothManager().getConnectionState(mConnectedBD, BluetoothProfile.GATT_SERVER);
            LogApp.Logd(TAG, "NotifyECG gatt="+gatt+"gatt_server="+gatt_server+"gatt2="+gatt2+"gatt_server2="+gatt_server2);
            BluetoothGattService mBCG = mBluetoothGatt.getService(WMEGattAttributes.SERVICE_ID);
            if(null != mBCG){
                LogApp.Logd(TAG, "NotifyECG mBCG="+mBCG);
            }
        }catch(Exception e){
            LogApp.Loge(TAG, "NotifyECGStart exception");
        }
        
        byte[] value = new byte[WMEGattAttributes.WRITE_LEN];
        if (start) {
            myHandler.removeMessages(ECG_STOP_MSG);
            myHandler.removeMessages(ECG_START_MSG);
            if (System.currentTimeMillis() - mPreStartTime < 3000) {
                mPreStartTime = System.currentTimeMillis() - 1000;
                myHandler.removeMessages(ECG_START_MSG);
                myHandler.sendMessageDelayed(
                        Message.obtain(myHandler, ECG_START_MSG, totalTime),
                        3000);
                LogApp.Logd(TAG, "NotifyECG Start= delay");
            } else {
                devFineMe(false);
                mCmdStopSend = false;
                mMeasureTotalTime = totalTime;
                LogApp.Logd(TAG, "NotifyECG Start= start");
                value[0] = WMEGattAttributes.APP_ECG_START;
                sendCommand2Dev(value);
            }
        } else {
            // mECGMode = false;
            if (1 == totalTime) {// Called by User Action
                myHandler.removeMessages(ECG_START_MSG);
                myHandler.removeMessages(ECG_STOP_MSG);
                myHandler.sendEmptyMessageDelayed(ECG_STOP_MSG, 2000);
            } else {// Called by progress
                myHandler.removeMessages(ECG_STOP_MSG);
            }
            if (mECGMode || 1 == totalTime) {
                mCmdStopSend = true;
                LogApp.Logd(TAG, "NotifyECG Start= stop");
                mPreStartTime = System.currentTimeMillis();
                value[0] = WMEGattAttributes.APP_ECG_STOP;
                sendCommand2Dev(value);
            }

        }

    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param true is start,3m = 180
     * @Description: ECGStart command
     */
    public void NotifyECGStart(boolean start) {
        if (!start) {
            NotifyECGStart(start, 1);
        }
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param
     * @Description: // enable / disable remote control mode
     */
    public void NotifySBGetVersion() {
        LogApp.Logd("NotifySBGetVersion");
        byte[] value = new byte[2];
        value[0] = WMEGattAttributes.APP_GET_VERSION;
        value[1] = 0x01;

        sendCommand2Dev(value);
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param
     * @Description: // enable / disable remote control mode
     */
    public void NotifySBCameraMode(boolean onoff) {
        
        LogApp.Logd("NotifySBCameraMode onoff+" + onoff);
        byte[] value = new byte[WMEGattAttributes.WRITE_LEN];
        if (onoff) {
            // start camera flash
            value[0] = WMEGattAttributes.APP_START_CAMERA;
            mIsCameraMode = true;
        } else {
            // stop camera flash
            value[0] = WMEGattAttributes.APP_STOP_CAMERA;
            mIsCameraMode = false;
        }
        sendCommand2Dev(value);
        
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @Description: Reset ecgStopTime and remoteStopTime, as updateTime is
     *               called when BLE connection is established
     */
    @SuppressWarnings("deprecation")
    public void NotifySBUpdateTime() {
        Date date = new Date();
        byte[] value = new byte[WMEGattAttributes.APP_TIME_UPDATE_LEN];
        value[0] = WMEGattAttributes.APP_TIME_UPDATE;
        value[1] = (byte) date.getSeconds();
        value[2] = (byte) date.getMinutes();
        value[3] = (byte) date.getHours();
        value[4] = (byte) date.getDay();
        value[5] = (byte) date.getMonth();
        value[6] = (byte) (date.getYear() - 100);
        // LogApp.Logd("NotifySBUpdateTime ="+date.getSeconds()
        // +"-"+date.getMinutes()+"-"+date.getHours()+"-"
        // +date.getDay()+"-"+date.getMonth()+"-"+date.getYear()+"!");
        sendCommand2Dev(value);
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param
     * @Description:
     */
    public void NotifySBUnreadFBNotification(int count) {
        LogApp.Logd(TAG, "NotifySBUnreadFBNotification count=" + count);
        byte[] value = new byte[WMEGattAttributes.APP_UNREAD_UPDATE_LEN];
        value[0] = WMEGattAttributes.APP_UNREAD_UPDATE;
        value[1] = WMEGattAttributes.APP_UNREAD_FB;
        value[2] = 0x00; // no use
        value[3] = 0x00; // missing call count
        value[4] = 0x00; // new SMS count
        value[5] = 0x00; // new Email count
        value[6] = (byte) count; // Unread FB count
        value[7] = 0x00; // calendar event count

        sendCommand2Dev(value);
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param
     * @Description:
     */
    public void NotifySBupdateUnreadMail(int count) {
        LogApp.Logd(TAG, "NotifySBupdateUnreadMail count=" + count);
        byte[] value = new byte[WMEGattAttributes.APP_UNREAD_UPDATE_LEN];
        value[0] = WMEGattAttributes.APP_UNREAD_UPDATE;
        value[1] = WMEGattAttributes.APP_UNREAD_MAIL;
        value[2] = 0x00; // no use
        value[3] = 0x00; // missing call count
        value[4] = 0x00; // new SMS count
        value[5] = (byte) count; // new Email count
        value[6] = 0x00; // Unread FB count
        value[7] = 0x00; // calendar event count

        sendCommand2Dev(value);
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Alert incoming call notification
     */
    public void alertNotificationWithCall() {
        alertNotification(WMEGattAttributes.PHYODE_ALERT_CALL);
    }

    /**
     * @author F3060326 at 2013/10/08
     * @return void
     * @param
     * @Description: alertClearNotificationWithCall
     */
    public void alertClearNotificationWithCall() {
        LogApp.Logd(TAG, "NotifySBupdateGobackIdle=");
        byte[] value = new byte[WMEGattAttributes.APP_EVENT_ALERT_LEN];
        value[0] = WMEGattAttributes.APP_EVENT_ALERT;
        value[1] = (byte) (0x00);
        value[2] = (byte) (0x00);
        sendCommand2Dev(value);
    }
    
    
    
    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Alert alarm notification
     */
    public void alertNotificationWithAlarm() {
        alertNotification(WMEGattAttributes.PHYODE_ALERT_ALARM);
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Alert email notification
     */
    public void alertNotificationWithEmail() {
        alertNotification(WMEGattAttributes.PHYODE_ALERT_EMAIL);
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Alert FACEBOOK notification
     */
    public void alertNotificationWithFacebook() {
        alertNotification(WMEGattAttributes.PHYODE_ALERT_FB);
    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Alert FACEBOOK check in state
     */
    public void alertNotificationWithFacebookCheckin(boolean success) {
        if (success) {
            alertNotification(WMEGattAttributes.PHYODE_ALERT_FB_CHECKIN_OK);
        } else {
            alertNotification(WMEGattAttributes.PHYODE_ALERT_FB_CHECKIN_FAIL);
        }

    }

    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param
     * @Description: Alert notification
     */
    public void alertNotification(int alertSelections) {
        LogApp.Logd(TAG, "alertNotification alertSelections=" + alertSelections);
        alertSelections &= (WMEGattAttributes.PHYODE_ALERT_CALL
                | WMEGattAttributes.PHYODE_ALERT_EMAIL
                | WMEGattAttributes.PHYODE_ALERT_FB
                | WMEGattAttributes.PHYODE_ALERT_ALARM
                | WMEGattAttributes.PHYODE_ALERT_FB_CHECKIN_OK | WMEGattAttributes.PHYODE_ALERT_FB_CHECKIN_FAIL);
        byte[] value = new byte[WMEGattAttributes.APP_EVENT_ALERT_LEN];
        value[0] = WMEGattAttributes.APP_EVENT_ALERT;
        value[1] = (byte) (alertSelections & 0xFF);
        value[2] = (byte) ((alertSelections & 0xFF00) >> 8);

        sendCommand2Dev(value);
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param
     * @Description: set friendly name
     */
    public void NotifySBupdateFriendlyName(CharSequence name) {
        LogApp.Logd(TAG, "NotifySBupdateFriendlyName=" + name);
        byte[] value = new byte[WMEGattAttributes.APP_SET_FRIENDLY_NAME_LEN];
        value[0] = WMEGattAttributes.APP_SET_FRIENDLY_NAME;
        for (int i = 0; i < name.length(); i++) {
            value[i + 1] = (byte) name.charAt(i);
        }
        sendCommand2Dev(value);

        myHandler.sendEmptyMessageDelayed(GETNAME_DELAY, 500);

    }

    /**
     * @author F3060326 at 2013/8/10
     * @return void
     * @param
     * @Description: get geteFriendlyName
     */
    public void geteFriendlyName() {
        readCharacteristic(getWeNameReadCharacteristic());
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param
     * @Description: goBackToIdle
     */
    public void NotifySBupdateGobackIdle() {
        LogApp.Logd(TAG, "NotifySBupdateGobackIdle=");
    }

    /**
     * @author F3060326 at 2013/7/31
     * @return void
     * @param
     * @Description: Send command to remote control the camera AP to capture
     */
    public void RemoteControlCameraAP() {
        Intent intent = new Intent(Constants.COMMAND_CAMERA);
        this.sendBroadcast(intent);
    }

    
    // **************************************************************************************
    // **------------------------API for User Action --------------------------------------**
    // **************************************************************************************
    
    /**
     * @author F3060326 at 2013/9/25
     * @return void
     * @param @param handler
     * @Description: set callback handler for activity
     */
    public void setCallbackHandler(Handler handler) {
        LogApp.Logd(TAG, "set handler");
        mCurrenCallbackHandler = handler;
    }
    
    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     * 
     * @param address
     *            The device address of the destination device.
     * 
     * @return Return true if the connection is initiated successfully. The
     *         connection result is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        LogApp.Logd(TAG, "connect-->" + address + "&mBluetoothDeviceAddress="
                + mBluetoothDeviceAddress);
        if (getBluetoothAdapter() == null || address == null) {
            LogApp.Logd(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        if (!getBluetoothAdapter().isEnabled()){
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (mBluetoothDeviceAddress != null
                && address.equalsIgnoreCase(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            LogApp.Logd(TAG,
                    "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
//                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                // Add by JimmyChen for fixed
                //
                // android.os.DeadObjectException
                // E/BluetoothGatt(13380): at
                // android.os.BinderProxy.transact(Native Method)
                // E/BluetoothGatt(13380): at
                // android.bluetooth.IBluetoothGatt$Stub$Proxy.clientConnect(IBluetoothGatt.java:739)
                // E/BluetoothGatt(13380): at
                // android.bluetooth.BluetoothGatt.connect(BluetoothGatt.java:715)
                mBluetoothGatt.close();
                // return false;
                LogApp.Logd(TAG, "Closed and will connected new link");
            }
        }

        final BluetoothDevice device = getBluetoothAdapter().getRemoteDevice(
                address);
        if (device == null) {
            LogApp.Logd(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        LogApp.Logd(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
//      mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        LogApp.Logd(TAG, "disconnect");
        if (getBluetoothAdapter() == null || mBluetoothGatt == null) {
            LogApp.Logd(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mUserFergot = true;
        mBluetoothGatt.disconnect();
        // Add by JimmyChen
        mBluetoothDeviceAddress = "";
        
        //Add close state avoid to scan nothing in VKY devices
        mBluetoothGatt.close();
        //Fixed  APPG2-5489 
        //Device pair column still showed "connected" after forget device then go to setting again. 
        dealwithDisConnected();
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     * 
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        LogApp.Logd(TAG, "initialize");
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (getBluetoothManager() == null) {
            LogApp.Loge(TAG, "Unable to initialize BluetoothManager.");
            return false;
        }

        if (getBluetoothAdapter() == null) {
            LogApp.Loge(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * @author F3060326 at 2013/8/7
     * @return boolean
     * @param @return
     * @Description: Auto connect the saved address
     */
    public boolean autoConnect() {
        myHandler.removeMessages(AUTO_CONNECT_MSG);
        LogApp.Logd(TAG, "autoConnect");
        //donnot invoke BLE API when BT off
        
        if(!getBluetoothAdapter().isEnabled()){
            return false;
        }
        
        if(isBTConnected()){
            //do nothing
            LogApp.Logd("autoConnect dismiss because bt is connected!");
            return true;
        }
        
        boolean autoConnect = false;
        String address = Utility.getSharedPreferenceValue(this,
                Utility.BLE_MAC, "");
        if (TextUtils.isEmpty(address)) {
            // do nothing
        } else {
            if (connect(address)) {
                // connected pass
                autoConnect = true;
                LogApp.Logd("Auto Connecting,waiting Success");
            } else {
                // connected fail
                LogApp.Logd("Auto Connected Fail");
            }
        }
        return autoConnect;
    }

    /**
     * @author F3060326 at 2013/8/20
     * @return boolean
     * @param @return
     * @Description: return measure and coaching check state
     */
    public boolean isBTConnected() {
        return getConnectedBLEdevices() == null ? false : true;
    }

    /**
     * @author F3060326 at 2013/8/8
     * @return void
     * @param
     * @Description: HomeActivity ondetoryed May be we can release service when
     *               BT close or device not paired
     */
    public void UserLeaveAction() {
        mBluetoothDeviceAddress = "";
        if (!getBluetoothAdapter().isEnabled()
                || null == getConnectedBLEdevices()) {
            stopAllRunning();
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public void close() {
        LogApp.Logd(TAG, "close");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * @author F3060326 at 2013/8/20
     * @return BluetoothDevice
     * @param @return
     * @Description: return connected BluetoothDevice
     */
    public BluetoothDevice getConnectedBLEdevices() {
        LogApp.Logd(TAG, "getConnectedBLEdevices mConnectedBD="+mConnectedBD);
        // if (mBluetoothGatt == null) return null;
        if (null == mBluetoothManager)
            return null;
        return mConnectedBD;
    }
    
    /**
     * @author F3060326 at 2013/9/29
     * @return void
     * @param 
     * @Description: reset ble state when timeout
     */
    public void reSetWMEState(final boolean isTimeOut){
        
        Thread myThread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                LogApp.Logd(TAG, "reSetWMEState disable");
                if(getBluetoothAdapter().isEnabled()){
                    getBluetoothAdapter().disable();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        
                    }
                    if(isTimeOut){
                        if (!getBluetoothAdapter().isEnabled()) {
                            LogApp.Logd(TAG, "reSetWMEState enabled and auto connect1");
                            getBluetoothAdapter().enable();
                            myHandler.sendEmptyMessageDelayed(AUTO_CONNECT_MSG, MSG_DELAY_TIME);
                        }
                    }
                }
            }
        });
        myThread.run();
    }
    
    /**
     * @author F3060326 at 2013/11/2
     * @return void
     * @param @param command
     * @Description: reset BT state and send close camera CMD
     */
    public void reSetCameraState(boolean command){
        mIsCameraMode = false;
        if(command){
            //fixed protect 1% issue in APPG2-5618
            //WME device still show Camera icon evan app is back to WME home page.
            myHandler.sendEmptyMessageDelayed(CAMERA_OFF_MSG, 3000);
            myHandler.sendEmptyMessageDelayed(CAMERA_OFF_MSG, 5000);
        }
    }
}
