package com.isluji.travial.data;

import android.app.Application;

import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaWithQuestions;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * View Model to keep a reference to the trivia Repository
 * and an up-to-date list of all trivias.
 */

public class AppViewModel extends AndroidViewModel {

    // TODO? Store a list of trivias or a single trivia?
    // TODO? Create a custom constructor -> ViewModelProvider.Factory
    // TODO? LiveData or MutableLiveData (trivias and _trivias)?

    private AppRepository mRepository;

    // Using LiveData and caching what getAllTrivias returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes)
    //   and only update the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<TriviaWithQuestions>> mAllTrivias;

    public AppViewModel(Application app) {
        super(app);

        mRepository = new AppRepository(app);
        mAllTrivias = mRepository.getAllTrivias();
    }


    /*
     * Wrapper methods that completely hide
     * the implementation from the UI
     */

    public LiveData<List<TriviaWithQuestions>> getAllTrivias() {
        return mAllTrivias;
    }
}
