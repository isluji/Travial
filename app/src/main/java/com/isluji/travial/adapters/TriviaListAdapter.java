package com.isluji.travial.adapters;

import android.content.Context;
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
import com.isluji.travial.fragments.TriviaFragment.OnListFragmentInteractionListener;
import com.isluji.travial.model.Trivia;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyContent.DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TriviaListAdapter extends RecyclerView.Adapter<TriviaListAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private List<Trivia> mTrivias;  // Cached copy of trivias
    private final OnListFragmentInteractionListener mListener;

    public TriviaListAdapter(Context context, OnListFragmentInteractionListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =  mInflater.inflate(R.layout.fragment_trivia, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // If data is ready, set the values to the views
        if (mTrivias != null) {
            Trivia current = mTrivias.get(position);

            int resId = Resources.getSystem().getIdentifier(
                    "mipmap-hdpi/ic_launcher.png", "drawable",
                    getClass().getPackage().getName());

            holder.mTitleView.setText( current.getTitle() );
            holder.mDifficultyView.setText(String.valueOf( current.getDifficulty() ));
            holder.mRelatedPoiView.setImageResource(resId);

        // Covers the case of data not being ready yet.
        } else {
            holder.mTitleView.setText("No Trivia");
        }

        // TODO: Implement the click listener properly
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.getItemId());
                }
            }
        });
    }

    public void setTrivias(List<Trivia> trivias){
        mTrivias = trivias;
        this.notifyDataSetChanged();
    }

    // getItemCount() is called many times,
    // and when it is first called, mTrivias has not been updated
    // (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mTrivias != null) {
            return mTrivias.size();
        } else {
            return 0;
        }
    }


    /** Trivia ViewHolder */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitleView;
        private final TextView mDifficultyView;
        private final ImageView mRelatedPoiView;

        private ViewHolder(View view) {
            super(view);

            mTitleView = view.findViewById(R.id.textTitle);
            mDifficultyView = view.findViewById(R.id.textDifficulty);
            mRelatedPoiView = view.findViewById(R.id.imgRelatedPoi);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
