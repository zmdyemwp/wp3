package com.fihtdc.smartbracelet.fragment;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.GuestProfileActivity;
import com.fihtdc.smartbracelet.activity.UserChooserActivity;
import com.fihtdc.smartbracelet.activity.MeasureActivity;
import com.fihtdc.smartbracelet.activity.TutorialActivity;
import com.fihtdc.smartbracelet.provider.BraceletInfo;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Profile;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;

public class UserChooserFragment extends CommonFragment implements View.OnClickListener{
    TextView mMasterName;
    UserChooserActivity mActivity;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mActivity = (UserChooserActivity)getActivity();
        return inflater.inflate(R.layout.user_chooser, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.master).setOnClickListener(this);
        view.findViewById(R.id.guest).setOnClickListener(this);
        view.findViewById(R.id.tutorial).setOnClickListener(this);
        mMasterName = (TextView)view.findViewById(R.id.master_name);
        getLoaderManager().initLoader(0, null, mProfileLoader);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.master:
            //Check BT whether connect
            if (mActivity != null && !mActivity.isBTConnected()) {
                Utility.startPairedForResult(mActivity, this);
                return;
            }

            Intent intent = new Intent(mActivity, MeasureActivity.class);
            startActivityForResult(intent, 0);
            break;
        case R.id.guest:
            Intent guestIntent = new Intent(mActivity, GuestProfileActivity.class);
            startActivityForResult(guestIntent, 0);
            break;
        case R.id.tutorial:
            Intent tutorialIntent = new Intent();
            tutorialIntent.setClass(getActivity(), TutorialActivity.class);
            tutorialIntent.putExtra(Constants.TYPE_EXTRA, Constants.TYPE_MEASURE);
            startActivity(tutorialIntent);
            break;

        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            mActivity.finish();
        } else if (FlingFragment.RESULT_SHOW_TIMEOUT_DIALOG == resultCode) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", AlertDialogFragment.DIALOG_TYPE_TIMEOUT);
            bundle.putInt("stringId", R.string.timeout_wait_long);
            Utility.startAlertDialogActivity(mActivity, bundle);
        } else if (FlingFragment.RESULT_SHOW_BLUETOOTH_DIALOG == resultCode) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", AlertDialogFragment.DIALOG_TYPE_TIMEOUT);
            bundle.putInt("stringId", R.string.timeout_bluetooth);
            Utility.startAlertDialogActivity(getActivity(), bundle);
        }
    }
    
    private LoaderCallbacks<Cursor> mProfileLoader = new LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            Uri uri = BraceletInfo.Profile.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            String name = "";
            if (data != null && data.moveToFirst()) {
                name = data.getString(data.getColumnIndex(Profile.COLUMN_NAME_PROFILE_NAME));
            }
            mMasterName.setText(getString(R.string.master_label, name));
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // TODO Auto-generated method stub
            
        }
    };
}
