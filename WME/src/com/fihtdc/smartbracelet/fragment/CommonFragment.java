package com.fihtdc.smartbracelet.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.fihtdc.smartbracelet.activity.PairActivity;


public class CommonFragment extends Fragment {
    boolean mHasRemoveOnGlobal = false;
    View mGlobalView = null;
    
    /**
     * @author F3060326 at 2013/9/2
     * @return void
     * @param 
     * @Description: fragment need Override this function
     */
    public void onActivityGlobalLayout(){
        
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    protected void addOnGlobalLayoutListener(View v){
        mGlobalView = v;
        mGlobalView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }
    
    @Override
    public void onDestroyView() {
        if(!mHasRemoveOnGlobal && null!=mGlobalView){
            mGlobalView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
        super.onDestroyView();
    }
    
    OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mHasRemoveOnGlobal = true;
            onActivityGlobalLayout();
            mGlobalView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        case PairActivity.REQUEST_PAIR_CODE:
            if (Activity.RESULT_OK == resultCode) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
            break;
        default:
            break;

        }
    }
}
