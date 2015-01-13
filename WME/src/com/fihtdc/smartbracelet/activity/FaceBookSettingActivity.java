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

package com.fihtdc.smartbracelet.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.FacebookSettingsFragment;

public class FaceBookSettingActivity extends FragmentActivity implements OnClickListener {
	 private FacebookSettingsFragment userSettingsFragment;
	 private View mActionBarCustomView;
	    public TextView mTitle;
	    public ImageView mLeft;
	    public ImageView mRight;
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.login_fragment_activity);
	        FragmentManager fragmentManager = getSupportFragmentManager();
	        userSettingsFragment = (FacebookSettingsFragment) fragmentManager.findFragmentById(R.id.login_fragment);
	        mActionBarCustomView = getLayoutInflater().inflate(R.layout.action_bar_custom, null);
	        final ActionBar bar = getActionBar();
	        bar.setCustomView(mActionBarCustomView,
	                new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	        userSettingsFragment.setSessionStatusCallback(new Session.StatusCallback() {
	            @Override
	            public void call(Session session, SessionState state, Exception exception) {
	                Log.d("LoginUsingLoginFragmentActivity", String.format("New session state: %s", state.toString()));
	            }
	        });
	        mLeft = (ImageView)mActionBarCustomView.findViewById(R.id.left);
	        mTitle = (TextView)mActionBarCustomView.findViewById(R.id.middle);
	        mRight = (ImageView)mActionBarCustomView.findViewById(R.id.right);
	        mLeft.setImageResource(R.drawable.ic_menu_back);
	        mRight.setImageResource(R.drawable.ic_menu_home);
	        mLeft.setOnClickListener(this);
	        mRight.setOnClickListener(this);
	        mTitle.setText(getTitle());
	    }

	    @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        userSettingsFragment.onActivityResult(requestCode, resultCode, data);
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	
	    @Override
	    public void setTitle(int titleId) {
	        super.setTitle(titleId);
	        mTitle.setText(getTitle());
	    }

	    @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	        case R.id.left:
	            onClickLeft();
	            break;
	        case R.id.right:
	            onClickRight();
	            break;
	        default:
	            break;
	        }
	    }
	    
	    //Click left image view on action bar
	    public void onClickLeft(){
	        finish();
	    }
	    
	    //Click right image view on action bar
	    public void onClickRight(){
	    	setResult(RESULT_OK);
	        finish();
	    }


}
