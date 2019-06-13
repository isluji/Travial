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
import java.util.Set;
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

    LiveData<List<TriviaWithQuestions>> getUserTrivias(Set<String> userPoiIds) {
        return mDao.getUserTrivias(userPoiIds);
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
                Log.v("user-auth-log", "New user: Signed up in the system");
                user = new User(email, googleId);
                mAsyncTaskDao.insertUser(user);
            } else {
                Log.v("user-auth-log", "Existent user: Fetching user data");
            }

            return user;
        }
    }

    // AsyncTask for getUserTrivias(userEmail)
//    private static class getUserTrivias_AsyncTask extends AsyncTask<String, Void, List<TriviaWithQuestions>> {
//
//        private AppDao mAsyncTaskDao;
//
//        getUserTrivias_AsyncTask(AppDao dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected List<TriviaWithQuestions> doInBackground(final String... params) {
//            String userEmail = params[0];
//
//            // Retrieve the user data if he's registered
//            User user = mAsyncTaskDao.findUserByEmail(userEmail);
//
//            // User wasn't registered -> Create new User
//            if (user == null) {
//                Log.v("user-auth-log", "New user: Signed up in the system");
//                user = new User(email, googleId);
//                mAsyncTaskDao.insertUser(user);
//            } else {
//                Log.v("user-auth-log", "Existent user: Fetching user data");
//            }
//
//            return user;
//        }
//    }
}

