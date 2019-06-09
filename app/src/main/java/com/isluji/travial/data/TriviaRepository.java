package com.isluji.travial.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

class TriviaRepository {

    private AppDao mDao;

    private LiveData<List<TriviaWithQuestions>> mUserTrivias;
    private LiveData<List<TriviaResult>> mUserResults;

    // Constructor that gets a handle to the database
    // and initializes the member variables.
    TriviaRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        mDao = db.getAppDao();

//        mUserTrivias = mDao.getUserTrivias();
//        mUserResults = mDao.getUserResults();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<TriviaWithQuestions>> getUserTrivias(String userEmail) {
        return mDao.getUserTrivias(userEmail);
    }

    LiveData<List<TriviaResult>> getUserResults(String userEmail) {
        return mDao.getUserResults(userEmail);
    }


    // ***** Wrapper methods for the AsyncTask queries *****

    long insertResult(TriviaResult newResult)
            throws ExecutionException, InterruptedException {
        return new insertResult_AsyncTask(mDao).execute(newResult).get();
    }

    User createOrRetrieveUser(String email, String googleId)
            throws ExecutionException, InterruptedException {
        return new createOrRetrieveUser_AsyncTask(mDao).execute(email, googleId).get();
    }


    // ***** AsyncTask queries (inner classes) *****

    // TODO: This should be a normal method or an AsyncTask?
    // AsyncTask for createOrRetrieveUser(email, googleId)
    private static class createOrRetrieveUser_AsyncTask extends AsyncTask<String, Void, User> {

        private AppDao mAsyncTaskDao;

        createOrRetrieveUser_AsyncTask(AppDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected User doInBackground(final String... params) {
            String email = params[0];
            String googleId = params[1];

            // Retrieve the user data if he's registered
            User user = mAsyncTaskDao.findUserByEmail(email);

            // User wasn't registered -> Create new User
            if (user == null) {
                user = new User(email, googleId);
                mAsyncTaskDao.insertUser(user);
            }

            return user;
        }
    }

    // AsyncTask for insertResult(newResult)
    private static class insertResult_AsyncTask extends AsyncTask<TriviaResult, Void, Long> {

        private AppDao mAsyncTaskDao;

        insertResult_AsyncTask(AppDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(final TriviaResult... params) {
            return mAsyncTaskDao.insertResult(params[0]);
        }
    }
}

