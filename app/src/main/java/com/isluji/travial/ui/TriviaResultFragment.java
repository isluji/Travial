package com.isluji.travial.ui;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isluji.travial.R;
import com.isluji.travial.data.AppViewModel;

public class TriviaResultFragment extends Fragment {

    private AppViewModel mViewModel;
    private boolean mTriviaPassed;

    public static TriviaResultFragment newInstance() {
        return new TriviaResultFragment();
    }

    // TODO: Show a different screen if the user pass or fail the test

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trivia_result_fragment, container, false);

        // TODO?

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        // TODO: Use the ViewModel
    }

}
