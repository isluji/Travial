package com.isluji.travial.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isluji.travial.R;
import com.isluji.travial.ui.TriviaFragment.OnListFragmentInteractionListener;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestionWithAnswers;
import com.isluji.travial.model.TriviaWithQuestions;

import java.util.Collections;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TriviaQuestionWithAnswers} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class TriviaAdapter extends RecyclerView.Adapter<TriviaAdapter.ViewHolder> {

//    private final OnListFragmentInteractionListener mListener;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    // Cached copy of the selected Trivia with its Questions
    private TriviaWithQuestions mSelectedTrivia;

    public TriviaAdapter(/* OnListFragmentInteractionListener listener */) {
        mSelectedTrivia = new TriviaWithQuestions(null/*, Collections.emptyList()*/);

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

            RadioButton option1 = (RadioButton) holder.mAnswerGroup.getChildAt(0);
            RadioButton option2 = (RadioButton) holder.mAnswerGroup.getChildAt(1);
            RadioButton option3 = (RadioButton) holder.mAnswerGroup.getChildAt(2);

            TriviaAnswer answer1 = current.getAnswers().get(0);
            TriviaAnswer answer2 = current.getAnswers().get(1);
            TriviaAnswer answer3 = current.getAnswers().get(2);

            option1.setText(answer1.getText());
            option2.setText(answer2.getText());
            option3.setText(answer3.getText());

//            option1.setChecked(answer1.isSelected());
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;

        if (mSelectedTrivia.getQuestions() != null) {
            // We add an extra item to count the footer
            itemCount = mSelectedTrivia.getQuestions().size() + 1;
        }

        return itemCount;
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
