package com.isluji.travial.data;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapsViewModel extends AndroidViewModel {

    private MapsRepository mRepository;

    private LiveData<List<String>> mAppPoiIds;

    private Set<Place> mAppPois;
    private Set<Place> mProbablePlaces;

    // Matches each Marker with its place's photo,
    // so we can distinguish which photo to use in the InfoWindowAdapter
    private Map<Marker, Bitmap> mMarkersWithPhoto;

    // Keeps track of the last selected marker (though it may no longer be selected).
    // This is useful for refreshing the info window.
    private Marker mLastSelectedMarker;


    public MapsViewModel(@NonNull Application app) {
        super(app);

        mRepository = new MapsRepository(app);
        mAppPoiIds = mRepository.getAllPoiIds();

        mMarkersWithPhoto = new HashMap<>();
        mAppPois = new LinkedHashSet<>();
        mProbablePlaces = new LinkedHashSet<>();
    }


    // ************ Wrapper methods **************
    // (they completely hide the implementation from the UI)

    public LiveData<List<String>> getAllPoiIds() {
        return mAppPoiIds;
    }

    public void unlockPoiForUser(String poiId, String userEmail) {
        mRepository.unlockPoiForUser(poiId, userEmail);
    }

    public void addPoi(Place poi) {
        mAppPois.add(poi);
    }

    public void addProbablePlace(Place probablePlace) {
        mProbablePlaces.add(probablePlace);
    }

    public void attachPhotoToMarker(Marker marker, Bitmap photo) {
        mMarkersWithPhoto.put(marker, photo);
    }

    public Bitmap getPhoto(Marker marker) {
        return mMarkersWithPhoto.get(marker);
    }

    public Place getMostProbableCurrentPlace() {
        return mProbablePlaces.iterator().next();
    }

    public void updateSelectedMarker(Marker marker) {
        mLastSelectedMarker = marker;
    }


    // ---------- Custom methods ------------

    // Returns the most probable current POI
    // and null if the user isn't located in a POI
    public Place getCurrentPoi() {
        List<String> poiIds = mAppPoiIds.getValue();

        if (poiIds != null) {
            for (Place probPlace : mProbablePlaces) {
                if (poiIds.contains(probPlace.getId())) {
                    return probPlace;
                }
            }
        }

        return null;
    }
}
