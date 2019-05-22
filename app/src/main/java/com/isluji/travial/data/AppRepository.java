package com.isluji.travial.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaWithQuestions;
import java.util.List;

class AppRepository {

    private AppDao mDao;
    private LiveData<List<TriviaWithQuestions>> mAllTrivias;

    // Constructor that gets a handle to the database
    // and initializes the member variables.
    AppRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        mDao = db.getAppDao();
        mAllTrivias = mDao.getAllTrivias();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<TriviaWithQuestions>> getAllTrivias() {
        return mAllTrivias;
    }
}

