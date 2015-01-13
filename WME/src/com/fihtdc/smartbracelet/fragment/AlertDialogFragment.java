package com.fihtdc.smartbracelet.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.ProfileActivity;

public class AlertDialogFragment extends DialogFragment {
    //Dialog use in ProfileActivity
    public static final int DIALOG_TYPE_EDIT_PROFILE = 1;
    public static final int DIALOG_TYPE_NOT_COMPLETE = 2;
    public static final int DIALOG_TYPE_COMPLETE = 3;
    public static final int DIALOG_TYPE_TIMEOUT = 4;

    public AlertDialogFragment() {

    }
    
    public interface DialogButtonClickListener{
        void onPositiveButtonClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int type = getArguments().getInt("type");
        switch (type) {
        case DIALOG_TYPE_EDIT_PROFILE:
            return new AlertDialog.Builder(getActivity())
            .setMessage(R.string.profile_edit_prompt)
            .setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }).create();
        case DIALOG_TYPE_NOT_COMPLETE:
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.profile_not_complete_prompt)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
        case DIALOG_TYPE_COMPLETE:
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.profile_complete_prompt)
                    .setNegativeButton(R.string.button_pair_label, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (getActivity() != null) {
                                ((ProfileActivity)getActivity()).onClickRight();
                            }
                        }
                    })
                    .setPositiveButton(R.string.button_edit_profile_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
        case DIALOG_TYPE_TIMEOUT:
            int stringId = getArguments().getInt("stringId");
            if (stringId == 0) {
                stringId = R.string.timeout_wait_long;
            }
            return new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK)
                    .setTitle(R.string.timeout_title)
                    .setMessage(stringId)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    getActivity().finish();
                                }
                            }).create();

        default:
            break;
        }

        return null;

    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        int type = getArguments().getInt("type");
        switch (type) {
        case DIALOG_TYPE_EDIT_PROFILE:
            Activity parent = getActivity();
            if (parent != null && parent instanceof ProfileActivity) {
                ((ProfileActivity)parent).startViewsAnimation();
            }
            break;
        default:
            break;
        }
    }
    
    
}
