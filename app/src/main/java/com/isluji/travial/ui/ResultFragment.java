package com.isluji.travial.ui;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.isluji.travial.R;
import com.isluji.travial.data.TriviaViewModel;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.TriviaWithQuestions;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ResultFragment extends Fragment {

    private TriviaViewModel mViewModel;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    @SuppressWarnings("unused")
    static ResultFragment newInstance() {
        ResultFragment fragment = new ResultFragment();

//        Bundle args = new Bundle();
//        args.putInt("columnCount", columnCount);
//        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trivia_result_fragment, container, false);

        // Get the shared ViewModel between MainActivity and its fragments
        mViewModel = ViewModelProviders
                .of(Objects.requireNonNull(this.getActivity()))
                .get(TriviaViewModel.class);

        // Evaluate the recently completed trivia
        // and store the result in the DB
        TriviaResult result = mViewModel.evaluateSelectedTrivia();

        try {
            mViewModel.insertResult(result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Obtain all the required view variables
        TriviaWithQuestions twq = mViewModel.getSelectedTrivia();

        TextView txtMessage = view.findViewById(R.id.txtMessage);
        TextView txtScore = view.findViewById(R.id.txtScore);
        ImageView imgResult = view.findViewById(R.id.imgResult);
        Button btnBack = view.findViewById(R.id.btnBack);
        Button btnResults = view.findViewById(R.id.btnResults);

        // Show a different screen whether the user passes or fails the test
        int scoreId, messageId, imgId, colorId;

        if (twq.isValidScore(result.getScore())) {
            if (twq.isPassed(result.getScore())) {
                colorId = Color.GREEN;
                scoreId = R.string.trivia_result_passed;
                messageId = R.string.trivia_result_message_passed;
                imgId = R.mipmap.trivia_passed;
            } else {
                colorId = Color.RED;
                scoreId = R.string.trivia_result_failed;
                messageId = R.string.trivia_result_message_failed;
                imgId = R.mipmap.trivia_failed;
            }

            String scoreText = getString(scoreId) + " (" + result.getScore()
                    + "/" + twq.getMaxScore() + ")";

            txtMessage.setTextColor(colorId);
            txtMessage.setText(messageId);
            txtScore.setText(scoreText);
            imgResult.setImageResource(imgId);
        } else {
            txtMessage.setTextColor(Color.RED);
            txtMessage.setText("Evaluation failed");
            txtScore.setText("Score is not valid");
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
            }
        });

        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getFragmentManager())
                    .beginTransaction()
                    .replace(R.id.app_bar_main, new ResultListFragment())
                    .addToBackStack(null)
                    .commit();
            }
        });

        return view;
    }
}
