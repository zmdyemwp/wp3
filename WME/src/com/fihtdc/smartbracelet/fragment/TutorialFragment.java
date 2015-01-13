package com.fihtdc.smartbracelet.fragment;

import java.lang.reflect.Field;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.view.CircleFlowIndicator;
import com.fihtdc.smartbracelet.view.ViewFlow;

public class TutorialFragment extends CommonFragment {
    private static final int MEASURE_PAGES = 6;
    private static final int COACHING_PAGES = 11;
    private int mType = Constants.TYPE_MEASURE;

    private ViewFlow mViewFlow;
    private CircleFlowIndicator mIndic;
    private BaseAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutorial_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(Constants.TYPE_EXTRA, Constants.TYPE_MEASURE);
        }
        mViewFlow = (ViewFlow) view.findViewById(R.id.viewflow);
        mAdapter = new ImageAdapter(getActivity());
        mViewFlow.setAdapter(mAdapter, 0);
        mIndic = (CircleFlowIndicator) view.findViewById(R.id.viewflowindic);
        mViewFlow.setFlowIndicator(mIndic);
    }

    public class ImageAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ImageAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (Constants.TYPE_MEASURE == mType) {
                return MEASURE_PAGES;
            } else if (Constants.TYPE_COACHING == mType) {
                return COACHING_PAGES;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = mInflater.inflate(R.layout.tutorial_item, null);
//            }
//
//            ImageView image = (ImageView) convertView.findViewById(R.id.image);
//            if (Constants.TYPE_MEASURE == mType) {
//                image.setImageResource(MEASURE_IDS[position]);
//            } else if (Constants.TYPE_COACHING == mType) {
//                image.setImageResource(COACHING_IDS[position]);
//            } else {
//                image.setImageDrawable(null);
//            }
            
            convertView = mInflater.inflate(getIndexLayout(position+1), null);
            return convertView;
            }
    }

    
    private int getIndexLayout(int index){
        int i = R.layout.health_mguide_1;
        String s = "";
            if (Constants.TYPE_MEASURE == mType) {
            s = "health_mguide_"+index;
            } else if (Constants.TYPE_COACHING == mType) {
            s = "health_cguide_"+index;
        }
        try {
            Field fild = R.layout.class.getDeclaredField(s);
            try {
                i = fild.getInt("");
            } catch (Exception e) {
                
            }
        } catch (Exception e) {

        }
        return i;
    }
}
