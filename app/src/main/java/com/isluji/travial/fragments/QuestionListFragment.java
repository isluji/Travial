package com.isluji.travial.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isluji.travial.R;
import com.isluji.travial.adapters.QuestionListAdapter;
import com.isluji.travial.data.AppViewModel;
import com.isluji.travial.model.TriviaQuestionWithAnswers;
import com.isluji.travial.model.TriviaWithQuestions;

import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class QuestionListFragment extends Fragment {

    private static final String SELECTED_TRIVIA_POSITION = "selectedTriviaPosition";

    private OnListFragmentInteractionListener mListener;
    private AppViewModel mAppViewModel;
    private int mSelectedTriviaPosition;

    /**
     * Mandatory empty constructor for the fragment manager
     * to instantiate the fragment (e.g. upon screen orientation changes).
     */
    public QuestionListFragment() { }

    /**
     * If we need to pass some parameters when we create the Fragment,
     * we must use this custom initializer (NOT override the constructor)
     */
    @SuppressWarnings("unused")
    public static QuestionListFragment newInstance(int position) {
        QuestionListFragment fragment = new QuestionListFragment();

        Bundle args = new Bundle();
        args.putInt(SELECTED_TRIVIA_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSelectedTriviaPosition = getArguments().getInt(SELECTED_TRIVIA_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_list, container, false);

        if (view instanceof RecyclerView) {

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            final QuestionListAdapter adapter = new QuestionListAdapter();

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // Get a new or existing ViewModel from the ViewModelProvider.
            mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

            // Add an observer on the LiveData returned by getAllTrivias.
            // The onChanged() method fires when the observed data changes
            // and the activity is in the foreground.
            mAppViewModel.getAllTrivias().observe(this, new Observer<List<TriviaWithQuestions>>() {
                @Override
                public void onChanged(@Nullable final List<TriviaWithQuestions> trivias) {
                    // Update the cached copy of the trivias in the adapter.
                    adapter.setSelectedTrivia( Objects.requireNonNull(trivias).get(mSelectedTriviaPosition) );
                }
            });
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
        void onListFragmentInteraction(TriviaQuestionWithAnswers question);
    }
}
