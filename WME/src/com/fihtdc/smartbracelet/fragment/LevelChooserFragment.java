package com.fihtdc.smartbracelet.fragment;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.CoachingCustomActivity;
import com.fihtdc.smartbracelet.activity.CoachingStartActivity;
import com.fihtdc.smartbracelet.activity.TutorialActivity;
import com.fihtdc.smartbracelet.entity.LevelParameter;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.LevelLinearLayout;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class LevelChooserFragment extends CommonFragment implements OnClickListener{
    public LevelChooserFragment(){
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View view = inflater.inflate(R.layout.level_chooser, null);
        return view;
    }
    
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }
    
    private void initView(View view){
       
        initLinearLayout(view,R.id.level1_imgview,"1");
        initLinearLayout(view,R.id.level2_imgview,"2");
        initLinearLayout(view,R.id.level3_imgview,"3");
        initLinearLayout(view,R.id.level4_imgview,"4");
        initLinearLayout(view,R.id.level5_imgview,"5");
        initLinearLayout(view,R.id.levelm_imgview,"M");
        initRemindImageView(view,R.id.remind);
    }
    
    private void initRemindImageView(View view,int resId){
        ImageView imgView = (ImageView)view.findViewById(resId);
        imgView.setOnClickListener(this);
    }
    
    private void initLinearLayout(View view,int resId,String levelName){
        LevelLinearLayout layout = (LevelLinearLayout)view.findViewById(resId);
        layout.setLevelName(levelName);
        layout.setOnClickListener(this);
    }

    private void enterStartPage(LevelParameter param){
        Intent inent = new Intent(getActivity(), CoachingStartActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_LEVEL_PARAMETER, param);
        inent.putExtras(bundle);
        startActivityForResult(inent, 0);
    }
    
    private void enterCustomLevelPage(){
        Intent inent = new Intent(getActivity(), CoachingCustomActivity.class);
        startActivityForResult(inent, 0);
    }
    
    @Override
    public void onClick(View v) {
        LevelParameter param = null;
        switch(v.getId()){
        case R.id.level1_imgview:
            param = new LevelParameter("1", 2, 1, 4,40);
            enterStartPage(param);
            break;
        case R.id.level2_imgview:
            param = new LevelParameter("2", 3, 1, 6,40);
            enterStartPage(param);
            break;
        case R.id.level3_imgview:
            param = new LevelParameter("3", 4, 1, 8,40);
            enterStartPage(param);
            break;
        case R.id.level4_imgview:
            param = new LevelParameter("4", 5, 2, 9,40);
            enterStartPage(param);
            break;
        case R.id.level5_imgview:
            param = new LevelParameter("5", 6, 3, 10,40);
            enterStartPage(param);
            break;
        case R.id.levelm_imgview:
            enterCustomLevelPage();
            break;
        case R.id.left:
            getActivity().finish();
            break;
        case R.id.right:
            getActivity().finish();
            break;
        case R.id.remind:
            Intent tutorialIntent = new Intent();
            tutorialIntent.setClass(getActivity(), TutorialActivity.class);
            tutorialIntent.putExtra(Constants.TYPE_EXTRA, Constants.TYPE_COACHING);
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
            getActivity().finish();
        } else if (FlingFragment.RESULT_SHOW_TIMEOUT_DIALOG == resultCode) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", AlertDialogFragment.DIALOG_TYPE_TIMEOUT);
            bundle.putInt("stringId", R.string.timeout_wait_long);
            Utility.startAlertDialogActivity(getActivity(), bundle);
        } else if (FlingFragment.RESULT_SHOW_BLUETOOTH_DIALOG == resultCode) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", AlertDialogFragment.DIALOG_TYPE_TIMEOUT);
            bundle.putInt("stringId", R.string.timeout_bluetooth);
            Utility.startAlertDialogActivity(getActivity(), bundle);
        }
    }
}
