package com.fihtdc.smartbracelet.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;

public class CustomActionBarActivity extends Activity implements View.OnClickListener {
    private View mActionBarCustomView;
    public TextView mTitle;
    public ImageView mLeft;
    public ImageView mRight;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mActionBarCustomView = getLayoutInflater().inflate(R.layout.action_bar_custom, null);
        final ActionBar bar = getActionBar();
        bar.setCustomView(mActionBarCustomView,
                new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        
        mLeft = (ImageView)mActionBarCustomView.findViewById(R.id.left);
        mTitle = (TextView)mActionBarCustomView.findViewById(R.id.middle);
        mRight = (ImageView)mActionBarCustomView.findViewById(R.id.right);

        if (mLeft != null) {
            mLeft.setOnClickListener(this);
        }
        
        if (mRight != null) {
            mRight.setOnClickListener(this);
        }
        
        if (mTitle != null) {
            mTitle.setText(getTitle());
        }
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        if (mTitle != null) {
            mTitle.setText(getTitle());
        }
    }
    
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mTitle != null) {
            mTitle.setText(getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left:
            onClickLeft();
            break;
        case R.id.right:
            onClickRight();
            break;
        default:
            break;
        }
    }
    
    //Click left image view on action bar
    public void onClickLeft(){
        finish();
    }
    
    //Click right image view on action bar
    public void onClickRight(){
        finish();
    }

}
