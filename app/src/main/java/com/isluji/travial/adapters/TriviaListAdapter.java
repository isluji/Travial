package com.isluji.travial.adapters;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isluji.travial.R;
import com.isluji.travial.dummy.DummyContent;
import com.isluji.travial.fragments.TriviaListFragment.OnListFragmentInteractionListener;
import com.isluji.travial.model.TriviaWithQuestions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyContent.DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TriviaListAdapter extends RecyclerView.Adapter<TriviaListAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;

    // Cached copy of the Trivias
    private List<TriviaWithQuestions> mTriviaList;

    public TriviaListAdapter(OnListFragmentInteractionListener listener) {
        mTriviaList = Collections.emptyList();

        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_trivia_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TriviaWithQuestions current = mTriviaList.get(position);

        int resId = Resources.getSystem().getIdentifier(
                "mipmap-hdpi/ic_launcher.png", "drawable",
                Objects.requireNonNull(getClass().getPackage()).getName());

        holder.mTitleView.setText( current.getTrivia().getTitle() );
        holder.mDifficultyView.setText( current.getTrivia().getDifficulty().toString() );
        holder.mRelatedPoiView.setImageResource( resId );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface
                    // (the activity, if the fragment is attached to one)
                    // that an item has been selected.
                    mListener.onListFragmentInteraction(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTriviaList.size();
    }

    public void setTrivias(List<TriviaWithQuestions> trivias){
        mTriviaList = trivias;
        this.notifyDataSetChanged();
    }


    /** Trivia ViewHolder */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitleView;
        private final TextView mDifficultyView;
        private final ImageView mRelatedPoiView;

        private ViewHolder(View itemView) {
            super(itemView);

            mTitleView = itemView.findViewById(R.id.textTitle);
            mDifficultyView = itemView.findViewById(R.id.textDifficulty);
            mRelatedPoiView = itemView.findViewById(R.id.imgRelatedPoi);
        }
    }
}
