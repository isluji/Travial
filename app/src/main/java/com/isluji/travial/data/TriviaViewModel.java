package com.isluji.travial.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.isluji.travial.R;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestionWithAnswers;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * View Model to keep a reference to the trivia Repository
 * and an up-to-date list of all trivias.
 */

public class TriviaViewModel extends AndroidViewModel {

    private TriviaRepository mRepository;

    private LiveData<List<TriviaWithQuestions>> mAllTrivias;
    private int mSelectedTriviaPosition;

    public TriviaViewModel(Application app) {
        super(app);

        mRepository = new TriviaRepository(app);
        mAllTrivias = mRepository.getAllTrivias();

        mSelectedTriviaPosition = 0;
    }

    public int getSelectedTriviaPosition() {
        return mSelectedTriviaPosition;
    }

    public void setSelectedTriviaPosition(int mSelectedTrivia) {
        this.mSelectedTriviaPosition = mSelectedTrivia;
    }


    // ***** Wrapper methods *****
    // (they completely hide the implementation from the UI)

    public LiveData<List<TriviaWithQuestions>> getAllTrivias() {
        return mAllTrivias;
    }

    public LiveData<List<TriviaResult>> getUserResults(String userEmail) {
        return mRepository.getUserResults(userEmail);
    }

    public TriviaWithQuestions getSelectedTrivia() {
        return Objects.requireNonNull(mAllTrivias.getValue())
                .get(mSelectedTriviaPosition);
    }

    public void insertUser(User newUser) {
        mRepository.insertUser(newUser);
    }

    public long insertResult(TriviaResult newResult) throws ExecutionException, InterruptedException {
        return mRepository.insertResult(newResult);
    }

    // -------------------------------------------------------

    public TriviaResult evaluateSelectedTrivia() {
        TriviaWithQuestions twq = this.getSelectedTrivia();
        double score = 0;

        // Calculate the score of all the questions
        for (TriviaQuestionWithAnswers qwa: twq.getQuestions()) {
            for (TriviaAnswer answer: qwa.getAnswers()) {
                if (answer.isSelected() && answer.isCorrect()) {
                    score += qwa.getQuestion().getScore();
                }
            }
        }

        // Get the current user's email address
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplication());
        String userEmail = sharedPrefs.getString("user_email", this.getApplication().getString(R.string.placeholder_user_email));

        // TODO: Pass the current user email
        return new TriviaResult(twq.getId(), userEmail, score);
    }
}
