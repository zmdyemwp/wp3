package com.fihtdc.smartbracelet.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.entity.MeasureResult;
import com.fihtdc.smartbracelet.entity.UserInfo;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Profile;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;

public class SuggestionActivity extends CustomActionBarActivity {
    private static final String TAG = "SuggestionActivity";

    int mType;
    int mSuggestionType;
    int mScore;
    
    boolean mIsGuest;
    UserInfo mUserInfo;

    TextView mSuggestionTypeText;
    TextView mScoreText;
    TextView mSuggestionText;

    MeasureResult mMeasureResult;
    int mActualAge = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion);
        mSuggestionTypeText = (TextView) findViewById(R.id.suggestionType);
        mScoreText = (TextView) findViewById(R.id.score);
        mSuggestionText = (TextView) findViewById(R.id.suggestion);

        mLeft.setImageResource(R.drawable.ic_menu_back);

        Intent intent = getIntent();
        if (intent != null) {
            mType = intent.getIntExtra(Constants.TYPE_EXTRA, Constants.TYPE_MEASURE);
            mSuggestionType = intent.getIntExtra(Constants.SUGGESTION_TYPE_EXTRA,
                    Constants.SUGGESTION_TYPE_AGILITY);
            mMeasureResult = intent.getParcelableExtra(Constants.MEASURE_RESULT_EXTRA);
            mIsGuest = intent.getBooleanExtra(Constants.IS_GUEST_EXTRA, false);
            mUserInfo = intent.getParcelableExtra(Constants.USE_INFO_EXTRA);
        }

        
        if (!mIsGuest) {
            mActualAge = getActualAge();
        } else {
            if (mUserInfo != null){
                mActualAge = mUserInfo.getAge();
            }
        }
        
        View summaryLayout = findViewById(R.id.suggestion_layout);
        if (Constants.TYPE_COACHING == mType) {
            // setTitle(R.string.coaching_title);
            summaryLayout.setBackgroundResource(R.drawable.health_coach_bg);
        } else {
            summaryLayout.setBackgroundResource(R.drawable.health_measure_bg_02);
            // setTitle(R.string.measure_title);
        }

        if (Constants.SUGGESTION_TYPE_AGILITY == mSuggestionType) {
            mSuggestionTypeText.setText(R.string.agility_label);
            if (mMeasureResult != null) {
                mScoreText.setText(String.valueOf(Utility.transformAgility(mMeasureResult
                        .getAgility())));
                mSuggestionText.setText(getAgilitySuggestion());
            }
        } else if (Constants.SUGGESTION_TYPE_ANS_AGE == mSuggestionType) {
            mSuggestionTypeText.setText(R.string.ans_age_label);
            if (mMeasureResult != null) {
                mScoreText.setText(String.valueOf(mMeasureResult.getAnsAge()));
                mSuggestionText.setText(getAnsSuggestion());
            }
        } else if (Constants.SUGGESTION_TYPE_BPM == mSuggestionType) {
            mSuggestionTypeText.setText(R.string.bpm_label);
            if (mMeasureResult != null) {
                mScoreText.setText(String.valueOf(mMeasureResult.getBpm()));
                mSuggestionText.setText(getBpmSuggestion());
            }
        }

    }

    @Override
    public void onClickLeft() {
        onBackPressed();
    }
    
    @Override
    public void onClickRight() {
        
    }

    private int getActualAge() {
        int age = 0;
        Cursor cursor = getContentResolver().query(Profile.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String birthday = cursor.getString(cursor
                        .getColumnIndex(Profile.COLUMN_NAME_BIRTHDAY));
                age = Math.max(Utility.getAge(birthday), 0);
            }
            cursor.close();
        }

        return age;
    }

    private String getAgilitySuggestion() {
        int aglityScore = Utility.transformAgility(mMeasureResult.getAgility());
        int ans = mMeasureResult.getAnsAge();
        int bpm = mMeasureResult.getBpm();
        return Utility.getAgilityDescrption(this, aglityScore, ans, mActualAge, bpm);
    }

    private String getAnsSuggestion() {
        int ans = mMeasureResult.getAnsAge();
        return Utility.getANSDescrption(this, ans, mActualAge);
    }

    public String getBpmSuggestion() {
        int bpm = mMeasureResult.getBpm();
        return Utility.getBPMDescrption(this);
    }
}
