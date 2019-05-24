package com.isluji.travial.data;

import android.app.Application;

import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;

import java.util.List;
import java.util.Objects;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * View Model to keep a reference to the trivia Repository
 * and an up-to-date list of all trivias.
 */

public class AppViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    private LiveData<List<TriviaWithQuestions>> mAllTrivias;

    public AppViewModel(Application app) {
        super(app);

        mRepository = new AppRepository(app);
        mAllTrivias = mRepository.getAllTrivias();
    }


    // ***** Wrapper methods *****
    // (they completely hide the implementation from the UI)

    public LiveData<List<TriviaWithQuestions>> getAllTrivias() {
        return mAllTrivias;
    }

    public TriviaWithQuestions getTrivia(int position) {
        return Objects.requireNonNull( mAllTrivias.getValue() ).get(position);
    }

    public void insertUser(User newUser) {
        mRepository.insertUser(newUser);
    }
}
