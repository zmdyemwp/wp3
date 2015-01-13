package com.fihtdc.smartbracelet.view;

import com.fihtdc.smartbracelet.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LevelLinearLayout extends LinearLayout {

    private TextView mLevelView = null; 
    public LevelLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public LevelLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context,attrs);
    }

    private void initView(Context context,AttributeSet attrs){
        if(null == context){
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.level_item, null);
        mLevelView = (TextView)view.findViewById(R.id.level_item_name);
        addView(view);
    }
    
    public void setLevelName(String str){
        mLevelView.setText(str);
    }
}
