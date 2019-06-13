package com.isluji.travial.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.PointOfInterest;
import com.isluji.travial.model.User;

import java.util.List;

class MapsRepository {

    private AppDao mDao;

    private LiveData<List<String>> mAllPoiIds;

    // Constructor that gets a handle to the database
    // and initializes the member variables.
    MapsRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        mDao = db.getAppDao();

        mAllPoiIds = mDao.getAllPoiIds();
    }


    LiveData<List<String>> getAllPoiIds() {
        return mAllPoiIds;
    }


    // ***** Wrapper methods for the AsyncTask queries *****

    void unlockPoiForUser(String poiId, String userEmail) {
        new unlockPoiForUser_AsyncTask(mDao).execute(poiId, userEmail);
    }


    // ***** AsyncTask queries (inner classes) *****

    // AsyncTask for unlockPoiForUser(poiId, userEmail)
    private static class unlockPoiForUser_AsyncTask extends AsyncTask<String, Void, Void> {

        private AppDao mAsyncTaskDao;

        unlockPoiForUser_AsyncTask(AppDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            String poiId = params[0];
            String userEmail = params[1];

            // Retrieve the user data
            User user = mAsyncTaskDao.findUserByEmail(userEmail);

            // Add the detected POI in his list of unlocked POIs
            user.unlockPoi(poiId);

            // Update the User in the DB with the updated set of POIs
            int updatedRows = mAsyncTaskDao.updateUser(user);
            Log.v("trivia-list-logs",
                    "DB: User -> Updated rows: " + updatedRows);

            return null;
        }
    }
}
