/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fihtdc.smartbracelet.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.util.Log;
import com.facebook.android.FacebookError;

/**
 * Skeleton base class for RequestListeners, providing default error handling.
 * Applications should handle these error conditions.
 */
@SuppressWarnings("deprecation")
public abstract class BaseRequestListener implements WMERequestListener {

    @Override
    public void onFacebookError(FacebookError e, final Object state) {
        Log.e("Facebook", "onFacebookError");
    }

    @Override
    public void onFileNotFoundException(FileNotFoundException e, final Object state) {
        Log.e("Facebook", "onFileNotFoundException");
    }

    @Override
    public void onIOException(IOException e, final Object state) {
        Log.e("Facebook", "onIOException");
    }

    @Override
    public void onMalformedURLException(MalformedURLException e, final Object state) {
    	Log.e("Facebook", "onMalformedURLException");
    }
    @Override
    public void onBachReqComplete(String[] response, Object state) {
        Log.e("Facebook", "onFacebookError");
    }

}
