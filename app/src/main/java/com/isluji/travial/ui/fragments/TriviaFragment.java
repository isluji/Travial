package com.isluji.travial.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isluji.travial.R;
import com.isluji.travial.adapters.QuestionListAdapter;
import com.isluji.travial.data.TriviaViewModel;
import com.isluji.travial.model.trivias.QuestionWithAnswers;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TriviaFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    private TriviaViewModel mViewModel;
    private RecyclerView mRecyclerView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    @SuppressWarnings("unused")
    static TriviaFragment newInstance() {
        TriviaFragment fragment = new TriviaFragment();

//        Bundle args = new Bundle();
//        args.putInt(SELECTED_TRIVIA_POSITION, position);
//        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_list, container, false);

        if (view instanceof RecyclerView) {
            /* ***** RecyclerView and Adapter code ***** */

            mRecyclerView = (RecyclerView) view;

            // Get the shared ViewModel between MainActivity and its fragments
            mViewModel = ViewModelProviders
                    .of(Objects.requireNonNull(this.getActivity()))
                    .get(TriviaViewModel.class);

            // Create an adapter and a layout manager for the RecyclerView
            final QuestionListAdapter adapter = new QuestionListAdapter();
            final LinearLayoutManager layoutManager =
                    new LinearLayoutManager(mRecyclerView.getContext());

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(layoutManager);

            // We already fetched user's trivias from DB in TriviaListFragment,
            // and it's cached in the ViewModel, so we can set up the new adapter
            adapter.setQuestions(mViewModel.getSelectedTrivia().getQuestions());
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }


    // ---------- Custom methods -----------

    public void resetTrivia() {
        QuestionListAdapter adapter = (QuestionListAdapter) mRecyclerView.getAdapter();

        if (adapter != null) {
            adapter.resetTrivia();
        }
    }

    public void setMarkUnanswered(boolean markUnanswered) {
        QuestionListAdapter adapter = (QuestionListAdapter) mRecyclerView.getAdapter();

        if (adapter != null) {
            adapter.setMarkUnanswered(markUnanswered);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(QuestionWithAnswers question);
    }
}
