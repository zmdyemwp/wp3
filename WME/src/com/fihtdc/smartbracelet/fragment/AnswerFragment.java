package com.fihtdc.smartbracelet.fragment;

import com.fihtdc.smartbracelet.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AnswerFragment extends CommonFragment {
    
    public static int[] mQuestionIdList = new int[] {
        R.string.faq_question1,R.string.faq_question2,
        R.string.faq_question3,R.string.faq_question4,
        R.string.faq_question5,R.string.faq_question6,
        R.string.faq_question7,R.string.faq_question8,
        R.string.faq_question9};
    
    public static int[] mQuestionAnswerIdList = new int[] {
        R.string.faq_question1_answer,R.string.faq_question2_answer,
        R.string.faq_question3_answer,R.string.faq_question4_answer,
        R.string.faq_question5_answer,R.string.faq_question6_answer,
        R.string.faq_question7_answer,R.string.faq_question8_answer,
        R.string.faq_question9_answer};
    
    private Activity mActivity;
    private TextView mQuestionTV;
    private TextView mAnswerTV;
    private int mQuestionId = -1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        
    }

    public void setQuestionId(int questionId) {
        mQuestionId = questionId;
        if(questionId < 0 || questionId > mQuestionIdList.length - 1 ) {
            return;
        }
        if(mQuestionTV != null) {
            mQuestionTV.setText(mQuestionIdList[questionId]);
        }
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fqa_answer_layout, null);
        mQuestionTV = (TextView)view.findViewById(R.id.question);
        mAnswerTV = (TextView)view.findViewById(R.id.answer);
        initViewData();
        return view;
    }

    private void initViewData() {
        if(mQuestionId != -1) {
            mQuestionTV.setText(mQuestionIdList[mQuestionId]);
            mAnswerTV.setText(mQuestionAnswerIdList[mQuestionId]);
        }
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    
}
