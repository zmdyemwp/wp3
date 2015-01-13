package com.fihtdc.smartbracelet.activity;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.entity.CoachingResult;
import com.fihtdc.smartbracelet.entity.MeasureResult;
import com.fihtdc.smartbracelet.entity.UserInfo;
import com.fihtdc.smartbracelet.facebook.Share;
import com.fihtdc.smartbracelet.fragment.CoachingCompleteFragment;
import com.fihtdc.smartbracelet.fragment.SummaryFragment;
import com.fihtdc.smartbracelet.provider.BraceletInfo;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Coaching;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Measure;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Profile;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;
import com.yl.ekgrr.EkgRR;

public class SummaryActivity extends Activity implements OnClickListener {
    private static final String TAG = "SummaryActivity";
	protected static final int PROFILE_MESSAGE_LOAD_FINISH = 100;
    private Context mContext;
    private int mType;
    private boolean mIsGuest;
    private UserInfo mUserInfo;
    private boolean mIsCoach;
    private boolean mIsMale;
    private int mAge;
    MeasureResult mMeasureResult = new MeasureResult();
    CoachingResult mCoachingResult = new CoachingResult();

    SummaryFragment mSummaryFragment;
    CoachingCompleteFragment mCoachingCompleteFragment;

    private String mLevelName = null;
    private int mCycleTime = 0;

    ImageView mFacebookButton;
    TextView mTitle;
    private int mDuration;
    private int mHit;

    int mVisbileFragmentIndex;
    
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case PROFILE_MESSAGE_LOAD_FINISH:
				onLoadFragment();
				break;
			default:
				break;
			}
			
		}
    	
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stack);
        mContext = this;
        View actionBarCustomView = getLayoutInflater().inflate(R.layout.summary_action_bar, null);
        final ActionBar bar = getActionBar();
        bar.setCustomView(actionBarCustomView, new ActionBar.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        mFacebookButton = (ImageView) actionBarCustomView.findViewById(R.id.facebook);
        mTitle = (TextView) actionBarCustomView.findViewById(R.id.middle);
        mTitle.setText(getTitle());

        Intent intent = getIntent();
        mType = intent.getIntExtra(Constants.TYPE_EXTRA, Constants.TYPE_MEASURE);
        mIsGuest = intent.getBooleanExtra(Constants.IS_GUEST_EXTRA, false);
        mDuration = intent.getIntExtra(Constants.KEY_MEASURE_TIME, 180);
        
        if (savedInstanceState == null) {
            // according to measure or coaching,load different fragment
            if (Constants.TYPE_COACHING == mType) {
                mIsCoach = true;
                mLevelName = intent.getStringExtra(Constants.KEY_LEVEL_CLASS);
                mHit = intent.getIntExtra(Constants.KEY_HIT_TIMES, 0);
                Log.i("Fly", "mLevelName ===" + mLevelName);
                mCycleTime = intent.getIntExtra(Constants.KEY_CYCLE_TIME, 0);
                getLoaderManager().initLoader(0, null, mProfileCursor);
            } else {
                if (mIsGuest) {
                    mUserInfo = intent.getParcelableExtra(Constants.USE_INFO_EXTRA);
                    mIsMale = getIsGender(mUserInfo.getGender());
                    mAge = mUserInfo.getAge();
                    onHandleMeasureData();
                    enterSummaryFragment();
                    onFragmentSetDataBundle();
                }else{
                	  getLoaderManager().initLoader(0, null, mProfileCursor);
                }
            }
            
        } else {
            mSummaryFragment = (SummaryFragment) getFragmentManager().findFragmentByTag(
                    "SummaryFragment");
            if (mSummaryFragment == null) {
                mSummaryFragment = new SummaryFragment();
            }
            if (mSummaryFragment != null) {
                mMeasureResult = savedInstanceState.getParcelable(Constants.MEASURE_RESULT_EXTRA);
                mSummaryFragment.setSummaryResult(savedInstanceState);
                // Robin added,add "return" may be better
                return;
            }
            mCoachingCompleteFragment = (CoachingCompleteFragment) getFragmentManager()
                    .findFragmentByTag("CoachingCompleteFragment");
            if (mCoachingCompleteFragment != null) {
                mCoachingResult = savedInstanceState.getParcelable(Constants.COACHING_RESULT_EXTRA);
                mCoachingCompleteFragment.setSummaryResult(savedInstanceState);
            }
        }
        if (Share.getShareInstance(this).isSupportFBShare() && com.fihtdc.smartbracelet.util.Utility
				.getSharedPreferenceValue(getApplicationContext(),
						Constants.KEY_FACEBOOK_FEATURES, false)) {
            mFacebookButton.setVisibility(View.VISIBLE);
        } else {
            mFacebookButton.setVisibility(View.GONE);
        }

    }

    private void onLoadFragment() {
    	  onHandleMeasureData();
    	  if (Constants.TYPE_COACHING == mType) {
    		  enterCoachingCompleteFragment(mLevelName);
    	  }else{
    		  enterSummaryFragment();   
    	  }
    	  onFragmentSetDataBundle();
		
	}
    private LoaderCallbacks<Cursor> mProfileCursor = new LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            Uri uri = BraceletInfo.Profile.CONTENT_URI;
            return new CursorLoader(SummaryActivity.this, uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
            if (cursor != null && cursor.moveToFirst()) {
                String birthdayValueStr = cursor.getString(cursor
                        .getColumnIndex(Profile.COLUMN_NAME_BIRTHDAY));
                int gender = cursor.getInt(cursor
                        .getColumnIndex(Profile.COLUMN_NAME_PROFILE_GENDER));
                mAge = Math.max(Utility.getAge(birthdayValueStr), 0);
                mIsMale = getIsGender(gender);
                mHandler.removeMessages(PROFILE_MESSAGE_LOAD_FINISH);
                mHandler.sendEmptyMessage(PROFILE_MESSAGE_LOAD_FINISH);
               
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            // TODO Auto-generated method stub

        }

    };

    private boolean getIsGender(int gender) {
        if (Profile.GENDER_MALE == gender) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogApp.Logd("Fly", "onSaveInstanceState Save outStatus");
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.MEASURE_RESULT_EXTRA, mMeasureResult);
        outState.putParcelable(Constants.COACHING_RESULT_EXTRA, mCoachingResult);
    }

    private void enterCoachingCompleteFragment(String string) {
        mCoachingCompleteFragment = (CoachingCompleteFragment) getFragmentManager()
                .findFragmentByTag("CoachingCompleteFragment");
        if (mCoachingCompleteFragment == null) {
            mCoachingCompleteFragment = new CoachingCompleteFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_LEVEL_CLASS, string);
        mCoachingCompleteFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.single_fragment, mCoachingCompleteFragment, "CoachingCompleteFragment");
        ft.commit();
    }

    private void enterSummaryFragment() {
        mSummaryFragment = (SummaryFragment) getFragmentManager().findFragmentByTag(
                "SummaryFragment");
        if (mSummaryFragment == null) {
            mSummaryFragment = new SummaryFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.TYPE_EXTRA, Constants.TYPE_MEASURE);
        if (mIsGuest) {
            bundle.putBoolean(Constants.IS_GUEST_EXTRA, true);
            bundle.putParcelable(Constants.USE_INFO_EXTRA, mUserInfo);
        }
        mSummaryFragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.single_fragment, mSummaryFragment, "SummaryFragment");
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left:
            onBackPressed();
            break;
        case R.id.right:
            Utility.shareScreenhot(this);
            break;
        case R.id.facebook:
            startFacebookShare();
            break;

        default:
            break;
        }
    }

    /**
     * @author F3060326 at 2013/9/11
     * @return void
     * @param
     * @Description: May be should send many item
     */
    private void startFacebookShare() {
        Bundle bundle = new Bundle();
        switch (mVisbileFragmentIndex) {
        case Constants.TYPE_MEASURE:
            bundle.putInt(Constants.TYPE_EXTRA, Constants.TYPE_MEASURE);
            bundle.putParcelable(Constants.MEASURE_RESULT_EXTRA, mMeasureResult);
            break;
        case Constants.TYPE_COACHING:
            bundle.putInt(Constants.TYPE_EXTRA, Constants.TYPE_COACHING);
            bundle.putParcelable(Constants.COACHING_RESULT_EXTRA, mCoachingResult);
            break;

        default:
            break;
        }

        Intent intent = new Intent(this,
                com.fihtdc.smartbracelet.activity.FacebookShareActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            getFragmentManager().popBackStack();
            removeCurrentFragment();
        }
    }

    public void removeCurrentFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment currentFrag = getFragmentManager().findFragmentById(R.id.single_fragment);
        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commit();
    }

    private void onHandleMeasureData() {
        LogApp.Logd("Fly", "mIsMale: " + mIsMale);
        LogApp.Logd("Fly", "mAge: " + mAge);
        LogApp.Logd("Fly", "mDuration: " + mDuration);
        LogApp.Logd("Fly", "mIsCoach: " + mIsCoach);
        EkgRR.doMeasureResult(mIsMale, mAge, mDuration, mIsCoach);
        getMeasureResult();
        String name = Utility.getCurrentProfileName(mContext);
        ContentValues values = getContentValues(name);
        if (TextUtils.isEmpty(name)) {
            // Should never happen
            LogApp.Loge(TAG, "Profile name is empty");
            return;
        }
        if (mIsCoach) {
            values.put(Coaching.COLUMN_NAME_LEVEL, mLevelName);
            values.put(Coaching.COLUMN_NAME_HIT_NUM, mHit);
            values.put(Coaching.COLUMN_NAME_CYCLE, mCycleTime);
            getContentResolver().insert(Coaching.CONTENT_URI, values);
            //mCoachingResult.setMeasureResult(mMeasureResult);
            mCoachingResult.setLevelName(mLevelName);
            mCoachingResult.setCycle(mCycleTime);
            mCoachingResult.setHit(mHit);
        } else {
        	 if (!mIsGuest) {
                  // If is guest, not need to save measure data
                  getContentResolver().insert(Measure.CONTENT_URI, values);
             }
        }
    }

    private void onFragmentSetDataBundle(){
    	Bundle bundle = new Bundle();
    	if(mIsCoach){
    		   bundle.putParcelable(Constants.COACHING_RESULT_EXTRA, mCoachingResult);
               if (mCoachingCompleteFragment != null) {
                   mCoachingCompleteFragment.setSummaryResult(bundle);
               }	
    	}else{
    		  bundle.putParcelable(Constants.MEASURE_RESULT_EXTRA, mMeasureResult);
              if (mSummaryFragment != null) {
                  mSummaryFragment.setSummaryResult(bundle);
              }
    	}
    }
    
    public void onDisplaySummaryContent(SummaryFragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.TYPE_EXTRA, Constants.TYPE_COACHING);
        bundle.putParcelable(Constants.MEASURE_RESULT_EXTRA, mMeasureResult);
        fragment.setSummaryResult(bundle);
    }

    private ContentValues getContentValues(String name) {
        ContentValues values = new ContentValues();
        values.put(Measure.COLUMN_NAME_BPM, mMeasureResult.getBpm());
        values.put(Measure.COLUMN_NAME_ANS_AGE, mMeasureResult.getAnsAge());
        values.put(Measure.COLUMN_NAME_AGILITY, mMeasureResult.getAgility());
        values.put(Measure.COLUMN_NAME_SDNN, mMeasureResult.getSdnn());
        values.put(Measure.COLUMN_NAME_AGE_HF, mMeasureResult.getAgeHF());
        values.put(Measure.COLUMN_NAME_AGE_LF, mMeasureResult.getAgeLF());
        values.put(Measure.COLUMN_NAME_EMOTION_STATUS, mMeasureResult.getEmotion());
        values.put(Coaching.COLUMN_NAME_PROFILE_NAME, name);
        values.put(Coaching.COLUMN_NAME_TEST_TIME, System.currentTimeMillis());
        return values;
    }

    private MeasureResult getMeasureResult() {
        int agility = EkgRR.getResult_Agility();
        int ansAge = EkgRR.getResult_ANS_Age();
        int bpm = EkgRR.getResult_BPM();
        int sdnn = EkgRR.getResult_SDNN();
        float VLF = EkgRR.getResult_VLF();//
        float LF = EkgRR.getResult_LF();
        float HF = EkgRR.getResult_HF();
        float agedLF = EkgRR.getAlignLF(LF, mIsMale, mAge);
        float agedHF = EkgRR.getAlignHF(HF, mIsMale, mAge);
        int emotion = EkgRR.getEmotionStatus(agedLF, agedHF);
        mMeasureResult.setEmotion(emotion);
        mMeasureResult.setAgeHF(agedHF);
        mMeasureResult.setAgeLF(agedLF);
        mMeasureResult.setAgility(agility);
        mMeasureResult.setAnsAge(ansAge);
        mMeasureResult.setBpm(bpm);
        mMeasureResult.setSdnn(sdnn);
        LogApp.Logd("Fly", "LF: " + LF);
        LogApp.Logd("Fly", "HF: " + HF);
        LogApp.Logd("Fly", "aged LF: " + agedLF);
        LogApp.Logd("Fly", "aged HF: " + agedHF);
        LogApp.Logd("Fly", "agility====" + agility);
        LogApp.Logd("Fly", "ansAge====" + ansAge);
        LogApp.Logd("Fly", "agility====" + agility);
        LogApp.Logd("Fly", "ansAge====" + ansAge);
        LogApp.Logd("Fly", "mBMP====" + bpm);
        LogApp.Logd("Fly", "emotion====" + emotion);
        return mMeasureResult;
    }

    public void setVisibleFragmentIndex(int index) {
        mVisbileFragmentIndex = index;
    }

}
