package com.isluji.travial.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.isluji.travial.R;
import com.isluji.travial.model.trivias.QuestionWithAnswers;
import com.isluji.travial.ui.fragments.TriviaFragment.OnListFragmentInteractionListener;
import com.isluji.travial.model.trivias.Answer;

import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link QuestionWithAnswers} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class QuestionListAdapter
        extends ListAdapter<QuestionWithAnswers, QuestionListAdapter.QuestionViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

//    private final OnListFragmentInteractionListener mListener;

    // Cached copy of the questions
    private List<QuestionWithAnswers> mQuestionList;

    // Specifies if we have to mark the unanswered questions
    private boolean mMarkUnanswered;
    private boolean mTriviaCompleted;


    public QuestionListAdapter() {
        super(DIFF_CALLBACK);

        mQuestionList = Collections.emptyList();

        // We only want to mark them once the user has sent his choices
        mMarkUnanswered = false;
        mTriviaCompleted = true;
    }


    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = new View(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.fragment_question_item,
                                parent, false);
                break;

            case VIEW_TYPE_FOOTER:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.fragment_question_footer,
                                parent, false);
                break;
        }

        return new QuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuestionViewHolder holder, int position) {
        // Only obtain the current question when
        // the list isn't empty and it's not the footer item
        if ( (getItemCount() > 1) && (getItemViewType(position) == VIEW_TYPE_ITEM) ) {
            holder.mItem = mQuestionList.get(position);

            holder.mTxtPosition.setText(
                    String.format("%s.", String.valueOf(position + 1))
            );

            holder.mTxtStatement.setText(
                    holder.mItem.getQuestion().getStatement()
            );

            holder.mTxtScore.setText(
                    String.valueOf(holder.mItem.getQuestion().getScore())
            );

            int answerCount = holder.mRgAnswers.getChildCount();
            List<Answer> answers = holder.mItem.getAnswers();

            holder.mRgAnswers.clearCheck();

            boolean answered = false;

            // (Implemented taking into account that
            // the order of the answers never change)
            for (int i = 0; i < answerCount; i++) {
                Answer answer = answers.get(i);
                RadioButton rb = (RadioButton) holder.mRgAnswers.getChildAt(i);

                if (answer.isSelected()) {
                    answered = true;
                }

                // Set the text for the question and
                // check the answer specified as 'selected' in the data object
                rb.setChecked(answer.isSelected());
                rb.setText(answer.getText());
            }

            // If there's at least an unanswered question,
            // the trivia hasn't been completed, and we should mark
            // these questions with a special color/background.
            if (!answered) {
                mTriviaCompleted = false;

                if (mMarkUnanswered) {
                    holder.mRgAnswers.setBackgroundColor(Color.MAGENTA);
                }
            }

            // RadioGroup checked option change listener
            // (we want it to update the 'selected' in the data)
            holder.mRgAnswers.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton selectedRb = group.findViewById(checkedId);
                int selectedIndex = group.indexOfChild(selectedRb);

                // (Implemented taking into account that
                // the order of the answers never change)
                for (int i = 0; i < answerCount; i++) {
                    answers.get(i).setSelected(i == selectedIndex);
                }

                if (holder.mItem.getSelectedAnswer() != null) {
                    Log.v("trivia-result-logs", "New selected answer: "
                            + holder.mItem.getSelectedAnswer().getText());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;

        if (mQuestionList != null) {
            // We add an extra item to count the footer
            itemCount = mQuestionList.size() + 1;
        }

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        // When the items of the RecyclerView are over,
        // we want to draw the footer ("Send" and "Reset" buttons)
        if (position == (getItemCount() - 1)) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void setQuestions(List<QuestionWithAnswers> questions) {
        mQuestionList = questions;

        this.notifyDataSetChanged();
    }

    public void resetTrivia() {
        for (QuestionWithAnswers qwa: mQuestionList) {
            Answer selectedAnswer = qwa.getSelectedAnswer();

            if (selectedAnswer != null) {
                Log.v("trivia-logs", "Unselected answer: "
                        + selectedAnswer.getText());
                selectedAnswer.setSelected(false);
            } else {
                Log.v("trivia-logs", "There was no selected answer");
            }
        }

        this.notifyDataSetChanged();
    }

    public boolean isTriviaCompleted() {
        return mTriviaCompleted;
    }

    public void setMarkUnanswered(boolean markUnanswered) {
        mMarkUnanswered = markUnanswered;

        this.notifyDataSetChanged();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        private QuestionWithAnswers mItem;

        private final TextView mTxtPosition;
        private final TextView mTxtStatement;
        private final TextView mTxtScore;
        private final RadioGroup mRgAnswers;

        private QuestionViewHolder(View itemView) {
            super(itemView);

            mTxtPosition = itemView.findViewById(R.id.textPosition);
            mTxtStatement = itemView.findViewById(R.id.textStatement);
            mTxtScore = itemView.findViewById(R.id.textScore);
            mRgAnswers = itemView.findViewById(R.id.rgAnswers);
        }
    }

    public static final DiffUtil.ItemCallback<QuestionWithAnswers> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<QuestionWithAnswers>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull QuestionWithAnswers oldQuestion,
                        @NonNull QuestionWithAnswers newQuestion) {
                    // User properties may have changed
                    // if reloaded from the DB, but ID is fixed.
                    return oldQuestion.getId() == newQuestion.getId();
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull QuestionWithAnswers oldQuestion,
                        @NonNull QuestionWithAnswers newQuestion) {
                    // NOTE: if you use equals, your object
                    // must properly override Object#equals().
                    // Incorrectly returning false here
                    // will result in too many animations.
                    return oldQuestion.equals(newQuestion);
                }
            };
}
