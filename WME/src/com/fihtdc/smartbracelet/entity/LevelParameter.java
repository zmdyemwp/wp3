package com.fihtdc.smartbracelet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class LevelParameter implements Parcelable{
    private String level = null;
    private int inhale ;
    private int hold ;
    private int outhale ;
    private int times;
    public LevelParameter(){
        
    }
    public LevelParameter(String level,int inhale,int hold,int outhale,int times){
        this.level = level;
        this.inhale = inhale;
        this.hold = hold;
        this.outhale = outhale;
        this.times = times;
    }
    public String getLevel() {
        return level;
    }
    public int getInhale() {
        return inhale;
    }
    public int getHold() {
        return hold;
    }
    public int getOuthale() {
        return outhale;
    }
    public int getTimes(){
        return times;
    }
    public static final Parcelable.Creator<LevelParameter> CREATOR = new Creator<LevelParameter>() {
        
        @Override
        public LevelParameter createFromParcel(Parcel source) {
            Log.d("Pei","createFromParcel");
            LevelParameter param = new LevelParameter();
            param.level = source.readString();
            param.inhale = source.readInt();
            param.hold = source.readInt();
            param.outhale = source.readInt();
            param.times = source.readInt();
            return param;
        }
        
        @Override
        public LevelParameter[] newArray(int size) {
            return new LevelParameter[size];
        }
        
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d("Pei","writeToParcel");
        dest.writeString(level);
        dest.writeInt(inhale);
        dest.writeInt(hold);
        dest.writeInt(outhale);
        dest.writeInt(times);
    }
    
}
