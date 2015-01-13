package com.fihtdc.smartbracelet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.SuggestionActivity;
import com.fihtdc.smartbracelet.activity.SummaryActivity;
import com.fihtdc.smartbracelet.entity.MeasureResult;
import com.fihtdc.smartbracelet.entity.UserInfo;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.MStatusView;

public class SummaryFragment extends StatisticsBaseFragment implements View.OnClickListener{
    LinearLayout mAnsAgeLayout;
    LinearLayout mAgilityLayout;
    LinearLayout mBpmLayout;
    TextView mAgilityText;
    TextView mAnsAgeText;
    TextView mBPMText;
    int mAgilityValue;
    int mAnsAgeValue;
    int mBPMValue;
    int mType;
    
    boolean mIsGuest;
    UserInfo mUserInfo;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(Constants.TYPE_EXTRA, Constants.TYPE_MEASURE);
            mIsGuest = bundle.getBoolean(Constants.IS_GUEST_EXTRA, false);
            mUserInfo = bundle.getParcelable(Constants.USE_INFO_EXTRA);
        }

    }

    @Override
	public void onActivityGlobalLayout() {
		
		super.onActivityGlobalLayout();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary, null);
    }

    public MStatusView getMStatusView(){
    	return mStatusView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
        SummaryActivity activity = (SummaryActivity)getActivity();
        activity.setVisibleFragmentIndex(Constants.TYPE_MEASURE);
    	
        View summaryLayout = view.findViewById(R.id.summary_layout);
        if (Constants.TYPE_COACHING == mType) {
            summaryLayout.setBackgroundResource(R.drawable.health_coach_bg);
        } else {
            summaryLayout.setBackgroundResource(R.drawable.health_measure_bg_02);
        }
        mAnsAgeLayout = (LinearLayout)view.findViewById(R.id.ans_age_layout);
        mAnsAgeLayout.setOnClickListener(this);
        
        mAgilityLayout = (LinearLayout)view.findViewById(R.id.agility_layout);
        mAgilityLayout.setOnClickListener(this);
        
        mBpmLayout = (LinearLayout)view.findViewById(R.id.bpm_layout);
        mBpmLayout.setOnClickListener(this);
        
        mAgilityText = (TextView)view.findViewById(R.id.agility_value);
        mAnsAgeText = (TextView)view.findViewById(R.id.ans_age_value);
        mBPMText= (TextView)view.findViewById(R.id.bpm_value);
        
        mAgilityText.setText(String.valueOf(Utility.transformAgility(mAgilityValue)));

        mAnsAgeText.setText(String.valueOf(mAnsAgeValue));

        mBPMText.setText(String.valueOf(mBPMValue));
      
        if (mMeasureResult == null) {
            mMeasureResult = new MeasureResult();
            mMeasureResult.setAgility(mAgilityValue);
            mMeasureResult.setAnsAge(mAnsAgeValue);
            mMeasureResult.setBpm(mBPMValue);
        }
        
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.agility_layout:
        case R.id.ans_age_layout:
        case R.id.bpm_layout:
            startSugguestionActivity(v.getId());
            break;

        default:
            break;
        }
    }
    
    private void startSugguestionActivity(int id){
        int suggestionType;
        switch (id) {
        case R.id.agility_layout:
            suggestionType = Constants.SUGGESTION_TYPE_AGILITY;
            break;
        case R.id.ans_age_layout:
            suggestionType = Constants.SUGGESTION_TYPE_ANS_AGE;
            break;
        case R.id.bpm_layout:
            suggestionType = Constants.SUGGESTION_TYPE_BPM;
            break;

        default:
            return;
        }
        
        Intent intent = new Intent();
        intent.setClass(getActivity(), SuggestionActivity.class);
        intent.putExtra(Constants.TYPE_EXTRA, mType);
        intent.putExtra(Constants.SUGGESTION_TYPE_EXTRA, suggestionType);
        intent.putExtra(Constants.MEASURE_RESULT_EXTRA, mMeasureResult);
        if (mIsGuest) {
            intent.putExtra(Constants.IS_GUEST_EXTRA, true);
            intent.putExtra(Constants.USE_INFO_EXTRA, mUserInfo);
        }
        startActivity(intent);
    }
    
    public void setSummaryResult(Bundle bundle){
    	LogApp.Logd("Fly","setSummaryResult(Bundle bundle)");
        if (bundle != null) {
          	LogApp.Logd("Fly","Bundle is not null");
            mMeasureResult = bundle.getParcelable(Constants.MEASURE_RESULT_EXTRA);
            if (mMeasureResult != null) {
            	LogApp.Logd("Fly"," mMeasureResult  is not null   mAgilityValue ====="+  mMeasureResult.getAgility());
                mAgilityValue = mMeasureResult.getAgility();
                if (mAgilityText != null) {
                    mAgilityText.setText(String.valueOf(Utility.transformAgility(mAgilityValue)));
                    mMeasureResult.setAgility(mAgilityValue);
                }
               
                mAnsAgeValue = mMeasureResult.getAnsAge();
                if (mAnsAgeText != null) {
                    mAnsAgeText.setText(String.valueOf(mAnsAgeValue));
                }

                mBPMValue = mMeasureResult.getBpm();
                if (mBPMText != null) {
                    mBPMText.setText(String.valueOf(mBPMValue));
                }
            }
        }
    }
}
