package com.isluji.travial.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.isluji.travial.R;
import com.isluji.travial.data.AppDatabase;
import com.isluji.travial.fragments.MapsFragment;
import com.isluji.travial.fragments.QuestionListFragment;
import com.isluji.travial.fragments.TriviaListFragment;
import com.isluji.travial.fragments.TriviaResultFragment;
import com.isluji.travial.model.TriviaQuestionWithAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import static android.view.View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        TriviaListFragment.OnListFragmentInteractionListener,
        QuestionListFragment.OnListFragmentInteractionListener,
        TriviaResultFragment.OnFragmentInteractionListener {

    final int PERMISSION_LOCATION = 111;

    private GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient mLocationClient;
//    private AppViewModel mAppViewModel;

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
//        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);


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
        this.setSupportActionBar(toolbar);

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {/*TODO?*/}

    @Override
    public void onConnected(@Nullable Bundle bundle) {/*TODO?*/}

    @Override
    public void onConnectionSuspended(int i) {/*TODO?*/}


    /* ***** Methods to load the fragments ***** */

    public void loadPlayFragment() {
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, new TriviaListFragment())
            .addToBackStack(null)
            .commit();
    }

    public void loadMapsFragment() {
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, new MapsFragment())
            .addToBackStack(null)
            .commit();
    }


    /* ***** Click listeners ***** */

    @Override   // Listener method for TriviaListFragment
    public void onListFragmentInteraction(int position) {
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, QuestionListFragment.newInstance(position))
            .addToBackStack(null)
            .commit();
    }

    @Override   // Listener method for QuestionListFragment
    public void onListFragmentInteraction(TriviaQuestionWithAnswers qwa) {
        // TODO?
    }

    @Override   // Listener method for TriviaResultFragment
    public void onFragmentInteraction(Uri uri) {
        // TODO?
    }

    public void onSendButtonClicked(View view) {
        // TODO: Show a different screen if the user pass or fail the test
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, new TriviaResultFragment())
            .addToBackStack(null)
            .commit();
    }

    public void onResetButtonClicked(View view) {
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        ViewGroup rootView = (ViewGroup) Objects.requireNonNull(
                fragments.get(fragments.size() - 1).getView() ).getRootView();

        for (View rg : getViewsByTag(rootView, getString(R.string.rg_answers_tag))) {
            ((RadioGroup) rg).clearCheck();
        }
    }

    // -----------------------------------------------

    /**
     * @see <a href="https://stackoverflow.com/questions/5062264/find-all-views-with-tag">
     *     Copyright: Shlomi Schwartz</a>
     */
    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);

            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();

            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }

        return views;
    }
}
