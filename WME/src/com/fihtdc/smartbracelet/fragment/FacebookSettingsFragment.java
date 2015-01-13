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

package com.fihtdc.smartbracelet.fragment;

import java.net.URISyntaxException;
import java.util.concurrent.locks.Lock;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.facebook.BaseRequestListener;
import com.fihtdc.smartbracelet.facebook.LoginButton;
import com.fihtdc.smartbracelet.facebook.SessionStore;
import com.fihtdc.smartbracelet.facebook.LoginButton.SessionListener;
import com.fihtdc.smartbracelet.facebook.SessionEvents;
import com.fihtdc.smartbracelet.facebook.SessionEvents.AuthListener;
import com.fihtdc.smartbracelet.facebook.SessionEvents.LogoutListener;
import com.fihtdc.smartbracelet.facebook.SessionEvents.ReOuthListener;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.SmartToast;
import com.fihtdc.smartbracelet.util.Utility;

/**
 * A Fragment that displays a Login/Logout button as well as the user's profile
 * picture and name when logged in.
 * <p/>
 * This Fragment will create and use the active session upon construction if it
 * has the available data (if the app ID is specified in the manifest). It will
 * also open the active session if it does not require user interaction (i.e. if
 * the session is in the {@link com.facebook.SessionState#CREATED_TOKEN_LOADED}
 * state. Developers can override the use of the active session by calling the
 * {@link #setSession(com.facebook.Session)} method.
 */
public class FacebookSettingsFragment extends FacebookFragment implements
        OnClickListener {
    private LinearLayout mFaceBookSettingLayout = null;
    private LinearLayout mFaceBookLauncherLayout = null;
    private LinearLayout mFaceBookNotificationLayout = null;
    private LinearLayout mFaceBookCheckInLayout = null;
    private LinearLayout mFaceBookCheckInTextLayout = null;
    private LoginButton mLoginButton;
    String[] permissions = { "offline_access", "publish_stream", "user_photos",
            "publish_actions", "photo_upload", "create_event", "rsvp_event",
            "manage_notifications", "publish_checkins", "read_requests",
            "read_mailbox" };
    private TextView connectedStateLabel;
    private EditText mCheckInMessage;
    private Switch mFacebookOpenSwitch;
    private Switch mFacebookOpenTopSwitch;
    private Switch mCheckInSwitch;
    private Switch mNotificationSwitch;
    private Drawable userProfilePic;
    private ImageView mUserPicImageView;
    private Session.StatusCallback sessionStatusCallback;
    private Context mContext;
    private boolean mIsHasSession;
    private Handler mHandler;
    final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
    private static final int DELAY_ANIM_MESSAGE = 101;
    private static final int TOTAL_DURATION = 500;
    private LinearLayout mControlLinearLayout;
    private LinearLayout mControlTopLinearLayout;
    FbAPIsAuthListener mAuthListener = new FbAPIsAuthListener();
    FbAPIsLogoutListener mLogoutListener = new FbAPIsLogoutListener();
    FbReouthListener mReouthListener = new FbReouthListener();
    private boolean mIsStartAuth;
    public interface SessionListenerCallBack {
        void addSesstionListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.com_facebook_usersettingsfragment, container, false);
        mLoginButton = (LoginButton) view
                .findViewById(R.id.com_facebook_usersettingsfragment_login_button);
        mLoginButton.setSesstionListenerCallBack(new SessionListenerCallBack() {

            @Override
            public void addSesstionListener() {
                Log.i("Fly", "add addSesstionListener()");
                SessionEvents.addAuthListener(mAuthListener);
                SessionEvents.addLogoutListener(mLogoutListener);
            }

        });
        com.fihtdc.smartbracelet.facebook.Utility.initReqEnviroment(mContext);
        mLoginButton.init(getActivity(), AUTHORIZE_ACTIVITY_RESULT_CODE,
                com.fihtdc.smartbracelet.facebook.Utility.mFacebook,
                permissions);
        connectedStateLabel = (TextView) view
                .findViewById(R.id.com_facebook_usersettingsfragment_profile_name);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                case DELAY_ANIM_MESSAGE:
                    onCloseFBFeatureTopLayoutChange();
                    onCloseFBCheckLayout();
                    onCloseFBCheckTextLayout();
                    onCloseFBNotiLayout();
                    onOpenFBControlLayout();
                    onOpenFBFeatureLayoutAnimation();
                    break;
                default:
                    break;
                }
                super.handleMessage(msg);
            }
        };
        initPageDisplay(view);
        onUserDataRequest();
        // if no background is set for some reason, then default to Facebook
        // blue
        if (getActivity().getIntent().getBooleanExtra(
                Constants.ACCOUNT_IS_REOUTH, false)) {
            mLoginButton.addSessionListener();
            SessionEvents.addAuthListener(mAuthListener);
            SessionEvents.addLogoutListener(mLogoutListener);
            com.fihtdc.smartbracelet.facebook.Utility.mFacebook.authorize(
                    getActivity(), permissions, AUTHORIZE_ACTIVITY_RESULT_CODE,
                    new LoginDialogListener());
            getActivity().getIntent().putExtra(Constants.ACCOUNT_IS_REOUTH,
                    false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        SessionEvents.addReOuthListener(mReouthListener);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        SessionEvents.removeReOuthListener(mReouthListener);
        super.onDestroy();
    }

    private void initPageDisplay(View view) {
        initFaceBookLauncherLayout(view);
        initFaceBookCheckInLayout(view);
        initFaceBookNotificationLayout(view);
        initFaceBookLauncherLayoutTop(view);
        initOtherLayout(view);
        initDisplay();
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

    private void onUserDataRequest() {
        if (com.fihtdc.smartbracelet.facebook.Utility.isSessionValid(mContext)) {
            requestUserData();
        } else {
            onDisplayNoLog();
        }
    }

    private void onDisplayNoLog() {
        mUserPicImageView.setVisibility(View.INVISIBLE);
        mLoginButton
                .setText(getString(R.string.com_facebook_loginview_log_in_button));
        connectedStateLabel.setText(getResources().getString(
                R.string.com_facebook_usersettingsfragment_not_logged_in));
    }

    /*
     * Request user name, and picture to show on the main screen.
     */
    public void requestUserData() {
        if (getActivity() != null && !isDetached()) {
            Log.i("Fly", "start requestUserData().............");
            mIsHasSession = true;
            mUserPicImageView.setVisibility(View.VISIBLE);
            connectedStateLabel.setText(getResources().getString(
                    R.string.com_facebook_loading));
            mLoginButton
                    .setText(getString(R.string.com_facebook_loginview_log_out_button));
            Bundle params = new Bundle();
            params.putString("fields", "name, picture");
            params.putString(Facebook.TOKEN,
                    SessionStore.getCacheAccessToken(mContext));
            com.fihtdc.smartbracelet.facebook.Utility
                    .initReqEnviroment(mContext);
            com.fihtdc.smartbracelet.facebook.Utility.mFacebook
                    .setTokenFromCache(
                            SessionStore.getCacheAccessToken(mContext),
                            SessionStore.getCacheExpires(mContext),
                            SessionStore.getCacheLastUpdate(mContext));
            com.fihtdc.smartbracelet.facebook.Utility.mAsyncRunner.request(
                    "me", params, new UserRequestListener());
        }
    }

    private void onDisplayLoging() {
        if (getActivity() != null && !isDetached()) {
            connectedStateLabel.setText(getResources().getString(
                    R.string.com_facebook_loading));
            mLoginButton
                    .setText(getString(R.string.com_facebook_loginview_log_out_button));
        }

    }

    /*
     * The Callback for notifying the application when authorization succeeds or
     * fails.
     */

    public class FbAPIsAuthListener implements AuthListener {
        @Override
        public void onAuthSucceed() {
            // fihtdc 2013/12/06 fly.f.ren update for fixed the user logo
            // display when logout begin
                if (Utility.getSharedPreferenceValue(mContext,
                        Constants.KEY_FACEBOOK_FEATURES, false)) {
                    mIsStartAuth = false;
                    requestUserData();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onDisplayLoging();

                        }
                    });
                }
            // fihtdc 2013/12/06 fly.f.ren update for fixed the user logo
            // display when logout end
        }

        @Override
        public void onAuthFail(String error) {
            mIsStartAuth = false;
            if (getActivity() == null || isDetached()) {
                return;
            }
            if (com.fihtdc.smartbracelet.util.Utility.getSharedPreferenceValue(
                    mContext,
                    com.fihtdc.smartbracelet.util.Utility.FB_ACCOUNT_NAME, "")
                    .equals("")) {
                onDisplayNoLog();
            }
        }
    }

    /*
     * The Callback for notifying the application when reouth starts and
     * finishes.
     */
    public class FbReouthListener implements ReOuthListener {
        @Override
        public void onReOuthStart() {
            mLoginButton.addSessionListener();
            SessionEvents.addAuthListener(mAuthListener);
            SessionEvents.addLogoutListener(mLogoutListener);
            if(!mIsStartAuth){
                mIsStartAuth = true;
                com.fihtdc.smartbracelet.facebook.Utility.mFacebook.authorize(
                        getActivity(), permissions, AUTHORIZE_ACTIVITY_RESULT_CODE,
                        new LoginDialogListener());
            }
           

        }

    }

    /*
     * The Callback for notifying the application when log out starts and
     * finishes.
     */
    public class FbAPIsLogoutListener implements LogoutListener {
        @Override
        public void onLogoutBegin() {
            Log.i("Fly", "FbAPIsLogoutListener onLogoutBegin");
            mIsHasSession = false;
            mIsStartAuth = false;
            Utility.setSharedPreferenceValue(mContext, Utility.FB_ACCOUNT_NAME,
                    "");
            Utility.setSharedPreferenceValue(mContext, Constants.KEY_CHECKIN,
                    false);
            Utility.setSharedPreferenceValue(mContext,
                    Constants.KEY_NOTIFICATION, false);
            Utility.setSharedPreferenceValue(mContext,
                    Constants.KEY_CHECKIN_TEXT, "");
            mCheckInSwitch.setChecked(false);
            mNotificationSwitch.setChecked(false);
            mCheckInMessage.setText("");
            // fihtdc fly.f.ren update for fixed the issue APPG2-5277
            // Utility.setSharedPreferenceValue(mContext,
            // Utility.FB_NOTIFICATION_COUNT, 0);
            if (getActivity() == null || isDetached()) {
                return;
            }
            onDisplayNoLog();
        }

        @Override
        public void onLogoutFinish() {
            // TODO Auto-generated method stub

        }

    }

    /*
     * Callback for fetching current user's name, picture, uid.
     */
    public class UserRequestListener extends BaseRequestListener {

        @Override
        public void onComplete(final String response, final Object state) {
            Log.i("Fly", "User data............."+response);
            boolean isLegal = com.fihtdc.smartbracelet.facebook.Utility
                    .checkRespLegitimate(response, mContext);
            if (!isLegal) {
                return;
            }
            JSONObject jsonObject;
            String name = null;
            try {
                jsonObject = new JSONObject(response);
                name = jsonObject.getString("name");
                com.fihtdc.smartbracelet.facebook.Utility.userUID = jsonObject
                        .getString("id");
            } catch (JSONException e) {

            }
            final String names = name;
            if (mIsHasSession) {
                Utility.setSharedPreferenceValue(mContext,
                        Utility.FB_ACCOUNT_NAME, name);
            }
            if (getActivity() == null || isDetached() || !mIsHasSession) {
                return;
            }
            if (com.fihtdc.smartbracelet.facebook.Utility.userUID != null
                    && !com.fihtdc.smartbracelet.facebook.Utility.userUID
                            .equals("")) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageRequest request = getImageRequest(com.fihtdc.smartbracelet.facebook.Utility.userUID);
                        if (request != null) {
                            // Do we already have the right picture? If so,
                            // leave it
                            // alone.
                            if (com.fihtdc.smartbracelet.facebook.Utility
                                    .isSessionValid(mContext)) {
                                connectedStateLabel.setText(names);
                                ImageDownloader.downloadAsync(request);
                            }
                        }
                    }
                });

            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        connectedStateLabel
                                .setText(getResources()
                                        .getString(
                                                R.string.com_facebook_usersettingsfragment_logged_in));

                    }
                });

            }
        }
    }

    private ImageRequest getImageRequest(final String uid) {
        ImageRequest request = null;
        ImageRequest.Builder requestBuilder = null;
        try {
            requestBuilder = new ImageRequest.Builder(
                    getActivity(),
                    ImageRequest
                            .getProfilePictureUrl(
                                    uid,
                                    getResources()
                                            .getDimensionPixelSize(
                                                    R.dimen.com_facebook_usersettingsfragment_profile_picture_width),
                                    getResources()
                                            .getDimensionPixelSize(
                                                    R.dimen.com_facebook_usersettingsfragment_profile_picture_height)));
            request = requestBuilder.setCallerTag(this)
                    .setCallback(new ImageRequest.Callback() {
                        @Override
                        public void onCompleted(ImageResponse response) {
                            // fly.f.ren 2013/08/21 add for fixed fc issue begin
                            if (getActivity() == null || isDetached() || !Utility.getSharedPreferenceValue(mContext,
                                    Constants.KEY_FACEBOOK_FEATURES, false)) {
                                return;
                            }
                            // fly.f.ren 2013/08/21 add for fixed fc issue end
                            processImageResponse(uid, response);
                        }
                    }).build();
        } catch (NotFoundException e) {
            Log.i("Fly", "NotFoundException");
        } catch (URISyntaxException e) {

        }

        return request;
    }

    private void processImageResponse(String id, ImageResponse response) {
        if (com.fihtdc.smartbracelet.facebook.Utility.isSessionValid(mContext)) {
            if (response != null) {
                mUserPicImageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    BitmapDrawable drawable = new BitmapDrawable(
                            FacebookSettingsFragment.this.getResources(),
                            bitmap);
                    drawable.setBounds(
                            0,
                            0,
                            getResources()
                                    .getDimensionPixelSize(
                                            R.dimen.com_facebook_usersettingsfragment_profile_picture_width),
                            getResources()
                                    .getDimensionPixelSize(
                                            R.dimen.com_facebook_usersettingsfragment_profile_picture_height));
                    userProfilePic = drawable;
                    mUserPicImageView.setImageDrawable(userProfilePic);
                }
            }
        }

    }

    private void initDisplay() {
        mFaceBookSettingLayout.setOnClickListener(this);
        mFaceBookLauncherLayout.setOnClickListener(this);
        mFaceBookNotificationLayout.setOnClickListener(this);
        mFaceBookCheckInLayout.setOnClickListener(this);
        mFaceBookCheckInTextLayout.setOnClickListener(this);
        mFaceBookSettingLayout
                .setBackgroundResource(R.drawable.bracelet_item_settings_single);
        mFaceBookLauncherLayout
                .setBackgroundResource(R.drawable.bracelet_item_settings_up);
        mFaceBookNotificationLayout
                .setBackgroundResource(R.drawable.bracelet_item_settings_middle);
        mFaceBookCheckInLayout
                .setBackgroundResource(R.drawable.bracelet_item_settings_middle);
        mFaceBookCheckInTextLayout
                .setBackgroundResource(R.drawable.bracelet_item_settings_down);
        if (!Utility.getSharedPreferenceValue(mContext,
                Constants.KEY_FACEBOOK_FEATURES, false)) {
            onCloseFBControlLayout();
            onCloseLoginStatusView();
        } else {
            onCloseFBFeatureTopLayoutChange();
            onOpenFBControlLayout();
        }
    }

    private void onOpenFBFeatureLayoutAnimation() {
        mFaceBookSettingLayout.setClickable(false);
        mFaceBookLauncherLayout.setClickable(false);
        mFacebookOpenTopSwitch.setClickable(false);
        mFacebookOpenSwitch.setClickable(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0,
                -mControlLinearLayout.getHeight(), 0);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translateAnimation);
        set.setDuration(TOTAL_DURATION);
        mControlLinearLayout.startAnimation(set);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onOpenFBNotiLayout();
            }
        }, 50);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onOpenFBCheckLayout();
            }
        }, 150);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onOpenFBCheckTextLayout();
            }
        }, 200);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onOpenLoginStatusView();
                mFaceBookSettingLayout.setClickable(true);
                mFaceBookLauncherLayout.setClickable(true);
                mFacebookOpenTopSwitch.setClickable(true);
                mFacebookOpenSwitch.setClickable(true);
            }
        }, TOTAL_DURATION);
    }

    private void onCloseFBFeatureLayoutAnimation() {
        // close feature animation
        onCloseLoginStatusView();
        mFaceBookSettingLayout.setClickable(false);
        mFaceBookLauncherLayout.setClickable(false);
        mFacebookOpenTopSwitch.setClickable(false);
        mFacebookOpenSwitch.setClickable(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0,
                -(mControlLinearLayout.getHeight() + mControlTopLinearLayout
                        .getHeight()));
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translateAnimation);
        set.setDuration(TOTAL_DURATION);
        mControlLinearLayout.startAnimation(set);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onCloseFBCheckTextLayout();
            }
        }, 50);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onCloseFBCheckLayout();
            }
        }, 150);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onCloseFBNotiLayout();
            }
        }, 200);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onCloseFBControlLayout();
                onOpenFBFeatureTopLayoutChange();
                mFaceBookSettingLayout.setClickable(true);
                mFaceBookLauncherLayout.setClickable(true);
                mFacebookOpenTopSwitch.setClickable(true);
                mFacebookOpenSwitch.setClickable(true);
            }
        }, TOTAL_DURATION);
    }

    private void initOtherLayout(View view) {
        mUserPicImageView = (ImageView) view
                .findViewById(R.id.facebook_user_pic);
        mUserPicImageView.setVisibility(View.INVISIBLE);
        mControlLinearLayout = (LinearLayout) view
                .findViewById(R.id.com_facebook_setting_linearlayout);
        mControlTopLinearLayout = (LinearLayout) view
                .findViewById(R.id.facebook_control_layout_top);
        mFaceBookCheckInTextLayout = (LinearLayout) view
                .findViewById(R.id.facebook_checkin_text_layout);
        mFaceBookSettingLayout = (LinearLayout) view
                .findViewById(R.id.facebook_setting_layout_top);
        mCheckInMessage = (EditText) view.findViewById(R.id.check_in_message);
        mCheckInMessage.setTextSize(16);
        mCheckInMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (null != arg0) {
                    Utility.setSharedPreferenceValue(mContext,
                            Constants.KEY_CHECKIN_TEXT, arg0.toString());
                }
            }
        });
        String checkInText = Utility.getSharedPreferenceValue(mContext,
                Constants.KEY_CHECKIN_TEXT, "");
        mCheckInMessage.setText(checkInText);
    }

    private void onCloseFBFeatureTopLayoutChange() {
        mControlTopLinearLayout.setVisibility(View.GONE);
    }

    private void onOpenFBControlLayout() {
        mFaceBookLauncherLayout.setVisibility(View.VISIBLE);
        mControlLinearLayout.setVisibility(View.VISIBLE);
        mFacebookOpenSwitch.setChecked(true);
    }

    private void onOpenFBNotiLayout() {
        mFaceBookNotificationLayout.setVisibility(View.VISIBLE);
    }

    private void onOpenFBCheckLayout() {
        mFaceBookCheckInLayout.setVisibility(View.VISIBLE);
    }

    private void onOpenFBCheckTextLayout() {
        mFaceBookCheckInTextLayout.setVisibility(View.VISIBLE);
    }

    private void onCloseFBNotiLayout() {
        mFaceBookNotificationLayout.setVisibility(View.GONE);
    }

    private void onCloseFBCheckLayout() {
        mFaceBookCheckInLayout.setVisibility(View.GONE);
    }

    private void onCloseFBCheckTextLayout() {
        mFaceBookCheckInTextLayout.setVisibility(View.GONE);
    }

    private void onCloseFBControlLayout() {
        mFaceBookLauncherLayout.setVisibility(View.GONE);
        mControlLinearLayout.setVisibility(View.GONE);
    }

    protected void onCloseLoginStatusView() {
        mLoginButton.setVisibility(View.INVISIBLE);
        connectedStateLabel.setVisibility(View.INVISIBLE);
        mUserPicImageView.setVisibility(View.INVISIBLE);

    }

    protected void onOpenLoginStatusView() {
        mLoginButton.setVisibility(View.VISIBLE);
        connectedStateLabel.setVisibility(View.VISIBLE);
    }

    private void onOpenFBFeatureTopLayoutChange() {
        mControlTopLinearLayout.setVisibility(View.VISIBLE);
    }

    private void initFaceBookLauncherLayoutTop(View view) {
        mFaceBookSettingLayout = (LinearLayout) view
                .findViewById(R.id.facebook_setting_layout_top);
        ImageView imgView = (ImageView) mFaceBookSettingLayout
                .findViewById(R.id.item_icon);
        imgView.setVisibility(View.GONE);
        TextView tview = (TextView) mFaceBookSettingLayout
                .findViewById(R.id.titleTxt);
        tview.setText(R.string.facebook_features);
        ImageView myview = (ImageView) mFaceBookSettingLayout
                .findViewById(R.id.item_forword);
        myview.setVisibility(View.GONE);
        mFacebookOpenTopSwitch = (Switch) mFaceBookSettingLayout
                .findViewById(R.id.switched);
        mFacebookOpenTopSwitch.setClickable(true);
        mFacebookOpenTopSwitch
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                            boolean arg1) {
                        if (arg1) {
                            Utility.setSharedPreferenceValue(mContext,
                                    Constants.KEY_FACEBOOK_FEATURES, true);
                            mHandler.removeMessages(DELAY_ANIM_MESSAGE);
                            mHandler.sendEmptyMessageDelayed(
                                    DELAY_ANIM_MESSAGE, 100);
                        }

                    }
                });
    }

    private void hiddenSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCheckInMessage.getWindowToken(), 0);

    }

    private void initFaceBookLauncherLayout(View view) {
        mFaceBookLauncherLayout = (LinearLayout) view
                .findViewById(R.id.facebook_setting_layout);
        ImageView imgView = (ImageView) mFaceBookLauncherLayout
                .findViewById(R.id.item_icon);
        imgView.setVisibility(View.GONE);
        TextView tview = (TextView) mFaceBookLauncherLayout
                .findViewById(R.id.titleTxt);
        tview.setText(R.string.facebook_features);
        ImageView myview = (ImageView) mFaceBookLauncherLayout
                .findViewById(R.id.item_forword);
        myview.setVisibility(View.GONE);
        mFacebookOpenSwitch = (Switch) mFaceBookLauncherLayout
                .findViewById(R.id.switched);
        mFacebookOpenSwitch.setClickable(true);
        mFacebookOpenSwitch
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                            boolean arg1) {
                        if (!arg1) {
                            // fihtdc 2013/12/06 fly.f.ren update for fixed the
                            // user logo display when logout begin
                                mFacebookOpenTopSwitch.setChecked(false);
                                onCloseFBFeatureLayoutAnimation();
                                Utility.setSharedPreferenceValue(mContext,
                                        Constants.KEY_FACEBOOK_FEATURES, false);
                                mLoginButton.logout();
                                hiddenSoftKeyBoard();
                            // fihtdc 2013/12/06 fly.f.ren update for fixed the
                            // user logo display when logout end
                        }
                    }
                });
    }

    private void initFaceBookNotificationLayout(View view) {
        mFaceBookNotificationLayout = (LinearLayout) view
                .findViewById(R.id.facebook_notification_layout);
        ImageView imgView = (ImageView) mFaceBookNotificationLayout
                .findViewById(R.id.item_icon);
        imgView.setVisibility(View.GONE);
        TextView tview = (TextView) mFaceBookNotificationLayout
                .findViewById(R.id.titleTxt);
        tview.setText(R.string.facebook_notification);
        ImageView myview = (ImageView) mFaceBookNotificationLayout
                .findViewById(R.id.item_forword);
        myview.setVisibility(View.GONE);
        final Switch switchView = (Switch) mFaceBookNotificationLayout
                .findViewById(R.id.switched);
        mNotificationSwitch = switchView;
        boolean isOpen = Utility.getSharedPreferenceValue(mContext,
                Constants.KEY_NOTIFICATION, false);
        if (isOpen) {
            switchView.setChecked(true);
        } else {
            switchView.setChecked(false);
        }
        switchView.setClickable(true);
        switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    if (mIsHasSession) {
                        Utility.setSharedPreferenceValue(mContext,
                                Constants.KEY_NOTIFICATION, true);
                        openNotification();
                    } else {
                        SmartToast
                                .makeText(
                                        mContext,
                                        mContext.getString(R.string.com_facebook_loginview_log_in_prompt_message),
                                        Toast.LENGTH_LONG).show();
                        switchView.setChecked(false);
                    }
                } else {
                    Utility.setSharedPreferenceValue(mContext,
                            Constants.KEY_NOTIFICATION, false);
                    // fihtdc fly.f.ren 2013/10/08 update for fixed APPG2-5277
                    // issue
                    // Utility.setSharedPreferenceValue(mContext,
                    // Utility.FB_NOTIFICATION_COUNT, 0);
                    closeNotification();
                }

            }

        });
    }

    private void closeNotification() {
        Intent intent = new Intent();
        intent.setAction(Constants.FACEBOOK_NOTIFICATION_CLOSE_ACTION);
        mContext.startService(intent);

    }

    private void openNotification() {
        Intent intent = new Intent();
        intent.setAction(Constants.FACEBOOK_NOTIFICATION_ACTION);
        mContext.startService(intent);
    }

    private void initFaceBookCheckInLayout(View view) {
        mFaceBookCheckInLayout = (LinearLayout) view
                .findViewById(R.id.facebook_check_in_layout);
        ImageView imgView = (ImageView) mFaceBookCheckInLayout
                .findViewById(R.id.item_icon);
        imgView.setVisibility(View.GONE);
        TextView tview = (TextView) mFaceBookCheckInLayout
                .findViewById(R.id.titleTxt);
        tview.setText(R.string.facebook_checkin);
        ImageView myview = (ImageView) mFaceBookCheckInLayout
                .findViewById(R.id.item_forword);
        myview.setVisibility(View.GONE);
        final Switch switchView = (Switch) mFaceBookCheckInLayout
                .findViewById(R.id.switched);
        mCheckInSwitch = switchView;
        switchView.setClickable(true);
        boolean isOpen = Utility.getSharedPreferenceValue(mContext,
                Constants.KEY_CHECKIN, false);
        if (isOpen) {
            switchView.setChecked(true);
        } else {
            switchView.setChecked(false);
        }
        switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    if (mIsHasSession) {
                        // to do server
                        Utility.setSharedPreferenceValue(mContext,
                                Constants.KEY_CHECKIN, true);
                    } else {
                        SmartToast
                                .makeText(
                                        mContext,
                                        mContext.getString(R.string.com_facebook_loginview_log_in_prompt_message),
                                        Toast.LENGTH_LONG).show();
                        switchView.setChecked(false);
                    }
                } else {
                    Utility.setSharedPreferenceValue(mContext,
                            Constants.KEY_CHECKIN, false);
                }

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setRetainInstance(true);
    }

    /**
     * @throws com.facebook.FacebookException
     *             if errors occur during the loading of user information
     */
    @Override
    public void onResume() {
        super.onResume();
        // fetchUserInfo();
        // updateUI();
    }

    /**
     * Sets the callback interface that will be called whenever the status of
     * the Session associated with this LoginButton changes.
     * 
     * @param callback
     *            the callback interface
     */
    public void setSessionStatusCallback(Session.StatusCallback callback) {
        this.sessionStatusCallback = callback;
    }

    /**
     * Sets the callback interface that will be called whenever the status of
     * the Session associated with this LoginButton changes.
     * 
     * @return the callback interface
     */
    public Session.StatusCallback getSessionStatusCallback() {
        return sessionStatusCallback;
    }

    @Override
    protected void onSessionStateChange(SessionState state, Exception exception) {
        if (sessionStatusCallback != null) {
            sessionStatusCallback.call(getSession(), state, exception);
        }
    }

    private void updateValue(Switch switchBox) {
        boolean flag = switchBox.isChecked();
        switchBox.setChecked(!flag);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
        case R.id.facebook_setting_layout:
            updateValue(mFacebookOpenSwitch);
            break;
        case R.id.facebook_setting_layout_top:
            updateValue(mFacebookOpenTopSwitch);
            break;
        case R.id.facebook_check_in_layout:
            updateValue(mCheckInSwitch);
            break;
        case R.id.facebook_notification_layout:
            updateValue(mNotificationSwitch);
            break;
        default:
            break;
        }

    }

}
