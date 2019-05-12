package com.isluji.travial.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.isluji.travial.R;
import com.isluji.travial.dummy.DummyContent;
import com.isluji.travial.fragments.QuestionListFragment.OnListFragmentInteractionListener;
import com.isluji.travial.model.TriviaQuestionWithAnswers;
import com.isluji.travial.model.TriviaWithQuestions;

import java.util.Collections;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyContent.DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {

//    private final OnListFragmentInteractionListener mListener;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    // Cached copy of the selected Trivia with its Questions
    private TriviaWithQuestions mSelectedTrivia;

    public QuestionListAdapter(/* OnListFragmentInteractionListener listener */) {
        mSelectedTrivia = new TriviaWithQuestions(null, Collections.emptyList());

//        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = new View(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_question_item, parent, false);
                break;

            case VIEW_TYPE_FOOTER:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_question_footer, parent, false);
                break;
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Only obtain the currently selected trivia when
        // the list isn't empty and it's not the footer item
        if ( (getItemCount() > 1) && (getItemViewType(position) == VIEW_TYPE_ITEM) ) {
            TriviaQuestionWithAnswers current = mSelectedTrivia.getQuestions().get(position);

            holder.mPositionView.setText(String.valueOf(position + 1) + '.');
            holder.mStatementView.setText(current.getQuestion().getStatement());
            holder.mScoreView.setText(String.valueOf(current.getQuestion().getScore()));

            ((RadioButton) holder.mAnswerGroup.getChildAt(0))
                    .setText(current.getAnswers().get(0).getText());
            ((RadioButton) holder.mAnswerGroup.getChildAt(1))
                    .setText(current.getAnswers().get(1).getText());
            ((RadioButton) holder.mAnswerGroup.getChildAt(2))
                    .setText(current.getAnswers().get(2).getText());
        }
    }

    @Override
    public int getItemCount() {
        // We add an extra item to count the footer
        return mSelectedTrivia.getQuestions().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // When the items of the RecyclerView are over,
        // we want to draw the footer ("Send" and "Reset" buttons)
        if (position == (this.getItemCount() - 1)) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }


    public void setSelectedTrivia(TriviaWithQuestions twq) {
        mSelectedTrivia = twq;
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // TODO: Design Question card and declare here the views
        private final TextView mPositionView;
        private final TextView mStatementView;
        private final TextView mScoreView;
        private final RadioGroup mAnswerGroup;

        private ViewHolder(View view) {
            super(view);

            mPositionView = view.findViewById(R.id.textPosition);
            mStatementView = view.findViewById(R.id.textStatement);
            mScoreView = view.findViewById(R.id.textScore);
            mAnswerGroup = view.findViewById(R.id.rgAnswers);
        }
    }
}
