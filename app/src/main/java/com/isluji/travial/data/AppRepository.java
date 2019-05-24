package com.isluji.travial.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;

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


    // ***** Wrapper methods for the AsyncTask queries *****

    void insertUser(User newUser) {
        new insertUserIfNewAsyncTask(mDao).execute(newUser);
    }


    // ***** AsyncTask queries *****

    private static class insertUserIfNewAsyncTask extends AsyncTask<User, Void, Void> {

        private AppDao mAsyncTaskDao;

        insertUserIfNewAsyncTask(AppDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            mAsyncTaskDao.insertUser(params[0]);
            return null;
        }
    }
}

