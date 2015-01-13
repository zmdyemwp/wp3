package com.fihtdc.smartbracelet.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.entity.MeasureResult;
import com.fihtdc.smartbracelet.entity.StatusPerson;
import com.fihtdc.smartbracelet.provider.BraceletInfo;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Measure;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.TimeAxisView;
import com.fihtdc.smartbracelet.view.TimeAxisView.OnContentTapListener;
import com.fihtdc.smartbracelet.view.TimeAxisView.OnMarchedContentChangedListener;
import com.fihtdc.smartbracelet.view.TimeAxisView.OnScollFlingCompleteListener;

public class MeasureHistoryFragment extends StatisticsBaseFragment implements View.OnClickListener,
        OnMarchedContentChangedListener, OnContentTapListener, OnScollFlingCompleteListener {
    private static final String SELECTED_EXTRA = "selected";
    private static final int SELECTED_AGILITY = 1;
    private static final int SELECTED_STATE = 2;
    
    private static final int FLAGS_TIME = DateUtils.FORMAT_SHOW_TIME /*| DateUtils.FORMAT_12HOUR
            | DateUtils.FORMAT_CAP_AMPM*/;
	private static final int DELAY_TIME_MESSAGE = 100;
    
    Context mContext;
    Cursor mCursor;
    TextView mDateText;
    TextView mTimeText;
    TextView mAgilityText;
    TextView mAnsAgeText;
    TextView mBPMText;
    TimeAxisView mAgilityView;
    TimeAxisView mStateView;
    Button mLeftButton;
    Button mRightButton;
    LinearLayout mAgilityLayout;
    LinearLayout mStateLayout;
    
    int mSelected;
    List<StatusPerson> mStatusList = new ArrayList<StatusPerson>();
    
    private Map<Integer,StatusPerson> mIndexStatusMap = new HashMap<Integer,StatusPerson>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        getActivity().setTitle(R.string.measure_history_title);
        if (savedInstanceState != null) {
            mSelected = savedInstanceState.getInt(SELECTED_EXTRA, SELECTED_AGILITY);
        } else {
            mSelected = SELECTED_AGILITY;
        }
    }
    
    private LoaderCallbacks<Cursor> mCursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            //Fixed remove where condition with name begin
            //String where = BraceletInfo.Measure.COLUMN_NAME_PROFILE_NAME + " = ?";
            String sortOrder = BraceletInfo.Measure.COLUMN_NAME_TEST_TIME + " ASC";
            //String userName = Utility.getCurrentProfileName(mContext);
//            if (!TextUtils.isEmpty(userName)) {
//                return new CursorLoader(mContext, BraceletInfo.Measure.CONTENT_URI, null, where,
//                        new String[] { userName }, sortOrder);
//            } else {
                return new CursorLoader(mContext, BraceletInfo.Measure.CONTENT_URI, null, null,
                        null, sortOrder);
            //}
            //Fixed remove where condition with name end
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mCursor = cursor;
            mAgilityView.setCursor(cursor);
            mStateView.setCursor(cursor);
            if(cursor ==null || cursor.getCount()==0){
            	if(mStatusList!=null){
            		mStatusList.clear();
            	}
            	setmStatusPersonList(mStatusList);
            	onLauncherStatusAnim();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAgilityView.setCursor(null);
            mStateView.setCursor(null);
        }
        
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.measure_history, null);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
        mDateText = (TextView)view.findViewById(R.id.date);
        mTimeText = (TextView)view.findViewById(R.id.time);
        mAgilityText = (TextView)view.findViewById(R.id.agility_value);
        mAnsAgeText = (TextView)view.findViewById(R.id.ans_age_value);
        mBPMText = (TextView)view.findViewById(R.id.bpm_value);
        mAgilityLayout = (LinearLayout)view.findViewById(R.id.agilityLayout);
        mStateLayout = (LinearLayout)view.findViewById(R.id.stateLayout);
        mAgilityView = (TimeAxisView)view.findViewById(R.id.agilityHistogramView);
        mStateView = (TimeAxisView)view.findViewById(R.id.statehistogramView);
        mLeftButton = (Button)view.findViewById(R.id.left_switch);
        mRightButton = (Button)view.findViewById(R.id.right_switch);
        mLeftButton.setText(R.string.agility_label);
        mRightButton.setText(R.string.m_state_label);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        mAgilityView.setOnContentTapListener(this);
        mAgilityView.setOnMarchedContnetChangedListener(this);
        mStateView.setOnContentTapListener(this);
        mStateView.setOnMarchedContnetChangedListener(this);
        mStateView.setOnScollFlingCompleteListener(this);
        updateView(mSelected);
        
        getLoaderManager().initLoader(0, null, mCursorLoader);
    }
    
    private void updateView(int selected){
        mSelected = selected; 
        if (SELECTED_AGILITY == selected) {
            mLeftButton.setSelected(true);
            mRightButton.setSelected(false);
            mAgilityLayout.setVisibility(View.VISIBLE);
            mStateLayout.setVisibility(View.GONE);
        } else if (SELECTED_STATE == selected){
            mLeftButton.setSelected(false);
            mRightButton.setSelected(true);
            mAgilityLayout.setVisibility(View.GONE);
            mStateLayout.setVisibility(View.VISIBLE);
        	onLauncherStatusAnim();
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_EXTRA, mSelected);
    }

    @Override
    public void onContentSigleTap(View view, int index) {
        switch (view.getId()) {
        case R.id.statehistogramView:
			float startX = mStateView.getContentStartX(index);
			float startY = mStateView.getContentStartY(index);
			StatusPerson status =mIndexStatusMap.get(index);
			StatusPerson statusAim = new StatusPerson();
			statusAim.setmEndX(status.getmEndX());
			statusAim.setmEndY(status.getmEndY());
			statusAim.setmStartX(startX);
			statusAim.setmStartY(startY-mPersonImageHeight);
			onLauncherSingleStatusAnim(statusAim);
            break;
        default:
            break;
        }

    }

    @Override
    public void onContentDoubleTap(View view, int index) {
        switch (view.getId()) {
        case R.id.agilityHistogramView:
        case R.id.statehistogramView:
            showDeleteDialog(index);
            break;

        default:
            break;
        }

    }
    
    private void showDeleteDialog(int index){
        if (mCursor != null && mCursor.moveToPosition(index)) {
            long time = mCursor.getLong(mCursor.getColumnIndex(Measure.COLUMN_NAME_TEST_TIME));
            
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.delete_title);
            builder.setMessage(getString(R.string.delete_content,
                    Utility.getDisplayDate(time, "yyyy-MM-dd"),
                    Utility.getDisplayTime(mContext, time, DateUtils.FORMAT_SHOW_TIME)));
            builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (mContext != null) {
                        int id = mCursor.getInt(mCursor.getColumnIndex(Measure._ID));
                        String where = Measure._ID + " = ?";
                        mContext.getContentResolver().delete(Measure.CONTENT_URI, where,
                                new String[] { String.valueOf(id) });
                    }
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    
                }
            });
            builder.show();
        }
    }

    @Override
    public void onMarchedContentChanged(View view, int index) {
        switch (view.getId()) {
        case R.id.agilityHistogramView:
            if (mCursor != null && mCursor.moveToPosition(index)) {
                long time = mCursor.getLong(mCursor
                        .getColumnIndex(Measure.COLUMN_NAME_TEST_TIME));
                mDateText.setText(Utility.getDisplayDate( time, "yyyy-MM-dd"));
                mTimeText.setText(Utility.getDisplayTime(mContext, time, FLAGS_TIME));
                mAgilityText.setText(formatInt(Utility.transformAgility(mCursor.getInt(mCursor
                        .getColumnIndex(Measure.COLUMN_NAME_AGILITY)))));
                mAnsAgeText.setText(String.valueOf(mCursor.getInt(mCursor
                        .getColumnIndex(Measure.COLUMN_NAME_ANS_AGE))));
                mBPMText.setText(formatInt(mCursor.getInt(mCursor
                        .getColumnIndex(Measure.COLUMN_NAME_BPM))));
            } else {
                mDateText.setText("");
                mTimeText.setText("");
                mAgilityText.setText("");
                mAnsAgeText.setText("");
                mBPMText.setText("");
            }
            break;
        default:
            break;
        }
    }
    
    private String formatInt(int value){
        String result = String.valueOf(value);
        
        /*if (value < 10) {
            result = "00" + result;
        } else if (value < 100){
            result = "0" + result;
        }*/
        
        return result;
    }

    @Override
    public void onMarchedNothing(View view) {
        switch (view.getId()) {
        case R.id.agilityHistogramView:
            mDateText.setText("");
            mTimeText.setText("");
            mAgilityText.setText("");
            mAnsAgeText.setText("");
            mBPMText.setText("");
            break;

        default:
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left_switch:
            updateView(SELECTED_AGILITY);
            break;
        case R.id.right_switch:
            updateView(SELECTED_STATE);
            break;

        default:
            break;
        }
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        if(mStatusList!=null){
        	mStatusList.clear();
        	mStatusList = null;
        }
        if(mIndexStatusMap!=null){
        	mIndexStatusMap.clear();
        	mIndexStatusMap = null;
        }
    }

    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;;
			switch (what) {
			case DELAY_TIME_MESSAGE:
				int startIndex = msg.arg1;
				int endIndex = msg.arg2;
				onQueryMStatusInfo(	startIndex,	endIndex);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
    	
    };
    
    @Override
    public void OnScollFlingComplete(View view, int startIndex, int endIndex) {
        switch (view.getId()) {
        case R.id.statehistogramView:
        	Message msg = new Message();
        	msg.what =DELAY_TIME_MESSAGE;
        	msg.arg1 = startIndex;
        	msg.arg2 = endIndex;
        	mHandler.removeMessages(DELAY_TIME_MESSAGE);
        	mHandler.sendMessageDelayed(msg, 200);
            break;

        default:
            break;
        }
        
    }

	private void onQueryMStatusInfo(int startIndex, int endIndex) {
		mStatusList = new ArrayList<StatusPerson>();
		mStatusList.clear();
		Log.i("Fly", "startIndex==="+startIndex);
		Log.i("Fly", "endIndex==="+endIndex);
		mIndexStatusMap.clear();
		for(int i = startIndex; i<=endIndex;i++){
		    mCursor.moveToPosition(i);
			MeasureResult measureResult = new MeasureResult();
            measureResult.setAgility(mCursor.getInt(mCursor
                    .getColumnIndex(Measure.COLUMN_NAME_AGILITY)));		
            measureResult.setAnsAge(mCursor.getInt(mCursor
                    .getColumnIndex(Measure.COLUMN_NAME_ANS_AGE)));
            measureResult.setBpm(mCursor.getInt(mCursor
                    .getColumnIndex(Measure.COLUMN_NAME_BPM)));
            measureResult.setAgeHF(mCursor.getFloat(mCursor
                    .getColumnIndex(Measure.COLUMN_NAME_AGE_HF)));
            measureResult.setAgeLF(mCursor.getFloat(mCursor
                    .getColumnIndex(Measure.COLUMN_NAME_AGE_LF)));
            measureResult.setEmotion(mCursor.getInt(mCursor
                    .getColumnIndex(Measure.COLUMN_NAME_EMOTION_STATUS)));
            StatusPerson mStatus = new StatusPerson();
            mStatus.setmPersonHeight(mPersonImageHeight);
            mStatus.setmPersonWidth(mPersonImageWidth);
            mStatus.setmStatusAreaWidth(mStatusView.getWidth());
            mStatus.setmMResult(measureResult);
            if(i == startIndex){
            	 mStatus.setmStartX(mStatusView.getLeft());
    			 mStatus.setmStartY(mStatusView.getWidth()- mPersonImageHeight);
            }else{
                 mStatus.setmStartX(mStatusList.get(mStatusList.size()-1).getmEndX());
       			 mStatus.setmStartY(mStatusList.get(mStatusList.size()-1).getmEndY());
            }
            mStatus.calculeFallPosition(mStatusView.getLeft());
            mIndexStatusMap.put(i, mStatus);
            if ( mStatusList.size() == 0) {
                mStatusList.add(mStatus);
            } else if(mStatusList.size()>0 &&!mStatusList.get(mStatusList.size()-1).equals(mStatus)){
        	   mStatusList.add(mStatus);
           }
		 }
		setmStatusPersonList(mStatusList);
		onLauncherStatusAnim();
	}
    
  
}