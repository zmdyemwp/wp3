package com.fihtdc.smartbracelet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class MeasureResult implements Parcelable {
    int agility;
    int ansAge;
    int bpm;
    int sdnn;
    int emotion;
    float ageLF;
    public float getAgeLF() {
		return ageLF;
	}

	public void setAgeLF(float ageLF) {
		this.ageLF = ageLF;
	}

	public float getAgeHF() {
		return ageHF;
	}

	public void setAgeHF(float ageHF) {
		this.ageHF = ageHF;
	}

	float ageHF;
    public int getSdnn() {
		return sdnn;
	}

	public void setSdnn(int sdnn) {
		this.sdnn = sdnn;
	}

	public int getEmotion() {
		return emotion;
	}

	public void setEmotion(int emotion) {
		this.emotion = emotion;
	}
	public MeasureResult() {

    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getAnsAge() {
        return ansAge;
    }

    public void setAnsAge(int ansAge) {
        this.ansAge = ansAge;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public static final Parcelable.Creator<MeasureResult> CREATOR = new Creator<MeasureResult>() {

        @Override
        public MeasureResult createFromParcel(Parcel source) {
            MeasureResult param = new MeasureResult();
            param.agility = source.readInt();
            param.ansAge = source.readInt();
            param.bpm = source.readInt();
            param.emotion = source.readInt();
            param.ageLF = source.readFloat();
            param.ageHF = source.readFloat();
            return param;
        }

        @Override
        public MeasureResult[] newArray(int size) {
            return new MeasureResult[size];
        }

    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(agility);
        dest.writeInt(ansAge);
        dest.writeInt(bpm);
        dest.writeInt(emotion);
        dest.writeFloat(ageLF);
        dest.writeFloat(ageHF);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
}
