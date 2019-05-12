package com.isluji.travial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isluji.travial.R;
import com.isluji.travial.adapters.TriviaListAdapter;
import com.isluji.travial.data.AppViewModel;
import com.isluji.travial.model.TriviaWithQuestions;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TriviaListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private AppViewModel mAppViewModel;

    /**
     * Mandatory empty constructor for the fragment manager
     * to instantiate the fragment (e.g. upon screen orientation changes).
     */
    public TriviaListFragment() { }

    /**
     * If we need to pass some parameters when we create the Fragment,
     * we must use this custom initializer (NOT override the constructor)
     */
    @SuppressWarnings("unused")
    public static TriviaListFragment newInstance() {
        TriviaListFragment fragment = new TriviaListFragment();

//        Bundle args = new Bundle();
//        args.putInt("columnCount", columnCount);
//        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trivia_list, container, false);

        /** Data Binding code */

        // Inflate view and obtain an instance of the binding class.
//        FragmentTriviaBinding binding = FragmentTriviaBinding.inflate(this.getLayoutInflater());

        // Assign the component to a property in the binding class.
//        binding.setViewModel(appViewModel);

        // Make the LiveData be properly observed when bound to the XML.
//        binding.setLifecycleOwner(this);

        // Set the Activity's content view to the given layout
        // and return the associated binding.
//        binding = DataBindingUtil.setContentView(
//                Objects.requireNonNull(this.getActivity()),
//                binding.getRoot().getId());
//        this.getActivity().setContentView(binding.getRoot());

        // TEST access UI data from the activity/fragment
//        appViewModel.mAllTrivias.getValue().get(0).setTitle("Nuevo titulo");

        // TEST data binding access
//        Trivia trivia = new Trivia("trivia ejemplo", TriviaDifficulty.MEDIUM, 5, 1);
//        binding.getViewModel().testTrivia = trivia;


        /** RecyclerView and Adapter code */

        if (view instanceof RecyclerView) {
            // TODO? Si falla, cambiar "context" por "this.getActivity()" en los dos últimos métodos
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            final TriviaListAdapter adapter = new TriviaListAdapter(mListener);
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
                    adapter.setTrivias(trivias);
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
        void onListFragmentInteraction(int position);
    }
}
