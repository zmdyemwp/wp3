package com.fihtdc.smartbracelet.activity;

import java.io.ByteArrayOutputStream;

import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.entity.CoachingResult;
import com.fihtdc.smartbracelet.entity.MeasureResult;
import com.fihtdc.smartbracelet.facebook.Share;
import com.fihtdc.smartbracelet.facebook.Share.ShareResultListener;
import com.fihtdc.smartbracelet.provider.BraceletInfo;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Profile;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.SmartToast;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.AnimatedImageView;
import com.fihtdc.smartbracelet.view.MStatusView;
import com.yl.ekgrr.EkgRR;

public class FacebookShareActivity extends CustomActionBarActivity implements MStatusView.DrawFinishListener{
    private static final String TAG = "FacebookShareActivity";
    private static final int SHOW_WAIT_DIALOG = 1;
    
    int mType;
    MeasureResult mMeasureResult;
    CoachingResult mCoachingResult;

    EditText mEditText;
    LinearLayout mMeasureLayout;
    LinearLayout mCoachingLayout;
    Button mPostButton;
    TextView mAgilityText;
    TextView mAnsAgeText;
    TextView mBPMText;
    TextView mHitRateText;
    AnimatedImageView mAnimImageView = null;
    AnimatedImageView mWhaleImageView = null;
    
    private Handler mHandler;
    ProgressDialog mProgressDialog;
	protected String mProfileName;
	private MStatusView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_share);
        mHandler = new Handler();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mType = bundle.getInt(Constants.TYPE_EXTRA);
                mMeasureResult = bundle.getParcelable(Constants.MEASURE_RESULT_EXTRA);
                mCoachingResult = bundle.getParcelable(Constants.COACHING_RESULT_EXTRA);
            }
        }
        initView();
        getLoaderManager().initLoader(0, null, mProfileLoader);
    }
    private LoaderCallbacks<Cursor> mProfileLoader = new LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            Uri uri = BraceletInfo.Profile.CONTENT_URI;
            return new CursorLoader(FacebookShareActivity.this, uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.moveToFirst()) {
                mProfileName = data.getString(data.getColumnIndex(Profile.COLUMN_NAME_PROFILE_NAME));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // TODO Auto-generated method stub
            
        }
    };
	private boolean mDrawFinsh;

    private void initView() {
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setVisibility(View.INVISIBLE);
        mEditText = (EditText) findViewById(R.id.message);
        mMeasureLayout = (LinearLayout) findViewById(R.id.share_measure);
        mCoachingLayout = (LinearLayout) findViewById(R.id.share_coaching);
        mPostButton = (Button) findViewById(R.id.post);
        mAgilityText = (TextView) findViewById(R.id.agility);
        mAnsAgeText = (TextView) findViewById(R.id.ans);
        mBPMText = (TextView) findViewById(R.id.bpm);
        mStatusView = (MStatusView) findViewById(R.id.status_view_id);
   	    mStatusView.setmIsDrawSmallPerson(false);
        mStatusView.setmDrawFinishListener(this);
        if (mType == Constants.TYPE_MEASURE) {
            mMeasureLayout.setVisibility(View.VISIBLE);
            mCoachingLayout.setVisibility(View.GONE);

            if (mMeasureResult != null) {
                mAgilityText = (TextView) findViewById(R.id.agility);
                mAnsAgeText = (TextView) findViewById(R.id.ans);
                mBPMText = (TextView) findViewById(R.id.bpm);
                mAgilityText.setText(String.valueOf(Utility.transformAgility(mMeasureResult
                        .getAgility())));
                mAnsAgeText.setText(String.valueOf(mMeasureResult.getAnsAge()));
                mBPMText.setText(String.valueOf(mMeasureResult.getBpm()));
                
            }
        } else if (mType == Constants.TYPE_COACHING) {
            mCoachingLayout.setVisibility(View.VISIBLE);
            mMeasureLayout.setVisibility(View.GONE);
            
            if(null != mCoachingResult){
                mAgilityText = (TextView) findViewById(R.id.level_name);
                mAnsAgeText = (TextView) findViewById(R.id.cycle_value);
                mBPMText = (TextView) findViewById(R.id.hit_value);
                mHitRateText = (TextView) findViewById(R.id.hitrate_value);
                mAnimImageView = (AnimatedImageView)findViewById(R.id.rate);
                mWhaleImageView = (AnimatedImageView)findViewById(R.id.complete_whale);
                setCoachingUI(mCoachingResult);
            }
        }
    }

    private void setCoachingUI(CoachingResult coachingResult){
        Resources res = this.getResources();
        if (mCoachingResult != null) {
            
            String name = res.getString(R.string.level);
            mAgilityText.setText(name+" " +mCoachingResult.getLevelName());
            int cycleTime = mCoachingResult.getCycle();
            mAnsAgeText.setText(changeStringStyle(res.getString(R.string.upper_cycle)+": "+ cycleTime));
            int hit = mCoachingResult.getHit();
            mBPMText.setText(changeStringStyle(res.getString(R.string.hit)+": "+hit));
            int hitRate = Utility.getHitRate(hit, cycleTime);
            mHitRateText.setText(changeStringStyle(res.getString(R.string.hit_rate)+": "+hitRate+"%"));
            
            int starNum = Utility.getHitStarNum(hit, cycleTime);
            mAnimImageView.setImageLevel(starNum);
            mWhaleImageView.setImageLevel(starNum);
        }
    }
    
    private SpannableString changeStringStyle(String str){
        TextAppearanceSpan tas = new TextAppearanceSpan(this, R.style.text_gray_style);
        SpannableString spannStr = new SpannableString(str);
        int end = str.indexOf(":");
        spannStr.setSpan(tas, 0, end +1 , 0);
        return spannStr;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.post:
        	if(!Utility.isInternetWorkValid(this)){
        		SmartToast.makeText(this, this.getResources().getString(R.string.network_unavaliable), Toast.LENGTH_LONG).show();
        		return;
        	}
            View view = null;
            if (mType == Constants.TYPE_MEASURE) {
                view = mMeasureLayout;
            } else if (mType == Constants.TYPE_COACHING) {
                view = mCoachingLayout;
            }
            
            if (view == null) {
                LogApp.Loge(TAG, "View is null!");
                return;
            }
            
            final Bitmap bmp = Utility.getScreenshot(view);
            
            if (bmp == null) {
                LogApp.Loge(TAG, "Bitmap is null!");
                return;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            String picDes = mEditText.getText().toString();
            removeDialog(SHOW_WAIT_DIALOG);
            showDialog(SHOW_WAIT_DIALOG);;
            Share.getShareInstance(this).onShareToFb(this,picDes,mProfileName, bytes, new ShareResultListener() {
                @Override
                public void onShareResult(boolean isSuccess) {
                    if (mProgressDialog != null
                            && Utility.isActivityLive(FacebookShareActivity.this)) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    if (isSuccess) {
                        showToast(getString(R.string.facebook_share_message_success));
                        //fihtdc 2013/11/25  fly.f.ren modified for share success logic issue beigin
                        FacebookShareActivity.this.finish();
                        //fihtdc 2013/11/25  fly.f.ren modified for share success logic issue end
                    } else {
                        showToast(getString(R.string.facebook_share_message_fail));
                    }
                    
                    Utility.recycleBitmap(bmp);
                }
            });
            break;

        default:
            break;
        }

        super.onClick(v);
    }
    

    public void showToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(FacebookShareActivity.this, msg,
                        Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case SHOW_WAIT_DIALOG:
            if (mProgressDialog != null && Utility.isActivityLive(FacebookShareActivity.this)) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.wait_message));
            mProgressDialog.setCancelable(false);
            return mProgressDialog;
        default:
            break;
        }

        return null;
    }

	@Override
	public void onDrawFinish() {
		  if(!mDrawFinsh){
			  mDrawFinsh = true;
			  mStatusView.setmIsDrawSmallPerson(true);
			  float endX = EkgRR.getEmotionMap_X(mMeasureResult.getAgeLF(),mStatusView.getWidth());
	  		  float endY = EkgRR.getEmotionMap_Y(mMeasureResult.getAgeHF(), mStatusView.getWidth());
	          mStatusView.setStatusViewPos(endX,endY);
		  }else{
			   mStatusView.setVisibility(View.VISIBLE);
		  }
		
	}

}
