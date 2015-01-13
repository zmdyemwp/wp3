/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * A type of <code>CameraPreference</code> whose number of possible values
 * is limited.
 */
public class ListPreference extends CameraPreference {
    private final String TAG = "ListPreference";
    private final String mKey;
    private String mValue;
    private final CharSequence[] mDefaultValues;

    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
	//2012/3/24 Jinsheng add for IRMI.B-1781 begin
    private CharSequence[] mInitEntries;
    private CharSequence[] mInitEntryValues;
	//2012/3/24 Jinsheng add for IRMI.B-1781 end
    private boolean mLoaded = false;
    
	//2012/3/24 Jinsheng add for IRMI.B-1781 begin
    AttributeSet mAttrs;
    Context mContext;
	//2012/3/24 Jinsheng add for IRMI.B-1781 end

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
		//2012/3/24 Jinsheng add for IRMI.B-1781 begin
        mAttrs = attrs;
        mContext = context;
		//2012/3/24 Jinsheng add for IRMI.B-1781 end

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ListPreference, 0, 0);

        mKey = Util.checkNotNull(
                a.getString(R.styleable.ListPreference_key));

        // We allow the defaultValue attribute to be a string or an array of
        // strings. The reason we need multiple default values is that some
        // of them may be unsupported on a specific platform (for example,
        // continuous auto-focus). In that case the first supported value
        // in the array will be used.
        int attrDefaultValue = R.styleable.ListPreference_defaultValue;
        TypedValue tv = a.peekValue(attrDefaultValue);
        if (tv != null && tv.type == TypedValue.TYPE_REFERENCE) {
            mDefaultValues = a.getTextArray(attrDefaultValue);
        } else {
            mDefaultValues = new CharSequence[1];
            mDefaultValues[0] = a.getString(attrDefaultValue);
        }

		//2012/3/24 Jinsheng add for IRMI.B-1781 begin
        mInitEntries = a.getTextArray(R.styleable.ListPreference_entries);
        mInitEntryValues = a.getTextArray(R.styleable.ListPreference_entryValues);
		//2012/3/24 Jinsheng add for IRMI.B-1781 end
        setEntries(a.getTextArray(R.styleable.ListPreference_entries));
        setEntryValues(a.getTextArray(
                R.styleable.ListPreference_entryValues));
        a.recycle();
    }

    public String getKey() {
        return mKey;
    }

    public CharSequence[] getEntries() {
        return mEntries;
    }

    public CharSequence[] getEntryValues() {
        return mEntryValues;
    }
    
    //2012/3/26 Jinsheng add for IRMI.B-1781 begin
    public CharSequence[] getInitEntries() {
        return mInitEntries;
    }
    
    public CharSequence[] getInitEntryValues() {
        return mInitEntryValues;
    }
    //2012/3/26 Jinsheng add for IRMI.B-1781 end

    public void setEntries(CharSequence entries[]) {
        mEntries = entries == null ? new CharSequence[0] : entries;
    }

    public void setEntryValues(CharSequence values[]) {
        mEntryValues = values == null ? new CharSequence[0] : values;
    }

    public String getValue() {
        if (!mLoaded) {
            mValue = getSharedPreferences().getString(mKey,
                    findSupportedDefaultValue());
            mLoaded = true;
        }
        return mValue;
    }

    // Find the first value in mDefaultValues which is supported.
    private String findSupportedDefaultValue() {
        for (int i = 0; i < mDefaultValues.length; i++) {
            for (int j = 0; j < mEntryValues.length; j++) {
                // Note that mDefaultValues[i] may be null (if unspecified
                // in the xml file).
                if (mEntryValues[j].equals(mDefaultValues[i])) {
                    return mDefaultValues[i].toString();
                }
            }
        }
        return null;
    }

    public void setValue(String value) {
        if (findIndexOfValue(value) < 0) throw new IllegalArgumentException();
        mValue = value;
        persistStringValue(value);
    }

    public void setValueIndex(int index) {
        setValue(mEntryValues[index].toString());
    }

    public int findIndexOfValue(String value) {
        for (int i = 0, n = mEntryValues.length; i < n; ++i) {
            if (Util.equals(mEntryValues[i], value)) return i;
        }
        return -1;
    }

    public String getEntry() {
        return mEntries[findIndexOfValue(getValue())].toString();
    }

    protected void persistStringValue(String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(mKey, value);
        editor.apply();
    }

    @Override
    public void reloadValue() {
        this.mLoaded = false;
    }

    public void filterUnsupported(List<String> supported) {
        ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
        ArrayList<CharSequence> entryValues = new ArrayList<CharSequence>();
        for (int i = 0, len = mEntryValues.length; i < len; i++) {
            if (supported.indexOf(mEntryValues[i].toString()) >= 0) {
                entries.add(mEntries[i]);
                entryValues.add(mEntryValues[i]);
            }
        }
        int size = entries.size();
        mEntries = entries.toArray(new CharSequence[size]);
        mEntryValues = entryValues.toArray(new CharSequence[size]);
    }
    
    //2012/3/23 Jinsheng add for IRMI.B-1781 begin
    public void filterUnsupportedDynamic(List<String> supported){   
        ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
        ArrayList<CharSequence> entryValues = new ArrayList<CharSequence>();
        for (int i = 0, len = mInitEntryValues.length; i < len; i++) {
            if (supported.indexOf(mInitEntryValues[i].toString()) >= 0) {
                entries.add(mInitEntries[i]);
                entryValues.add(mInitEntryValues[i]);
            }
        }
        int size = entries.size();
        mEntries = entries.toArray(new CharSequence[size]);
        mEntryValues = entryValues.toArray(new CharSequence[size]);
    
        //filterUnsupported(supported);
        print();
    }
    //2012/3/23 Jinsheng add for IRMI.B-1781 begin end

    public void print() {
        Log.v(TAG, "Preference key=" + getKey() + ". value=" + getValue());
        for (int i = 0; i < mEntryValues.length; i++) {
            Log.v(TAG, "entryValues[" + i + "]=" + mEntryValues[i]);
        }
    }
}