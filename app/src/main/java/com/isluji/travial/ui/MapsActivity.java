package com.isluji.travial.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.isluji.travial.R;
import com.isluji.travial.data.MapsViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    // Depending on the location accuracy that we choose,
    // we set these 2 variables to simplify the code
    private static final String LOCATION_ACCURACY = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int MY_LOCATION_REQUEST = LOCATION_ACCURACY.length();
    private static final int REQUEST_CHECK_SETTINGS = 33;

    // Tag for the maps-related logs
    private String MAPS_LOG_TAG;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProvider;
    private PlacesClient mPlacesClient;
    private MapsViewModel mViewModel;

    MapInfoWindowFragment mMapIwFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_maps);

        MAPS_LOG_TAG = getString(R.string.google_maps_log);

        // Initialize Google Maps & Places SDKs
        this.setUpGoogleAPIs();

        // Request the map through the mapFragment
         mMapIwFragment = (MapInfoWindowFragment)
                this.getSupportFragmentManager()
                        .findFragmentById(R.id.infoWindowMap);

        if (mMapIwFragment != null) {
            // Get the GoogleMap object asynchronously from our fragment.
            mMapIwFragment.getMapAsync(this);
        } else {
            Log.e(MAPS_LOG_TAG,
                    "Error getting MapInfoWindowFragment");
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

        // Setting an info window adapter allows us to change
        // both the contents and look of the info window.
        mMap.setInfoWindowAdapter(new PoiInfoWindowAdapter());

        // Set listeners for marker events.
        // See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
//        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowClickListener(this);
//        mMap.setOnInfoWindowCloseListener(this);
//        mMap.setOnInfoWindowLongClickListener(this);

        // Override the default content description on the view,
        // for accessibility mode. Ideally this string would be localised.
        mMap.setContentDescription("Map with all the Points of Interest");

        // After checking location permissions and settings,
        // perform the location-related tasks
        this.handleLocationTasks();

        // Zoom level 15 -> Streets
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        // "Check in" button click listener
        Button btnCheckInPoi = this.findViewById(R.id.btnCheckInPoi);
        btnCheckInPoi.setOnClickListener(v -> checkInPoi());

        // ******** LiveData Observer ********

        mViewModel.getAllPoiIds().observe(this,
            poiIds -> {
                if (poiIds != null) {
                    Log.v(MAPS_LOG_TAG, "MapsActivity: PoI IDs loaded from BD");

                    for (String poiId : poiIds) {
                        // Adds a marker in the POI location and updates the ViewModel
                        this.addPoiToMap(poiId);
                    }
                }
            }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        // This verification should be done during onStart()
        // because the system calls it when the user returns to the activity,
        // which ensures that location permissions are granted,
        // and location settings (best provider) are recommended,
        // each time the activity resumes from the stopped state.
//        if (this.checkMapReady()) {
//          this.checkLocationSettings();
//        }
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
                        Log.i(MAPS_LOG_TAG,
                                "Recommended location settings enabled!");
                        this.locationRelatedTasks();
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.i(MAPS_LOG_TAG,
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
    }

    private void setUpGoogleAPIs() {
        // Initialize the Google Maps location client.
        mFusedLocationProvider = LocationServices
                .getFusedLocationProviderClient(this);

        // Initialize Places SDK.
        Places.initialize(this, getString(R.string.google_maps_key));

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
            Log.i(MAPS_LOG_TAG,
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
    private void requestLocationPermission() {
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
                Log.i(MAPS_LOG_TAG,
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
                    Log.e(MAPS_LOG_TAG,
                            "Location settings request failed");
                }

                Log.v(MAPS_LOG_TAG,
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
                        Log.e(MAPS_LOG_TAG,
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
                Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG/*, Place.Field.VIEWPORT*/);

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

                            Log.v(MAPS_LOG_TAG,
                                    String.format("Current place likelihood %d: %s",
                                            i, pl.getPlace().getName()));
                        }

                        // **********************************************
                        // NOW WE HAVE THE CURRENT PLACE -> DO TASKS HERE
                        // **********************************************

                        // Center the camera in the current place
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                                mViewModel.getMostProbableCurrentPlace().getLatLng()));
                        Log.v(MAPS_LOG_TAG,
                                "Centered camera in the most probable place");

                        // Now the user can register Points of Interest
                        Button btnCheckInPoi = this.findViewById(R.id.btnCheckInPoi);
                        btnCheckInPoi.setEnabled(true);
                    } else {
                        Log.i(MAPS_LOG_TAG,
                                "There are no known places near the user");
                    }

                // CASE B) Task failed with an exception
                } else {
                    Exception exception = task.getException();

                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        Log.e(MAPS_LOG_TAG,
                                "Get current place failed (no exception thrown)");
                    }
                }
            });
    }

    private void addPoiToMap(String placeId) {
        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.TYPES, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest
                .builder(placeId, placeFields)
                .build();

        // Send the place request
        mPlacesClient.fetchPlace(request)
            .addOnCompleteListener(placeTask -> {

                // CASE A) Task completed successfully
                if (placeTask.isSuccessful()) {
                    FetchPlaceResponse placeResponse = placeTask.getResult();

                    SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(this.getApplicationContext());

                    // We're not editing here, so we don't have to copy the set
                    Set<String> userPoiIds = sharedPrefs
                            .getStringSet("user_poi_ids", new LinkedHashSet<>());

                    if (placeResponse != null) {
                        Place poi = placeResponse.getPlace();

                        // Add POI to the ViewModel
                        mViewModel.addPoi(poi);

                        // ****************************************
                        // NOW WE HAVE THE PLACE -> DO TASKS HERE
                        // ****************************************

                        // Check if the user has unlocked this POI
                        // in order to change the color of the marker
                        boolean unlocked;

                        if (userPoiIds != null) {
                            unlocked = userPoiIds.contains(poi.getId());
                        } else {
                            unlocked = false;
                        }

                        /* ---------- PHOTO REQUEST ---------- */

                        List<PhotoMetadata> photoMetadatas = poi.getPhotoMetadatas();

                        // Place has at least a photo
                        if (photoMetadatas != null && !photoMetadatas.isEmpty()) {
                            // Initialize the params for addPoiMarker()
                            PhotoMetadata photoMetadata = photoMetadatas.get(0);
                            String photoAttribs = photoMetadata.getAttributions();

                            // Create a FetchPhotoRequest.
                            FetchPhotoRequest photoRequest = FetchPhotoRequest
                                    .builder(photoMetadata)
                                    .setMaxWidth(428) // Optional.
                                    .setMaxHeight(256) // Optional.
                                    .build();

                            mPlacesClient.fetchPhoto(photoRequest)

                                    .addOnCompleteListener(photoTask -> {
                                        Bitmap poiPhoto = null;

                                        // CASE A) Photo fetched successfully
                                        if (photoTask.isSuccessful()) {
                                            FetchPhotoResponse photoResponse = photoTask.getResult();

                                            if (photoResponse != null) {
                                                poiPhoto = photoResponse.getBitmap();
                                            }

                                        // CASE B) Failed to fetch the photo
                                        } else {
                                            Exception exception = photoTask.getException();

                                            if (exception instanceof ApiException) {
                                                ApiException apiException = (ApiException) exception;
                                                int statusCode = apiException.getStatusCode();

                                                // Handle error with given status code.
                                                Log.e(MAPS_LOG_TAG,
                                                        "Place not found: " + exception.getMessage());
                                            }
                                        }

                                        // ****************************************
                                        // NOW WE HAVE THE PHOTO -> ADD MARKER HERE
                                        // ****************************************

                                        // Creates a marker with photo
                                        this.addPoiMarker(poi, poiPhoto, photoAttribs, unlocked);

                                    // CASE C) Photo request canceled
                                    }).addOnCanceledListener(() -> {
                                        // Creates a marker without photo
                                        this.addPoiMarker(poi, null, photoAttribs, unlocked);
                                    });

                        // CASE D) Place hasn't got any photos
                        } else {
                            this.addPoiMarker(poi, null, null, unlocked);
                        }

                    } else {
                        Log.e(MAPS_LOG_TAG,
                                "Error fetching POI: " + request.getPlaceId());
                    }

                // CASE B) Task failed with an exception
                } else {
                    Exception exception = placeTask.getException();

                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        Log.e(MAPS_LOG_TAG,
                                "POI not found: " + request.getPlaceId());
                    }
                }

            });
    }

    /** Add a marker in the given Place */
    private void addPoiMarker(Place place, @Nullable Bitmap photo, @Nullable String photoAttribs, boolean unlocked) {
//        Log.v(getString(R.string.google_maps_log),
//                "---------------------------------------");
//        Log.v(getString(R.string.google_maps_log),
//                "Place: " + place.getName());
//        Log.v(getString(R.string.google_maps_log),
//                "Third-party attribs: " + place.getAttributions());
//        Log.v(getString(R.string.google_maps_log),
//                "Photo: " + photo);
//        Log.v(getString(R.string.google_maps_log),
//                "Photo attribs: " + photoAttribs);
//        Log.v(getString(R.string.google_maps_log),
//                "Website URI: " + place.getWebsiteUri());
//        Log.v(getString(R.string.google_maps_log),
//                "Unlocked: " + unlocked);

        // LatLng is the only essential property for a marker -> can't be null
        if (place.getLatLng() != null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(place.getLatLng());   // Only essential property

            // Beautify the type list contents
            List<Place.Type> placeTypes = place.getTypes();
            String snippet = "Unknown Place Types";  // PLACEHOLDER

            if (placeTypes != null && !placeTypes.isEmpty()) {
                StringBuilder sb = new StringBuilder();

                for (Place.Type type: placeTypes) {
                    sb.append(type.toString());

                    if (placeTypes.lastIndexOf(type) != placeTypes.size() - 1) {
                        sb.append(" | ");
                    }
                }

                snippet = sb.toString();
            }

            // Title -> Name is never null
            markerOptions.title(place.getName());
            markerOptions.snippet(snippet);

            // Show in blue those POIs that user has already unlocked
            if (unlocked) {
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            // Add marker to the map
            Marker marker = mMap.addMarker(markerOptions);

            // -----------------------------------------------------------

            // PROBLEM: Neither Marker nor MarkerOpts contain fields
            // to store the Places photo and attributions

            // ATTRIBS -> Store them in a map and pass it as Marker's 'tag'
            Map<String, String> attributions = new HashMap<>();

            // Place attributions -> third-party attributions
            String placeAttribsHtml = "<span>Place details by </span>";
            String photoAttribsHtml = "<span>Photo by </span>";
            String licenseHtml = "<span>Licensed by </span>";

            // 1. Building place attributions' HTML string
            StringBuilder sb = new StringBuilder(placeAttribsHtml);
            List<String> placeAttribs = place.getAttributions();

            if (placeAttribs != null && !placeAttribs.isEmpty()) {
                for (String attrib : placeAttribs) {
                    sb.append(attrib);

                    if (placeAttribs.lastIndexOf(attrib)
                            != placeAttribs.size() - 1) {
                        sb.append("<span> | </span>");
                    }
                }
            } else {
                Uri placeWebUri = place.getWebsiteUri();

                if (placeWebUri != null) {
                    sb.setLength(0);    // Reset the buffer
                    sb.append("<span data-uri=\"");
                    sb.append(placeWebUri.toString());
                    sb.append("\">More place details clicking this window<span>");
                } else {
                    sb.append("<span>an unknown Google user</span>");
                }
            }

            placeAttribsHtml = sb.toString();
            sb.setLength(0);    // Clears the buffer

            // 2. Building photo attributions' HTML string
            sb.append(photoAttribsHtml);

            if (photoAttribs != null) {
                sb.append(photoAttribs);
            } else {
                sb.append("<span>an unknown Google user</span>");
            }

            photoAttribsHtml = sb.toString();
            sb.setLength(0);    // Clears the buffer

            // 3. Building license HTML string
            sb.append(licenseHtml);

            // TODO: Any way to find out the real license?
            sb.append(getString(R.string.cc_attrib_license_html));

            licenseHtml = sb.toString();
            sb.setLength(0);    // Clears the buffer

            // Add all attributions to the HashMap
            attributions.put("place", placeAttribsHtml);
            attributions.put("photo", photoAttribsHtml);
            attributions.put("license", licenseHtml);

            marker.setTag(attributions);

            // TODO: InteractiveInfoWindowAndroid functionality
            this.toggleInfoWindow(marker, 0, 0, false);

            // PHOTOS -> Bind them with the marker in a member map,
            //      so we can retrieve it in the InfoWindowAdapter
            if (photo != null) {
                mViewModel.attachPhotoToMarker(marker, photo);
            }
        } else {
            Log.e(MAPS_LOG_TAG,
                    String.format("Error adding marker in '%s': No LatLng found",
                            place.getName()));
        }
    }

    private void toggleInfoWindow(Marker marker, int offsetX, int offsetY, boolean enableOffsetX) {
//        // Request the map through the mapFragment
//        MapInfoWindowFragment mMapIwFragment = (MapInfoWindowFragment)
//                this.getSupportFragmentManager()
//                        .findFragmentById(R.id.infoWindowMap);

        if (mMapIwFragment != null) {
            // NOTE 1) If you want to use dp offsets,
            // you should convert the values to px by yourself.
            // The constructor expects absolute pixel values.
            // TODO: Convert dp values to px

            // Create marker specification by providing InfoWindow's
            // x and y offsets from marker's screen location.
            InfoWindow.MarkerSpecification markerSpec =
                    new InfoWindow.MarkerSpecification(offsetX, offsetY);

            // NOTE 2) By default offsetX will be ignored,
            // so if you want it to take effect:
            if (enableOffsetX) {
                markerSpec.setCenterByX(false);
            }

            // Creates an interactive InfoWindow with the given specs.
            InfoWindow infoWindow = new InfoWindow(marker, markerSpec, mMapIwFragment);

            // Get an instance of InfoWindowManager to control our InfoWindow.
            InfoWindowManager iwManager = mMapIwFragment.infoWindowManager();

            // Shows the InfoWindow or hides it if it is already opened.
//            iwManager.toggle(infoWindow, true);
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

    private boolean checkMapReady() {
        boolean mapReady = true;

        if (mMap == null) {
            Toast.makeText(this,
                    "Map is not ready yet", Toast.LENGTH_SHORT).show();
            mapReady = false;
        }

        return mapReady;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        }

        mViewModel.updateSelectedMarker(marker);

        // We return false to indicate that we have not consumed the event
        // and that we wish for the default behavior to occur
        // (which is for the camera to move such that the marker is centered
        // and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // The info window will not respect any of the interactivity typical
        // for a normal view such as touch or gesture events. However,
        // you can listen to a generic click event on the whole info window.

        // ACTION -> Navigate to the photo attribution link

        // Retrieve the attributions from the 'tag' property
        @SuppressWarnings("unchecked")
        Map<String, String> attributions = (Map<String, String>) marker.getTag();

        if (attributions != null) {
            // Extract the links from the place attributions
            String atrribs = attributions.get("place");
            List<String> links = this.extractLinks(atrribs);

            // If no place links, then use photo links
            if (links == null || links.isEmpty()) {
                atrribs = attributions.get("photo");
                links = this.extractLinks(atrribs);
            }

            Uri linkUri = Uri.parse(links.get(0));

            // Launch a intent to browse the link
            Intent intent = new Intent(Intent.ACTION_VIEW, linkUri);
            this.startActivity(intent);
        }
    }

    private CharSequence parseHtml(CharSequence snippet) {
        if (snippet != null) {
            snippet = HtmlCompat.fromHtml(
                    snippet.toString(),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            );
        }

        return snippet;
    }

    private List<String> extractLinks(String text) {
        List<String> links = new ArrayList<>();

        Matcher matcher = Patterns.WEB_URL.matcher(text);

        while (matcher.find()) {
            String url = matcher.group();
            Log.d(MAPS_LOG_TAG, "URL extracted: " + url);
            links.add(url);
        }

        return links;
    }


    // ------------- Inner classes and listeners -----------------

    /** Listen when an InfoWindow is hiding or showing **/
    private InfoWindowManager.WindowShowListener windowShowListener =
            new InfoWindowManager.WindowShowListener() {
                @Override
                public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {

                }

                @Override
                public void onWindowShown(@NonNull InfoWindow infoWindow) {

                }

                @Override
                public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {

                }

                @Override
                public void onWindowHidden(@NonNull InfoWindow infoWindow) {

                }
            };


    /** Demonstrates customizing the InfoWindow and/or its contents. */
    class PoiInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both ViewGroups containing an ImageView with id "imgPhoto"
        // and two TextViews with ids "txtName" and "txtTypes".
//        private final View mWindow;
        private final View mContents;

        PoiInfoWindowAdapter() {
//            mWindow = getLayoutInflater()
//                    .inflate(R.layout.photo_info_window,
//                            findViewById(R.id.map), false);
            mContents = getLayoutInflater()
                    .inflate(R.layout.photo_info_window,
                            findViewById(R.id.infoWindowMap), false);
        }

        // Provide a view that will be used for the entire info window.
        @Override
        public View getInfoWindow(Marker marker) {
            return null;    // This means that getInfoContents will be called.
        }

        // Just customize the contents of the window but still
        // keep the default info window frame and background.
        @Override
        public View getInfoContents(Marker marker) {
            // Update the View with the POI properties
            this.render(marker, mContents);

            return mContents;
        }

        private void render(Marker marker, View infoContents) {
            ImageView imgPhoto = infoContents.findViewById(R.id.imgPhoto);
            TextView txtName = infoContents.findViewById(R.id.txtName);
            TextView txtTypes = infoContents.findViewById(R.id.txtTypes);

            TextView txtPlaceAttribs = infoContents.findViewById(R.id.txtPlaceAttribs);
            TextView txtPhotoAttribs = infoContents.findViewById(R.id.txtPhotoAttribs);
            TextView txtLicense = infoContents.findViewById(R.id.txtLicense);

            // Retrieve the photo from our custom map
            Bitmap poiPhoto = mViewModel.getPhoto(marker);

            // Retrieve the attributions from the 'tag' property
            @SuppressWarnings("unchecked")
            Map<String, String> attributions = (Map<String, String>) marker.getTag();

            imgPhoto.setImageBitmap(poiPhoto);
            txtName.setText(marker.getTitle());
            txtTypes.setText(marker.getSnippet());

            if (attributions != null) {
                // Get the raw HTML string from the HashMap
                CharSequence placeAttribs = attributions.get("place");
                CharSequence photoAttribs = attributions.get("photo");
                CharSequence license = attributions.get("license");

                // Parse the HTML code for the TextViews
                placeAttribs = parseHtml(placeAttribs);
                photoAttribs = parseHtml(photoAttribs);
                license = parseHtml(license);

                // Update the TextViews' content
                txtPlaceAttribs.setText(placeAttribs);
                txtPhotoAttribs.setText(photoAttribs);
                txtLicense.setText(license);
            }

            if (poiPhoto == null) {
                imgPhoto.setVisibility(View.GONE);
                txtPhotoAttribs.setVisibility(View.GONE);
            } else {
                imgPhoto.setVisibility(View.VISIBLE);
                txtPhotoAttribs.setVisibility(View.VISIBLE);
            }

            // Clear the tag to free system memory
//            marker.setTag(null);
        }
    }
}
