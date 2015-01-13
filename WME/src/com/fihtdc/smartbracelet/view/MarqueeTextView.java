/*
 * JimmyChen @20130420 show Text view Marquee because the app name length is sometimes so long
 */

package com.fihtdc.smartbracelet.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return super.isEnabled();
    }

    @Override
    public boolean isFocused() {
        return true;
    }
    
    
}

