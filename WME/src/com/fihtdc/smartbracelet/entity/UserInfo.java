package com.fihtdc.smartbracelet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {
    int gender;
    int age;

    public UserInfo(){
        
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {

        // TODO Auto-generated method stub
        return 0;
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        
        @Override
        public UserInfo createFromParcel(Parcel source) {
            UserInfo param = new UserInfo();
            param.gender = source.readInt();
            param.age = source.readInt();
            return param;
        }
        
        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
        
    };
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(gender);
        dest.writeInt(age);
    }

}
