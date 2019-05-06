package com.isluji.travial.data;

import android.app.Application;

import com.isluji.travial.model.Trivia;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppViewModel extends AndroidViewModel {

    // TODO? Store a list of trivias or a single trivia?
    // TODO? Create a custom constructor -> ViewModelProvider.Factory
    // TODO? LiveData or MutableLiveData (trivias and _trivias)?

    // _trivias and trivias are for proper encapsulation
//    private final MutableLiveData<List<Trivia>> _trivias;

    private AppRepository mRepository;

    // Cache the list of trivias.
    private LiveData<List<Trivia>> mAllTrivias;

    // Constructor that gets a reference to the repository
    // and gets the list of trivias from the repository.
    public AppViewModel (Application app) {
        super(app);

        mRepository = new AppRepository(app);
        mAllTrivias = mRepository.getAllTrivias();
    }

    /* Wrapper methods that completely hide the implementation from the UI */

    public LiveData<List<Trivia>> getAllTrivias() {
        return mAllTrivias;
    }

    public void insertTrivia(Trivia trivia) {
        mRepository.insertTrivia(trivia);
    }
}
