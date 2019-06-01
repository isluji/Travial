package com.isluji.travial.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.isluji.travial.model.PointOfInterest;

import java.util.List;

class MapsRepository {

    private AppDao mDao;

    private LiveData<List<PointOfInterest>> mAllPois;

    // Constructor that gets a handle to the database
    // and initializes the member variables.
    MapsRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        mDao = db.getAppDao();

        mAllPois = mDao.getAllPois();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<PointOfInterest>> getAllPois() {
        return mAllPois;
    }
}
