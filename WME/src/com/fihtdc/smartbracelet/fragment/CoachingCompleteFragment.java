package com.fihtdc.smartbracelet.fragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.SummaryActivity;
import com.fihtdc.smartbracelet.entity.CoachingResult;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.AnimatedImageView;

public class CoachingCompleteFragment extends CommonFragment {

    private SummaryActivity mContext = null;
    private CoachingResult mCoachingResult;
    private AnimatedImageView mAnimImageView = null;
    private AnimatedImageView mWhaleView = null;
    
    private TextView mLevelNameView ;
    private TextView mCycleView;
    private TextView mHitView;
    private TextView mHitRateView;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = (SummaryActivity) getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle onSavedInstanceState){
        return inflater.inflate(R.layout.coaching_complete, null);
    }
    
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        SummaryActivity activity = (SummaryActivity)getActivity();
        activity.setVisibleFragmentIndex(Constants.TYPE_COACHING);
        
        initView(view);
    }
    
    private void initView(View view){
        LogApp.Logd("Pei", "CoachingComplete-->init");
        mLevelNameView = (TextView)view.findViewById(R.id.level_name);
        String str = getArguments().getString(Constants.KEY_LEVEL_CLASS);
        Resources res = mContext.getResources();
        String name = res.getString(R.string.level);
        mLevelNameView.setText(name+" " +str);
        mCycleView = (TextView)view.findViewById(R.id.cycle_value);
        mHitView = (TextView)view.findViewById(R.id.hit_value);
        mHitRateView = (TextView)view.findViewById(R.id.hitrate_value);
        ImageView imgView = (ImageView)view.findViewById(R.id.go);
        imgView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SummaryFragment newFragment = (SummaryFragment) getFragmentManager()
                        .findFragmentByTag("SummaryFragment");
                if (newFragment == null) {
                    newFragment = new SummaryFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.TYPE_EXTRA, Constants.TYPE_COACHING);
                //bundle.putParcelable(Constants.MEASURE_RESULT_EXTRA, mCoachingResult.getMeasureResult());
                newFragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.single_fragment, newFragment, "SummaryFragment");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
                mContext.onDisplaySummaryContent(newFragment);
            }
        });
        mAnimImageView = (AnimatedImageView)view.findViewById(R.id.rate);
        mWhaleView = (AnimatedImageView)view.findViewById(R.id.complete_whale);
        putValueToViews();
    }
    
    private void putValueToViews(){
        if(null == mContext ){
            //mContext may be null,because setSummaryResult() run before onViewCreated().
            // now i let it return,and the follow action will run in the onViewCreated() function
            return;
        }
        Resources res = mContext.getResources();
        if (mCoachingResult != null) {
            int cycleTime = mCoachingResult.getCycle();
            if(null != mCycleView){
                mCycleView.setText(changeStringStyle(res.getString(R.string.upper_cycle)+": "+ cycleTime));
            }
            int hit = mCoachingResult.getHit();
            if(null != mHitView){
                mHitView.setText(changeStringStyle(res.getString(R.string.hit)+": "+hit));
            }
            int hitRate = Utility.getHitRate(hit, cycleTime);
            if(null != mHitRateView){
                mHitRateView.setText(changeStringStyle(res.getString(R.string.hit_rate)+": "+hitRate+"%"));
            }
            
            try {
                int starNum = Utility.getHitStarNum(hit, cycleTime);
                if(null != mAnimImageView){
                    mAnimImageView.setImageLevel(starNum);
                }
                if(null != mWhaleView){
                    doUpAnimation(starNum);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
    private SpannableString changeStringStyle(String str){
        TextAppearanceSpan tas = new TextAppearanceSpan(mContext, R.style.text_gray_style);
        SpannableString spannStr = new SpannableString(str);
        int end = str.indexOf(":");
        spannStr.setSpan(tas, 0, end +1 , 0);
        return spannStr;
    }
    
    private void doUpAnimation(int level){
        TranslateAnimation mUpTransAnimation = new TranslateAnimation(0f, 0f, 400f, 0f);  
        mUpTransAnimation.setDuration(2*1000);
        mWhaleView.setImageLevel(level);
        mWhaleView.startAnimation(mUpTransAnimation);
        mUpTransAnimation.start();
    }
    
    public void setSummaryResult(Bundle bundle) {
        //TODO Maybe this function run before onViewCreated or late,
        //Here maybe need to add codes which assign value to views
        LogApp.Logd("Pei", "setSummaryResult(Bundle bundle)");
        if (bundle != null) {
            mCoachingResult = bundle.getParcelable(Constants.COACHING_RESULT_EXTRA);
            putValueToViews();
        }
        
        
        
    }
}
