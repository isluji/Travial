package com.isluji.travial.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.isluji.travial.R;
import com.isluji.travial.data.AppDatabase;
import com.isluji.travial.data.MapsViewModel;

import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Depending on the location accuracy that we choose,
    // we set these 2 variables to simplify the code
    private static final String LOCATION_ACCURACY = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int MY_LOCATION_REQUEST = LOCATION_ACCURACY.length();

    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationClient;
    private MapsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        // Initialize the Google Maps location client
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /* ***** Set up Room DB ***** */

        // Get an instance of the created database
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        Log.v(getString(R.string.room_db_name), "Se ha llamado a AppDB.getDatabase()");

        // Get a new or existing ViewModel from the ViewModelProvider
        mViewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play Services is not installed on the device,
     * the user will be prompted to install it inside the SupportMapFragment.
     * This method will only be triggered once the user has
     * installed Google Play Services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check the location permissions
        boolean coarseLocationGranted =
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean fineLocationGranted =
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        // If we don't have any location permissions, we have to request them
        if (!coarseLocationGranted && !fineLocationGranted) {

            boolean shouldShowExplanation =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                            this, LOCATION_ACCURACY
                    );

            // If the user repeatedly refuses to give permission,
            // we show them an explanation about why we need it.
            if (shouldShowExplanation) {
                this.explainPermissionUsage();

            // No explanation needed, we can directly request the permission.
            } else {
                this.requestLocationPermission();
            }

        // If we have the required permission, we can do our thing
        } else {
            this.locateUser();
        }

        // -----------------------------------------

        Button btnRegister = this.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Check if the location matches with any POI
                //  and, in that case, unblock those trivias for this user
            }
        });
    }

    /** The user response is handled in onRequestPermissionsResult() */
    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{LOCATION_ACCURACY}, MY_LOCATION_REQUEST
        );
    }

    /** Here we handle the user response to requestLocationPermission() */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            boolean permissionGranted = (grantResults.length > 0) &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED);

            if (permissionGranted) {
                // Now we can do the location-related task
                this.locateUser();
            } else {
                // We can't disable the location functionality.
                // We return to the main screen, but the user
                // will be required permission when he enters again.
                this.onBackPressed();
            }
        } else {
            // TODO? Check for other permissions this app might request
        }
    }

    // -----------------------------------------------------

    /** Obtains the current location, sets a marker and moves there the camera */
    private void locateUser() throws SecurityException {
        mLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            // Got last known location. In some rare situations this can be null.
            public void onSuccess(Location location) {
                if (location != null) {
                    // Logic to handle location object
                    addFocusedMarker(location);
                } else {
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    Log.v(getString(R.string.google_maps_tag),
                            "gpsEnabled: " + gpsEnabled);
                    Log.v(getString(R.string.google_maps_tag),
                            "networkEnabled: " + networkEnabled);

                    // TODO? Control 3 possible causes of location == null
                }
            }
        });
    }

    /** Show an explanation to the user *asynchronously* */
    private void explainPermissionUsage() {
        // Create dialog listener to handle the user response
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    /** Add a marker in the given location and move the camera */
    public void addFocusedMarker(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.addMarker(new MarkerOptions().position(latLng).title("Your current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
