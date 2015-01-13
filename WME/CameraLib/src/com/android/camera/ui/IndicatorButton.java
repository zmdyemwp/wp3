/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.camera.ui;

import com.android.camera.CameraSettings;
import com.android.camera.IconListPreference;
import com.android.camera.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

// An indicator button that represents one camera setting. Ex: flash. Pressing it opens a popup
// window.
public class IndicatorButton extends AbstractIndicatorButton
        implements BasicSettingPopup.Listener, EffectSettingPopup.Listener{
    private final String TAG = "IndicatorButton";
    private IconListPreference mPreference;
    private IconListPreference mPreferenceSecond ;//2012/2/17 add for IRMI.B-505 color effect
    // Scene mode can override the original preference value.
    private String mOverrideValue;
    private Listener mListener;
    
    private boolean mIsSecondNeedUpdate = false;//2012/2/27 Jinsheng add for color effect update

    static public interface Listener {
        public void onSettingChanged();
    }

    public void setSettingChangedListener(Listener listener) {
        mListener = listener;
    }

    public IndicatorButton(Context context, IconListPreference pref) {
        super(context);
        mPreference = pref;
        mPreferenceSecond = null;
        reloadPreference();
    }
	
	public IndicatorButton(Context context, IconListPreference pref,IconListPreference secondPref) {
        super(context);
        mPreference = pref;
        mPreferenceSecond = secondPref;
        reloadPreference();
    }

    @Override
    public void reloadPreference() {
        Log.i(TAG,"IndicatorButton reloadPreference");
		
		//2012/2/27 Jinsheng add for color effect update begin
        boolean bUpdateSecondIcon = false;
        
        if(mPreference == null && mPreferenceSecond == null)return;
        
        if(mPreference == null && mPreferenceSecond != null){//Only mPreference
            bUpdateSecondIcon = true;
        }
        else if(mPreference != null && mPreferenceSecond == null){//Only mPreferenceSecond
            bUpdateSecondIcon = false;
        }else{//Two preference,need check which is change(if item clicked)
            bUpdateSecondIcon = mIsSecondNeedUpdate;     
        }
        //2012/2/27 Jinsheng add for color effect update end        
				
        if(bUpdateSecondIcon == true){//2012/2/13 Jinsheng add for camera coloreffect
            
            int[] iconIds1 = mPreferenceSecond.getLargeIconIds();
            if (iconIds1 != null) {
                // Each entry has a corresponding icon.
                int index1 = 0;
                // if (mOverrideValue == null) {
                index1 = mPreferenceSecond.findIndexOfValue(mPreferenceSecond.getValue());
                
                //2012/3/26 Jinsheng add for IRMI.B-1781 begin
                if (index1 == -1) {
                    // Avoid the crash 
                    Log.e(TAG, "Fail to find override value=" + mOverrideValue);
                    mPreferenceSecond.print();
                    return;
                }
                //2012/3/26 Jinsheng add for IRMI.B-1781 end
//                } else {
//                    index1 = mPreferenceSecond.findIndexOfValue(mOverrideValue);
//                    if (index == -1) {
//                        // Avoid the crash if camera driver has bugs.
//                        Log.e(TAG, "Fail to find override value=" + mOverrideValue);
//                        mPreferenceSecond.print();
//                        return;
//                    }
//                }
                setImageResource(iconIds1[index1]);
            } else {
                // The preference only has a single icon to represent it.
                setImageResource(mPreferenceSecond.getSingleIcon());
            }
//            super.reloadPreference();
//            return;
        }
        else{
            int[] iconIds = mPreference.getLargeIconIds();
            if (iconIds != null) {
                // Each entry has a corresponding icon.
                int index = 0;
                if (mOverrideValue == null) {
                    index = mPreference.findIndexOfValue(mPreference.getValue());
                    if (index == -1){
                        Log.e(TAG, "Fail to find  value=" + mPreference.getValue());
                        mPreference.print();
                        return;
                    }
                } else {
                    index = mPreference.findIndexOfValue(mOverrideValue);
                    if (index == -1) {
                        // Avoid the crash if camera driver has bugs.
                        Log.e(TAG, "Fail to find override value=" + mOverrideValue);
                        mPreference.print();
                        return;
                    }
                }
                setImageResource(iconIds[index]);
            } else {
                // The preference only has a single icon to represent it.
                setImageResource(mPreference.getSingleIcon());
            }
        }
        
        super.reloadPreference();
    }

    public String getKey() {
        if(mPreference != null){//2012/2/13 Jinsheng add for camera coloreffect
            return mPreference.getKey();
        }else if(mPreferenceSecond != null){//2012/2/13 Jinsheng add for camera coloreffect
            return mPreferenceSecond.getKey();
        }
        
        return null;
    }

    @Override
    public boolean isOverridden() {
        return mOverrideValue != null;
    }

    @Override
    public void overrideSettings(final String ... keyvalues) {
        mOverrideValue = null;
        for (int i = 0; i < keyvalues.length; i += 2) {
            String key = keyvalues[i];
            String value = keyvalues[i + 1];
            if (key.equals(getKey())) {
                mOverrideValue = value;
                setEnabled(value == null);
                break;
            }
        }
        reloadPreference();
    }

    @Override
    protected void initializePopup() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup root = (ViewGroup) getRootView().findViewById(R.id.frame_layout);

        //AbstractSettingPopup popup;
        if (CameraSettings.KEY_VIDEO_EFFECT.equals(getKey())) {
            EffectSettingPopup effect = (EffectSettingPopup) inflater.inflate(
                    R.layout.effect_setting_popup, root, false);
            effect.initialize(mPreference);
            effect.initialize_effect(mPreferenceSecond,false);//2012/2/27 Jinsheng add mPreferenceSecond
            effect.setSettingChangedListener(this);
            mPopup = effect;
        }
		//2012/2/13 Jinsheng add for camera coloreffect begin
		else if (CameraSettings.KEY_COLOR_EFFECT.equals(getKey())) {
            EffectSettingPopup effect = (EffectSettingPopup) inflater.inflate(
                    R.layout.effect_setting_popup, root, false);
            effect.initialize_effect(mPreferenceSecond,true);
            effect.setSettingChangedListener(this);
            mPopup = effect;
		//2012/2/13 Jinsheng add for camera coloreffect end
        }else {
            BasicSettingPopup basic = (BasicSettingPopup) inflater.inflate(
                    R.layout.basic_setting_popup, root, false);
            basic.initialize(mPreference);
            basic.setSettingChangedListener(this);
            mPopup = basic;
        }
        root.addView(mPopup);
    }

    @Override
    public void onSettingChanged() {
        reloadPreference();
        // Dismiss later so the activated state can be updated before dismiss.
        dismissPopupDelayed();
        if (mListener != null) {
            mListener.onSettingChanged();
        }
    }
    
    @Override
    public void onSettingChanged(int index) {
        if(index == 1)mIsSecondNeedUpdate = true;
        onSettingChanged();       
        mIsSecondNeedUpdate = false;
    }
    
}
