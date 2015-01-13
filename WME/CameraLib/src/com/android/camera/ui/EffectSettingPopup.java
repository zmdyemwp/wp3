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
import com.android.camera.EffectsRecorder;
import com.android.camera.IconListPreference;
import com.android.camera.PreferenceGroup;
import com.android.camera.PreferenceInflater;
import com.android.camera.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

// A popup window that shows video effect setting. It has two grid view.
// One shows the goofy face effects. The other shows the background replacer
// effects.
public class EffectSettingPopup extends AbstractSettingPopup implements
        AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "EffectSettingPopup";
    private String mNoEffect;
    private String mNoColorEffect;//ltt
    private IconListPreference mPreference;
    private Listener mListener;
    private View mClearEffects;
    private GridView mSillyFacesGrid;
    private GridView mBackgroundGrid;
    private IconListPreference mColorPreference;//ting-ting.lu for color effect
    private GridView mColorEffectGrid;//ting-ting.lu for color effect

    // Data for silly face items. (text, image, and preference value)
    ArrayList<HashMap<String, Object>> mSillyFacesItem =
            new ArrayList<HashMap<String, Object>>();
    ArrayList<HashMap<String, Object>> mColorEffectItem =
            new ArrayList<HashMap<String, Object>>();//ting-ting.lu for color effect
    
    // Data for background replacer items. (text, image, and preference value)
    ArrayList<HashMap<String, Object>> mBackgroundItem =
            new ArrayList<HashMap<String, Object>>();


    static public interface Listener {
        public void onSettingChanged(int index);//2012/2/27 Jinsheng modify for color effect update
    }

    public EffectSettingPopup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNoEffect = context.getString(R.string.pref_video_effect_default);
        mNoColorEffect = context.getString(R.string.pref_camera_coloreffect_default);//ltt
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mClearEffects = findViewById(R.id.clear_effects);
        mClearEffects.setOnClickListener(this);
        mSillyFacesGrid = (GridView) findViewById(R.id.effect_silly_faces);
        mBackgroundGrid = (GridView) findViewById(R.id.effect_background);
        mColorEffectGrid = (GridView) findViewById(R.id.color_effect);//ting-ting.lu for color effect
    }

    public void initialize(IconListPreference preference) {
        mPreference = preference;
        Context context = getContext();
        CharSequence[] entries = mPreference.getEntries();
        CharSequence[] entryValues = mPreference.getEntryValues();
        int[] iconIds = mPreference.getImageIds();
        if (iconIds == null) {
            iconIds = mPreference.getLargeIconIds();
        }

        // Set title.
        mTitle.setText(mPreference.getTitle());

        for(int i = 0; i < entries.length; ++i) {
            String value = entryValues[i].toString();
            if (value.equals(mNoEffect)) continue;  // no effect, skip it.
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("value", value);
            map.put("text", entries[i].toString());
            if (iconIds != null) map.put("image", iconIds[i]);
            if (value.startsWith("goofy_face")) {
                mSillyFacesItem.add(map);
            } else if (value.startsWith("backdropper")) {
                mBackgroundItem.add(map);
            }
        }

        boolean hasSillyFaces = mSillyFacesItem.size() > 0;
        boolean hasBackground = mBackgroundItem.size() > 0;

        // Initialize goofy face if it is supported.
        if (hasSillyFaces) {
            findViewById(R.id.effect_silly_faces_title).setVisibility(View.VISIBLE);
            findViewById(R.id.effect_silly_faces_title_separator).setVisibility(View.VISIBLE);
            mSillyFacesGrid.setVisibility(View.VISIBLE);
            SimpleAdapter sillyFacesItemAdapter = new SimpleAdapter(context,
                    mSillyFacesItem, R.layout.effect_setting_item,
                    new String[] {"text", "image"},
                    new int[] {R.id.text, R.id.image});
            mSillyFacesGrid.setAdapter(sillyFacesItemAdapter);
            mSillyFacesGrid.setOnItemClickListener(this);
        }

        if (hasSillyFaces && hasBackground) {
            findViewById(R.id.effect_background_separator).setVisibility(View.VISIBLE);
        }

        // Initialize background replacer if it is supported.
        if (hasBackground) {
            findViewById(R.id.effect_background_title).setVisibility(View.VISIBLE);
            findViewById(R.id.effect_background_title_separator).setVisibility(View.VISIBLE);
            mBackgroundGrid.setVisibility(View.VISIBLE);
            SimpleAdapter backgroundItemAdapter = new SimpleAdapter(context,
                    mBackgroundItem, R.layout.effect_setting_item,
                    new String[] {"text", "image"},
                    new int[] {R.id.text, R.id.image});
            mBackgroundGrid.setAdapter(backgroundItemAdapter);
            mBackgroundGrid.setOnItemClickListener(this);
        }

        reloadPreference();
    }

  //add by ting-ting.lu for coloreffect
    //2012/2/27 Jinsheng add IconListPreference preference
    public void initialize_effect(IconListPreference preference,boolean hideFaceEffect) {
        if(hideFaceEffect) mPreference = null;//Just color effect
        mColorPreference = preference;
        
        Context context = getContext();
        //2012/2/27 Jinsheng modify for color effect begin
//        PreferenceInflater inflater = new PreferenceInflater(mContext);
//        PreferenceGroup group =
//                (PreferenceGroup) inflater.inflate(R.xml.video_preferences);
//        mColorPreference = (IconListPreference) group.findPreference(CameraSettings.KEY_COLOR_EFFECT);
        //2012/2/27 Jinsheng modify for color effect end
        CharSequence[] entries = mColorPreference.getEntries();
        CharSequence[] entryValues = mColorPreference.getEntryValues();
        int[] iconIds = mColorPreference.getImageIds();
        if (iconIds == null) {
            iconIds = mColorPreference.getLargeIconIds();
        }

        // Set title.
        if(hideFaceEffect){//Just color effect
            mTitle.setText(mColorPreference.getTitle());
        }
        
        for(int i = 0; i < entries.length; ++i) {
            String value = entryValues[i].toString();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("value", value);
            map.put("text", entries[i].toString());
            if (iconIds != null) map.put("image", iconIds[i]);
            mColorEffectItem.add(map);
        }

            findViewById(R.id.color_effect_title).setVisibility(View.VISIBLE);
            mColorEffectGrid.setVisibility(View.VISIBLE);
            SimpleAdapter colorEffectItemAdapter = new SimpleAdapter(context,
                    mColorEffectItem, R.layout.effect_setting_item,
                    new String[] {"text", "image"},
                    new int[] {R.id.text, R.id.image});
            mColorEffectGrid.setAdapter(colorEffectItemAdapter);
            mColorEffectGrid.setOnItemClickListener(this);

        if(true == hideFaceEffect){
            mClearEffects.setVisibility(View.GONE);
        }
        //reloadPreference();
    }
    
    //2012/3/24 Jinsheng add for IRMI.B-1781 begin
    @Override
    protected void dynamicUpdate(){
        CharSequence[] entries = mColorPreference.getEntries();
        CharSequence[] entryValues = mColorPreference.getEntryValues();
        int[] iconIds = mColorPreference.getImageIds();
        if (iconIds == null) {
            iconIds = mColorPreference.getLargeIconIds();
        }
     
        mColorEffectItem.clear();
        
        for(int i = 0; i < entries.length; ++i) {
            String value = entryValues[i].toString();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("value", value);
            map.put("text", entries[i].toString());
            if (iconIds != null) map.put("image", iconIds[i]);
            mColorEffectItem.add(map);
        }
        
        mColorEffectGrid.setVisibility(View.VISIBLE);
        SimpleAdapter colorEffectItemAdapter = new SimpleAdapter(getContext(),
                mColorEffectItem, R.layout.effect_setting_item,
                new String[] {"text", "image"},
                new int[] {R.id.text, R.id.image});
        mColorEffectGrid.setAdapter(colorEffectItemAdapter);
        mColorEffectGrid.setOnItemClickListener(this);
       
        reloadPreference();
    }
    //2012/3/24 Jinsheng add for IRMI.B-1781 end
    
    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (getVisibility() != View.VISIBLE && mPreference != null) {
                // Do not show or hide "Clear effects" button when the popup
                // is already visible. Otherwise it looks strange.
                boolean noEffect = mPreference.getValue().equals(mNoEffect);
                mClearEffects.setVisibility(noEffect ? View.GONE : View.VISIBLE);
            }
            reloadPreference();
        }
        super.setVisibility(visibility);
    }

    // The value of the preference may have changed. Update the UI.
    @Override
    public void reloadPreference() {
        //2012/2/18 Jinsheng add for camera coloreffect begin
        if(mColorPreference != null){
            String valueColor = mColorPreference.getValue();
            mColorEffectGrid.setItemChecked(mColorEffectGrid.getCheckedItemPosition(), false);
            
            if (valueColor.equals(mNoColorEffect)) return;
            
            for (int i = 0; i < mColorEffectItem.size(); i++) {
                if (valueColor.equals(mColorEffectItem.get(i).get("value"))) {
                    mColorEffectGrid.setItemChecked(i, true);
                    return;
                }
            }
        }
    
        if(mPreference == null)
        {
            return;
        } 
        //2012/2/18 Jinsheng add for camera coloreffect end
        
        mBackgroundGrid.setItemChecked(mBackgroundGrid.getCheckedItemPosition(), false);
        mSillyFacesGrid.setItemChecked(mSillyFacesGrid.getCheckedItemPosition(), false);

        String value = mPreference.getValue();
        if (value.equals(mNoEffect)) return;

        for (int i = 0; i < mSillyFacesItem.size(); i++) {
            if (value.equals(mSillyFacesItem.get(i).get("value"))) {
                mSillyFacesGrid.setItemChecked(i, true);
                return;
            }
        }

        for (int i = 0; i < mBackgroundItem.size(); i++) {
            if (value.equals(mBackgroundItem.get(i).get("value"))) {
                mBackgroundGrid.setItemChecked(i, true);
                return;
            }
        }

        Log.e(TAG, "Invalid preference value: " + value);
        mPreference.print();
    }

    public void setSettingChangedListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
            int index, long id) {
        String value;
        int nClickPref = 0;//2012/2/27 Jinsheng add for color effect update
        if (parent == mSillyFacesGrid) {
            value = (String) mSillyFacesItem.get(index).get("value");
            mPreference.setValue(value);
        } else if (parent == mBackgroundGrid) {
            value = (String) mBackgroundItem.get(index).get("value");
            mPreference.setValue(value);
        } else{// add by ting-ting.lu for color effect
            value = (String) mColorEffectItem.get(index).get("value");
            mColorPreference.setValue(value);
            nClickPref = 1;//2012/2/27 Jinsheng add for color effect update
        }

        // Tapping the selected effect will deselect it (clear effects).
        /*if (value.equals(mPreference.getValue())) {
            mPreference.setValue(mNoEffect);
        } else {
            mPreference.setValue(value);
        }*/
        reloadPreference();
        if (mListener != null) mListener.onSettingChanged(nClickPref);//2012/2/27 Jinsheng modify for color effect update
    }

    @Override
    public void onClick(View v) {
        // Clear the effect.
        if(mPreference != null){//2012/2/18 Jinsheng add for IRMI.B-489
            mPreference.setValue(mNoEffect);
        }
		
        if(mColorPreference != null){//2012/2/20 add for IRMI.B-505
            //mColorPreference.setValue(mNoColorEffect);//Just clear the face effect
        }
		
        reloadPreference();
        if (mListener != null) mListener.onSettingChanged(0);//2012/2/27 Jinsheng modify for color effect update
    }
}
