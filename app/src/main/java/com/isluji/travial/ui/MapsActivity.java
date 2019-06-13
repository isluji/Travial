package com.isluji.travial.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback/*,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener*/ {

    // Depending on the location accuracy that we choose,
    // we set these 2 variables to simplify the code
    private static final String LOCATION_ACCURACY = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int MY_LOCATION_REQUEST = LOCATION_ACCURACY.length();
    private static final int REQUEST_CHECK_SETTINGS = 33;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProvider;
    private PlacesClient mPlacesClient;
    private MapsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Initialize Google Maps & Places SDKs
        this.setUpGoogleAPIs();

        if (mMap == null) {

            SupportMapFragment mapFragment = (SupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.map);

            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            } else {
                Log.e(getString(R.string.google_maps_log),
                        "Error getting mapFragment");
            }
        }

        // Get a new or existing ViewModel from the ViewModelProvider.
        mViewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
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

        // After checking location permissions and settings,
        // perform the location-related tasks
        this.handleLocationTasks();

        // ---------- Event listeners ----------

        // Observer for changes of AllPoiIds from the DB
        mViewModel.getAllPoiIds().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> poiIds) {
                Log.v(getString(R.string.google_maps_log),
                        "Cargados POI IDs de la BD: " + poiIds);

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

        // "Check in" button
        Button btnCheckInPoi = this.findViewById(R.id.btnCheckInPoi);
        btnCheckInPoi.setOnClickListener(v -> checkInPoi());
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (mMap == null) {
//            // Obtain the SupportMapFragment and get notified
//            // when the map is ready to be used.
//            SupportMapFragment mapFragment = (SupportMapFragment)
//                    getSupportFragmentManager().findFragmentById(R.id.map);
//
//            if (mapFragment != null) {
//                mapFragment.getMapAsync(this);
//            } else {
//                Log.e(getString(R.string.google_maps_log),
//                        "Error getting mapFragment");
//            }
//        }

        // This verification should be done during onStart()
        // because the system calls it when the user returns to the activity,
        // which ensures that location permissions are granted,
        // and location settings (best provider) are recommended,
        // each time the activity resumes from the stopped state.
//        this.checkLocationSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final LocationSettingsStates states =
                LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            // Check location settings request
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.i(getString(R.string.google_maps_log),
                                "Recommended location settings enabled!");
                        this.locationRelatedTasks();
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.i(getString(R.string.google_maps_log),
                                "User chose not to change location settings");
                        this.locationRelatedTasks();
                        break;

                    default:
                        break;
                }

                break;
        }
    }

    private void locationRelatedTasks() throws SecurityException {
        // Replaced for "My Location" layer
//        this.updateCurrentLocation();

        if (!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }

        this.updateCurrentPlace();
        this.centerOnMyLocation();
    }

    private void setUpGoogleAPIs() {
        // Initialize the Google Maps location client.
        mFusedLocationProvider = LocationServices
                .getFusedLocationProviderClient(this);

        // Initialize Places SDK.
        Places.initialize(this.getApplicationContext(),
                getString(R.string.google_maps_key));

        // Create a new Places client instance.
        mPlacesClient = Places.createClient(this);
    }

    // ---------- Methods for permission request ----------

    /** Request location permissions to the user */
    private void handleLocationTasks() {
        if (!this.hasLocationPermission()) {
            // If the user repeatedly refuses to give permission,
            // we show them an explanation about why we need it.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, LOCATION_ACCURACY)) {
                this.explainPermissionRequest();

            // No explanation needed, we can directly request the permission.
            } else {
                this.requestLocationPermission();
            }
        } else {
            Log.i(getString(R.string.google_maps_log),
                    "User had already granted location permissions");

            this.locationRelatedTasks();
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
                Log.i(getString(R.string.google_maps_log),
                        "Location permissions granted!");

                // Now we should ask the user for the location settings
                this.checkLocationSettings();
            } else {
                // We can't disable the location functionality.
                // We return to the main screen, but the user
                // will be required permission when he enters again.
                this.onBackPressed();
            }
        }
    }

    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000); // 5 seconds

        // Add all of the LocationRequests that the app will be using
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest
                .Builder().addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            // CASE A) All location settings are satisfied.
            try {
                LocationSettingsResponse response =
                        task1.getResult(ApiException.class);

                if (response != null) {
                    final LocationSettingsStates states =
                            response.getLocationSettingsStates();
                } else {
                    Log.e(getString(R.string.google_maps_log),
                            "Location settings request failed");
                }

                Log.v(getString(R.string.google_maps_log),
                        "All location settings are satisfied");

                // We can initialize location requests here.
                this.locationRelatedTasks();

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {

                    // CASE B) Location settings are not satisfied.
                    // But could be fixed by showing the user a dialog.
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;

                            // Show the dialog with startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            e.printStackTrace();
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                            e.printStackTrace();
                        }

                        break;

                    // CASE C) Location settings are not satisfied.
                    // However, we have no way to fix the settings,
                    // so we won't show the dialog.
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(getString(R.string.google_maps_log),
                                "Location settings are not satisfied,\n"
                                        + "but there's no way to fix the settings");

                        this.locationRelatedTasks();

                        break;
                }
            }
        });
    }


    // ---------- Methods to handle Locations and Places ----------

    /** Obtains the current (or nearest) Place */
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
                        List<PlaceLikelihood> likelihoods = response.getPlaceLikelihoods();

                        // Get the 2 most probable places
                        for (int i = 0; (i < 2) && (i < likelihoods.size()); i++) {
                            PlaceLikelihood pl = likelihoods.get(i);
                            mViewModel.addProbablePlace(pl.getPlace());
                            Log.v(getString(R.string.google_maps_log),
                                    String.format("Current place likelihood %d: %s",
                                            i, pl.getPlace().getName()));
                        }

                        // Now the user can register Points of Interest
                        Button btnCheckInPoi = this.findViewById(R.id.btnCheckInPoi);
                        btnCheckInPoi.setEnabled(true);

                        // TODO? Handle currentPlace tasks HERE (if any)
                    } else {
                        Log.i(getString(R.string.google_maps_log),
                                "There are no known places near the user");
                    }

                // CASE B) Task failed with an exception
                } else {
                    Exception exception = task.getException();

                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        Log.e(getString(R.string.google_maps_log),
                                "Get current place failed (no exception thrown)");
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

                    // We're not editing here, so we don't have to copy the set
                    Set<String> userPoiIds = sharedPrefs
                            .getStringSet("user_poi_ids", new LinkedHashSet<>());

                    if (response != null) {
                        Place poi = response.getPlace();

                        // Add POI to the ViewModel
                        mViewModel.addPoi(poi);

                        // Check if the user has unlocked this POI
                        // in order to change the color of the marker
                        boolean unlocked = false;

                        if (userPoiIds != null) {
                            unlocked = userPoiIds.contains(poi.getId());
                        }

                        this.setMarker(poi, unlocked);
                    } else {
                        Log.e(getString(R.string.google_maps_log),
                                "Error fetching POI: " + request.getPlaceId());
                    }

                // CASE B) Task failed with an exception
                } else {
                    Exception exception = task.getException();

                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        Log.e(getString(R.string.google_maps_log),
                                "POI not found: " + request.getPlaceId());
                    }
                }

            });
    }

    /** Add a marker in the given Place */
    private void setMarker(Place place, boolean unlocked) {
        // LatLng is the only essential property for a marker -> can't be null
        if (place.getLatLng() != null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(place.getLatLng())
                    .title(place.getName());        // Title is never null

            List<Place.Type> placeTypes = place.getTypes();

            if (placeTypes != null) {
                StringBuilder typesSnippet = new StringBuilder();

                for (Place.Type type: placeTypes) {
                    typesSnippet.append(type.toString());

                    if (placeTypes.lastIndexOf(type) != placeTypes.size() - 1) {
                        typesSnippet.append(" | ");
                    }
                }

                markerOptions.snippet(typesSnippet.toString());
            }

            if (unlocked) {
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        } else {
            Log.i(getString(R.string.google_maps_log),
                    String.format("Error adding marker in '%s': No LatLng found",
                            place.getName()));
        }
    }

    private void checkInPoi() {
        String message;
        DialogInterface.OnCancelListener onCancelListener;

        Place currentPoi = mViewModel.getCurrentPoi();

        // If the user is located in one of the available POIs
        if (currentPoi != null) {
            String currentPoiId = currentPoi.getId();
            String currentPoiName = currentPoi.getName();

            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this.getApplicationContext());

            Set<String> userPoiIds = new LinkedHashSet<>(
                    // According to SharedPreferences documentation,
                    // we must NOT modify the getStringSet() Set instance,
                    // so we create a new object copying the contents
                    sharedPrefs.getStringSet("user_poi_ids", new LinkedHashSet<>())
            );

            String userEmail = sharedPrefs
                    .getString("user_email", null);

            // Validate params (userPoiIds can't be null)
            if (userEmail != null) {

                // CASE A) User hadn't unlocked this POI
                if (!userPoiIds.contains(currentPoiId)) {

                    // Update the set of POIs of the User in SharedPreferences
                    userPoiIds.add(currentPoiId);

                    sharedPrefs.edit()
                            .putStringSet("user_poi_ids", userPoiIds)
                            .apply();

                    // Update the list of POIs of the User in the DB
                    mViewModel.unlockPoiForUser(currentPoiId, userEmail);

                    message = String.format("CONGRATULATIONS!!!\n\n" +
                            "You've just unlocked all trivias from:\n\n%s", currentPoiName);

                // CASE B) User had already unlocked this POI
                } else {
                    message = "You had already unlocked this Point of Interest";
                }

                // Both cases A and B -> Go to the list of trivias
                onCancelListener = dialog -> {
                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra("fragment", "trivia_list");
                    this.startActivity(i);
                };

            // CASE C) userEmail is null
            } else {    // todo?
                message = "Sorry! Something has gone wrong";

                // Go back to main activity
                onCancelListener = dialog -> this.onBackPressed();
            }

        // CASE D) User is not located in a POI
        } else {
            message = "You are not located in any Point of Interest";

            // Go back to main activity
            onCancelListener = dialog -> this.onBackPressed();
        }

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setOnCancelListener(onCancelListener)
                .create()
                .show();
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

                Log.v(getString(R.string.google_maps_log),
                        "Centered map in the user location");
            }
        }
    }
}
