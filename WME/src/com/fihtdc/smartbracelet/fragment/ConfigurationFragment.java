package com.fihtdc.smartbracelet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.AboutUsActivity;
import com.fihtdc.smartbracelet.activity.FAQActivity;
import com.fihtdc.smartbracelet.activity.HomeActivity;
import com.fihtdc.smartbracelet.activity.ProfileActivity;
import com.fihtdc.smartbracelet.activity.SettingsActivity;

public class ConfigurationFragment extends CommonFragment{
    private static int REQUEST_CODE = 1;
    ListView mList;
    ConfigurationAdapter mAdapter;
    
    Context mContext;
    
    String[] mLabels;
    int[] mIcons = { R.drawable.health_setting_profile_ic, R.drawable.health_setting_settings_ic,
            R.drawable.health_setting_aboutus_ic, R.drawable.health_setting_faq_ic };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.configuration, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();

        mList = (ListView) view.findViewById(R.id.list);
        mLabels = mContext.getResources().getStringArray(R.array.configuration_array);

        mAdapter = new ConfigurationAdapter(mContext);
        int length = mLabels.length;
        for (int i = 0; i < length; i++) {
            mAdapter.add(new ConfigurationItem(mLabels[i], mIcons[i]));
        }
        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new OnItemClickListener() {
            Intent intent = new Intent();

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                case 0:
                    intent.setClass(mContext, ProfileActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    getActivity()
                            .overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    break;
                case 1:
                    intent.setClass(mContext, SettingsActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    getActivity()
                            .overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    break;
                case 2:
                    intent.setClass(mContext, AboutUsActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    getActivity()
                            .overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    break;
                case 3:
                    intent.setClass(mContext, FAQActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    getActivity()
                            .overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    break;

                default:
                    break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                if (getActivity() instanceof HomeActivity) {
                    HomeActivity main = (HomeActivity)getActivity();
                    main.showAbove();
                }
            }
        }
    }

    private class ConfigurationItem {
        public String label;
        public int iconRes;
        
        public ConfigurationItem(String label, int iconRes) {
            this.label = label; 
            this.iconRes = iconRes;
        }
    }
    
    public class ConfigurationAdapter extends ArrayAdapter<ConfigurationItem>{
        public ConfigurationAdapter(Context context) {
            super(context, 0);
        }
        
        @Override
        public int getCount() {
            return mLabels.length ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.configuration_item, null);
            }
            
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setText(getItem(position).label);

            return convertView;
        }
        
    }
}
