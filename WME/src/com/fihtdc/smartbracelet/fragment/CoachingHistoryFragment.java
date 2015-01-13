package com.fihtdc.smartbracelet.fragment;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.entity.CoachingResult;
import com.fihtdc.smartbracelet.entity.MeasureResult;
import com.fihtdc.smartbracelet.provider.BraceletInfo;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Coaching;
import com.fihtdc.smartbracelet.provider.BraceletInfo.DayCoaching;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.AnimatedImageView;
import com.fihtdc.smartbracelet.view.MStatusView;
import com.fihtdc.smartbracelet.view.TimeAxisView;
import com.fihtdc.smartbracelet.view.TimeAxisView.OnContentTapListener;
import com.fihtdc.smartbracelet.view.TimeAxisView.OnMarchedContentChangedListener;

public class CoachingHistoryFragment extends StatisticsBaseFragment implements OnContentTapListener,
        OnMarchedContentChangedListener ,OnClickListener{
    private Context mContext;
    private static final int FLAGS_TIME = DateUtils.FORMAT_SHOW_TIME ;
    private static final int SELECTED_TIME = 1;
    private static final int SELECTED_DAY = 2;
    private int mSelected;
    
    private Button mLeftButton = null;
    private Button mRightButton = null;
    
    //views of time page
    private ViewGroup mTimeLayout = null;
    private TextView mDateTextView = null;
    private TextView mTimeTextView = null;
    private TextView mLevelTextView = null;
    private TextView mCycleView = null;
    private TextView mHitView = null;
    private TextView mHitRateView = null;
    private AnimatedImageView mAnimImageView = null;
    private TimeAxisView mTimeView = null;
    
    //views of day page
    private LinearLayout mDayLayout = null;
    private TextView mDayDateView = null;
    //private TextView mDayWeekView = null;
    private LinearLayout mLevel1Layout = null;
    private LinearLayout mLevel2Layout = null;
    private LinearLayout mLevel3Layout = null;
    private LinearLayout mLevel4Layout = null;
    private LinearLayout mLevel5Layout = null;
    private LinearLayout mLevelMLayout = null;
    private TimeAxisView mDayView;
    private LinearLayout mStatusHistoryLayout;
    private LinearLayout mBaseInfoHistoryLayout;
    private ImageView mNextImageView;
    private ImageView mBackImageView;
    private CoachingResult mCoachingResult;
    private int mDisplayStyle;//0,Display Hit cycle ;1,Display BPM ANS Age Agility ;2,Display Status Emotion
    private static final int EMOTION_DISPLAY_STYLE =2;
    private static final int AGILITY_DISPLAY_STYLE =1;
    private static final int HIT_DISPLAY_STYLE =0;
    private Cursor mCursor;
    private Cursor mDayCursor = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        getActivity().setTitle(R.string.coach_history_title);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.coaching_history, null);
    }
    
    private LoaderCallbacks<Cursor> mCursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            if(id == 0){
                String sortOrder = Coaching.COLUMN_NAME_TEST_TIME + " ASC"; 
                return new CursorLoader(mContext, Coaching.CONTENT_URI,
                        null, null, null, sortOrder);
            }else if(id == 1){
                String sortOrder = DayCoaching.COLUMN_NAME_TEST_TIME + " ASC"; 
                return new CursorLoader(mContext,DayCoaching.CONTENT_URI,null,null,null,sortOrder);
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            int id = loader.getId();
            if(id == 0){
                mCursor = cursor;
                mTimeView.setCursor(cursor);
                if(mCursor ==null || mCursor.getCount() ==0){
                	  clearTimeViewsValue();
                	  removeAllViews();
                }
            }else if(id == 1){
                mDayCursor = cursor;
                mDayView.setCursor(cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mTimeView.setCursor(null);
            mDayView.setCursor(null);
        }
        
    };
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
        initView(view);
    }
    
    private void initView(View view){
        mDateTextView = (TextView)view.findViewById(R.id.date);
        mTimeTextView = (TextView)view.findViewById(R.id.time);
        mLevelTextView = (TextView)view.findViewById(R.id.level_class);
        mCycleView = (TextView)view.findViewById(R.id.cycle_result);
        mHitView = (TextView)view.findViewById(R.id.hit_result);
        mHitRateView = (TextView)view.findViewById(R.id.hit_rate_result);
        mAnimImageView = (AnimatedImageView)view.findViewById(R.id.rate_star);
        mStatusView =  (MStatusView) view.findViewById(R.id.status_view_id);
        mDayDateView = (TextView)view.findViewById(R.id.day_date);
        mLevel1Layout = (LinearLayout)view.findViewById(R.id.level1_layout);
        mLevel2Layout = (LinearLayout)view.findViewById(R.id.level2_layout);
        mLevel2Layout.setBackgroundResource(R.drawable.coaching_history_day_item_bg);
        mLevel3Layout = (LinearLayout)view.findViewById(R.id.level3_layout);
        mLevel4Layout = (LinearLayout)view.findViewById(R.id.level4_layout);
        mLevel4Layout.setBackgroundResource(R.drawable.coaching_history_day_item_bg);
        mLevel5Layout = (LinearLayout)view.findViewById(R.id.level5_layout);
        mLevelMLayout = (LinearLayout)view.findViewById(R.id.levelm_layout);
        mLevelMLayout.setBackgroundResource(R.drawable.coaching_history_day_item_bg);
         //fly.f.ren add for added new requriment(iphone add) begin
        mStatusHistoryLayout = (LinearLayout) view.findViewById(R.id.coach_history_status_info);
        mBaseInfoHistoryLayout = (LinearLayout) view.findViewById(R.id.coach_history_base_info);
        mNextImageView = (ImageView) view.findViewById(R.id.coach_history_next);
        mBackImageView = (ImageView) view.findViewById(R.id.coach_history_back);
        mNextImageView.setOnClickListener(this);
        mBackImageView.setOnClickListener(this);
        onHiddenStatusLayout();
        //fly.f.ren add for added new requriment(iphone add) end
        mLeftButton = (Button)view.findViewById(R.id.left_switch);
        mRightButton = (Button)view.findViewById(R.id.right_switch);
        mLeftButton.setText(R.string.time);
        mRightButton.setText(R.string.day);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        mTimeLayout = (ViewGroup)view.findViewById(R.id.coach_time_layout);
        mDayLayout = (LinearLayout)view.findViewById(R.id.coach_history_day);
        mTimeView = (TimeAxisView)view.findViewById(R.id.timeAxisView);
        mTimeView.setOnContentTapListener(this);
        mTimeView.setOnMarchedContnetChangedListener(this);
        mDayView = (TimeAxisView)view.findViewById(R.id.dayAxisView);
        mDayView.setOnContentTapListener(this);
        mDayView.setOnMarchedContnetChangedListener(this);
        
        updateView(SELECTED_TIME);
        getLoaderManager().initLoader(0, null, mCursorLoader);
        getLoaderManager().initLoader(1, null, mCursorLoader);
    }
    @Override
    public void onContentSigleTap(View view, int index) {
        //
        
    }
   private void onDisplayStatusLayout(){
	   if(null!=mStatusHistoryLayout && null!= mBaseInfoHistoryLayout){
		   mStatusHistoryLayout.setVisibility(View.VISIBLE);
		   mBaseInfoHistoryLayout.setVisibility(View.GONE);
	   }
   }
   
   private void onHiddenStatusLayout(){
	   if(null!=mStatusHistoryLayout && null!= mBaseInfoHistoryLayout){
		   mStatusHistoryLayout.setVisibility(View.GONE);
		   mBaseInfoHistoryLayout.setVisibility(View.VISIBLE);
	   }
   }
    
    private void showDeleteDialog(int index){
        if (mCursor != null && mCursor.moveToPosition(index)) {
            final long time = mCursor.getLong(mCursor.getColumnIndex(Coaching.COLUMN_NAME_TEST_TIME));
            
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.delete_title);
            builder.setMessage(getString(R.string.delete_content,
                    Utility.getDisplayDate(time, "yyyy-MM-dd"),
                    Utility.getDisplayTime(mContext, time, DateUtils.FORMAT_SHOW_TIME)));
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (mContext != null) {
                        String where = Coaching.COLUMN_NAME_TEST_TIME + " = "+"'" + time +"'";
                        mContext.getContentResolver().delete(Coaching.CONTENT_URI, where,null);
                    }
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    
                }
            });
            builder.show();
        }
    }
    
    @Override
    public void onContentDoubleTap(View view, int index) {
        switch(view.getId()){
        case R.id.dayAxisView:
            break;
        case R.id.timeAxisView:
            showDeleteDialog(index);
            break;
        default:
            break;
        }
        
    }

    @Override
    public void onMarchedContentChanged(View view, int index) {
        switch(view.getId()){
        case R.id.timeAxisView:
            if(mCursor != null && mCursor.moveToPosition(index)){
            	onWrapperCoachResult();
            	onDisplayStyleShow();
            	onResetNextBackNavigate();
            }else{
                clearTimeViewsValue();
                removeAllViews();
                
            }
            break;
        case R.id.dayAxisView:
            if(mDayCursor != null && mDayCursor.moveToPosition(index)){
                long time = mDayCursor.getLong(mDayCursor
                        .getColumnIndex(DayCoaching.COLUMN_NAME_TEST_TIME));
                mDayDateView.setText(Utility.getDisplayDate(time, "yyyy-MM-dd EEE"));
                //TODO mDayWeekView.setText(Utility.getDisplayTime(mContext, time, FLAGS_TIME));
                int cycle = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_CYCLE1));
                int hit = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_HIT1));
                
                putValue2Views(mLevel1Layout,"1",cycle,hit);
                cycle = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_CYCLE2));
                hit = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_HIT2));
                putValue2Views(mLevel2Layout,"2",cycle,hit);
                cycle = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_CYCLE3));
                hit = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_HIT3));
                putValue2Views(mLevel3Layout,"3",cycle,hit);
                cycle = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_CYCLE4));
                hit = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_HIT4));
                putValue2Views(mLevel4Layout,"4",cycle,hit);
                cycle = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_CYCLE5));
                hit = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_HIT5));
                putValue2Views(mLevel5Layout,"5",cycle,hit);
                cycle = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_CYCLEM));
                hit = mDayCursor.getInt(mDayCursor.getColumnIndex(DayCoaching.COLUMN_NAME_HITM));
                putValue2Views(mLevelMLayout,"M",cycle,hit);
                
            }else{
                clearDayViewsValue();
            }
            break;
        default:
            break;
        
        }
    }
    /**
     * fly.f.ren add for navigate 
     */
    private void onResetNextBackNavigate() {
		switch (mDisplayStyle) {
		case HIT_DISPLAY_STYLE:
			mNextImageView.setVisibility(View.VISIBLE);
			mBackImageView.setVisibility(View.INVISIBLE);
			break;
		case AGILITY_DISPLAY_STYLE:
			mNextImageView.setVisibility(View.VISIBLE);
			mBackImageView.setVisibility(View.VISIBLE);
			break;
		case EMOTION_DISPLAY_STYLE:
			mNextImageView.setVisibility(View.INVISIBLE);
			mBackImageView.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		
	}

	/**
     * fly.f.ren added display all kinds of infomation
     */
    private void onDisplayStyleShow() {
		switch (mDisplayStyle) {
		case HIT_DISPLAY_STYLE:
			onHiddenStatusLayout();
			onShowHitInfo();
			break;
		case AGILITY_DISPLAY_STYLE:
			onHiddenStatusLayout();
			onShowAgilityInfo();
			break;
		case EMOTION_DISPLAY_STYLE:
			onDisplayStatusLayout();
			onShowEmotionInfo();
			break;

		default:
			break;
		}
		
	}
    /**
     * on Show Emotion Info
     */
	private void onShowEmotionInfo() {
	 setmMeasureResult(mCoachingResult.getMeasureResult());
	 onLauncherStatusAnim();
	}

	private void onShowAgilityInfo() {
		 mDateTextView.setText(Utility.getDisplayDate(mCoachingResult.getmTime(), "yyyy-MM-dd"));
         mTimeTextView.setText(Utility.getDisplayTime(mContext, mCoachingResult.getmTime(), FLAGS_TIME));
         Resources res = mContext.getResources();
         mLevelTextView.setText(res.getString(R.string.level)+": "+mCoachingResult.getLevelName());
         int agility = Utility.transformAgility(mCoachingResult.getMeasureResult().getAgility());
         mCycleView.setText(res.getString(R.string.agility_label)+": "+agility);
         int ansAge = mCoachingResult.getMeasureResult().getAnsAge();
         mHitView.setText(res.getString(R.string.ans_age_label)+": "+ansAge);
         int bpm = mCoachingResult.getMeasureResult().getBpm();
         mHitRateView.setText(res.getString(R.string.bpm_label)+": "+bpm);
         mAnimImageView.setVisibility(View.VISIBLE);
         try {
             mAnimImageView.setImageLevel(Utility.getHitStarNum(mCoachingResult.getHit(),mCoachingResult.getCycle()));
         } catch (Exception e) {
             mAnimImageView.setImageLevel(0);
         }
		
	}

	private void onShowHitInfo() {
          mDateTextView.setText(Utility.getDisplayDate( mCoachingResult.getmTime(), "yyyy-MM-dd"));
          mTimeTextView.setText(Utility.getDisplayTime(mContext, mCoachingResult.getmTime(), FLAGS_TIME));
          Resources res = mContext.getResources();
          mLevelTextView.setText(res.getString(R.string.level)+": "+mCoachingResult.getLevelName());
          int cycle = mCoachingResult.getCycle();
          mCycleView.setText(res.getString(R.string.upper_cycle)+": "+cycle);
          int hit = mCoachingResult.getHit();
          mHitView.setText(res.getString(R.string.hit)+": "+hit);
          //TODO need calculate and setImage
          mHitRateView.setText(res.getString(R.string.hit_rate)+": "+Utility.getHitRate(hit, cycle)+"%");
          mAnimImageView.setVisibility(View.VISIBLE);
          try {
              mAnimImageView.setImageLevel(Utility.getHitStarNum(hit,cycle));
          } catch (Exception e) {
              mAnimImageView.setImageLevel(0);
          }
		
	}

	/**
     *  fly.f.ren add for wrappper coach result
     */
    private void onWrapperCoachResult() {
    	mCoachingResult = new CoachingResult();
		MeasureResult measureResult = new MeasureResult();
		measureResult.setAgeHF(mCursor.getFloat(mCursor.getColumnIndex(BraceletInfo.Measure.COLUMN_NAME_AGE_HF)));
		measureResult.setAgeLF(mCursor.getFloat(mCursor.getColumnIndex(BraceletInfo.Measure.COLUMN_NAME_AGE_LF)));
		measureResult.setAgility(mCursor.getInt(mCursor.getColumnIndex(BraceletInfo.Coaching.COLUMN_NAME_AGILITY)));
		measureResult.setAnsAge(mCursor.getInt(mCursor.getColumnIndex(BraceletInfo.Coaching.COLUMN_NAME_ANS_AGE)));
		measureResult.setBpm(mCursor.getInt(mCursor.getColumnIndex(BraceletInfo.Coaching.COLUMN_NAME_BPM)));
		mCoachingResult.setMeasureResult(measureResult);
		mCoachingResult.setLevelName(mCursor.getString(mCursor.getColumnIndex(Coaching.COLUMN_NAME_LEVEL)));
		mCoachingResult.setCycle(mCursor.getInt(mCursor.getColumnIndex(Coaching.COLUMN_NAME_CYCLE)));
		mCoachingResult.setHit(mCursor.getInt(mCursor.getColumnIndex(Coaching.COLUMN_NAME_HIT_NUM)));
		mCoachingResult.setmTime(mCursor.getLong(mCursor
                .getColumnIndex(Coaching.COLUMN_NAME_TEST_TIME)));
	}

	private void putValue2Views(LinearLayout layout,String level,int cycle,int hit){
        ((TextView)layout.findViewById(R.id.level)).setText(""+level);
        ((TextView)layout.findViewById(R.id.cycle)).setText(""+cycle);
        ((TextView)layout.findViewById(R.id.hit)).setText(""+hit);
        
        ((TextView)layout.findViewById(R.id.hit_rate)).setText(Utility.getHitRate(hit, cycle)+"%");
    }
    private void clearDayViewsValue(){
        mDayDateView.setText("");
        clearItemViewValue(mLevel1Layout);
        clearItemViewValue(mLevel2Layout);
        clearItemViewValue(mLevel3Layout);
        clearItemViewValue(mLevel4Layout);
        clearItemViewValue(mLevel5Layout);
        clearItemViewValue(mLevelMLayout);
    }
    
    private void clearItemViewValue(LinearLayout layout){
        ((TextView)layout.findViewById(R.id.level)).setText("");
        ((TextView)layout.findViewById(R.id.cycle)).setText("");
        ((TextView)layout.findViewById(R.id.hit)).setText("");
        ((TextView)layout.findViewById(R.id.hit_rate)).setText("");
    }
    private void clearTimeViewsValue(){
        mDateTextView.setText("");
        mTimeTextView.setText("");
        mLevelTextView.setText("");
        mCycleView.setText("");
        mHitView.setText("");
        mHitRateView.setText("");
        mNextImageView.setVisibility(View.INVISIBLE);
        mBackImageView.setVisibility(View.INVISIBLE);
        mAnimImageView.setVisibility(View.INVISIBLE);
        
    }
    @Override
    public void onMarchedNothing(View view) {
        switch(view.getId()){
        case R.id.timeAxisView:
            clearTimeViewsValue();
            break;
        case R.id.dayAxisView:
            clearDayViewsValue();
            break;
        }
        
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.left_switch:
            updateView(SELECTED_TIME);
            break;
        case R.id.right_switch:
            updateView(SELECTED_DAY);
            break;
        case R.id.coach_history_back:
        	mDisplayStyle = mDisplayStyle -1;
        	onDisplayStyleShow();
        	onResetNextBackNavigate();
        	break;
        case R.id.coach_history_next:
        	mDisplayStyle = mDisplayStyle+1;
        	onDisplayStyleShow();
        	onResetNextBackNavigate();
        	break;
        default:
            break;
        }
    }
    
    private void updateView(int type){
        mSelected = type;
        if(mSelected == SELECTED_TIME){
            mLeftButton.setSelected(true);
            mRightButton.setSelected(false);
            mTimeLayout.setVisibility(View.VISIBLE);
            mDayLayout.setVisibility(View.GONE);
        }else if(mSelected == SELECTED_DAY){
            mLeftButton.setSelected(false);
            mRightButton.setSelected(true);
            mDayLayout.setVisibility(View.VISIBLE);
            mTimeLayout.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(null != mCursor){
            mCursor.close();
            mCursor = null;
        }
        if(null != mDayCursor ){
            mDayCursor.close();
            mDayCursor = null;
        }
    }
}
