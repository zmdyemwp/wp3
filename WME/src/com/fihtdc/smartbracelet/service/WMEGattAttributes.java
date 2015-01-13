/*
 * Copyright (C) 2013 The Android Open Source Project
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

import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class WMEGattAttributes {
//    public static HashMap<String, String> attributes = new HashMap();
    
    //Service UUID
    public static UUID SERVICE_ID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static UUID SERVICE_DEVICESNAME_ID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");//read name  
    public static UUID SERVICE2_ID = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");//useless 
    public static UUID SERVICE3_ID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");//useless 
    public static UUID SERVICE4_ID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");//useless 
    
    //Characteristic UUID
    public static UUID SNF_CHARACTERISTIC_ID   = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static UUID WRITE_CHARACTERISTIC_ID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    public static UUID LNF_CHARACTERISTIC_ID   = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");
    
    public static UUID NAME_CHARACTERISTIC_ID   = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    
    
    //Descriptor UUID
    public static UUID Descriptor_ID1 = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    public static UUID Descriptor_ID2 = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    
    //
    public static UUID SERVICE4_CHARACTERISTIC_ID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public static UUID SERVICE2_CHARACTERISTIC_ID = UUID.fromString("0002a05-0000-1000-8000-00805f9b34fb");
    
    /*-------WMEWME control command begin----------*/
    // alert selection bit definition
    public  static final int PHYODE_ALERT_NONE = 0x0000;      // all alerts are stopped
    public  static final int PHYODE_ALERT_CALL  = 0x0001 ;     // incoming call
    public  static final int PHYODE_ALERT_EMAIL  = 0x0004  ;    // incremented unread email count
    public  static final int PHYODE_ALERT_FB     = 0x0008  ;    // incremented unread FB friend request/message/notification
    public static final int PHYODE_ALERT_ALARM  = 0x0040   ;   // alarm
    public static final int PHYODE_ALERT_FB_CHECKIN_OK = 0x0080   ;   // FB check in OK
    public static final int PHYODE_ALERT_FB_CHECKIN_FAIL = 0x0100   ;   // FB check in failed
    
    public static final int  WRITE_LEN        =  1;

    public static final int  LONG_NOTIFICATION_LEN  =   18;
    public static final int  SHORT_NOTIFICATION_LEN   =      2;

    //update time command
    public static final int  APP_TIME_UPDATE     =   0x03;
    public static final int  APP_TIME_UPDATE_LEN   =7;

    //set and get setting command
    public static final int  APP_SET_STAT =  0x02;
    public static final int  APP_SET_STAT_LEN  =4;

    public static final int  APP_GET_STAT     =  0x01;
    public static final int  APP_GET_STAT_LEN   = 1;
    //status notification
    public static final int  DEV_STAT_NOTIF      =  0x01;

    //waking up App command
    public static final int  DEV_TRIG  =  0xFE;

    //unread command
    public static final int  APP_UNREAD_UPDATE             = 0x10;
    public static final int  APP_UNREAD_UPDATE_LEN=     8;
    public static final int  APP_UNREAD_FB              =    0x08;
    public static final int  APP_UNREAD_MAIL   =  0x04;

    //alert command
    public static final int  APP_EVENT_ALERT    =  0x11;
    public static final int  APP_EVENT_ALERT_LEN    =  3;

    //user action to host
    public static final int  DEV_USER_ACTION        =         0x03;
    public static final int  DEV_USER_ACTION_FB_CHECK_IN    = 0x01;
    public static final int  DEV_USER_ACTION_EMERGENCY_CHECK_IN=   0x02;
    public static final int  DEV_USER_ACTION_START_FIND_ME=  0x03;
    public static final int  DEV_USER_ACTION_STOP_FIND_ME   =    0x04;
    public static final int  DEV_USER_ACTION_ECG_START=     0x20;
    public static final int  DEV_USER_ACTION_ECG_STOP   =   0x21;
    public static final int  DEV_REMOTE_CONTROL    = 0x40;

    //ECG command
    public static final int  APP_ECG_START      = 0x20;
    public static final int  APP_ECG_STOP     =0x21;

    public static final int  APP_ECG_RESULT        = 0x24;
    public static final int  APP_ECG_RESULT_LEN    =10;


    //change parameter
    public static final int  APP_CHANGE_CONN_PARAM     =0xF0;
    public static final int  APP_CHANGE_CONN_PARAM_LEN   =5;

    //user define icon
    public static final int  APP_SET_USER_ICON     = 0x30;
    public static final int  APP_SET_USER_ICON_LEN=  14;

    //change trigger
    public static final int  APP_CHANGE_TRIG_PARAM          =   0xF1;
    public static final int  APP_CHANGE_TRIG_PARAM_LEN     = 5;

    public static final int  DEV_CONN_PARAM_CHG         =0xF0;


    //Camera Capture Command
    public static final int  APP_START_CAMERA =    0x40;
    public static final int  APP_STOP_CAMERA       =  0x41;

    // set friendly name command
    public static final int  APP_SET_FRIENDLY_NAME  =           0x31;
    public static final int  APP_SET_FRIENDLY_NAME_LEN =         14;

    // go back to idle
    public static final int  APP_GO_BACK_TO_IDLE  =   0xF3;

    // read CC2540 version
    public static final int  APP_GET_VERSION     = 0x05;
    
    /*----------WMEWME control command end---------------*/
    
//    static {
//        // Sample Services.
//        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
//        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
//        // Sample Characteristics.
//        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
//        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
//    }
//
//    public static String lookup(String uuid, String defaultName) {
//        String name = attributes.get(uuid);
//        return name == null ? defaultName : name;
//    }
}
