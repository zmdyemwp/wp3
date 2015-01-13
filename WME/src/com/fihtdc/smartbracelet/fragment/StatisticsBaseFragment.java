package com.fihtdc.smartbracelet.fragment;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.entity.MeasureResult;
import com.fihtdc.smartbracelet.entity.StatusPerson;
import com.fihtdc.smartbracelet.view.MStatusView;
import com.fihtdc.smartbracelet.view.ShapeHolder;

public class StatisticsBaseFragment extends CommonFragment implements MStatusView.DrawFinishListener{
	FrameLayout mStatusContainer;
	MyAnimationView mAnimView;
	private List<StatusPerson> mStatusPersonList =new ArrayList<StatusPerson>();
	private int mStatusCount;
	protected MStatusView mStatusView;
	protected boolean mLauncherFlag;
	private Cursor mCursor;
	protected MeasureResult mMeasureResult;
	
	public Drawable mPersonImage;
	public Drawable mPersonWhileImage;
	int mPersonImageWidth;
	int mPersonImageHeight;
	private int mVirtualMPersonCount;
	public boolean mIsCancelAnimationFinish = true;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}

	private void initView() {
		mStatusContainer = (FrameLayout) getActivity().findViewById(R.id.container);
		mStatusView = (MStatusView) getActivity().findViewById(R.id.status_view_id);
		mStatusView .setmDrawFinishListener(this);
		
		mPersonImage = getResources().getDrawable(
                R.drawable.health_coach_people_image);
		mPersonWhileImage = getResources().getDrawable(
                R.drawable.health_coach_whitepeople_image);
		mPersonImageHeight = mPersonImage.getIntrinsicHeight();
		mPersonImageWidth = mPersonImage.getIntrinsicWidth();
		addOnGlobalLayoutListener(mStatusView);
	}
	
	protected void onLauncherStatusAnim(){
		if(mLauncherFlag) {
			onInitSummeryMStatusPerson();
			onInitHistory();
		}
	}
	
	private void onInitHistory() {
		if(mStatusPersonList.size()>=1){
			if(mAnimView !=null){
				mAnimView.cancelAnimation();
			}
			mStatusCount = 0;
			mStatusContainer.removeAllViews();
			mAnimView = new MyAnimationView(getActivity(),false);
			mStatusContainer.addView(mAnimView);
			mAnimView.startAnimation(mStatusPersonList.get(mStatusCount),true);
		}else{
			mStatusContainer.removeAllViews();
		}
	}
    public void removeAllViews(){
    	mStatusContainer.removeAllViews();
    }
	
	protected void onLauncherSingleStatusAnim(StatusPerson status){
		if(mLauncherFlag) {
			mAnimView = new MyAnimationView(getActivity(),true);
			mStatusContainer.addView(mAnimView);
			mAnimView.startAnimation(status,false);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY",
				"WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}
	
	public void onInitSummeryMStatusPerson() {
		if(mMeasureResult!=null){
			if(	mStatusPersonList != null){
				mStatusPersonList.clear();
			}
			StatusPerson person = new StatusPerson();
			person.setmStartX(0);
			person.setmStartY(mStatusView.getmStatusViewHeight() - mPersonImageHeight);
			person.setmStatusAreaWidth(mStatusView.getmStatusViewHeight());
			person.setmPersonHeight(mPersonImageHeight);
			person.setmPersonWidth(mPersonImageWidth);
			Log.i("Fly", "mStatusHeight===="+mStatusView.getmRectHeight());
			Log.i("Fly", "rect width"+mStatusView.getmRectWidth());
			person.setmMResult(mMeasureResult);
			person.calculeFallPosition(0);
			mStatusPersonList.add(person);
		}
	}
	public class XYHolder {
		private float mX;
		private float mY;

		public XYHolder(float x, float y) {
			mX = x;
			mY = y;
		}

		public float getX() {
			return mX;
		}

		public void setX(float x) {
			mX = x;
		}

		public float getY() {
			return mY;
		}

		public void setY(float y) {
			mY = y;
		}
	}

	public class XYEvaluator implements TypeEvaluator {
		public Object evaluate(float fraction, Object startValue,
				Object endValue) {
			XYHolder startXY = (XYHolder) startValue;
			XYHolder endXY = (XYHolder) endValue;
			return new XYHolder(startXY.getX() + fraction
					* (endXY.getX() - startXY.getX()), startXY.getY()
					+ fraction * (endXY.getY() - startXY.getY()));
		}
	}

	public class BallXYHolder {

		private ShapeHolder mBall;

		public BallXYHolder(ShapeHolder ball) {
			mBall = ball;
		}

		public void setXY(XYHolder xyHolder) {
			mBall.setX(xyHolder.getX());
			mBall.setY(xyHolder.getY());
		}

		public XYHolder getXY() {
			return new XYHolder(mBall.getX(), mBall.getY());
		}
	}
   
	
	
	
	public class MyAnimationView extends View implements
			ValueAnimator.AnimatorUpdateListener {

		public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
		ValueAnimator bounceAnim = null;
		ShapeHolder ball = null;
		BallXYHolder ballHolder = null;
		private boolean isMutilPerson;
		private boolean mIsWhilePeople;

		public MyAnimationView(Context context,boolean isWhilePeopel) {
			super(context);
			mIsWhilePeople = isWhilePeopel;
			ball = createBall(0, 0);
			ballHolder = new BallXYHolder(ball);
		}

		private void createAnimation(StatusPerson person) {
			if (bounceAnim == null) {
				XYHolder startXY = new XYHolder(person.getmStartX(),
						person.getmStartY());
				XYHolder endXY = new XYHolder(person.getmEndX(), person.getmEndY());
				bounceAnim = ObjectAnimator.ofObject(ballHolder, "XY",
						new XYEvaluator(), startXY, endXY);
				bounceAnim.setDuration(1500);
				bounceAnim.addUpdateListener(this);
				bounceAnim.addListener(new AnimatorListener() {

					@Override
					public void onAnimationStart(Animator arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animator arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animator arg0) {
						Log.i("Fly", "status person end");
						if(isMutilPerson){
							if(mIsCancelAnimationFinish){
								bounceAnim = null;
								mStatusCount++;
								notifyContainerChange();
							}
						}
						if(mIsWhilePeople){
							MyAnimationView.this.setVisibility(View.GONE);
						}
					}

					@Override
					public void onAnimationCancel(Animator arg0) {
						if(mIsWhilePeople){
							mStatusContainer.removeView(MyAnimationView.this);
						}
					}
				});
			}
		}
		public void cancelAnimation(){
			mIsCancelAnimationFinish =false;
			if(bounceAnim!=null){
				bounceAnim.cancel();
				bounceAnim.end();
				bounceAnim = null;
			}
			mIsCancelAnimationFinish = true;
			
		}
		public void startAnimation(StatusPerson person,boolean mutilPerson) {
			isMutilPerson =mutilPerson;
			createAnimation(person);
			bounceAnim.start();
		}
	
		private ShapeHolder createBall(float x, float y) {
			OvalShape circle = new OvalShape();
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            return shapeHolder;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.save();
			BitmapDrawable bd;
			if(mIsWhilePeople){
				bd = (BitmapDrawable) mPersonWhileImage;
			}else{
				bd = (BitmapDrawable) mPersonImage;
			}
			canvas.drawBitmap(bd.getBitmap(), ball.getX(), ball.getY(),
					new Paint());
			canvas.restore();
		}

		public void onAnimationUpdate(ValueAnimator animation) {
			invalidate();
		}

	}

	protected void notifyContainerChange() {
		if (mStatusCount < mStatusPersonList.size()) {
			if(getActivity()!=null && !isDetached()){
				mAnimView = new MyAnimationView(getActivity(),false);
				mStatusContainer.addView(mAnimView);
				mAnimView.startAnimation(mStatusPersonList.get(mStatusCount),true);
			}
		}
	}

	@Override
	public void onDrawFinish() {
		 if(!mLauncherFlag ){
			 mLauncherFlag = true;
			 onLauncherStatusAnim();
		 }
		 mLauncherFlag = true;
	}
    
	public Cursor getmCursor() {
		return mCursor;
	}

	public void setmCursor(Cursor mCursor) {
		this.mCursor = mCursor;
	}

	public MeasureResult getmMeasureResult() {
		return mMeasureResult;
	}

	public void setmMeasureResult(MeasureResult mMeasureResult) {
		this.mMeasureResult = mMeasureResult;
		
	}

	public int getmVirtualMPersonCount() {
		return mVirtualMPersonCount;
	}

	public void setmVirtualMPersonCount(int mVirtualMPersonCount) {
		this.mVirtualMPersonCount = mVirtualMPersonCount;
	}
	public List<StatusPerson> getmStatusPersonList() {
		return mStatusPersonList;
	}

	public void setmStatusPersonList(List<StatusPerson> mStatusPersonList) {
		if(mStatusPersonList != null){
			this.mStatusPersonList = mStatusPersonList;
		}else{
			this.mStatusPersonList =new ArrayList<StatusPerson>();
		}
	
	}


}
