package com.fihtdc.smartbracelet.entity;

import java.security.spec.ECGenParameterSpec;

import android.util.Log;

import com.fihtdc.smartbracelet.util.LogApp;
import com.yl.ekgrr.EkgRR;

public class StatusPerson {
	enum Status{
		PASSIVE,EXCITABLE,PESSIMISTIC,ANIXIOUS,BALANCE
	}
	private float mEndX;
	private float mEndY;
	
	private float mStartX;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(mEndX);
		result = prime * result + Float.floatToIntBits(mEndY);
		result = prime * result
				+ ((mMResult == null) ? 0 : mMResult.hashCode());
		result = prime * result + Float.floatToIntBits(mPersonHeight);
		result = prime * result + Float.floatToIntBits(mPersonWidth);
		result = prime * result + Float.floatToIntBits(mStartX);
		result = prime * result + Float.floatToIntBits(mStartY);
		result = prime * result + ((mStatus == null) ? 0 : mStatus.hashCode());
		result = prime * result + Float.floatToIntBits(mStatusAreaWidth);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
	    StatusPerson person = (StatusPerson) obj;
	    if(person.mMResult.getAgeHF() == mMResult.getAgeHF() && person.mMResult.getAgeLF() == mMResult.getAgeLF()){
	    	return true;
	    }
		return false;
	}
	private float mStartY;
	private Status mStatus;
	private float mPersonHeight;
	private float mPersonWidth;
	private float mStatusAreaWidth;
	public float getmStatusAreaWidth() {
		return mStatusAreaWidth;
	}
	public void setmStatusAreaWidth(float mStatusAreaWidth) {
		this.mStatusAreaWidth = mStatusAreaWidth;
	}
	private MeasureResult mMResult;
	public StatusPerson(){
		
	}
	public StatusPerson(float mX, float mY) {
		super();
		this.setmEndX(mX);
		this.setmEndY(mY);
	}
	public StatusPerson(float mEndX, float mEndY, float mStartX, float mStartY) {
		super();
		this.setmEndX(mEndX);
		this.setmEndY(mEndY);
		this.mStartX = mStartX;
		this.mStartY = mStartY;
	}
	
	public float getmStartX() {
		return mStartX;
	}
	public void setmStartX(float mStartX) {
		this.mStartX = mStartX;
	}
	public float getmStartY() {
		return mStartY;
	}
	public void setmStartY(float mStartY) {
		this.mStartY = mStartY;
	}
	public Status getmStatus() {
		return mStatus;
	}
	public void setmStatus(Status mStatus) {
		this.mStatus = mStatus;
	}
	
	public Status calculcateStatus(){
		Log.i("Fly", "Emotion Status==="+mMResult.getEmotion());
		if(mMResult == null){
			return Status.BALANCE;
		}
		switch(mMResult.getEmotion()){
		case EkgRR.EMOSTATUS_BALANCED:
			return Status.BALANCE;
		case EkgRR.EMOSTATUS_ANXIOUS:
			return Status.ANIXIOUS;
		case  EkgRR.EMOSTATUS_EXCITABLE:
			return Status.EXCITABLE;
		case EkgRR.EMOSTATUS_PASSIVE:
			 return Status.PASSIVE;
		case EkgRR.EMOSTATUS_PESSIMISTIC:
			return Status.PESSIMISTIC;
		default:
			return Status.BALANCE;
		}
	}
		
	
	public float getmEndX() {
		return mEndX;
	}
	
	public void setmEndX(float mEndX) {
		this.mEndX = mEndX;
	}
	
	public float getmEndY() {
		return mEndY;
	}
	
	public void setmEndY(float mEndY) {
		this.mEndY = mEndY;
	}

	public void calculeFallPosition(int marginLeft) {
		calculcateStatus();
		mEndX = EkgRR.getEmotionMap_X(mMResult.getAgeLF(),mStatusAreaWidth);
		mEndY = EkgRR.getEmotionMap_Y(mMResult.getAgeHF(), mStatusAreaWidth);
		mEndX = mEndX -mPersonWidth/2+marginLeft;
		mEndY = mEndY -mPersonHeight ;
		LogApp.Logd("Fly", "mStatusAreaWidth====="+mStatusAreaWidth);
		LogApp.Logd("Fly", "mMResult.getAgeLF()====="+mMResult.getAgeLF());
		LogApp.Logd("Fly", "mMResult.getAgeHF()====="+mMResult.getAgeHF());
		LogApp.Logd("Fly", "mPersonWidth====="+mPersonWidth);
		LogApp.Logd("Fly", "mPersonHeight====="+mPersonHeight);
		LogApp.Logd("Fly", "mEndX====="+mEndX);
		LogApp.Logd("Fly", "mEndY====="+mEndY);
	}
	public float getmPersonHeight() {
		return mPersonHeight;
	}
	public void setmPersonHeight(float mPersonHeight) {
		this.mPersonHeight = mPersonHeight;
	}
	public float getmPersonWidth() {
		return mPersonWidth;
	}
	public void setmPersonWidth(float mPersonWidth) {
		this.mPersonWidth = mPersonWidth;
	}
	public MeasureResult getmMResult() {
		return mMResult;
	}
	public void setmMResult(MeasureResult mMResult) {
		this.mMResult = mMResult;
	}
}
