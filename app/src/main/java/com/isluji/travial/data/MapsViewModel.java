package com.isluji.travial.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.isluji.travial.model.PointOfInterest;

import java.util.List;

public class MapsViewModel extends AndroidViewModel {

    private MapsRepository mRepository;

    private LiveData<List<PointOfInterest>> mAllPois;

    public MapsViewModel(@NonNull Application app) {
        super(app);

        mRepository = new MapsRepository(app);
        mAllPois = mRepository.getAllPois();
    }


    // ***** Wrapper methods *****
    // (they completely hide the implementation from the UI)

    public LiveData<List<PointOfInterest>> getAllPois() {
        return mAllPois;
    }
}
