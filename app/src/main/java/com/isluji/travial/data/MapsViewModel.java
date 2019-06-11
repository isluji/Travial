package com.isluji.travial.data;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.libraries.places.api.model.Place;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MapsViewModel extends AndroidViewModel {

    private MapsRepository mRepository;

    private LiveData<List<String>> mAllPoiIds;
    private Set<Place> mPoiSet;

    private Place mCurrentPlace;

    public MapsViewModel(@NonNull Application app) {
        super(app);

        mRepository = new MapsRepository(app);
        mAllPoiIds = mRepository.getAllPoiIds();

        mPoiSet = new HashSet<>();
    }


    // ***** Wrapper methods *****
    // (they completely hide the implementation from the UI)

    public LiveData<List<String>> getAllPoiIds() {
        return mAllPoiIds;
    }

    public void unlockPoiForUser(String poiId, String userEmail) {
        mRepository.unlockPoiForUser(poiId, userEmail);
    }

    public void addPoi(Place poi) {
        mPoiSet.add(poi);
    }

    public Place getCurrentPlace() {
        return mCurrentPlace;
    }

    public void setCurrentPlace(Place currentPlace) {
        this.mCurrentPlace = currentPlace;
    }

    public boolean isUserInAPoi() {
        return Objects.requireNonNull(
                mAllPoiIds.getValue()
        ).contains(mCurrentPlace.getId());
    }
}
