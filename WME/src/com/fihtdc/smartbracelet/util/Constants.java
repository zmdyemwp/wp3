package com.fihtdc.smartbracelet.util;

import java.io.File;

import com.fihtdc.smartbracelet.provider.BraceletInfo;

import android.os.Environment;

public class Constants {
    public static final String FROM_WELCOM_EXTRA = "fromWelcome";
    
    public static final String TYPE_EXTRA = "type";
    public static final int TYPE_MEASURE = 1;
    public static final int TYPE_COACHING = 2;
    
    public static final String SUGGESTION_TYPE_EXTRA = "suggestion_type";
    public static final int SUGGESTION_TYPE_ANS_AGE = 1;
    public static final int SUGGESTION_TYPE_AGILITY = 2;
    public static final int SUGGESTION_TYPE_BPM = 3;
    
    public static final String USE_INFO_EXTRA = "userInfo";
    public static final String IS_GUEST_EXTRA = "isGuest";
    
    public static final String MEASURE_RESULT_EXTRA = "measureResult";
    public static final String COACHING_RESULT_EXTRA = "coachingResult";
    public static final int MEASURE_DURATION = 180;
    
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator;
    public static final String APP_SDCARD_PATH = SDCARD_PATH + "SmartBracelet" + File.separator;
    public static final String SCREENSHOT_NAME = "screenshot";
    public static final String KEY_ENTER_COACHING = "enter_coaching";

    
    public static final String KEY_FACEBOOK_FEATURES = "facebook features";
    public static final String KEY_NOTIFICATION = "facebook_notification";
    public static final String KEY_CHECKIN = "checkIn";
    public static final String KEY_CHECKIN_TEXT = "checkIn_text";
    
    public static final String ACCOUNT_IS_REOUTH = "isReOuth";

    //level parameter key,pass a pacelable object,
    public static final String KEY_LEVEL_PARAMETER = "level_parameter";
    // level class key,it has six classes:1,2,3,4,5,M
    public static final String KEY_LEVEL_CLASS = "level_class";
    public static final String KEY_CYCLE_TIME = "cycle_time";
    public static final String KEY_MEASURE_TIME = "measure_time";
    public static final String KEY_HIT_TIMES = "hit_times";
    
    public static final String KEY_VERSION = "wme_version";

    //check gmail internal action
    public static final String CHECK_GMAIL_ACTION="com.fihtdc.smartbracelet.CheckGmail";
    
    // Control service action
    public static final String COMMAND_ACTION="com.fihtdc.smartbracelet.COMMAND_ACTION";
    public static final String COMMAND_CAMERA="com.fihtdc.smartbracelet.COMMAND_CAMARA_CAPTURE";
    public static final String COMMAND_RESET ="com.fihtdc.smartbracelet.COMMAND_RESET";
    public static final String COMMAND_REBOOT_ACTION = "com.fihtdc.smartbracelet.COMMAND_REBOOT_ACTION";
    
    // Control facebook service action
    public static final String FACEBOOK_NOTIFICATION_ACTION="facebook.android.intent.action.NOTIFICATION";
    public static final String FACEBOOK_NOTIFICATION_EXTRA="facebook.android.intent.extra.NOTIFICATION";
    public static final String FACEBOOK_CHECKIN_ACTION="facebook.android.intent.action.CHECK_IN";
    public static final String FACEBOOK_NOTIFICATION_CLOSE_ACTION="facebook.android.intent.action.CLOSE_NOTIFICATION";
    
    public static final String FACEBOOK_NOTIFICATION_REQUEST="facebook_notification_request";
    public static final String FACEBOOK_SERVICE_STOP="facebook_service_stop";
    
    //Control extra data
    public static final String COMMAND_EXTRA = "COMMAND_EXTRA";
    
    //Email extra date
    public static final String COMMAND_EXTRA_EMAIL = "CMD_EXTRA_EMAIL";
    //Email extra date
    public static final String COMMAND_EXTRA_EMAIL_COUNT = "CMD_EXTRA_EMAIL_COUNT";
    //Alarm extra date
    public static final String COMMAND_EXTRA_ALARM = "CMD_EXTRA_ALARM";
    //FACEBOOK notification extra date
    public static final String COMMAND_EXTRA_FB_NF = "CMD_EXTRA_FB_NF";
    //FACEBOOK notification extra date
    public static final String COMMAND_EXTRA_FB_NF_COUNT = "CMD_EXTRA_FB_NF_COUNT";
    //FACEBOOK notification extra date
    public static final String COMMAND_EXTRA_FB_CHECKIN_OK = "CMD_EXTRA_FB_CO";
    //FACEBOOK notification extra date
    public static final String COMMAND_EXTRA_FB_CHECKIN_FAIL = "CMD_EXTRA_FB_CF";
    //InComing call extra date
    public static final String COMMAND_EXTRA_INCOMING_CALL = "CMD_EXTRA_INCOMING_CALL";
    //InComing call extra date
    public static final String COMMAND_EXTRA_STOP_INCOMING_CALL = "CMD_EXTRA_STOP_INCOMING_CALL";
    
    //Main start
    public static final String COMMAND_START_ACTION = "com.fihtdc.smartbracelet.COMMAND_START";
    
    public static final String COMMAND_CAMERA_ACTION = "com.fihtdc.smartbracelet.CAMERA_ACTION";
    
    public static final String COMMAND_CAMERA_STATE_ON = "com.fihtdc.WME_CAMERA_ON";
    public static final String COMMAND_CAMERA_STATE_OFF = "com.fihtdc.WME_CAMERA_OFF";
    
    public static final String COMMAND_LOST_CAMERA = "com.fihtdc.smartbracelet.COMMAND_CAMARA_LOST";
    
    //start pair intent
    public static final String EXTRA_PAIR_SETTINGS = "PAIR_FROM_SETTINGS";
    
    public static final String[] DAYCOACHING_PROJ = new String[]{
        BraceletInfo.DayCoaching._ID,
        BraceletInfo.DayCoaching.COLUMN_NAME_DAYCOACHING_DATE,
        BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1,
        BraceletInfo.DayCoaching.COLUMN_NAME_HIT1,
        BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2,
        BraceletInfo.DayCoaching.COLUMN_NAME_HIT2,
        BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3,
        BraceletInfo.DayCoaching.COLUMN_NAME_HIT3,
        BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4,
        BraceletInfo.DayCoaching.COLUMN_NAME_HIT4,
        BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5,
        BraceletInfo.DayCoaching.COLUMN_NAME_HIT5,
        BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM,
        BraceletInfo.DayCoaching.COLUMN_NAME_HITM,
        BraceletInfo.DayCoaching.COLUMN_NAME_TEST_TIME
    };
    
    public static final int ZERO_HIGHLIGHT_STAR = 0;
    public static final int ONE_HIGHLIGHT_STAR = 1;
    public static final int TWO_HIGHLIGHT_STAR = 2;
    public static final int THREE_HIGHLIGHT_STAR = 3;
    
    //This integer value is the number of unread conversations in this label of gmail.
    public static final String NUM_UNREAD_CONVERSATIONS = "numUnreadConversations";
    
    public static final String ACCOUNT_TYPE_GOOGLE = "com.google";
    public static final String[] FEATURES_MAIL = {"service_mail"};
    
    public static final int ECG_TIME_CALLBACK = 100; //ms
    public static final int BPM_UPDATE_TIME = 6 * 1000; //ms
}
