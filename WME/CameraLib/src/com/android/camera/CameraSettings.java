/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.camera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

//import com.fih.FihConfig;


/**
 *  Provides utilities and keys for Camera settings.
 */
public class CameraSettings {
    private static final int NOT_FOUND = -1;

    public static final String KEY_VERSION = "pref_version_key";
    public static final String KEY_LOCAL_VERSION = "pref_local_version_key";
    public static final String KEY_RECORD_LOCATION = RecordLocationPreference.KEY;
    public static final String KEY_VIDEO_QUALITY = "pref_video_quality_key";
    public static final String KEY_VIDEO_TIME_LAPSE_FRAME_INTERVAL = "pref_video_time_lapse_frame_interval_key";
    public static final String KEY_PICTURE_SIZE = "pref_camera_picturesize_key";
    public static final String KEY_JPEG_QUALITY = "pref_camera_jpegquality_key";
    public static final String KEY_FOCUS_MODE = "pref_camera_focusmode_key";
    public static final String KEY_FLASH_MODE = "pref_camera_flashmode_key";
    public static final String KEY_VIDEOCAMERA_FLASH_MODE = "pref_camera_video_flashmode_key";
    public static final String KEY_WHITE_BALANCE = "pref_camera_whitebalance_key";
    public static final String KEY_SCENE_MODE = "pref_camera_scenemode_key";
    public static final String KEY_EXPOSURE = "pref_camera_exposure_key";
    public static final String KEY_VIDEO_EFFECT = "pref_video_effect_key";
    public static final String KEY_CAMERA_ID = "pref_camera_id_key";
    public static final String KEY_CAMERA_FIRST_USE_HINT_SHOWN = "pref_camera_first_use_hint_shown_key";
//FihtdcCode@20111121 Peiming Wang add ICS begin*****
    public static final String KEY_COLOR_EFFECT = "pref_camera_coloreffect_key";
    public static final String KEY_AUTOEXPOSURE = "pref_camera_autoexposure_key";
    public static final String KEY_ANTIBANDING = "pref_camera_antibanding_key";
    public static final String KEY_SHARPNESS = "pref_camera_sharpness_key";
    public static final String KEY_CONTRAST = "pref_camera_contrast_key";
    public static final String KEY_SATURATION = "pref_camera_saturation_key";
    public static final String KEY_BRIGHTNESS = "pref_camera_brightness_key";
    public static final String KEY_GRID = "pref_camera_grid_key";
    public static final String KEY_SHUTTER_SOUND = "pref_camera_shutter_sound_key";
    public static final String KEY_VIDEO_SIZE = "pref_camera_videosize_key";
    public static final String KEY_VIDEO_ENCODER = "pref_camera_videoencoder_key";
    public static final String KEY_AUDIO_ENCODER = "pref_camera_audioencoder_key";
    public static final String KEY_VIDEO_DURATION = "pref_camera_video_duration_key";
//FihtdcCode@20111121 Peiming Wang add ICS end*****
    public static final String KEY_VIDEO_FIRST_USE_HINT_SHOWN = "pref_video_first_use_hint_shown_key";

    public static final String EXPOSURE_DEFAULT_VALUE = "0";
    //2012/5/31 Jinsheng use CameraSettings.ANTI_BANDING_DEFAULT_VALUE instead of R.string.pref_camera_antibanding_default
    //because event we use translatable="false" for the string,it also be translate
    public static final String ANTI_BANDING_DEFAULT_VALUE = "auto";

    public static final int CURRENT_VERSION = 5;
    public static final int CURRENT_LOCAL_VERSION = 2;
//FihtdcCode@20111121 Peiming Wang add ICS begin*****
    private static final int MMS_VIDEO_DURATION = 30;//2012/4/24 Jinsheng modify for IPD.B-1943
    //public static final int DEFAULT_VIDEO_DURATION = 30 * 60; // 30 sec
    public static final int DEFAULT_VIDEO_DURATION = 0; // no limit
    private static final int YOUTUBE_VIDEO_DURATION = 10 * 60; // 10 mins
    private static final String VIDEO_QUALITY_HIGH = "high";
    private static final String VIDEO_QUALITY_MMS = "mms";
    private static final String VIDEO_QUALITY_YOUTUBE = "youtube";
    public static final String VIDEOSIZEKEY = "preview-size-values";//ltt
    //2012/4/20 Jinsheng add for IPD.B-1780 support volume key zoom
    public static final int ZOOM_KEY_STEP = 6;//max zoom step is 60,zoom 10 step once use key
//FihtdcCode@20111121 Peiming Wang add ICS end*****
    private static final String TAG = "CameraSettings";

    private final Context mContext;
    private Parameters mParameters;//2012/3/24 Jinsheng modify for IRMI.B-1781
    private final CameraInfo[] mCameraInfo;
    private final int mCameraId;

    public CameraSettings(Activity activity, Parameters parameters,
                          int cameraId, CameraInfo[] cameraInfo) {
        mContext = activity;
        mParameters = parameters;
        mCameraId = cameraId;
        mCameraInfo = cameraInfo;
    }
    
	//2012/3/24 Jinsheng add for IRMI.B-1781 begin
    public void updateParameters(Parameters parameters)
    {
        mParameters = parameters;
    }
	//2012/3/24 Jinsheng add for IRMI.B-1781 end

    public PreferenceGroup getPreferenceGroup(int preferenceRes) {
        PreferenceInflater inflater = new PreferenceInflater(mContext);
        PreferenceGroup group =
                (PreferenceGroup) inflater.inflate(preferenceRes);
        initPreference(group);
        return group;
    }

    public static String getDefaultVideoQuality(int cameraId,
            String defaultQuality) {
        int quality = Integer.valueOf(defaultQuality);
        if (CamcorderProfile.hasProfile(cameraId, quality)) {
            return defaultQuality;
        }
        return Integer.toString(CamcorderProfile.QUALITY_HIGH);
    }

    public static void initialCameraPictureSize(
            Context context, Parameters parameters) {
        // When launching the camera app first time, we will set the picture
        // size to the first one in the list defined in "arrays.xml" and is also
        // supported by the driver.
        List<Size> supported = parameters.getSupportedPictureSizes();
        if (supported == null) return;
        for (String candidate : context.getResources().getStringArray(
                R.array.pref_camera_picturesize_entryvalues)) {
            if (setCameraPictureSize(candidate, supported, parameters)) {
                SharedPreferences.Editor editor = ComboPreferences
                        .get(context).edit();
                editor.putString(KEY_PICTURE_SIZE, candidate);
                editor.apply();
                return;
            }
        }
        Log.e(TAG, "No supported picture size found");
    }

    public static void removePreferenceFromScreen(
            PreferenceGroup group, String key) {
        removePreference(group, key);
    }

    public static boolean setCameraPictureSize(
            String candidate, List<Size> supported, Parameters parameters) {
        int index = candidate.indexOf('x');
        if (index == NOT_FOUND) return false;
        int width = Integer.parseInt(candidate.substring(0, index));
        int height = Integer.parseInt(candidate.substring(index + 1));
        for (Size size : supported) {
            if (size.width == width && size.height == height) {
                Log.i(TAG,"Set picture size:width="+width+"  height="+height);
                parameters.setPictureSize(width, height);
                return true;
            }
        }
        return false;
    }

    private void initPreference(PreferenceGroup group) {
        ListPreference videoQuality = group.findPreference(KEY_VIDEO_QUALITY);
        ListPreference timeLapseInterval = group.findPreference(KEY_VIDEO_TIME_LAPSE_FRAME_INTERVAL);
        ListPreference pictureSize = group.findPreference(KEY_PICTURE_SIZE);
        ListPreference whiteBalance =  group.findPreference(KEY_WHITE_BALANCE);
        ListPreference sceneMode = group.findPreference(KEY_SCENE_MODE);
        ListPreference colorEffect = group.findPreference(KEY_COLOR_EFFECT);//2012/2/4 Jinsheng add color effect for IRM ICS 
        ListPreference flashMode = group.findPreference(KEY_FLASH_MODE);
        ListPreference focusMode = group.findPreference(KEY_FOCUS_MODE);
        ListPreference exposure = group.findPreference(KEY_EXPOSURE);
        IconListPreference cameraIdPref =
                (IconListPreference) group.findPreference(KEY_CAMERA_ID);
        ListPreference videoFlashMode =
                group.findPreference(KEY_VIDEOCAMERA_FLASH_MODE);
        ListPreference videoEffect = group.findPreference(KEY_VIDEO_EFFECT);
        ListPreference videoEncoder = group.findPreference(KEY_VIDEO_ENCODER);//ting-ting.lu 
        ListPreference videoSize = group.findPreference(KEY_VIDEO_SIZE);//ting-ting.lu 
		//2012/4/20 Jinsheng add for IPD.B-1790 begin
		ListPreference antiBanding = group.findPreference(KEY_ANTIBANDING);
		
		if (antiBanding != null) {
            filterUnsupportedOptions(group,antiBanding, mParameters.getSupportedAntibanding());
        }
		//2012/4/20 Jinsheng add for IPD.B-1790 end

        // Since the screen could be loaded from different resources, we need
        // to check if the preference is available here
        if (videoQuality != null) {
            Log.i(TAG, "getSupportedVideoQuality().length="+getSupportedVideoQuality().size());
            //for(int i=0;i<getSupportedVideoQuality().size();i++)
                //Log.i("ltt", "getSupportedVideoQuality["+i+"]="+getSupportedVideoQuality().get(i).toString());
            filterUnsupportedOptions(group, videoQuality, getSupportedVideoQuality());
        }
        
        if (videoSize != null) {
            createSettings(
                    videoSize, VIDEOSIZEKEY, R.array.pref_camera_videosize_entries,
                    R.array.pref_camera_videosize_entryvalues,group);//
        }else Log.d(TAG, "!!!VideoSize=null");
		//add by ting-ting.lu end

        if (pictureSize != null) {
            filterUnsupportedOptions(group, pictureSize, sizeListToStringList(
                    mParameters.getSupportedPictureSizes()));
        }
        
        Log.i(TAG,"support white balance:"+mParameters.getSupportedWhiteBalance());
        if (whiteBalance != null) {
            filterUnsupportedOptions(group,
                    whiteBalance, mParameters.getSupportedWhiteBalance());
        }
        
        if (sceneMode != null) {
            filterUnsupportedOptions(group,
                    sceneMode, mParameters.getSupportedSceneModes());
        }
        //2012/2/4 Jinsheng add color effect for IRM ICS begin
        if (colorEffect != null) {
            filterUnsupportedOptions(group,
                    colorEffect, mParameters.getSupportedColorEffects());
        }
        //2012/2/4 Jinsheng add color effect for IRM ICS end
        if (flashMode != null) {
            filterUnsupportedOptions(group,
                    flashMode, mParameters.getSupportedFlashModes());
        }
        if (focusMode != null) {
            if (mParameters.getMaxNumFocusAreas() == 0) {
                filterUnsupportedOptions(group,
                        focusMode, mParameters.getSupportedFocusModes());
            } else {
                // Remove the focus mode if we can use tap-to-focus.
                removePreference(group, focusMode.getKey());
            }
        }
        if (videoFlashMode != null) {
            filterUnsupportedOptions(group,
                    videoFlashMode, mParameters.getSupportedFlashModes());
        }
        if (exposure != null) buildExposureCompensation(group, exposure);
        if (cameraIdPref != null) buildCameraId(group, cameraIdPref);

        if (timeLapseInterval != null) resetIfInvalid(timeLapseInterval);
        if (videoEffect != null) {
            initVideoEffect(group, videoEffect);
            resetIfInvalid(videoEffect);
        }
    }
    
    //Add by Lu tingting begin
    private void createSettings(
            ListPreference pref, String paramName, int prefEntriesResId,
            int prefEntryValuesResId,PreferenceGroup group) {
        // Disable the preference if the parameter is not supported.
        String supportedParamStr = mParameters.get(paramName);

        // Get the supported parameter settings.
        StringTokenizer tokenizer = new StringTokenizer(supportedParamStr, ",");
        List<String> supportedParam = new ArrayList<String>();
        while (tokenizer.hasMoreElements()) {
            //Log.i("ltt", "tokenizer.nextToken()="+tokenizer.nextToken());
            supportedParam.add(tokenizer.nextToken());
        }
        if (supportedParam == null || supportedParam.size() <= 1) {
            removePreference(group, pref.getKey());
            return;
        }
		
        pref.filterUnsupported(supportedParam);
        //Log.i("ltt", "pref.size="+pref.getEntry().length());
        if (pref.getEntries().length <= 1) {
            removePreference(group, pref.getKey());
            return;
        }
        resetIfInvalid(pref);
    }
    //Add by Lu tingting end
    
    private void buildExposureCompensation(
            PreferenceGroup group, ListPreference exposure) {
        int max = mParameters.getMaxExposureCompensation();
        int min = mParameters.getMinExposureCompensation();
        if (max == 0 && min == 0) {
            if(mCameraId == 1){//2012/4/11 Jinsheng add for front camera don't support exposure and disable it
                //Let continue
            }else{
                removePreference(group, exposure.getKey());
                return;
            }
        }
        float step = mParameters.getExposureCompensationStep();

        // show only integer values for exposure compensation
        int maxValue = (int) Math.floor(max * step);
        int minValue = (int) Math.ceil(min * step);
        CharSequence entries[] = new CharSequence[maxValue - minValue + 1];
        CharSequence entryValues[] = new CharSequence[maxValue - minValue + 1];
        for (int i = minValue; i <= maxValue; ++i) {
            entryValues[maxValue - i] = Integer.toString(Math.round(i / step));
            StringBuilder builder = new StringBuilder();
            if (i > 0) builder.append('+');
            entries[maxValue - i] = builder.append(i).toString();
        }
        exposure.setEntries(entries);
        exposure.setEntryValues(entryValues);
    }

    private void buildCameraId(
            PreferenceGroup group, IconListPreference preference) {
        int numOfCameras = mCameraInfo.length;
        if (numOfCameras < 2) {
            removePreference(group, preference.getKey());
            return;
        }

        CharSequence[] entryValues = new CharSequence[2];

        //FihtdcCode@20120110 Peiming GOXI.B-811   begin
//        Boolean onlyFrontCamera = FihConfig.getBooleanCust("CAMERA_ONLY_FRONT_CAM");
//        if(onlyFrontCamera)
//        {
//            entryValues[0]="1";
//            entryValues[1]="1";
//        }  
//        else
//        {                  
            for (int i = 0; i < mCameraInfo.length; ++i) {
                int index =
                        (mCameraInfo[i].facing == CameraInfo.CAMERA_FACING_FRONT)
                        ? CameraInfo.CAMERA_FACING_FRONT
                        : CameraInfo.CAMERA_FACING_BACK;
                if (entryValues[index] == null) {
                 Log.e(TAG, "entryValues[index] == null"+entryValues[1]);//peiming test          
                    entryValues[index] = "" + i;
                    if (entryValues[((index == 1) ? 0 : 1)] != null) break;
                }
//            }            
        }
        preference.setEntryValues(entryValues);
    }

    private static boolean removePreference(PreferenceGroup group, String key) {
        for (int i = 0, n = group.size(); i < n; i++) {
            CameraPreference child = group.get(i);
            if (child instanceof PreferenceGroup) {
                if (removePreference((PreferenceGroup) child, key)) {
                    return true;
                }
            }
            if (child instanceof ListPreference &&
                    ((ListPreference) child).getKey().equals(key)) {
                group.removePreference(i);
                return true;
            }
        }
        return false;
    }

    private void filterUnsupportedOptions(PreferenceGroup group,
            ListPreference pref, List<String> supported) {

        // Remove the preference if the parameter is not supported or there is
        // only one options for the settings.
        //if (supported == null || supported.size() <= 1) {
        //if (supported == null || supported.size() < 1) {//2012/3/28 Jinsheng modify this for IRMI.B-1993
          if (supported == null || supported.size() < 1 || (supported.size() == 1 && (!inWhiteList(pref)))) {//2012/4/10 Jinsheng modify this for IPD.B-1044   
            removePreference(group, pref.getKey());
            return;
        }

        pref.filterUnsupported(supported);
        //if (pref.getEntries().length <= 1) {
        //if (pref.getEntries().length < 1 ) {//2012/3/28 Jinsheng modify this for IRMI.B-1993
        if (pref.getEntries().length < 1 || (supported.size() == 1 && (!inWhiteList(pref)))) {//2012/4/10 Jinsheng modify this for IPD.B-1044
            removePreference(group, pref.getKey());
            return;
        }

        resetIfInvalid(pref);
    }
    
    //2012/4/10 Jinsheng add this for IPD.B-1044 begin
    //True:Even only one value is support,show it
    //Flase:Follow the original logic,if only one value is support,remove this setting
    private boolean inWhiteList(ListPreference pref){
        String key = pref.getKey();
        if(key.equals(KEY_WHITE_BALANCE) || key.equals(KEY_SCENE_MODE) || key.equals(KEY_EXPOSURE))//2012/4/11 Jinsheng modify for front camera don't support exposure and disable it
            return true;
        
        return false;    
    }
    //2012/4/10 Jinsheng add this for IPD.B-1044 end
    
    private void resetIfInvalid(ListPreference pref) {
        // Set the value to the first entry if it is invalid.
        String value = pref.getValue();
        if (pref.findIndexOfValue(value) == NOT_FOUND) {
            pref.setValueIndex(0);
        }
    }

    private static List<String> sizeListToStringList(List<Size> sizes) {
        ArrayList<String> list = new ArrayList<String>();
        for (Size size : sizes) {
            //2012/6/9 Jinsheng modify for TBPI.B-852 begin
            //size will be format to Arabic at Arabic language 
            //list.add(String.format("%dx%d", size.width, size.height));
            String sizeString = size.width+"x"+size.height;
            Log.i(TAG,"sizeString:"+sizeString);
            list.add(sizeString);
            //2012/6/9 Jinsheng modify for TBPI.B-852 end
        }
        return list;
    }

    public static void upgradeLocalPreferences(SharedPreferences pref) {
        int version;
        try {
            version = pref.getInt(KEY_LOCAL_VERSION, 0);
        } catch (Exception ex) {
            version = 0;
        }
        if (version == CURRENT_LOCAL_VERSION) return;

        SharedPreferences.Editor editor = pref.edit();
        if (version == 1) {
            // We use numbers to represent the quality now. The quality definition is identical to
            // that of CamcorderProfile.java.
            editor.remove("pref_video_quality_key");
        }
        editor.putInt(KEY_LOCAL_VERSION, CURRENT_LOCAL_VERSION);
        editor.apply();
    }

    public static void upgradeGlobalPreferences(SharedPreferences pref) {
        int version;
        try {
            version = pref.getInt(KEY_VERSION, 0);
        } catch (Exception ex) {
            version = 0;
        }
        if (version == CURRENT_VERSION) return;

        SharedPreferences.Editor editor = pref.edit();
        if (version == 0) {
            // We won't use the preference which change in version 1.
            // So, just upgrade to version 1 directly
            version = 1;
        }
        if (version == 1) {
            // Change jpeg quality {65,75,85} to {normal,fine,superfine}
         //FihtdcCode@20111121 Peiming Wang add ICS begin*****	
           /*
	    String quality = pref.getString(KEY_JPEG_QUALITY, "85");
            if (quality.equals("65")) {
                quality = "normal";
            } else if (quality.equals("75")) {
                quality = "fine";
            } else {
                quality = "superfine";
            }
            editor.putString(KEY_JPEG_QUALITY, quality);
	*/	
        //Add by KenLin @20101021 begin, set default value from CDA
        //If CDA is null, set default value from default string label
		Log.v(TAG, "upgradePreferences set default value-->");		
		String qualityValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Picture_Quality);
		String effectValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Effect);
		//String meteringValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Metering_Mode);				
		//String antiValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Anti_Banding);
		//String saturationValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Saturation);		
		//String contrastValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Contrast);		
		//String sharpnessValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Sharpness);		
		//String brightnessValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Brightness);
		//String gridValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Grid);		
		String shutterSoundValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Shutter_Sound);		
		String storeLocationValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Store_Location);		
		String whiteBalanceValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_White_Balance);		
		//String flashValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Camera_Flash_Mode);
		//String videoSizValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Video_Size);		
		String videoEncoderValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Video_Encoder);		
		String videoDurationValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Video_Duration);		
		//String videoQualityValue = DefaultValue.getDefaultValueObject().getDefaultValue(DefaultValue.mIndex_Video_Quality);		
					
		
		Log.v(TAG, "upgradePreferences qualityValue: " + qualityValue);
		Log.v(TAG, "upgradePreferences effectValue: " + effectValue);
		//Log.v(TAG, "upgradePreferences meteringValue: " + meteringValue);
		//Log.v(TAG, "upgradePreferences antiValue: " + antiValue);
		//Log.v(TAG, "upgradePreferences saturationValue: " + saturationValue);
		//Log.v(TAG, "upgradePreferences contrastValue: " + contrastValue);
		//Log.v(TAG, "upgradePreferences sharpnessValue: " + sharpnessValue);
		//Log.v(TAG, "upgradePreferences brightnessValue: " + brightnessValue);
		//Log.v(TAG, "upgradePreferences gridValue: " + gridValue);
		Log.v(TAG, "upgradePreferences shutterSoundValue: " + shutterSoundValue);
		Log.v(TAG, "upgradePreferences storeLocationValue: " + storeLocationValue);
		Log.v(TAG, "upgradePreferences whiteBalanceValue: " + whiteBalanceValue);
		//Log.v(TAG, "upgradePreferences flashValue: " + flashValue);
		//Log.v(TAG, "upgradePreferences videoSizValue: " + videoSizValue);
		Log.v(TAG, "upgradePreferences videoEncoderValue: " + videoEncoderValue);
		Log.v(TAG, "upgradePreferences videoDurationValue: " + videoDurationValue);
		//Log.v(TAG, "upgradePreferences videoQualityValue: " + videoQualityValue);
				
		
		editor.putString(KEY_JPEG_QUALITY, qualityValue);
		editor.putString(KEY_COLOR_EFFECT, effectValue);
		//editor.putString(KEY_AUTOEXPOSURE, meteringValue);
		//editor.putString(KEY_ANTIBANDING, antiValue);
		//editor.putString(KEY_SATURATION, saturationValue);
		//editor.putString(KEY_CONTRAST, contrastValue);
		//editor.putString(KEY_SHARPNESS, sharpnessValue);
		//editor.putString(KEY_BRIGHTNESS, brightnessValue);
		//editor.putString(KEY_GRID, gridValue);
		editor.putString(KEY_SHUTTER_SOUND, shutterSoundValue);
		editor.putString(KEY_RECORD_LOCATION, storeLocationValue);
		editor.putString(KEY_WHITE_BALANCE, whiteBalanceValue);
		//editor.putString(KEY_FLASH_MODE, flashValue);
		//editor.putString(KEY_VIDEO_SIZE, videoSizValue);
		editor.putString(KEY_VIDEO_ENCODER, videoEncoderValue);
		editor.putString(KEY_VIDEO_DURATION, videoDurationValue);
		//editor.putString(KEY_VIDEO_QUALITY, videoQualityValue);	

		Log.v(TAG, "<--upgradePreferences set default value");
	//FihtdcCode@20111121 Peiming Wang add ICS end*****				
            version = 2;
        }
        if (version == 2) {
            editor.putString(KEY_RECORD_LOCATION,
                    pref.getBoolean(KEY_RECORD_LOCATION, false)
                    ? RecordLocationPreference.VALUE_ON
                    : RecordLocationPreference.VALUE_NONE);
            version = 3;
        }
        if (version == 3) {
            // Just use video quality to replace it and
            // ignore the current settings.
            editor.remove("pref_camera_videoquality_key");
            editor.remove("pref_camera_video_duration_key");
        }

        editor.putInt(KEY_VERSION, CURRENT_VERSION);
        editor.apply();
    }

    public static int readPreferredCameraId(SharedPreferences pref) {
        //FihtdcCode@20120110 Peiming GOXI.B-811   begin
        //Jimmy marked
//        Boolean onlyFrontCamera = FihConfig.getBooleanCust("CAMERA_ONLY_FRONT_CAM");
//        Log.i(TAG,"readPreferredCameraId");
//        if(onlyFrontCamera)
//        {
//            Log.v(TAG, "readPreferredCameraId ===  onlyFrontCamera");    
//            return Integer.parseInt(pref.getString(KEY_CAMERA_ID, "1")); 
//        }     
//        else
            return Integer.parseInt(pref.getString(KEY_CAMERA_ID, "0"));
            
        //FihtdcCode@20120110 Peiming GOXI.B-811   end     
    }

    public static void writePreferredCameraId(SharedPreferences pref,
            int cameraId) {
        Log.i(TAG,"writePreferredCameraId cameraId:"+cameraId);
        Editor editor = pref.edit();
        editor.putString(KEY_CAMERA_ID, Integer.toString(cameraId));
        editor.apply();
    }

    public static int readExposure(ComboPreferences preferences) {
        String exposure = preferences.getString(
                CameraSettings.KEY_EXPOSURE,
                EXPOSURE_DEFAULT_VALUE);
        try {
            return Integer.parseInt(exposure);
        } catch (Exception ex) {
            Log.e(TAG, "Invalid exposure: " + exposure);
        }
        return 0;
    }

    public static int readEffectType(SharedPreferences pref) {
        String effectSelection = pref.getString(KEY_VIDEO_EFFECT, "none");
        if (effectSelection.equals("none")) {
            return EffectsRecorder.EFFECT_NONE;
        } else if (effectSelection.startsWith("goofy_face")) {
            return EffectsRecorder.EFFECT_GOOFY_FACE;
        } else if (effectSelection.startsWith("backdropper")) {
            return EffectsRecorder.EFFECT_BACKDROPPER;
        }
        Log.e(TAG, "Invalid effect selection: " + effectSelection);
        return EffectsRecorder.EFFECT_NONE;
    }

    public static Object readEffectParameter(SharedPreferences pref) {
        String effectSelection = pref.getString(KEY_VIDEO_EFFECT, "none");
        if (effectSelection.equals("none")) {
            return null;
        }
        int separatorIndex = effectSelection.indexOf('/');
        String effectParameter =
                effectSelection.substring(separatorIndex + 1);
        if (effectSelection.startsWith("goofy_face")) {
            if (effectParameter.equals("squeeze")) {
                return EffectsRecorder.EFFECT_GF_SQUEEZE;
            } else if (effectParameter.equals("big_eyes")) {
                return EffectsRecorder.EFFECT_GF_BIG_EYES;
            } else if (effectParameter.equals("big_mouth")) {
                return EffectsRecorder.EFFECT_GF_BIG_MOUTH;
            } else if (effectParameter.equals("small_mouth")) {
                return EffectsRecorder.EFFECT_GF_SMALL_MOUTH;
            } else if (effectParameter.equals("big_nose")) {
                return EffectsRecorder.EFFECT_GF_BIG_NOSE;
            } else if (effectParameter.equals("small_eyes")) {
                return EffectsRecorder.EFFECT_GF_SMALL_EYES;
            }
        } else if (effectSelection.startsWith("backdropper")) {
            // Parameter is a string that either encodes the URI to use,
            // or specifies 'gallery'.
            return effectParameter;
        }

        Log.e(TAG, "Invalid effect selection: " + effectSelection);
        return null;
    }


    public static void restorePreferences(Context context,
            ComboPreferences preferences, Parameters parameters) {
        int currentCameraId = readPreferredCameraId(preferences);

        // Clear the preferences of both cameras.
        int backCameraId = CameraHolder.instance().getBackCameraId();
        if (backCameraId != -1) {
            preferences.setLocalId(context, backCameraId);
            Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        }
        int frontCameraId = CameraHolder.instance().getFrontCameraId();
        if (frontCameraId != -1) {
            preferences.setLocalId(context, frontCameraId);
            Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        }

        // Switch back to the preferences of the current camera. Otherwise,
        // we may write the preference to wrong camera later.
        preferences.setLocalId(context, currentCameraId);

        upgradeGlobalPreferences(preferences.getGlobal());
        upgradeLocalPreferences(preferences.getLocal());

        // Write back the current camera id because parameters are related to
        // the camera. Otherwise, we may switch to the front camera but the
        // initial picture size is that of the back camera.
        initialCameraPictureSize(context, parameters);
        writePreferredCameraId(preferences, currentCameraId);
    }

    private ArrayList<String> getSupportedVideoQuality() {
        ArrayList<String> supported = new ArrayList<String>();
        // Check for supported quality
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_1080P)) {
            supported.add(Integer.toString(CamcorderProfile.QUALITY_1080P));
        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_720P)) {
            supported.add(Integer.toString(CamcorderProfile.QUALITY_720P));
        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_480P)) {
            supported.add(Integer.toString(CamcorderProfile.QUALITY_480P));
        }
		//Lutintting add for ICS Camera begin
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_CIF)) {
            supported.add(Integer.toString(CamcorderProfile.QUALITY_CIF));
        }
//        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_FWVGA)) {//2012/3/20 Jinsheng mark for compile
//            supported.add(Integer.toString(CamcorderProfile.QUALITY_FWVGA));
//        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_HIGH)) {//1
            supported.add(Integer.toString(CamcorderProfile.QUALITY_HIGH));
        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_LOW)) {//0
            supported.add(Integer.toString(CamcorderProfile.QUALITY_LOW));
        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_QCIF)) {
            supported.add(Integer.toString(CamcorderProfile.QUALITY_QCIF));
        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_QVGA)) {//11
            supported.add(Integer.toString(CamcorderProfile.QUALITY_QVGA));
        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_VGA)) {
            supported.add(Integer.toString(CamcorderProfile.QUALITY_VGA));
        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_WQVGA)) {
            supported.add(Integer.toString(CamcorderProfile.QUALITY_VGA));
        }
        if (CamcorderProfile.hasProfile(mCameraId, CamcorderProfile.QUALITY_WVGA)) {
            supported.add(Integer.toString(CamcorderProfile.QUALITY_WVGA));
        }
		//Lutintting add for ICS Camera end
        return supported;
    }

    private void initVideoEffect(PreferenceGroup group, ListPreference videoEffect) {
        CharSequence[] values = videoEffect.getEntryValues();

        boolean goofyFaceSupported =
                EffectsRecorder.isEffectSupported(EffectsRecorder.EFFECT_GOOFY_FACE);
        boolean backdropperSupported =
                EffectsRecorder.isEffectSupported(EffectsRecorder.EFFECT_BACKDROPPER) &&
                mParameters.isAutoExposureLockSupported() &&
                mParameters.isAutoWhiteBalanceLockSupported();
        Log.i("ltt", "goofyFaceSupported="+goofyFaceSupported);
        Log.i("ltt", "backdropperSupported="+backdropperSupported);
        ArrayList<String> supported = new ArrayList<String>();
        for (CharSequence value : values) {
            String effectSelection = value.toString();
            if (!goofyFaceSupported && effectSelection.startsWith("goofy_face")) continue;
            if (!backdropperSupported && effectSelection.startsWith("backdropper")) continue;
            supported.add(effectSelection);
        }

        filterUnsupportedOptions(group, videoEffect, supported);
    }


//FihtdcCode@20111121 Peiming Wang add ICS begin*****
   public static int getVidoeDurationInMillis(String quality) {    
        if (VIDEO_QUALITY_MMS.equals(quality)) {
            return MMS_VIDEO_DURATION * 1000;
        } else if (VIDEO_QUALITY_YOUTUBE.equals(quality)) {
            return YOUTUBE_VIDEO_DURATION * 1000;
        }	
        return DEFAULT_VIDEO_DURATION * 1000;       
    }
    
    
     public static int getVideoQuality(String quality) {
     
        if(VIDEO_QUALITY_YOUTUBE.equals(
                quality) || VIDEO_QUALITY_HIGH.equals(quality))
            return 1;
        else
            return 0;      
    }
//FihtdcCode@20111121 Peiming Wang add ICS end*****
}
