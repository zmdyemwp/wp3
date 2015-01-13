package com.fihtdc.smartbracelet.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.GuestProfileActivity;
import com.fihtdc.smartbracelet.activity.MeasureActivity;
import com.fihtdc.smartbracelet.entity.UserInfo;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Profile;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.wheel.OnWheelScrollListener;
import com.fihtdc.smartbracelet.wheel.WheelView;
import com.fihtdc.smartbracelet.wheel.adapters.ArrayWheelAdapter;
import com.fihtdc.smartbracelet.wheel.adapters.NumericWheelAdapter;

public class GuestProfileFragment extends CommonFragment implements View.OnClickListener,
        OnWheelScrollListener {
    private Context mContext;
    private GuestProfileActivity mActivity;
    private static final int MIN_AGE = 10;
    private static final int MAX_AGE = 99;
    private static final int CURRENT_AGE = 30;
    
    WheelView mGenderWheel;
    WheelView mAgeWheel;
    
    TextView mGender;
    TextView mAge;
    
    String[] mGenderStrings;
    String[] mAgeStrings;
    
    int mGenderValue;
    int mAgeValue;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = (GuestProfileActivity)getActivity();
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.guest_profile, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.start).setOnClickListener(this);
        mGender = (TextView)view.findViewById(R.id.gender);
        mAge = (TextView)view.findViewById(R.id.age);
        mGenderWheel = (WheelView)view.findViewById(R.id.genderWheel);
        mAgeWheel = (WheelView)view.findViewById(R.id.ageWheel);
        initView();
    }
    
    private void initView(){
        mGenderStrings = mContext.getResources()
                .getStringArray(R.array.profile_gender);
        mGenderWheel.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mGenderStrings));
        mGenderWheel.setCurrentItem(0);
        mGenderWheel.addScrollingListener(this);
        mGender.setText(mGenderStrings[0]);
        
        mAgeWheel.setViewAdapter(new NumericWheelAdapter(mContext, MIN_AGE, MAX_AGE));
        mAgeWheel.setCurrentItem(CURRENT_AGE - MIN_AGE);
        mAgeWheel.addScrollingListener(this);
        mAge.setText(String.valueOf(CURRENT_AGE));
        mAgeValue = CURRENT_AGE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start:
            //Check BT whether connect
            if (mActivity != null && !mActivity.isBTConnected()) {
                Utility.startPairedForResult(mActivity, this);
                return;
            }
            
            Intent intent = new Intent(mActivity, MeasureActivity.class);
            
            UserInfo info = new UserInfo();
            info.setAge(mAgeValue);

            if (mGenderValue == 0) {
                info.setGender(Profile.GENDER_FEMALE);
            } else {
                info.setGender(Profile.GENDER_MALE);
            }

            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.IS_GUEST_EXTRA, true);
            bundle.putParcelable(Constants.USE_INFO_EXTRA, info);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
            break;

        default:
            break;
        }
    }

    @Override
    public void onScrollingStarted(WheelView wheel) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onScrollingFinished(WheelView wheel) {
        int index = wheel.getCurrentItem();
        switch (wheel.getId()) {
        case R.id.genderWheel:
            mGenderValue = index;
            mGender.setText(mGenderStrings[mGenderValue]);
            break;
        case R.id.ageWheel:
            mAgeValue = MIN_AGE + index;
            mAge.setText(String.valueOf(mAgeValue));
            break;

        default:
            break;
        }
        
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivity.setResult(resultCode);
        mActivity.finish();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
