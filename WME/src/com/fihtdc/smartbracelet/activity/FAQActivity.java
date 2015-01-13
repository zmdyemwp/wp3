package com.fihtdc.smartbracelet.activity;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.callback.IFQACallback;
import com.fihtdc.smartbracelet.fragment.AnswerFragment;
import com.fihtdc.smartbracelet.fragment.QuestionFragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

public class FAQActivity extends CustomActionBarActivity implements IFQACallback{
    private QuestionFragment mQuestionFragment;
    private AnswerFragment mAnswerFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_activity);
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);
        if(savedInstanceState == null) {
            initView();
//            showQuestionList();
        }
    }

    private void initView() {
        mQuestionFragment = new QuestionFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fqa_fragment, mQuestionFragment);
        ft.commit();
    }
    
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
    
    @Override
    public void onClickLeft() {
        onBackPressed();
    }
    
    @Override
    public void onClickRight() {
        setResult(RESULT_OK);
        super.onClickRight();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            getFragmentManager().popBackStack();
            removeCurrentFragment();
        }
    }
    
    public void removeCurrentFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment currentFrag = getFragmentManager().findFragmentById(R.id.fqa_fragment);
        if (currentFrag != null){
            transaction.remove(currentFrag);
            //LogApp.Logd(TAG, "fragName "+currentFrag.getClass().getSimpleName());
        }

        transaction.commit();
    }

    @Override
    public void showCorrespondingAnswer(int position) {
        if(mAnswerFragment == null) {
            mAnswerFragment = new AnswerFragment();
        }
        mAnswerFragment.setQuestionId(position);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fqa_fragment, mAnswerFragment);
        ft.addToBackStack(null);
        ft.commit();
        
    }
}
