package com.isluji.travial.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.PointOfInterest;

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

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<String>> getAllPoiIds() {
        return mAllPoiIds;
    }
}
