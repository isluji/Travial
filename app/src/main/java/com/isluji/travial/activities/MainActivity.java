package com.isluji.travial.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.isluji.travial.R;
import com.isluji.travial.data.AppDatabase;
import com.isluji.travial.data.AppViewModel;
import com.isluji.travial.fragments.MapsFragment;
import com.isluji.travial.fragments.QuestionFragment;
import com.isluji.travial.fragments.TriviaFragment;
import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaQuestion;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        TriviaFragment.OnListFragmentInteractionListener,
        QuestionFragment.OnListFragmentInteractionListener {

    final int PERMISSION_LOCATION = 111;

    private GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient mLocationClient;
    private AppViewModel mAppViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Database code */

        // Get an instance of the created database
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        Log.v("app-db", "Se ha llamado a AppDB.getDatabase()");

        // DB is created when the first query is executed
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                db.runInTransaction(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });


        /** ViewModel code */

        // Obtain the ViewModel component.
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);


        /** Google Maps code */

        // Initialize the Google Play Services client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        // Initialize the Google Maps location client
        mLocationClient = new FusedLocationProviderClient(this);


        /** Main screen code */

        // Set variables for the view elements
        Button btnPlay = (Button) findViewById(R.id.btnPlay);
        Button btnLocation = (Button) findViewById(R.id.btnLocation);

        // OnClickListeners for the buttons
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlayFragment();
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMapsFragment();
            }
        });


        /** Navigation Drawer code */

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the FAB button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Drawer menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Back button closes the drawer if it's open
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Handle action bar item clicks here.
        // The action bar will automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // TODO: Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        // Close the drawer when the action is done
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Métodos para cargar los distintos fragmentos
     */

    public void loadPlayFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.app_bar_main, new TriviaFragment())
                .addToBackStack(null)
                .commit();
    }

    public void loadMapsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.app_bar_main, new MapsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onListFragmentInteraction(Trivia trivia) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.app_bar_main, new QuestionFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(TriviaQuestion question) {
        Log.v("questionList", "He pulsado " + question.getStatement());
    }
}
