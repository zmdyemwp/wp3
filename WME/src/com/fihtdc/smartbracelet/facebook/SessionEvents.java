/*
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

import java.util.LinkedList;

import android.util.Log;

public class SessionEvents {

    private static LinkedList<AuthListener> mAuthListeners = new LinkedList<AuthListener>();
    private static LinkedList<LogoutListener> mLogoutListeners = new LinkedList<LogoutListener>();
    
    private static LinkedList<ReOuthListener> mReOuthListeners = new LinkedList<ReOuthListener>();

    /**
     * Associate the given listener with this Facebook object. The listener's
     * callback interface will be invoked when authentication events occur.
     * 
     * @param listener
     *            The callback object for notifying the application when auth
     *            events happen.
     */
    public static void addAuthListener(AuthListener listener) {
    	if(!mAuthListeners.contains(listener)){
    		  mAuthListeners.add(listener);
    	}
      
    }
    /**
     * Associate the given listener with this Facebook object. The listener's
     * callback interface will be invoked when re authentication events occur.
     * 
     * @param listener
     *            The callback object for notifying the application when re outh
     *            events happen.
     */
    public static void addReOuthListener(ReOuthListener listener){
    	if(!mReOuthListeners.contains(listener)){
    		mReOuthListeners.add(listener);
  	}
    }
    public static void removeReOuthListener(ReOuthListener listener){
    	if(mReOuthListeners.contains(listener)){
    		mReOuthListeners.remove(listener);
  	}
    }
    
    public  static boolean isReOutListener(){
      	return mReOuthListeners.size()>0?true:false;
    }

    /**
     * Remove the given listener from the list of those that will be notified
     * when authentication events occur.
     * 
     * @param listener
     *            The callback object for notifying the application when auth
     *            events happen.
     */
    public static void removeAuthListener(AuthListener listener) {
        mAuthListeners.remove(listener);
    }

    /**
     * Associate the given listener with this Facebook object. The listener's
     * callback interface will be invoked when logout occurs.
     * 
     * @param listener
     *            The callback object for notifying the application when log out
     *            starts and finishes.
     */
    public static void addLogoutListener(LogoutListener listener) {
    	if(!mLogoutListeners.contains(listener)){
    		   mLogoutListeners.add(listener);
    	}
    }

    /**
     * Remove the given listener from the list of those that will be notified
     * when logout occurs.
     * 
     * @param listener
     *            The callback object for notifying the application when log out
     *            starts and finishes.
     */
    public static void removeLogoutListener(LogoutListener listener) {
        mLogoutListeners.remove(listener);
    }

    public static void onLoginSuccess() {
        for (AuthListener listener : mAuthListeners) {
            listener.onAuthSucceed();
        }
    }

    public static void onLoginError(String error) {
        for (AuthListener listener : mAuthListeners) {
            listener.onAuthFail(error);
        }
    }

    public static void onLogoutBegin() {
        for (LogoutListener l : mLogoutListeners) {
            l.onLogoutBegin();
        }
    }

    public static void onLogoutFinish() {
    	if(mLogoutListeners.size()>1){
    		Log.i("Fly", "mLogoutListeners.size==="+mLogoutListeners.size());
    		    LogoutListener listener =	mLogoutListeners.get(0);
    	    	mLogoutListeners.remove(listener);
    	    	mLogoutListeners.add(listener);
    	}
        for (LogoutListener l : mLogoutListeners) {
            l.onLogoutFinish();
        }
    }
    
    public static void onReOuthAction(){
        if(mReOuthListeners.size()>0){
            mReOuthListeners.get(0).onReOuthStart();
        }
    }

    /**
     * Callback interface for authorization events.
     */
    public static interface AuthListener {

        /**
         * Called when a auth flow completes successfully and a valid OAuth
         * Token was received. Executed by the thread that initiated the
         * authentication. API requests can now be made.
         */
        public void onAuthSucceed();

        /**
         * Called when a login completes unsuccessfully with an error.
         * 
         * Executed by the thread that initiated the authentication.
         */
        public void onAuthFail(String error);
    }

    /**
     * Callback interface for logout events.
     */
    public static interface LogoutListener {
        /**
         * Called when logout begins, before session is invalidated. Last chance
         * to make an API call. Executed by the thread that initiated the
         * logout.
         */
        public void onLogoutBegin();

        /**
         * Called when the session information has been cleared. UI should be
         * updated to reflect logged-out state.
         * 
         * Executed by the thread that initiated the logout.
         */
        public void onLogoutFinish();
    }
    /**
     * Callback interface for  re outh events.
     */
    public static interface ReOuthListener{
    	  /**
         * Called when access token has expired
         */
    	public void onReOuthStart();
    }

}
