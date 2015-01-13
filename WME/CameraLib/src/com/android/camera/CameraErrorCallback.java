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

package com.android.camera;

import android.util.Log;

public class CameraErrorCallback
        implements android.hardware.Camera.ErrorCallback {
    private static final String TAG = "CameraErrorCallback";

    public void onError(int error, android.hardware.Camera camera) {
        Log.e(TAG, "Got camera error callback. error=" + error);
        if (error == android.hardware.Camera.CAMERA_ERROR_SERVER_DIED) {
            // We are not sure about the current state of the app (in preview or
            // snapshot or recording). Closing the app is better than creating a
            // new Camera object.
            Log.i(TAG,"Received error CAMERA_ERROR_SERVER_DIED");
            throw new RuntimeException("Received error CAMERA_ERROR_SERVER_DIED");
        }
        //2012/6/12 Jinsheng add for error handle begin
        else if(error == android.hardware.Camera.CAMERA_ERROR_UNKNOWN){
            Log.i(TAG,"Received error CAMERA_ERROR_UNKNOWN");
            throw new RuntimeException("Received error CAMERA_ERROR_UNKNOWN");
        }
        else{
            Log.i(TAG,"Received undefined error");
            throw new RuntimeException("Received undefined error");
        }
        //2012/6/12 Jinsheng add for error handle end
    }
}
