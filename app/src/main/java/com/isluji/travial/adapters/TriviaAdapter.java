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
            holder.mItem = mSelectedTrivia.getQuestions().get(position);

            holder.mTxtPosition.setText(String.valueOf(position + 1) + '.');
            holder.mTxtStatement.setText( holder.mItem.getQuestion().getStatement() );
            holder.mTxtScore.setText( String.valueOf(holder.mItem.getQuestion().getScore()) );

            RadioButton option1 = (RadioButton) holder.mRgAnswers.getChildAt(0);
            RadioButton option2 = (RadioButton) holder.mRgAnswers.getChildAt(1);
            RadioButton option3 = (RadioButton) holder.mRgAnswers.getChildAt(2);

            TriviaAnswer answer1 = holder.mItem.getAnswers().get(0);
            TriviaAnswer answer2 = holder.mItem.getAnswers().get(1);
            TriviaAnswer answer3 = holder.mItem.getAnswers().get(2);

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
        private TriviaQuestionWithAnswers mItem;

        private final TextView mTxtPosition;
        private final TextView mTxtStatement;
        private final TextView mTxtScore;
        private final RadioGroup mRgAnswers;

        private ViewHolder(View view) {
            super(view);

            mTxtPosition = view.findViewById(R.id.textPosition);
            mTxtStatement = view.findViewById(R.id.textStatement);
            mTxtScore = view.findViewById(R.id.textScore);
            mRgAnswers = view.findViewById(R.id.rgAnswers);
        }
    }
}
