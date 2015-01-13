package com.fihtdc.smartbracelet.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.ProfileActivity;
import com.fihtdc.smartbracelet.provider.BraceletInfo;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Profile;
import com.fihtdc.smartbracelet.provider.BraceletProvider;
import com.fihtdc.smartbracelet.provider.BraceletSQLiteHelper;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.ChooserDialog;
import com.fihtdc.smartbracelet.view.ChooserDialog.onCompleteResultLisener;

public class ProifleBriefFragment extends CommonFragment implements OnClickListener,
        onCompleteResultLisener {
    private static final int SHOW_HEIGHT_CHOOSER_DIALOG = 10;
    private static final int SHOW_WEIGHT_CHOOSER_DIALOG = 11;
    private static final int SHOW_BIRTHDAY_CHOOSER_DIALOG = 12;
    
    private static final int MESSAGE_SHOW_COMPLETE_DIALOG = 1;

    private Activity mActivity;
    private TextView mNameHit;
    private View mNameFrame;
    private LinearLayout mGenderLayout;
    private LinearLayout mHeightLayout;
    private LinearLayout mWeightLayout;
    private LinearLayout mBirthdayLayout;
    private ImageView mGenderImage;
    private ImageView mClearImage;
    private TextView mSexTV;
    private TextView mHeightTV;
    private TextView mWeightTV;
    private TextView mBirthdayTV;
    private EditText mProfileNameET;
    private int mGender;
    private int mGenderCash;
    private String mProfileName;
    private String mProfileCashName;
    private int mHeightValue;
    private int mHeightCashValue;
    private int mHeightUnit;
    private int mHeightCashUnit;
    private int mWeightValue;
    private int mWeightCashValue;
    private int mWeightUnit;
    private int mWeightCashUnit;
    private String mBirthdayValueStr;
    private String mBirthdayCashValueStr;
    
    boolean mFromWelcome = false;
    boolean mHasProfile = false;
    boolean mSaveInstanceStateNull = true;
    
    Animation mFadeAnim;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFromWelcome = bundle.getBoolean(Constants.FROM_WELCOM_EXTRA, false);
        }
        
        if (savedInstanceState == null) {
            mSaveInstanceStateNull = true;
        } else {
            mSaveInstanceStateNull = false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.profile_brief_frg, null);
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNameHit = (TextView)view.findViewById(R.id.name_hit);
        mNameFrame = view.findViewById(R.id.name_frame);
        mGenderImage = (ImageView)view.findViewById(R.id.sex_imbtn);
        mClearImage = (ImageView)view.findViewById(R.id.clear);
        mGenderLayout = (LinearLayout)view.findViewById(R.id.gender_layout);
        mHeightLayout = (LinearLayout)view.findViewById(R.id.height_layout);
        mWeightLayout = (LinearLayout)view.findViewById(R.id.weight_layout);
        mBirthdayLayout = (LinearLayout)view.findViewById(R.id.birthday_layout);
        mProfileNameET = (EditText) view.findViewById(R.id.name);
        mSexTV = (TextView) view.findViewById(R.id.sex_text);
        mHeightTV = (TextView) view.findViewById(R.id.height_text);
        mWeightTV = (TextView) view.findViewById(R.id.weight_text);
        mBirthdayTV = (TextView) view.findViewById(R.id.birthday_text);
        mClearImage.setOnClickListener(this);
        mGenderImage.setOnClickListener(this);
        view.findViewById(R.id.height_imbtn).setOnClickListener(this);
        view.findViewById(R.id.weight_imbtn).setOnClickListener(this);
        view.findViewById(R.id.birthday_imbtn).setOnClickListener(this);
        
        mFadeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);
        mProfileNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mClearImage.setVisibility(View.VISIBLE);
                } else {
                    mClearImage.setVisibility(View.INVISIBLE);
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String whole = s.toString();
                    String last = whole.substring(whole.length() - 1, whole.length());

                    //Can not input blank when there is no any character
                    if (whole.length() == 1 && " ".equals(last)) {
                        s.delete(whole.length() - 1, whole.length());
                    }
                    
                } catch (Exception e) {
                    
                }
            }
        });
        
        mProfileNameET.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //TODO
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    showCompleteDialog();
                }
                return false;
            }
        });
        getLoaderManager().initLoader(0, null, mProfileCursor);

    }
    
    public void startViewsAnimation(){
        if (TextUtils.isEmpty(mProfileNameET.getText())) {
            mNameFrame.startAnimation(mFadeAnim);
            mNameHit.startAnimation(mFadeAnim);
        } else {
            mNameFrame.setAnimation(null);
            mNameHit.setAnimation(null);
        }
        
        if (TextUtils.isEmpty(mSexTV.getText())) {
            mGenderLayout.startAnimation(mFadeAnim);
        } else {
            mGenderLayout.setAnimation(null);
        }
        
        if (TextUtils.isEmpty(mHeightTV.getText())) {
            mHeightLayout.startAnimation(mFadeAnim);
        } else {
            mHeightLayout.setAnimation(null);
        }
        
        if (TextUtils.isEmpty(mWeightTV.getText())) {
            mWeightLayout.startAnimation(mFadeAnim);
        } else {
            mWeightLayout.setAnimation(null);
        }
        
        if (TextUtils.isEmpty(mBirthdayTV.getText())) {
            mBirthdayLayout.startAnimation(mFadeAnim);
        } else {
            mBirthdayLayout.setAnimation(null);
        }
    }
    
    public void stopViewsAnimation(){
        if (!TextUtils.isEmpty(mProfileNameET.getText())) {
            mNameHit.setAnimation(null);
            mNameFrame.setAnimation(null);
        }
        
        if (!TextUtils.isEmpty(mSexTV.getText())) {
            mGenderLayout.setAnimation(null);
        }
        
        if (!TextUtils.isEmpty(mHeightTV.getText())) {
            mHeightLayout.setAnimation(null);
        }
        
        if (!TextUtils.isEmpty(mWeightTV.getText())) {
            mWeightLayout.setAnimation(null);
        }
        
        if (!TextUtils.isEmpty(mBirthdayTV.getText())) {
            mBirthdayLayout.setAnimation(null);
        }
    }

    private void setGenderText(int gender) {
        if (Profile.GENDER_MALE == gender) {
            mGenderImage.setImageResource(R.drawable.profile_male_ic);
            mSexTV.setText(R.string.profile_gender_male);
        } else if (Profile.GENDER_FEMALE == gender){
            mGenderImage.setImageResource(R.drawable.profile_female_ic);
            mSexTV.setText(R.string.profile_gender_female);
        } else {
            mGenderImage.setImageResource(R.drawable.profile_gender_ic);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.sex_imbtn:
            if (mGenderCash == Profile.GENDER_MALE) {
                mGenderCash =  Profile.GENDER_FEMALE;
            } else if (mGenderCash == Profile.GENDER_FEMALE) {
                mGenderCash = Profile.GENDER_MALE;
            }
            setGenderText(mGenderCash);
            showCompleteDialog();
            break;
        case R.id.height_imbtn:
            showDialog(SHOW_HEIGHT_CHOOSER_DIALOG);
            break;
        case R.id.weight_imbtn:
            showDialog(SHOW_WEIGHT_CHOOSER_DIALOG);
            break;
        case R.id.birthday_imbtn:
            showDialog(SHOW_BIRTHDAY_CHOOSER_DIALOG);
            break;
        case R.id.clear:
            mProfileNameET.setText(null);
            if (!mHasProfile && mFromWelcome){
                if (TextUtils.isEmpty(mProfileNameET.getText())) {
                    mNameFrame.startAnimation(mFadeAnim);
                    mNameHit.startAnimation(mFadeAnim);
                }
            }
            break;

        default:
            break;
        }

    }

    private void showDialog(int type) {
        Dialog dialog = null;
        switch (type) {
        case SHOW_HEIGHT_CHOOSER_DIALOG:
            String[] heightValue = new String[] { String.valueOf(mHeightCashValue),
                    String.valueOf(mHeightCashUnit) };
            dialog = new ChooserDialog(mActivity, ChooserDialog.CHOOSER_HEIGHT_DIALOG,
                    heightValue, this);
            break;
        case SHOW_WEIGHT_CHOOSER_DIALOG:
            String[] weightValue = new String[] {  String.valueOf(mWeightCashValue),
                    String.valueOf(mWeightCashUnit) };
            dialog = new ChooserDialog(mActivity, ChooserDialog.CHOOSER_WEIGHT_DIALOG, weightValue,
                    this);
          
            break;
        case SHOW_BIRTHDAY_CHOOSER_DIALOG:
            String[] birthdayValue = new String[] { mBirthdayCashValueStr };
            dialog = new ChooserDialog(mActivity, ChooserDialog.CHOOSER_BIRTHDAY_DIALOG,
                    birthdayValue, this);
            break;

        default:
            break;
        }
        if (dialog != null) {
            dialog.show();
        }

    }

    @Override
    public void onResult(String[] value, int dialogType) {
        switch (dialogType) {
        case ChooserDialog.CHOOSER_HEIGHT_DIALOG:
            mHeightCashValue = Integer.valueOf(value[0]);
            mHeightCashUnit = Integer.valueOf(value[1]);
            setHeightText(mHeightCashUnit);
            
            break;
        case ChooserDialog.CHOOSER_WEIGHT_DIALOG:
            mWeightCashValue = Integer.valueOf(value[0]);
            mWeightCashUnit = Integer.valueOf(value[1]);
            setWeightText(mWeightCashUnit);
            break;
        case ChooserDialog.CHOOSER_BIRTHDAY_DIALOG:
            mBirthdayCashValueStr = value[0];
            int age = Utility.getAge(mBirthdayCashValueStr);
            if (age >= 0) {
                mBirthdayTV.setText(String.valueOf(age));
            } else {
                mBirthdayCashValueStr = mBirthdayValueStr;
            }
            break;

        default:
            break;
        }
        
        showCompleteDialog();
    }

    private void setHeightText(int unitType) {
        switch (unitType) {
        case ChooserDialog.CHOOSER_HEIGHT_DIALOG_UNIT_IS_CM:
            mHeightTV.setText(String.valueOf(mHeightCashValue)
                    + getString(R.string.profile_unit_cm));
            break;
        case ChooserDialog.CHOOSER_HEIGHT_DIALOG_UNIT_IS_IN:
            mHeightTV.setText(String.valueOf(mHeightCashValue)
                    + getString(R.string.profile_unit_in));
            break;
        default:
            break;
        }
    }

    private void setWeightText(int unitType) {
        switch (unitType) {
        case ChooserDialog.CHOOSER_WEIGHT_DIALOG_UNIT_IS_KG:
            mWeightTV.setText(String.valueOf(mWeightCashValue)
                    + getString(R.string.profile_unit_kg));
            break;
        case ChooserDialog.CHOOSER_WEIGHT_DIALOG_UNIT_IS_LB:
            mWeightTV.setText(String.valueOf(mWeightCashValue)
                    + getString(R.string.profile_unit_lb));
            break;
        default:
            break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void saveProfileInfo() {
        mProfileCashName = mProfileNameET.getText().toString().trim();
        if (mHeightValue != mHeightCashValue || mWeightValue != mWeightCashValue
                || !mBirthdayCashValueStr.equals(mBirthdayValueStr)
                || !mProfileCashName.equals(mProfileName) || mGender != mGenderCash) {
            Uri uri = BraceletInfo.Profile.CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", 0);
            contentValues.put(Profile.COLUMN_NAME_PROFILE_NAME, mProfileCashName);
            contentValues.put(Profile.COLUMN_NAME_PROFILE_GENDER, mGenderCash);
            contentValues.put(Profile.COLUMN_NAME_HEIGHT, mHeightCashValue);
            contentValues.put(Profile.COLUMN_NAME_HEIGHT_UNIT, mHeightCashUnit);
            contentValues.put(Profile.COLUMN_NAME_WEIGHT, mWeightCashValue);
            contentValues.put(Profile.COLUMN_NAME_WEIGHT_UNIT, mWeightCashUnit);
            contentValues.put(Profile.COLUMN_NAME_BIRTHDAY, mBirthdayCashValueStr);
            String where = "_id=?";
            ContentResolver cr = mActivity.getContentResolver();
            
            final ArrayList<ContentProviderOperation> oprList = new ArrayList<ContentProviderOperation>();
            oprList.add(ContentProviderOperation.newDelete(uri)
                    /*.withSelection(where, new String[] { "0" })*/.build());
            oprList.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());

            try {
                cr.applyBatch(BraceletInfo.AUTHORITY, oprList);
            } catch (RemoteException e) {

            } catch (OperationApplicationException e) {

            } finally {
                oprList.clear();
            }
        }
    }

    /*
     * @param showToast
     * @Description: check profile is edit complete
     */
    public boolean checkProfileComplete(boolean showToast) {
        if (TextUtils.isEmpty(mProfileNameET.getText())) {
            if (showToast) {
                Toast.makeText(mActivity, R.string.profile_name_cannot_be_null, 
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        if (TextUtils.isEmpty(mSexTV.getText())) {
            if (showToast) {
                Toast.makeText(mActivity, R.string.profile_gender_cannot_be_null,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        if (mHeightCashValue <= 0) {
            if (showToast) {
                Toast.makeText(mActivity, R.string.profile_height_cannot_be_null,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        if (mWeightCashValue <= 0) {
            if (showToast) {
                Toast.makeText(mActivity, R.string.profile_height_cannot_be_null,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        if (TextUtils.isEmpty(mBirthdayCashValueStr)) {
            if (showToast) {
                Toast.makeText(mActivity, R.string.profile_birthday_cannot_be_null,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true;
    }
    
    Handler mHandle = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SHOW_COMPLETE_DIALOG:
                showEditProfileDialog();
                break;

            default:
                break;
            }
        }
        
    };

    private LoaderCallbacks<Cursor> mProfileCursor = new LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            Uri uri = BraceletInfo.Profile.CONTENT_URI;
            return new CursorLoader(mActivity, uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
            if (cursor != null && cursor.moveToFirst()) {
                mProfileName = cursor.getString(cursor
                        .getColumnIndex(Profile.COLUMN_NAME_PROFILE_NAME));

                mHeightValue = cursor.getInt(cursor.getColumnIndex(Profile.COLUMN_NAME_HEIGHT));
                mHeightUnit = cursor.getInt(cursor.getColumnIndex(Profile.COLUMN_NAME_HEIGHT_UNIT));
                mWeightValue = cursor.getInt(cursor.getColumnIndex(Profile.COLUMN_NAME_WEIGHT));
                mWeightUnit = cursor.getInt(cursor.getColumnIndex(Profile.COLUMN_NAME_WEIGHT_UNIT));
                mBirthdayValueStr = cursor.getString(cursor
                        .getColumnIndex(Profile.COLUMN_NAME_BIRTHDAY));
                mGender = cursor.getInt(cursor.getColumnIndex(Profile.COLUMN_NAME_PROFILE_GENDER));
                mHeightCashValue = mHeightValue;
                mHeightCashUnit = mHeightUnit;
                mWeightCashValue = mWeightValue;
                mWeightCashUnit = mWeightUnit;
                mBirthdayCashValueStr = mBirthdayValueStr;
                mGenderCash = mGender;
                mProfileCashName = mProfileName;

                mProfileNameET.setText(mProfileName);
                setGenderText(mGender);
                setHeightText(mHeightUnit);
                setWeightText(mWeightUnit);
                int age = Utility.getAge(mBirthdayValueStr);
                
                if (age >= 0) {
                    mBirthdayTV.setText(String.valueOf(age));
                } else {
                    mBirthdayCashValueStr = mBirthdayValueStr;
                }
                
                mHasProfile = true;
            } else {
                mHasProfile = false;
            }

            mHandle.sendEmptyMessage(MESSAGE_SHOW_COMPLETE_DIALOG);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            // TODO Auto-generated method stub

        }

    };
    
    private void showCompleteDialog() {
        // If first time to edit profile and edit complete, to show prompt
        // dialog
        if (!mHasProfile && mFromWelcome && checkProfileComplete(false)) {
            if (mActivity != null && !mActivity.isFinishing()) {
                saveProfileInfo();
                Utility.showAlertDialog(mActivity, AlertDialogFragment.DIALOG_TYPE_COMPLETE, null);
            }
        }
        
        if (!mHasProfile && mFromWelcome){
            stopViewsAnimation();
        }
    }
    
    private void showEditProfileDialog() {
        // If first time to edit profile, to show prompt dialog
        // mSaveInstanceStateNull avoid to show much fragment when config change
        if (mSaveInstanceStateNull && mFromWelcome && !mHasProfile) {
            Utility.showAlertDialog(mActivity, AlertDialogFragment.DIALOG_TYPE_EDIT_PROFILE, null);
        }
    }
}
