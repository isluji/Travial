package com.isluji.travial.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isluji.travial.R;
import com.isluji.travial.ui.TriviaListFragment.OnListFragmentInteractionListener;
import com.isluji.travial.model.trivias.TriviaWithQuestions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TriviaWithQuestions} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class TriviaListAdapter extends RecyclerView.Adapter<TriviaListAdapter.TriviaViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    private final OnListFragmentInteractionListener mListener;

    // Cached copy of the trivias
    private List<TriviaWithQuestions> mTriviaList;

    public TriviaListAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
        mTriviaList = Collections.emptyList();
    }


    @NonNull
    @Override
    public TriviaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = new View(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.fragment_trivia_item,
                                parent, false);
                break;

            case VIEW_TYPE_FOOTER:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.fragment_trivia_footer,
                                parent, false);
                break;
        }

        return new TriviaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TriviaViewHolder holder, int position) {
        // Only obtain the current trivia when
        // the list isn't empty and it's not the footer item
        if ( (getItemCount() > 1) && (getItemViewType(position) == VIEW_TYPE_ITEM) ) {
            holder.mItem = mTriviaList.get(position);

            // TODO: Arreglar esta guarrerida
            int resId = Resources.getSystem().getIdentifier(
                    "mipmap-hdpi/ic_launcher.png", "drawable",
                    Objects.requireNonNull(getClass().getPackage()).getName());

            holder.mImgPoiPhoto.setImageResource(resId);

            holder.mTxtTitle.setText(
                    holder.mItem.getTrivia().getTitle()
            );

            holder.mTxtDifficulty.setText(
                    holder.mItem.getTrivia().getDifficulty().toString()
            );

            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    // Notify the active callbacks interface
                    // (the activity, if the fragment is attached to one)
                    // that an item has been selected.
                    mListener.onListFragmentInteraction(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;

        if (mTriviaList != null) {
            // We add an extra item to count the footer
            itemCount = mTriviaList.size() + 1;
        }

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        // When the items of the RecyclerView are over,
        // we want to draw the footer ("Powered by Google" attribution)
        if (position == (getItemCount() - 1)) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void setTrivias(List<TriviaWithQuestions> trivias){
        mTriviaList = trivias;
        this.notifyDataSetChanged();
    }


    /** Trivia QuestionViewHolder */
    class TriviaViewHolder extends RecyclerView.ViewHolder {
        private TriviaWithQuestions mItem;

        private final TextView mTxtTitle;
        private final TextView mTxtDifficulty;
        private final ImageView mImgPoiPhoto;

        private TriviaViewHolder(View itemView) {
            super(itemView);

            mTxtTitle = itemView.findViewById(R.id.txtTitle);
            mTxtDifficulty = itemView.findViewById(R.id.txtDifficulty);
            mImgPoiPhoto = itemView.findViewById(R.id.imgPoiPhoto);
        }
    }
}
