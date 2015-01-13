package com.fihtdc.smartbracelet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.CoachingStartActivity;
import com.fihtdc.smartbracelet.activity.CoachingActivity;
import com.fihtdc.smartbracelet.entity.LevelParameter;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;

public class CoachingStartFragment extends CommonFragment {

    private Context mContext;
    private CoachingStartActivity mActivity;
    
    public CoachingStartFragment(){
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = (CoachingStartActivity)getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle onSavedInstanceState){
        return inflater.inflate(R.layout.coaching_start, null);
    }
    
    @Override
    public void onViewCreated(View view,Bundle bundle){
        super.onViewCreated(view, bundle);
        initView(view);
    }
    
    private void initView(View view){
        final LevelParameter levelParam = getArguments().getParcelable(Constants.KEY_LEVEL_PARAMETER);
        LinearLayout startLayout = (LinearLayout)view.findViewById(R.id.start_layout);
        startLayout.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                enterPreparePage(levelParam);
            }
        });
        
        TextView textLevel = (TextView)startLayout.findViewById(R.id.level_name);
        Resources res = mContext.getResources();
        textLevel.setText(res.getString(R.string.level)+" "+levelParam.getLevel());
        TextView textInhale = (TextView)startLayout.findViewById(R.id.level_inhale);
        textInhale.setText(res.getString(R.string.inhale)+" "+levelParam.getInhale()+" "+res.getString(R.string.sec));
        TextView textHold = (TextView)startLayout.findViewById(R.id.level_hold);
        textHold.setText(res.getString(R.string.hold)+" "+levelParam.getHold()+" "+res.getString(R.string.sec));
        TextView textExhale = (TextView)startLayout.findViewById(R.id.level_exhale);
        textExhale.setText(res.getString(R.string.exhale)+" "+levelParam.getOuthale()+" "+res.getString(R.string.sec));
    }
    
    private void enterPreparePage(LevelParameter params){
        //Check BT whether connect
        if (mActivity != null && !mActivity.isBTConnected()) {
            Utility.startPairedForResult(mContext, this);
            return;
        }
        

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_LEVEL_PARAMETER, params);
        Intent intent = new Intent(mActivity, CoachingActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivity.setResult(resultCode);
        mActivity.finish();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
