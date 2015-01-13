package com.fihtdc.smartbracelet.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.fihtdc.smartbracelet.R;

public class MStatusView extends View{
    private Paint mPaint = new Paint();
    private TextPaint mTextPaint = new TextPaint();
    
    private final RectF mPassiveRect = new RectF();
    private final RectF mExcitableRect = new RectF();
    private final RectF mPessimisticRect = new RectF();
    private final RectF mRectAnixious = new RectF();
    private int mStatusViewWidth;
    private int mStatusViewHeight;
    private DrawFinishListener mDrawFinishListener;
    private boolean mIsRoundRect;
    private boolean mIsSmallFontRect;
    private boolean mIsDrawSmallPerson;
    public boolean ismIsDrawSmallPerson() {
		return mIsDrawSmallPerson;
	}

	public void setmIsDrawSmallPerson(boolean mIsDrawSmallPerson) {
		this.mIsDrawSmallPerson = mIsDrawSmallPerson;
	}

	private static final int RECT_ROUND_ARC = 30;
    private float mEndX;
    private float mEndY;
    
    public int getmStatusViewWidth() {
		return mStatusViewWidth;
	}

	public void setmStatusViewWidth(int mStatusViewWidth) {
		this.mStatusViewWidth = mStatusViewWidth;
	}

	public int getmStatusViewHeight() {
		return mStatusViewHeight;
	}

	public void setmStatusViewHeight(int mStatusViewHeight) {
		this.mStatusViewHeight = mStatusViewHeight;
	}

	public int getmRectWidth() {
		return mRectWidth;
	}

	public void setmRectWidth(int mRectWidth) {
		this.mRectWidth = mRectWidth;
	}

	public int getmRectHeight() {
		return mRectHeight;
	}

	public void setmRectHeight(int mRectHeight) {
		this.mRectHeight = mRectHeight;
	}

	public int getmCircleRadius() {
		return mCircleRadius;
	}

	public void setmCircleRadius(int mCircleRadius) {
		this.mCircleRadius = mCircleRadius;
	}

	private int mRectWidth;
    private int mRectHeight;

    private int mCircleRadius;
    private int mTransantCircleRadius;
    private float mFontHeight;

    public MStatusView(Context context) {
        super(context);
    }

    public interface DrawFinishListener{
    	public void onDrawFinish();
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mStatusViewWidth = getWidth();
        mStatusViewHeight = getWidth();
        mRectWidth = getWidth() / 2;
        mRectHeight = getWidth()/2;
        
        // passive rect
        mPassiveRect.set(0, 0, mRectWidth, mRectHeight);
        // excitable rect
        mExcitableRect.set(mRectWidth, 0, mStatusViewWidth, mRectHeight);
        // pessimisticrect rect
        mPessimisticRect.set(0, mRectHeight, mRectWidth, mStatusViewHeight);
        // anixious rect
        mRectAnixious.set(mRectWidth, mRectHeight, mStatusViewWidth, mStatusViewHeight);
        // circle
        mCircleRadius = (int) (2.8* mStatusViewWidth/11)/2;
        // translate  circle
        mTransantCircleRadius= (int) (3.0* mStatusViewWidth/11)/2;

    }

    public MStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MStatusView);
        mIsRoundRect = a.getBoolean(0, false);
        mIsSmallFontRect = a.getBoolean(1, false);
        a.recycle();
        initPaint();
    }
    
    private void initPaint(){
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
       if(!mIsSmallFontRect){
    	   mTextPaint.setTextSize(getResources().getDimension(R.dimen.state_view_text_size));
       }else{
    	   mTextPaint.setTextSize(getResources().getDimension(R.dimen.state_view_small_text_size));
       }
        FontMetrics fm = mTextPaint.getFontMetrics();
        mFontHeight = (float)Math.ceil(fm.descent - fm.top);
        
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if(mIsRoundRect){
        	  drawRoundRects(canvas);
        }else{
        	 drawRects(canvas);
        }
      
        drawCenter(canvas);
        drawText(canvas);
        drawArrows(canvas);
        if(mIsDrawSmallPerson){
        	drawSmallPerson(canvas);
        }
        canvas.restore();
        
        if(mDrawFinishListener!=null){
        	  mDrawFinishListener.onDrawFinish();
        }
      
    }
    
    
    
    private void drawSmallPerson(Canvas canvas) {
    	BitmapDrawable bd;
			bd = (BitmapDrawable) getResources().getDrawable(
	                R.drawable.health_coach_people_image);
		canvas.drawBitmap(bd.getBitmap(), mEndX, mEndY,
				new Paint());
		
	}

	private void drawRoundRects(Canvas canvas) {
    	  // Draw round rectangle
        mPaint.setColor(getResources().getColor(R.color.passive_color));
        canvas.drawRoundRect(mPassiveRect, RECT_ROUND_ARC,RECT_ROUND_ARC,mPaint);
        mPassiveRect.set(RECT_ROUND_ARC, 0, mRectWidth, mRectHeight);
        canvas.drawRect(mPassiveRect,mPaint);
        mPassiveRect.set(0, RECT_ROUND_ARC, mRectWidth, mRectHeight);
        canvas.drawRect(mPassiveRect,mPaint);
        
        mPaint.setColor(getResources().getColor(R.color.excitable_color));
        canvas.drawRoundRect(mExcitableRect,RECT_ROUND_ARC,RECT_ROUND_ARC, mPaint);
        mExcitableRect.set(mRectWidth, 0, mStatusViewWidth-RECT_ROUND_ARC, mRectHeight);
        canvas.drawRect(mExcitableRect,mPaint);
        mExcitableRect.set(mRectWidth, RECT_ROUND_ARC, mStatusViewWidth, mRectHeight);
        canvas.drawRect(mExcitableRect,mPaint);
        
        
        mPaint.setColor(getResources().getColor(R.color.pessimistic_color));
        canvas.drawRoundRect(mPessimisticRect,RECT_ROUND_ARC,RECT_ROUND_ARC ,mPaint);
        mPessimisticRect.set(0, mRectHeight, mRectWidth, mStatusViewHeight-RECT_ROUND_ARC);
        canvas.drawRect(mPessimisticRect,mPaint);
        mPessimisticRect.set(RECT_ROUND_ARC, mRectHeight,  mRectWidth, mStatusViewHeight);
        canvas.drawRect(mPessimisticRect,mPaint);
        

        mPaint.setColor(getResources().getColor(R.color.anxious_color));
        canvas.drawRoundRect(mRectAnixious,RECT_ROUND_ARC,RECT_ROUND_ARC, mPaint);
        mRectAnixious.set(mRectWidth, mRectHeight, mStatusViewWidth, mStatusViewHeight-RECT_ROUND_ARC);
        canvas.drawRect(mRectAnixious,mPaint);
        mRectAnixious.set(mRectWidth, mRectHeight, mStatusViewWidth-RECT_ROUND_ARC, mStatusViewHeight);
        canvas.drawRect(mRectAnixious,mPaint);
		
	}

	private void drawRects(Canvas canvas){
        // Draw rectangle
        mPaint.setColor(getResources().getColor(R.color.passive_color));
        canvas.drawRect(mPassiveRect,mPaint);
        
        mPaint.setColor(getResources().getColor(R.color.excitable_color));
        canvas.drawRect(mExcitableRect, mPaint);

        mPaint.setColor(getResources().getColor(R.color.pessimistic_color));
        canvas.drawRect(mPessimisticRect, mPaint);

        mPaint.setColor(getResources().getColor(R.color.anxious_color));
        canvas.drawRect(mRectAnixious, mPaint);
     
   
    }
    
    private void drawCenter(Canvas canvas){
        //outer circle
        mPaint.setColor(getResources().getColor(R.color.translate_while_color));
        canvas.drawCircle(mRectWidth, mRectHeight, mTransantCircleRadius, mPaint);
        //inner circle
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mRectWidth, mRectHeight, mCircleRadius, mPaint);
    }
    
    private void drawText(Canvas canvas){
        int textMargin = 20;
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Align.LEFT);
        canvas.drawText(getResources().getString(R.string.passive_area_flag), textMargin,
                mFontHeight, mTextPaint);

        mTextPaint.setTextAlign(Align.RIGHT);
        canvas.drawText(getResources().getString(R.string.excitable_area_flag), mStatusViewWidth
                - textMargin, mFontHeight, mTextPaint);

        mTextPaint.setTextAlign(Align.LEFT);
        canvas.drawText(getResources().getString(R.string.pessimistic_area_flag), textMargin,
                mStatusViewHeight - textMargin, mTextPaint);

        mTextPaint.setTextAlign(Align.RIGHT);
        canvas.drawText(getResources().getString(R.string.anxious_area_flag), mStatusViewWidth
                - textMargin, mStatusViewHeight - textMargin, mTextPaint);

        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        canvas.drawText(getResources().getString(R.string.balanced_area_flag), mRectWidth,
                mRectHeight + textMargin/2, mTextPaint);
    }
    
    private void drawArrows(Canvas canvas){
        int offestX = 30;
        int offestY = 45;
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.translate_while_color));

        Path path = new Path();
        path.moveTo(mRectWidth, 0);
        path.lineTo(mRectWidth + offestX, offestY);
        path.lineTo(mRectWidth - offestX, offestY);
        path.close();
        canvas.drawPath(path, mPaint);
        
        path.moveTo(mStatusViewWidth, mRectHeight);
        path.lineTo(mStatusViewWidth - offestY, mRectHeight - offestX);
        path.lineTo(mStatusViewWidth - offestY, mRectHeight + offestX);
        path.close();
        canvas.drawPath(path, mPaint);
        
        path.moveTo(mRectWidth, mStatusViewHeight);
        path.lineTo(mRectWidth - offestX, mStatusViewHeight - offestY);
        path.lineTo(mRectWidth + offestX, mStatusViewHeight - offestY);
        path.close();
        canvas.drawPath(path, mPaint);
        
        path.moveTo(0, mRectHeight);
        path.lineTo(offestY, mRectHeight - offestX);
        path.lineTo(offestY, mRectHeight + offestX);
        path.close();
        canvas.drawPath(path, mPaint);
    }

	public DrawFinishListener getmDrawFinishListener() {
		return mDrawFinishListener;
	}

	public void setmDrawFinishListener(DrawFinishListener mDrawFinishListener) {
		this.mDrawFinishListener = mDrawFinishListener;
	}

	public void setStatusViewPos(float endX, float endY) {
		mEndX =endX;
		mEndY = endY;
		mEndX = mEndX -getResources().getDrawable(
                R.drawable.health_coach_people_image).getIntrinsicWidth()/2;
		mEndY = mEndY -getResources().getDrawable(
                R.drawable.health_coach_people_image).getIntrinsicHeight() ;
		invalidate();
	}
}
