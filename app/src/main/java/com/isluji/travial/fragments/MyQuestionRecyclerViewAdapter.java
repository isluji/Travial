package com.isluji.travial.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.isluji.travial.R;
import com.isluji.travial.dummy.DummyContent;
import com.isluji.travial.fragments.QuestionFragment.OnListFragmentInteractionListener;
import com.isluji.travial.model.TriviaQuestion;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyContent.DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyQuestionRecyclerViewAdapter extends RecyclerView.Adapter<MyQuestionRecyclerViewAdapter.ViewHolder> {

    private final List<TriviaQuestion> mQuestionList;
    private final OnListFragmentInteractionListener mListener;

    public MyQuestionRecyclerViewAdapter(List<TriviaQuestion> items, OnListFragmentInteractionListener listener) {
        mQuestionList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_question, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mQuestion = mQuestionList.get(position);

        holder.mPositionView.setText(String.valueOf( position ));
        holder.mStatementView.setText( holder.mQuestion.getStatement() );
        holder.mScoreView.setText(String.valueOf( holder.mQuestion.getScore() ));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mQuestion);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        // TODO: Design Question card and declare here the views
        public final TextView mPositionView;
        public final TextView mStatementView;
        public final TextView mScoreView;

        public final CheckBox mAnswerCB1;
        public final CheckBox mAnswerCB2;
        public final CheckBox mAnswerCB3;

        public TriviaQuestion mQuestion;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mPositionView = (TextView) view.findViewById(R.id.textPosition);
            mStatementView = (TextView) view.findViewById(R.id.textStatement);
            mScoreView = (TextView) view.findViewById(R.id.textStatement);

            mAnswerCB1 = (CheckBox) view.findViewById(R.id.cbAnswer1);
            mAnswerCB2 = (CheckBox) view.findViewById(R.id.cbAnswer2);
            mAnswerCB3 = (CheckBox) view.findViewById(R.id.cbAnswer3);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStatementView.getText() + "'";
        }
    }
}
