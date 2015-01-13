package com.fihtdc.smartbracelet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CoachingResult implements Parcelable{

    private MeasureResult mMeasureResult;

    private String mLevelName = null;
    private int mCycle = 0;
    private int mHit = 0;
    private long mTime;
    
    public CoachingResult(){
        
    }
    
    public MeasureResult getMeasureResult() {
        return mMeasureResult;
    }

    public void setMeasureResult(MeasureResult mMeasureResult) {
        this.mMeasureResult = mMeasureResult;
    }

    public String getLevelName() {
        return mLevelName;
    }

    public void setLevelName(String mLevelName) {
        this.mLevelName = mLevelName;
    }

    public int getCycle() {
        return mCycle;
    }

    public void setCycle(int mCycle) {
        this.mCycle = mCycle;
    }

    public int getHit() {
        return mHit;
    }

    public void setHit(int mHit) {
        this.mHit = mHit;
    }

    public static final Parcelable.Creator<CoachingResult> CREATOR = new Creator<CoachingResult>() {

        @Override
        public CoachingResult createFromParcel(Parcel source) {
            CoachingResult param = new CoachingResult();
            //param.mMeasureResult = source.readParcelable(null);
            param.mLevelName = source.readString();
            param.mCycle = source.readInt();
            param.mHit = source.readInt();
            return param;
        }

        @Override
        public CoachingResult[] newArray(int size) {
            return new CoachingResult[size];
        }

    };
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeParcelable(mMeasureResult, flags/*PARCELABLE_WRITE_RETURN_VALUE*/);
        dest.writeString(mLevelName);
        dest.writeInt(mCycle);
        dest.writeInt(mHit);
        
    }

	public long getmTime() {
		return mTime;
	}

	public void setmTime(long mTime) {
		this.mTime = mTime;
	}
    
}
