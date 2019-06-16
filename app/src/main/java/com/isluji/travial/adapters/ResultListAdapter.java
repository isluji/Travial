package com.isluji.travial.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isluji.travial.R;
import com.isluji.travial.model.trivias.Result;
import com.isluji.travial.ui.ResultListFragment.OnListFragmentInteractionListener;

import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;

    // Cached copy of the user's results
    private List<Result> mResultList;

    public ResultListAdapter(OnListFragmentInteractionListener listener) {
        mResultList = Collections.emptyList();

        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_result_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mResultList.get(position);

        // TODO: Pass the correct strings
        holder.mTxtTriviaName.setText( String.valueOf(holder.mItem.getTriviaId()) );
        holder.mTxtScore.setText( String.valueOf(holder.mItem.getScore()) );
        holder.mTxtDateTime.setText( holder.mItem.getFinishedDate().toString() );

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface
                    // (the activity, if the fragment is attached to one)
                    // that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public void setResults(List<Result> results) {
        mResultList = results;
        this.notifyDataSetChanged();
    }


    /** Result QuestionViewHolder */
    class ViewHolder extends RecyclerView.ViewHolder {
        private Result mItem;

        private final View mView;
        private final TextView mTxtTriviaName;
        private final TextView mTxtScore;
        private final TextView mTxtDateTime;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mTxtTriviaName = itemView.findViewById(R.id.triviaText);
            mTxtScore = itemView.findViewById(R.id.scoreText);
            mTxtDateTime = itemView.findViewById(R.id.dateText);
        }
    }
}
