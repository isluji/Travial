package com.isluji.travial.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.isluji.travial.model.Trivia;

import java.util.List;

public class AppRepository {

    private AppDao mAppDao;

    private LiveData<List<Trivia>> mAllTrivias;

    // Constructor that gets a handle to the database
    // and initializes the member variables.
    AppRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);

        mAppDao = db.getAppDao();
        mAllTrivias = mAppDao.getAllTrivias();
    }

    // Wrapper for getAllTrivias().
    LiveData<List<Trivia>> getAllTrivias() {
        return mAllTrivias;
    }

    // Wrapper for the insert() method.
    public void insertTrivia(Trivia trivia) {
        new insertAsyncTask(mAppDao).execute(trivia);
    }

    /** AsyncTask class */
    // Ensures that insert() is called on a non-UI thread
    // (otherwise, the app would crash because Room forbids blocking the UI).
    private static class insertAsyncTask extends AsyncTask<Trivia, Void, Void> {

        private AppDao mAsyncTaskDao;

        insertAsyncTask(AppDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Trivia... params) {
            mAsyncTaskDao.insertTrivia(params[0]);
            return null;
        }
    }
}

