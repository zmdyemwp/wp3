package com.fihtdc.smartbracelet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.CoachingCustomActivity;
import com.fihtdc.smartbracelet.activity.CoachingActivity;
import com.fihtdc.smartbracelet.entity.LevelParameter;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.wheel.OnWheelChangedListener;
import com.fihtdc.smartbracelet.wheel.OnWheelScrollListener;
import com.fihtdc.smartbracelet.wheel.WheelView;
import com.fihtdc.smartbracelet.wheel.adapters.ArrayWheelAdapter;
import com.fihtdc.smartbracelet.wheel.adapters.NumericWheelAdapter;

public class CoachingCustomFragment extends CommonFragment implements OnWheelChangedListener,OnWheelScrollListener{

    private static final int MAX_CYCLE = 40;
    private static final int MIN_CYCLE = 10;
    private static final int CURRENT_CYCLE = 10;
    private static final int CURRENT_CYCLE_ITEM = 0;
    private Context mContext ;
    private TextView mCyclesaView;
    private TextView mInhaleView;
    private TextView mHoldView;
    private TextView mExhaleView;
    
    private WheelView mCycleWheelView = null;
    private WheelView mInhaleWheelView = null;
    private WheelView mHoldWheelView = null;
    private WheelView mExhaleWheelView = null;
    
    private String[] mInhaleArray = null;
    private String[] mHoldArray = null;
    private String[] mExhaleArray = null;
    
    private CoachingCustomActivity mActivity;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = (CoachingCustomActivity)getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.customlevel, null);
        return view;
    }
    
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView(view);
    }
    
    private void initData(){
        Resources res = mContext.getResources();
        mInhaleArray = res.getStringArray(R.array.custom_inhale);
        mHoldArray = res.getStringArray(R.array.custom_hold);
        mExhaleArray = res.getStringArray(R.array.custom_exhale);
    }
    private void initView(View view){
        mCyclesaView = initLayout(view,mCyclesaView,R.id.param_cycle,R.string.upper_cycle,R.string.times);
        mCyclesaView.setText(String.valueOf(CURRENT_CYCLE));
        mInhaleView = initLayout(view,mInhaleView, R.id.param_inhale, R.string.upper_inhale, R.string.upper_unit_sec);
        mInhaleView.setText(mInhaleArray[0]);
        mHoldView = initLayout(view,mHoldView,R.id.param_hold,R.string.upper_hold,R.string.upper_unit_sec);
        mHoldView.setText(mHoldArray[0]);
        mExhaleView = initLayout(view,mExhaleView, R.id.param_exhale, R.string.upper_exhale,R.string.upper_unit_sec);
        mExhaleView.setText(mExhaleArray[0]);
        Button startBtn = (Button)view.findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                enterPreparePage();
                
            }
        });
        
        mCycleWheelView = (WheelView)view.findViewById(R.id.cycle_wheel);
        mCycleWheelView.setViewAdapter(new NumericWheelAdapter(mContext, MIN_CYCLE, MAX_CYCLE));
        mCycleWheelView.setCurrentItem( CURRENT_CYCLE_ITEM);
        mCycleWheelView.addScrollingListener(this);
        mInhaleWheelView = (WheelView)view.findViewById(R.id.inhale_wheel);
        mInhaleWheelView.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mInhaleArray));
        mInhaleWheelView.setCurrentItem(0);
        mInhaleWheelView.addScrollingListener(this);
        mHoldWheelView = (WheelView)view.findViewById(R.id.hold_wheel);
        mHoldWheelView.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mHoldArray));
        mHoldWheelView.setCurrentItem(0);
        mHoldWheelView.addScrollingListener(this);
        mExhaleWheelView = (WheelView)view.findViewById(R.id.exhale_wheel);
        mExhaleWheelView.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mExhaleArray));
        mExhaleWheelView.setCurrentItem(0);
        mExhaleWheelView.addScrollingListener(this);
    }
    
    private int getWheelViewValue(WheelView view){
        int value = view.getCurrentItem();
        switch(view.getId()){
            case R.id.cycle_wheel:
                value = value+ 10;
                break;
            case R.id.inhale_wheel:
                value = value+ 3;
                break;
            case R.id.exhale_wheel:
                value = value + 6;
                break;
            case R.id.hold_wheel:
                value = value + 1;
                break;
            default:
                break;
        }
        return value;
    }
    
    private void enterPreparePage(){
        //Check BT whether connect
        if (mActivity != null && !mActivity.isBTConnected()) {
            Utility.startPairedForResult(mActivity, this);
            return;
        }
        
        //TODO need calculate values by WheelView
        int cycle = getWheelViewValue(mCycleWheelView);
        int inhale = getWheelViewValue(mInhaleWheelView);
        int hold = getWheelViewValue(mHoldWheelView);
        int exhale = getWheelViewValue(mExhaleWheelView);
        LevelParameter param = new LevelParameter("M", inhale, hold, exhale,cycle);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_ENTER_COACHING, true);
        bundle.putParcelable(Constants.KEY_LEVEL_PARAMETER, param);
        Intent intent = new Intent(mActivity, CoachingActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivity.setResult(resultCode);
        mActivity.finish();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private TextView initLayout(View groupView,TextView view,int layoutId,int title,int unit){
        LinearLayout cycleLayout = (LinearLayout)groupView.findViewById(layoutId);
        TextView titleView = (TextView)cycleLayout.findViewById(R.id.param_title_view);
        titleView.setText(title);
        view = (TextView)cycleLayout.findViewById(R.id.param_value_view);
        /*TextView unitView = (TextView)cycleLayout.findViewById(R.id.param_unit_view);
        unitView.setText(unit);*/
        return view;
    }

    @Override
    public void onScrollingStarted(WheelView wheel) {
        
    }

    @Override
    public void onScrollingFinished(WheelView wheel) {
        int index = wheel.getCurrentItem();
        switch(wheel.getId()){
            case R.id.cycle_wheel:
                int i = MIN_CYCLE+index;
                mCyclesaView.setText(String.valueOf(i));
                break;
            case R.id.inhale_wheel:
                mInhaleView.setText(mInhaleArray[index]);
                break;
            case R.id.hold_wheel:
                mHoldView.setText(mHoldArray[index]);
                break;
            case R.id.exhale_wheel:
                mExhaleView.setText(mExhaleArray[index]);
                break;
            default:
                break;
            
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        
    }
}
