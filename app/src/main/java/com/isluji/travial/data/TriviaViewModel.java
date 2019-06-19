package com.isluji.travial.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.Marker;
import com.isluji.travial.R;
import com.isluji.travial.model.trivias.Answer;
import com.isluji.travial.model.trivias.QuestionWithAnswers;
import com.isluji.travial.model.trivias.Result;
import com.isluji.travial.model.trivias.TriviaWithQuestions;
import com.isluji.travial.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * View Model to keep a reference to the trivia Repository
 * and an up-to-date list of all trivias.
 */

public class TriviaViewModel extends AndroidViewModel {

    private TriviaRepository mRepository;

    private int mSelectedTriviaPosition;

    private LiveData<List<TriviaWithQuestions>> mUserTrivias;
    private LiveData<List<Result>> mUserResults;


    public TriviaViewModel(Application app) {
        super(app);

        mRepository = new TriviaRepository(app);
        mSelectedTriviaPosition = 0;
    }

    public void setSelectedTriviaPosition(int mSelectedTrivia) {
        this.mSelectedTriviaPosition = mSelectedTrivia;
    }


    // ************ Wrapper methods **************
    // (they completely hide the implementation from the UI)

    public LiveData<List<TriviaWithQuestions>> getUserTrivias(Set<String> userPoiIds) {
        mUserTrivias = mRepository.getUserTrivias(userPoiIds);

        return mUserTrivias;
    }

    public LiveData<List<Result>> getUserResults(String userEmail) {
        mUserResults = mRepository.getUserResults(userEmail);

        return mUserResults;
    }

    public long insertResult(Result newResult)
            throws ExecutionException, InterruptedException {
        return mRepository.insertResult(newResult);
    }

    public User createOrRetrieveUser(String email, String googleId)
            throws ExecutionException, InterruptedException {
        return mRepository.createOrRetrieveUser(email, googleId);
    }

    // -------------------------------------------------------

    public TriviaWithQuestions getSelectedTrivia() {
        TriviaWithQuestions twq = null;

        if (mUserTrivias.getValue() != null) {
            twq = mUserTrivias.getValue().get(mSelectedTriviaPosition);
        }

        return twq;
    }

    public Result evaluateSelectedTrivia() {
        TriviaWithQuestions twq = this.getSelectedTrivia();
        double score = 0;

        // Calculate the score of all the questions
        for (QuestionWithAnswers qwa: twq.getQuestions()) {
            for (Answer answer: qwa.getAnswers()) {
                if (answer.isSelected() && answer.isCorrect()) {
                    score += qwa.getQuestion().getScore();
                }
            }
        }

        // Get the current user's email address
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(
                        this.getApplication().getApplicationContext());

        String userEmail = sharedPrefs.getString("user_email",
                this.getApplication().getString(R.string.placeholder_user_email));

        return new Result(twq.getId(), userEmail, score);
    }
}
