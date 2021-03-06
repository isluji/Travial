package com.isluji.travial.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isluji.travial.R;
import com.isluji.travial.adapters.ResultListAdapter;
import com.isluji.travial.data.TriviaViewModel;
import com.isluji.travial.model.trivias.Result;

import java.util.Objects;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ResultListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private TriviaViewModel mViewModel;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    @SuppressWarnings("unused")
    static ResultListFragment newInstance() {
        ResultListFragment fragment = new ResultListFragment();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_list, container, false);

        /* ***** RecyclerView and Adapter code ***** */

        if (view instanceof RecyclerView) {
            // TODO? Si falla, cambiar "context" por "this.getActivity()" en los dos últimos métodos
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            final ResultListAdapter adapter = new ResultListAdapter(mListener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // Get the shared ViewModel between MainActivity and its fragments
            mViewModel = ViewModelProviders
                    .of(Objects.requireNonNull(this.getActivity())) // TODO?
                    .get(TriviaViewModel.class);

            // Get the current user's email address
            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this.getActivity().getApplicationContext());
            String userEmail = sharedPrefs.getString("user_email",
                    this.getString(R.string.placeholder_user_email));

            // ******** LiveData Observer ********

            mViewModel.getUserResults(userEmail).observe(this,
                userResults -> {
                    // Update the cached copy of the trivias in the adapter.
                    adapter.setResults(userResults);

                    Log.v(getString(R.string.trivia_results_log),
                            "ResultListFragment: User results loaded from BD");
                }
            );
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
        void onListFragmentInteraction(Result item);
    }
}
