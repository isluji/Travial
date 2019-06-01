package com.isluji.travial.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

class TriviaRepository {

    private AppDao mDao;

    private LiveData<List<TriviaWithQuestions>> mAllTrivias;

    // Constructor that gets a handle to the database
    // and initializes the member variables.
    TriviaRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        mDao = db.getAppDao();

        mAllTrivias = mDao.getAllTrivias();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<TriviaWithQuestions>> getAllTrivias() {
        return mAllTrivias;
    }

    LiveData<List<TriviaResult>> getUserResults(String userEmail) {
        return mDao.getUserResults(userEmail);
    }


    // ***** Wrapper methods for the AsyncTask queries *****

    void insertUser(User newUser) {
        new insertUserIfNewAsyncTask(mDao).execute(newUser);
    }

    long insertResult(TriviaResult newResult) throws ExecutionException, InterruptedException {
        return new insertResultAsyncTask(mDao).execute(newResult).get();
    }


    // ***** AsyncTask queries (inner classes) *****

    // AsyncTask for insertUser(newUser)
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

    // AsyncTask for insertResult(newResult)
    private static class insertResultAsyncTask extends AsyncTask<TriviaResult, Void, Long> {

        private AppDao mAsyncTaskDao;

        insertResultAsyncTask(AppDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(final TriviaResult... params) {
            return mAsyncTaskDao.insertResult(params[0]);
        }
    }
}

