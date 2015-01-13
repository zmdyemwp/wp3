package com.fihtdc.smartbracelet.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.callback.IFQACallback;

public class QuestionFragment extends ListFragment {
    
    private String[] mQuestionStrs = null;
    private Activity mActivity;
    private ArrayAdapter<String> mQuestionAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity= activity;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        mQuestionAdapter = new ArrayAdapter<String>(mActivity, R.layout.question_list_item, mQuestionStrs);
        setListAdapter(mQuestionAdapter);
    }

    private void initData() {
        mQuestionStrs = new String[]{
                getString(R.string.faq_question1),
                getString(R.string.faq_question2),
                getString(R.string.faq_question3),
                getString(R.string.faq_question4),
                getString(R.string.faq_question5),
                getString(R.string.faq_question6),
                getString(R.string.faq_question7),
                getString(R.string.faq_question8),
                getString(R.string.faq_question9)};
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ((IFQACallback)mActivity).showCorrespondingAnswer(position);
    }

}
