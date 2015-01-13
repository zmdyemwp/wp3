/**
 * Copyright 2010-present Facebook
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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.facebook.android.Facebook;

@SuppressWarnings("deprecation")
public class SessionStore {

    private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String LAST_UPDATE = "last_update";
    private static final String KEY = com.fihtdc.smartbracelet.util.Utility.SHARED_PREFS_NAME;

    /*
     * Save the access token and expiry date so you don't have to fetch it each
     * time
     */
    public static boolean save(Facebook session, Context context) {
    	Log.i("Fly", "Save Access Token");
    	if(session.getAccessToken()!=null){
    		    Log.i("Fly", "Save Access Token Success");
    		    Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
    	        editor.putString(TOKEN, session.getAccessToken());
    	        editor.putLong(EXPIRES, session.getAccessExpires());
    	        editor.putLong(LAST_UPDATE, session.getLastAccessUpdate());
    	        return editor.commit();
    	}
    	return false;
      
    }
    /*
     * Get the access token 
     */
    public static String  getCacheAccessToken(Context context){
    	return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(TOKEN, null);
    }
    /*
     * Get the Expires
     */
    public static long getCacheExpires(Context context){
    	return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getLong(EXPIRES, 0);
    }
    /*
     * Get the Last update
     */
    public static long getCacheLastUpdate(Context context){
    	return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getLong(LAST_UPDATE, 0);
    }

    /*
     * Restore the access token and the expiry date from the shared preferences.
     */
    public static boolean restore(Facebook session, Context context) {
    	Log.i("Fly", "SharePreference restore");
    	if(session == null || context == null){
    		return false;
    	}
        SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        session.setTokenFromCache(
                savedSession.getString(TOKEN, null),
                savedSession.getLong(EXPIRES, 0),
                savedSession.getLong(LAST_UPDATE, 0));
        return session.isSessionValid();
    }

    public static void clear(Context context) {
    	Log.i("Fly", "SharePreference clear ");
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.remove(EXPIRES);
        editor.remove(LAST_UPDATE);
        editor.remove(TOKEN);
        editor.commit();
    }

}
