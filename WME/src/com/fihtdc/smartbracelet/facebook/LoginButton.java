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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.facebook.SessionEvents.AuthListener;
import com.fihtdc.smartbracelet.facebook.SessionEvents.LogoutListener;
import com.fihtdc.smartbracelet.fragment.FacebookSettingsFragment.SessionListenerCallBack;
import com.fihtdc.smartbracelet.util.SmartToast;

public class LoginButton extends Button {
    private Facebook mFb;
    private Handler mHandler;
    private SessionListener mSessionListener = new SessionListener();
    private SessionListenerCallBack mCallBack;
    private String[] mPermissions;
    private Activity mActivity;
    private int mActivityCode;

    /**
     * Create the LoginButton.
     * 
     * @see View#View(Context)
     */
    public LoginButton(Context context) {
        super(context);
        // since onFinishInflate won't be called, we need to finish
        // initialization ourselves
    }

    /**
     * Create the LoginButton by inflating from XML
     * 
     * @see View#View(Context, AttributeSet)
     */
    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs.getStyleAttribute() == 0) {
            // apparently there's no method of setting a default style in xml,
            // so in case the users do not explicitly specify a style, we need
            // to use sensible defaults.
            this.setPadding(
                    getResources().getDimensionPixelSize(
                            R.dimen.com_facebook_loginview_padding_left),
                    getResources().getDimensionPixelSize(
                            R.dimen.com_facebook_loginview_padding_top),
                    getResources().getDimensionPixelSize(
                            R.dimen.com_facebook_loginview_padding_right),
                    getResources().getDimensionPixelSize(
                            R.dimen.com_facebook_loginview_padding_bottom));
            this.setWidth(getResources().getDimensionPixelSize(
                    R.dimen.com_facebook_loginview_width));
            this.setHeight(getResources().getDimensionPixelSize(
                    R.dimen.com_facebook_loginview_height));
            this.setGravity(Gravity.CENTER);
        }
    }

    /**
     * Create the LoginButton by inflating from XML and applying a style.
     * 
     * @see View#View(Context, AttributeSet, int)
     */
    public LoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public void init(final Activity activity, final int activityCode,
            final Facebook fb) {
        init(activity, activityCode, fb, new String[] {});
    }

    public void init(final Activity activity, final int activityCode,
            final Facebook fb, final String[] permissions) {
        mActivity = activity;
        mActivityCode = activityCode;
        mFb = fb;
        mPermissions = permissions;
        mHandler = new Handler();
        drawableStateChanged();
        setOnClickListener(new ButtonOnClickListener());
    }

    private final class ButtonOnClickListener implements OnClickListener {
        /*
         * Source Tag: login_tag
         */
        private long mBeforeClickMillis;

        @Override
        public void onClick(View arg0) {
          if(System.currentTimeMillis() - mBeforeClickMillis <=500){
              mBeforeClickMillis = System.currentTimeMillis();
              return;
           }
           mBeforeClickMillis = System.currentTimeMillis();
            if (LoginButton.this
                    .getText()
                    .toString()
                    .equals(getResources().getString(
                            R.string.com_facebook_loginview_log_out_action))) {
                String logout = getResources().getString(
                        R.string.com_facebook_loginview_log_out_action);
                String cancel = getResources().getString(
                        R.string.com_facebook_loginview_cancel_action);
                String message;
                message = mActivity.getResources().getString(
                        R.string.com_facebook_loginview_log_out_message);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(logout,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        if (com.fihtdc.smartbracelet.util.Utility
                                                .isInternetWorkValid(getContext())) {
                                            SessionEvents
                                                    .addAuthListener(mSessionListener);
                                            if (null != mCallBack) {
                                                mCallBack.addSesstionListener();
                                            }
                                            SessionEvents.onLogoutBegin();
                                            WMEAsyncFacebookRunner asyncRunner = new WMEAsyncFacebookRunner(
                                                    mFb);
                                            asyncRunner
                                                    .logout(getContext(),
                                                            new LogoutRequestListener());
                                        } else {
                                            SmartToast
                                                    .makeText(
                                                            getContext(),
                                                            getContext()
                                                                    .getResources()
                                                                    .getString(
                                                                            R.string.network_unavaliable),
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        }

                                    }
                                }).setNegativeButton(cancel, null);
                builder.create().show();

            } else {
                if (com.fihtdc.smartbracelet.util.Utility
                        .isInternetWorkValid(getContext())) {
                    SessionEvents.addAuthListener(mSessionListener);
                    if (null != mCallBack) {
                        mCallBack.addSesstionListener();
                    }
                    mFb.authorize(mActivity, mPermissions, mActivityCode,
                            new LoginDialogListener());

                } else {
                    SmartToast.makeText(
                            getContext(),
                            getContext().getResources().getString(
                                    R.string.network_unavaliable),
                            Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private final class LoginDialogListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
        }

        @Override
        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        @Override
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        @Override
        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }

    private class LogoutRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(String response, final Object state) {
            /*
             * callback should be run in the original thread, not the background
             * thread
             */
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SessionEvents.onLogoutFinish();
                }
            });
        }
    }

    public class SessionListener implements AuthListener, LogoutListener {

        public SessionListener() {

        }

        @Override
        public void onAuthSucceed() {
            SessionStore.save(mFb, getContext());
        }

        @Override
        public void onAuthFail(String error) {

        }

        @Override
        public void onLogoutBegin() {
            Log.i("Fly", "SessionListener onLogoutBegin()");
            SessionStore.restore(mFb, getContext());
        }

        @Override
        public void onLogoutFinish() {
            Log.i("Fly", "SessionListener onLogoutFinish()");
            mFb.setTokenFromCache(null, 0, 0);
            SessionStore.clear(getContext());
        }
    }

    public void logout() {
        if (mCallBack != null) {
            mCallBack.addSesstionListener();
        }
        SessionEvents.onLogoutBegin();
        WMEAsyncFacebookRunner asyncRunner = new WMEAsyncFacebookRunner(mFb);
        asyncRunner.logout(getContext(), new LogoutRequestListener());

    }

    public void setSesstionListenerCallBack(
            SessionListenerCallBack sessionListenerCallBack) {
        mCallBack = sessionListenerCallBack;
    }

    public void addSessionListener() {
        SessionEvents.addAuthListener(mSessionListener);
    }

}
