package com.isluji.travial.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.isluji.travial.R;
import com.isluji.travial.data.TriviaViewModel;
import com.isluji.travial.misc.TriviaUtils;
import com.isluji.travial.model.TriviaQuestionWithAnswers;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        TriviaListFragment.OnListFragmentInteractionListener,
        TriviaFragment.OnListFragmentInteractionListener,
        ResultListFragment.OnListFragmentInteractionListener {

    private static final int RC_SIGN_IN = 22;

    private GoogleApiClient mGoogleClient;
    private PeopleService mPeopleClient;
    private GoogleSignInClient mSignInClient;
    private GoogleSignInAccount mGoogleAccount;

    private TriviaViewModel mViewModel;

    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.setTitle(R.string.app_name);

        // Set up Google APIs
        try {
            this.setUpGoogleApis();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize timezone information
        // https://medium.com/androiddevelopers/room-time-2b4cf9672b98
        AndroidThreeTen.init(this);

        // Get a new or existing ViewModel from the ViewModelProvider
        mViewModel = ViewModelProviders.of(this).get(TriviaViewModel.class);

        // Set up the Navigation Drawer layout
        this.setUpNavigationDrawer();


        /* ***** Event listeners ***** */

        // Create variables for the view elements
        Button btnPlay = this.findViewById(R.id.btnPlay);
        Button btnUnlock = this.findViewById(R.id.btnUnlock);
        FloatingActionButton fab = this.findViewById(R.id.fab);
        SignInButton btnSignIn = this.findViewById(R.id.sign_in_button);

        // Google Sign-In button
        btnSignIn.setOnClickListener(v -> signIn());

        // "Play" button
        btnPlay.setOnClickListener(v -> loadTriviaListFragment());

        // "Unlock POI" button
        btnUnlock.setOnClickListener(v -> loadMapsActivity());

        // Floating Action Button
        fab.setOnClickListener(view -> Snackbar
                .make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());


        /* ***** Fragments ***** */

        // Load a fragment if the Intent we come from specifies it
        String fragmentToLoad = this.getIntent().getStringExtra("fragment");

        if (fragmentToLoad != null) {
            switch (fragmentToLoad) {
                case "trivia_list":
                    this.loadTriviaListFragment();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign-In mGoogleAccount.
        // (If the user is already signed in, 'mGoogleAccount' will be non-null.)
        mGoogleAccount = GoogleSignIn.getLastSignedInAccount(this);

        // Update your UI accordingly for your app.
        this.updateUiOnUserChange();
    }

    private void setUpNavigationDrawer() {
        // Initialize Navigation Drawer variables
        NavigationView navView = this.findViewById(R.id.nav_view);
        DrawerLayout drawer = this.findViewById(R.id.drawer_layout);
        AppBarLayout appBar = this.findViewById(R.id.app_bar_layout);
        Toolbar toolbar = this.findViewById(R.id.toolbar);

        // Set the toolbar
        this.setSupportActionBar(toolbar);

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Set the toggle as the drawer listener
        drawer.addDrawerListener(mDrawerToggle);

        // Hide drawer toggle and disable swipe to open
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        // Set the navigation view
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Sync the drawer toggle with the new config
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // Google Sign-In request
            case RC_SIGN_IN:
                // The Task returned from this call is always completed
                // (no need to attach a listener.)
                Task<GoogleSignInAccount> task = GoogleSignIn
                        .getSignedInAccountFromIntent(data);
                this.handleSignInResult(task);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = this.findViewById(R.id.drawer_layout);

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
        // The action bar will automatically handle clicks on the Home/Up button,
        // so long as you specify a parent activity in AndroidManifest.xml.
        boolean action = super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_settings:
                action = true;
                break;
        }

        return action;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // TODO: Handle navigation view item clicks here.
        Fragment fragment = null;
        String tag = null;

        switch (item.getItemId()) {
            case R.id.nav_main:
                this.getSupportFragmentManager().popBackStack();
                break;

            case R.id.nav_tutorial:
                fragment = new TutorialFragment();
                tag = "tutorial_fragment";
                break;

            case R.id.nav_results:
                fragment = new ResultListFragment();
                tag = "result_list_fragment";
                break;

            case R.id.nav_rankings:
                // TODO
                tag = "rankings_fragment";
                break;

            case R.id.nav_premium:
                // TODO
                tag = "premium_fragment";
                break;

            case R.id.nav_share:
                // TODO
                break;

            case R.id.nav_logout:
                this.logOut();
                break;
        }

        if (fragment != null) {
            // Delete previous transaction to avoid the accumulation of fragments.
            this.getSupportFragmentManager().popBackStack();

            // Switch to the selected fragment
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.app_bar_main, fragment, tag)
                    .addToBackStack(null)
                    .commit();
        }

        // Close the drawer when the action is done
        DrawerLayout drawer = this.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /* ***** Event listeners ***** */

    @Override   // Listener method for TriviaListFragment
    public void onListFragmentInteraction(int position) {
        this.loadTriviaFragment(position);
    }

    @Override   // Listener method for TriviaFragment
    public void onListFragmentInteraction(TriviaQuestionWithAnswers qwa) {
        // TODO?
    }

    @Override   // Listener method for ResultListFragment
    public void onListFragmentInteraction(TriviaResult item) {
        // TODO?
    }

    public void onSendButtonClicked(View view) {
        RecyclerView rvQuestions = view.getRootView().findViewById(R.id.rvQuestions);
        TriviaWithQuestions twq = mViewModel.getSelectedTrivia();

        // Set the 'selected' field of all the answers to true or false
        twq.storeChoices(rvQuestions);

        this.loadTriviaResultFragment();
    }

    public void onResetButtonClicked(View view) {
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        ViewGroup rootView = (ViewGroup) Objects.requireNonNull(
                fragments.get(fragments.size() - 1).getView() ).getRootView();

        for (View rg : TriviaUtils.getViewsByTag(rootView, getString(R.string.rg_answers_tag))) {
            ((RadioGroup) rg).clearCheck();
        }
    }


    /* ***** Google APIs setup ***** */

    private GoogleSignInOptions setUpSignInApi() {
        // Configure sign-in to request user's ID, email address, and basic profile.
        // (ID and basic profile are included in DEFAULT_SIGN_IN.)
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by 'gso'
        mSignInClient = GoogleSignIn.getClient(this, gso);

        return gso;
    }

    private void setUpPeopleApi() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        // Google API Console -> App's credentials
        String clientId = getString(R.string.google_client_id);
//        String clientSecret = getString(R.string.google_client_secret);

        // (Or your redirect URL for web based applications)
        String redirectUri = getString(R.string.google_redirect_uri);
        String scope = getString(R.string.google_scope);

        /* ***** STEP 1: Authorize ***** */
        String authorizationUrl =
            new GoogleBrowserClientRequestUrl(clientId, redirectUri, Arrays.asList(scope))
                .build();

        Log.v(getString(R.string.user_auth_log),
                "authorizationUrl: " + authorizationUrl);

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        // Read the authorization code from the standard input stream.
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String code = in.readLine();

        Log.v(getString(R.string.user_auth_log), "code: " + code);

        /* ***** STEP 2: Exchange ***** */
//        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
//            httpTransport, jsonFactory, clientId, clientSecret, code, redirectUri)
//                .execute();

        GoogleCredential credential = new GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
//            .setClientSecrets(clientId, clientSecret)
            .build();
//            .setFromTokenResponse(tokenResponse);

        mPeopleClient = new PeopleService.Builder(
            httpTransport, jsonFactory, credential)
                .build();

        // FINAL STEP: Get the person for the user
//        Person profile = peopleService.people().get("people/me")
//            .setPersonFields("names,emailAddresses")
//            .execute();
    }

    // Collection method to set up all APIs
    private void setUpGoogleApis() throws IOException {
        // Google Sign-In
        GoogleSignInOptions gso = this.setUpSignInApi();
        // People API
//        this.setUpPeopleApi();

        // Initialize the Google Play Services client
        mGoogleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API)
                .build();
    }


    /* ***** Methods for Google Sign-In ***** */

    private void signIn() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        this.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void logOut() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        // Remove the user credentials from Shared Preferences
        sharedPrefs.edit()
                .remove("user_email")
                .remove("user_google_id")
                .remove("user_poi_ids")
                .apply();

        // Reset the Google Account object
        mGoogleAccount = null;

        // Logged out successfully, show initial UI.
        this.updateUiOnUserChange();

        // Return the user to the main screen.
        this.getSupportFragmentManager().popBackStack();

        String goodbye = getString(R.string.goodbye);
        Toast.makeText(this, goodbye, Toast.LENGTH_LONG).show();
    }

    private void updateUiOnUserChange() {
        // User has already signed in to your app
        if (mGoogleAccount != null) {
            // Shows the normal UX for an authenticated user
            this.updateUiOnLogin();

        // User has not yet signed in or has logged out
        } else {
            // Restores the initial UI state
            // (displays the Google Sign-In button, among other changes)
            this.updateUiOnLogout();
        }
    }

    private void updateUiOnLogin() {
        DrawerLayout drawer = this.findViewById(R.id.drawer_layout);

        Button btnPlay = this.findViewById(R.id.btnPlay);
        Button btnUnlock = this.findViewById(R.id.btnUnlock);
        SignInButton btnSignIn = this.findViewById(R.id.sign_in_button);

        TextView userNameText = this.findViewById(R.id.userNameText);
        TextView userEmailText = this.findViewById(R.id.userEmailText);
        ImageView userPhotoImg = this.findViewById(R.id.userPhotoImg);

        // Hide the sign-in button and unveil the UI buttons
        btnSignIn.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
        btnUnlock.setVisibility(View.VISIBLE);

        // Show drawer toggle and enable swipe to open
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        // Update the profile section in the Navigation Drawer
        if (userNameText != null && userEmailText != null && userPhotoImg != null) {
            userNameText.setText(mGoogleAccount.getDisplayName());
            userEmailText.setText(mGoogleAccount.getEmail());
            Picasso.get().load(mGoogleAccount.getPhotoUrl())
                    .placeholder(R.mipmap.baseline_face_black_18dp)
                    .error(R.mipmap.baseline_broken_image_black_18dp)
                    .fit().into(userPhotoImg);
        }
    }

    private void updateUiOnLogout() {
        DrawerLayout drawer = this.findViewById(R.id.drawer_layout);

        Button btnPlay = this.findViewById(R.id.btnPlay);
        Button btnUnlock = this.findViewById(R.id.btnUnlock);
        SignInButton btnSignIn = this.findViewById(R.id.sign_in_button);

        TextView userNameText = this.findViewById(R.id.userNameText);
        TextView userEmailText = this.findViewById(R.id.userEmailText);
        ImageView userPhotoImg = this.findViewById(R.id.userPhotoImg);

        // Hide the UI buttons and show the sign-in button
        btnPlay.setVisibility(View.INVISIBLE);
        btnUnlock.setVisibility(View.INVISIBLE);
        btnSignIn.setVisibility(View.VISIBLE);

        // Hide drawer toggle and disable swipe to open
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        // Update the profile section in the Navigation Drawer
        if (userNameText != null && userEmailText != null && userPhotoImg != null) {
            userNameText.setText(R.string.placeholder_user_name);
            userEmailText.setText(R.string.placeholder_user_email);
            userPhotoImg.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mGoogleAccount = completedTask.getResult(ApiException.class);

            // Signed in successfully -> Show authenticated UI
            this.updateUiOnUserChange();

            String email = mGoogleAccount.getEmail();
            String googleId = mGoogleAccount.getId();

            Log.v(getString(R.string.user_auth_log), "USER: " + email);

            if (email != null && googleId != null) {
                // Show welcome message
                String welcome = getString(R.string.welcome) + mGoogleAccount.getDisplayName();
                Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();

                // If user exists, retrieve its data from the DB.
                // If he wasn't registered, insert a new user in the DB.
                User user = mViewModel.createOrRetrieveUser(email, googleId);

                Set<String> unlockedPoiIds = user.getUnlockedPoiIds();

                if (unlockedPoiIds.isEmpty()) {
                    Log.v(getString(R.string.user_auth_log),
                            "You still don't have any POI unlocked");
                } else {
                    Log.v(getString(R.string.user_auth_log),
                            "You have unlocked these POIs: " + unlockedPoiIds);
                }

                // Store the user ID in the default shared preferences file,
                // so we know who is the logged user in the rest of the app.
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(this.getApplicationContext());

                sharedPrefs.edit()
                        .putString("user_email", email)
                        .putString("user_google_id", googleId)
                        .putStringSet("user_poi_ids", user.getUnlockedPoiIds())
                        .apply();
            }
        } catch (Exception e) {
            if (e instanceof ApiException) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(getString(R.string.user_auth_log), "signInResult:failed code="
                        + ((ApiException) e).getStatusCode());
            } else {
                e.printStackTrace();
            }

            mGoogleAccount = null;
            this.updateUiOnUserChange();
        }
    }


    /* ***** Methods to load the fragments ***** */

    public void loadTriviaListFragment() {
        this.getSupportFragmentManager().popBackStack();
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.app_bar_main, new TriviaListFragment(),
                        "trivia_list_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void loadTriviaFragment(int position) {
        mViewModel.setSelectedTriviaPosition(position);

        this.getSupportFragmentManager().popBackStack();
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.app_bar_main, new TriviaFragment(),
                        "trivia_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void loadTriviaResultFragment() {
        this.getSupportFragmentManager().popBackStack();
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.app_bar_main, new ResultFragment(),
                        "result_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void loadMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        this.startActivity(intent);
    }

    // -------------------------------------------

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
