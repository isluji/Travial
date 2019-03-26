package com.isluji.travial.fragments;

import android.content.res.Resources;
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
public class MyTriviaRecyclerViewAdapter extends RecyclerView.Adapter<MyTriviaRecyclerViewAdapter.ViewHolder> {

    private final List<Trivia> mTriviaList;
    private final OnListFragmentInteractionListener mListener;

    public MyTriviaRecyclerViewAdapter(List<Trivia> items, OnListFragmentInteractionListener listener) {
        mTriviaList = items;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_trivia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTrivia = mTriviaList.get(position);

        holder.mTitleView.setText( holder.mTrivia.getTitle() );
        holder.mDifficultyView.setText(String.valueOf( holder.mTrivia.getDifficulty() ));

        Resources res = Resources.getSystem();
        int resId = res.getIdentifier(
                "mipmap-hdpi/ic_launcher.png",
                "drawable",
                getClass().getPackage().getName());
        holder.mRelatedPoiView.setImageResource(resId);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mTrivia);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTriviaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView mTitleView;
        public final TextView mDifficultyView;
        public final ImageView mRelatedPoiView;

        public Trivia mTrivia;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTitleView = (TextView) view.findViewById(R.id.textTitle);
            mDifficultyView = (TextView) view.findViewById(R.id.textDifficulty);
            mRelatedPoiView = (ImageView) view.findViewById(R.id.imgRelatedPoi);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
