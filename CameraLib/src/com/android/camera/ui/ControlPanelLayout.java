/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import com.android.camera.ModePicker;
/**
 * A layout which handles the the width of the control panel, which contains
 * the shutter button, thumbnail, front/back camera picker, and mode picker.
 * The purpose of this is to have a consistent width of control panel in camera,
 * camcorder, and panorama modes.
 */
public class ControlPanelLayout extends RelativeLayout {
    private static final String TAG = "ControlPanelLayout";

    //FihtdcCode@20120117 Peiming GOXI.B-994   begin 
    //ControlPanellayout support the phone and tablet at the same time.
    private ModePicker mModePicker;
	//FihtdcCode@20120117 Peiming GOXI.B-994   end
    public ControlPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthSpec);
        int heightSpecSize = MeasureSpec.getSize(heightSpec);
        
        //FihtdcCode@20120117 Peiming GOXI.B-994   begin 
        //ControlPanellayout support the phone and tablet at the same time.
        if (mModePicker.CURRENT_MODE==0) {
            //For phone
            int measuredSize = 0;
            int mode, longSideSize, shortSideSize, specSize;
    
            boolean isLandscape = (((Activity) getContext()).getRequestedOrientation()
                    == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    		Log.i(TAG, "ControlPanelLayout support phone");
            if (isLandscape) {
                mode = MeasureSpec.getMode(widthSpec);
                longSideSize = widthSpecSize;
                shortSideSize = heightSpecSize;
                specSize = widthSpecSize;
            } else {
                mode = MeasureSpec.getMode(heightSpec);
                longSideSize = heightSpecSize;
                shortSideSize = widthSpecSize;
                specSize = heightSpecSize;
            }
    
            if (widthSpecSize > 0 && heightSpecSize > 0 && mode == MeasureSpec.AT_MOST) {
                // Calculate how big 4:3 preview occupies. Then deduct it from the
                // width of the parent.
                measuredSize = (int) (longSideSize - shortSideSize / 3.0 * 4.0);
            } else {
                Log.e(TAG, "layout_xxx of ControlPanelLayout should be wrap_content");
            }
    
            // The width cannot be bigger than the constraint.
            if (mode == MeasureSpec.AT_MOST && measuredSize > specSize) {
                measuredSize = specSize;
            }
    
            if (isLandscape) {
                widthSpec = MeasureSpec.makeMeasureSpec(measuredSize, MeasureSpec.EXACTLY);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(measuredSize, MeasureSpec.EXACTLY);
            }
    
            super.onMeasure(widthSpec, heightSpec);
           
        }    
        else{// current_mode_bar is only for tablet.
            int widthMode = MeasureSpec.getMode(widthSpec);
            int measuredWidth = 0;
            Log.i(TAG, "ControlPanelLayout support tablet");
            if (widthSpecSize > 0 && heightSpecSize > 0 && widthMode == MeasureSpec.AT_MOST) {
                // Calculate how big 4:3 preview occupies. Then deduct it from the
                // width of the parent.
                measuredWidth = (int) (widthSpecSize - heightSpecSize / 3.0 * 4.0 - 16);
            } else {
                Log.e(TAG, "layout_width of ControlPanelLayout should be wrap_content");
            }
    
            // Make sure the width is bigger than the minimum width.
            int minWidth = getSuggestedMinimumWidth();
            if (minWidth > measuredWidth) {
                measuredWidth = minWidth;
            }
    
            // The width cannot be bigger than the constraint.
            if (widthMode == MeasureSpec.AT_MOST && measuredWidth > widthSpecSize) {
                measuredWidth = widthSpecSize;
            }
    
            super.onMeasure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                    heightSpec);
       }
	   //FihtdcCode@20120117 Peiming GOXI.B-994   end
    }
}
