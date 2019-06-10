package com.isluji.travial.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.isluji.travial.R;
import com.isluji.travial.data.MapsViewModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    // Depending on the location accuracy that we choose,
    // we set these 2 variables to simplify the code
    private static final String LOCATION_ACCURACY = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int MY_LOCATION_REQUEST = LOCATION_ACCURACY.length();

    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationClient;
    private PlacesClient mPlacesClient;
    private MapsViewModel mViewModel;

    private Button mBtnRegLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            // TODO?
        }

        // Initialize the Google Maps location client.
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Places SDK.
        Places.initialize(this.getApplicationContext(), getString(R.string.google_maps_key));

        // Create a new Places client instance.
        mPlacesClient = Places.createClient(this);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mViewModel = ViewModelProviders.of(this).get(MapsViewModel.class);

        mBtnRegLocation = this.findViewById(R.id.btnRegLocation);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     *
     * If Google Play Services is not installed on the device,
     * the user will be prompted to install it inside the SupportMapFragment.
     * This method will only be triggered once the user has
     * installed Google Play Services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Zoom level 15 -> Streets
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        // FIRST OF ALL, we have to ensure that we have location permissions
        // If we have, we launch the tasks; if we don't, we request permission
        if (this.hasLocationPermission()) {
            this.locationRelatedTasks();
        } else {
            this.getLocationPermission();
        }

        // ---------- Event listeners ----------

        mViewModel.getAllPoiIds().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> poiIds) {
                if (poiIds != null) {
                    for (String poiId : poiIds) {
                        // Add a marker in the POI location
                        // Update the set of POIs in the ViewModel
                        addPoiToMap(poiId);
                    }
                } else {
                    // TODO? Qué hacer si no hay POIs
                }
            }
        });

        mBtnRegLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockTrivias();
            }
        });
    }

    private void locationRelatedTasks() throws SecurityException {
        // Replaced for the My Location layer
//        this.updateCurrentLocation();

        mMap.setMyLocationEnabled(true);
        this.updateCurrentPlace();
        this.centerOnMyLocation();
    }


    // ---------- Methods for permission request ----------

    /** Request location permissions to the user */
    private void getLocationPermission() {
        // If the user repeatedly refuses to give permission,
        // we show them an explanation about why we need it.
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, LOCATION_ACCURACY)) {
            this.explainPermissionRequest();

        // No explanation needed, we can directly request the permission.
        } else {
            this.requestLocationPermission();
        }
    }

    /** Checks if the app has location permissions */
    private boolean hasLocationPermission() {
        boolean coarseLocationGranted =
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean fineLocationGranted =
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        return (coarseLocationGranted || fineLocationGranted);
    }

    /** Show an explanation to the user *asynchronously* */
    private void explainPermissionRequest() {
        // Create dialog listener to handle the user response
        DialogInterface.OnClickListener dialogListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    // Request location again to the (now informed) user
                    requestLocationPermission();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // Return the user to the main screen
                    onBackPressed();
                    break;
            }
        };

        // Launch alert dialog
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.permission_rationale))
                .setPositiveButton("OK", dialogListener)
                .setNegativeButton("Cancel", dialogListener)
                .create()
                .show();
    }

    /**
     *  A local method to request required permissions;
     *  See https://developer.android.com/training/permissions/requesting
     *
     *  User response is handled in onRequestPermissionsResult()
     */
    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{LOCATION_ACCURACY}, MY_LOCATION_REQUEST
        );
    }

    /** Handles the user response to requestLocationPermission() */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            boolean permissionGranted = (grantResults.length > 0) &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED);

            if (permissionGranted) {
                // Finally we can do the location-related tasks
                this.locationRelatedTasks();
            } else {
                // We can't disable the location functionality.
                // We return to the main screen, but the user
                // will be required permission when he enters again.
                this.onBackPressed();
            }
        }

        // TODO? Check for other permissions this app might request
    }


    // ---------- Methods to handle Locations and Places ----------

    /** Returns the current (or nearest) Place */
    private void updateCurrentPlace() throws SecurityException {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest
                .builder(placeFields).build();

        // Call findCurrentPlace and handle the response
        mPlacesClient.findCurrentPlace(request)
            .addOnCompleteListener(task -> {

                // CASE A) Task completed successfully
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();

                    // The response contains at least one place
                    if (response != null && !response.getPlaceLikelihoods().isEmpty()) {
                        PlaceLikelihood mostLikely = response.getPlaceLikelihoods().get(0);

                        // Update the ViewModel
                        mViewModel.setCurrentPlace(mostLikely.getPlace());

                        mBtnRegLocation.setEnabled(true);
                        // TODO? If we do something with currentPlace, it has to be HERE

                        Log.i(getString(R.string.google_maps_tag),
                                String.format("Current place is probably '%s'",
                                        mostLikely.getPlace().getName()));
                    } else {
                        // TODO? In this case currentPlace remains null,
                        //  this could produce exceptions in following methods
                        Log.e(getString(R.string.google_maps_tag),
                                "There are no known places near you");
                    }

                // CASE B) Task failed with an exception
                } else {
                    Exception exception = task.getException();

                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        Log.e(getString(R.string.google_maps_tag),
                                "Current place failed (no exception thrown)");
                    }
                }
            });
    }

    private void addPoiToMap(String placeId) {
        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.VIEWPORT,
                Place.Field.TYPES, Place.Field.PHOTO_METADATAS);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest
                .builder(placeId, placeFields).build();

        // Send the place request
        mPlacesClient.fetchPlace(request)
            .addOnCompleteListener(task -> {

                // CASE A) Task completed successfully
                if (task.isSuccessful()) {
                    FetchPlaceResponse response = task.getResult();

                    SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(this.getApplicationContext());
                    Set<String> userPoiIds = sharedPrefs
                            .getStringSet("user_poi_ids", new HashSet<>());

                    if (response != null) {
                        Place poi = response.getPlace();

                        mViewModel.addPoi(poi);

                        // Check if the user has unlocked this POI
                        // in order to change the color of the marker
                        boolean unlocked = false;

                        if (userPoiIds != null) {
                            unlocked = userPoiIds.contains(poi.getId());
                        }

                        this.setMarker(poi, unlocked);

                        Log.i(getString(R.string.google_maps_tag),
                                "POI found: " + poi.getName());
                    } else {
                        Log.e(getString(R.string.google_maps_tag),
                                "POI fetched null: " + request.getPlaceId());
                    }

                // CASE B) Task failed with an exception
                } else {
                    Exception exception = task.getException();

                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        Log.e(getString(R.string.google_maps_tag),
                                "POI not found: " + request.getPlaceId());
                    }
                }

            });
    }

    /** Add a marker in the given Place */
    private void setMarker(Place place, boolean unlocked) {
        // LatLng is the only essential property for a marker; can't be null
        if (place.getLatLng() != null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(place.getLatLng())
                    .title(place.getName());        // Title is never null

            if (place.getTypes() != null) {
                markerOptions.snippet(place.getTypes().toString());
            }

            if (unlocked) {
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        } else {
            Log.i(getString(R.string.google_maps_tag), String.format(
                    "Place '%s' hasn't got coordinates -> We can't set a marker",
                    place.getName()));
        }
    }

    private void unlockTrivias() {
        if (mViewModel.isUserInAPoi()) {
            String currentPoiId = mViewModel.getCurrentPlace().getId();

            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this.getApplicationContext());
            Set<String> poiIds = sharedPrefs
                    .getStringSet("user_poi_ids", new HashSet<>());

            // CASE A) User hadn't unlocked this POI
            if (poiIds != null) {
                if (!poiIds.contains(currentPoiId)) {
                    poiIds.add(currentPoiId);

                    // TODO 1: Update the list of POIs of the User in the DB
                    // TODO 2: Notify the user that he has unlocked new trivias
                    // TODO 3: Go to the list of trivias
                } else {
                    // CASE B) User had already unlocked this POI:
                    //  -> Notify him and go to the list of trivias
                    new AlertDialog.Builder(this)
                            .setMessage("You had already unlocked this Point of Interest")
                            .setOnCancelListener(dialog -> {
                                // TODO: Load MA in that fragment and test that it works
                                Intent i = new Intent(this, MainActivity.class);
                                i.putExtra("frgToLoad", "triviaList");
                                this.startActivity(i);
                            }).create()
                            .show();
                }
            }
        } else {
            // CASE C) User isn't in any POI:
            //  -> Notify him and return to the MainActivity
            new AlertDialog.Builder(this)
                .setMessage("You are not located in any Point of Interest")
                .setOnCancelListener(
                        dialog -> this.onBackPressed()
                ).create()
                .show();
        }
    }

    private void centerOnMyLocation() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Obtain the View from the MapFragment
        if (mapFragment != null) {
            View mapView = mapFragment.getView();

            // Programmatically click on the "My Location" blue button
            if (mapView != null) {
                // 0x2 -> ID found using the Layout Inspector
                mapView.findViewById(0x2).performClick();
            }
        }
    }


    // ---------- Google Map callbacks ----------

    @Override
    public boolean onMyLocationButtonClick() {
        // true -> the default behavior should not occur
        // false -> the default behavior should occur
        // Default behavior = Center the camera on the user location.
        return false;   // TODO?
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        // TODO: Add some behavior? (no default behavior)
    }
}
