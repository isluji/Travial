package com.isluji.travial.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.google.gson.Gson;
import com.isluji.travial.R;
import com.isluji.travial.data.AppDatabase;
import com.isluji.travial.data.AppViewModel;
import com.isluji.travial.misc.TriviaUtils;
import com.isluji.travial.model.TriviaQuestionWithAnswers;
import com.isluji.travial.model.TriviaWithQuestions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        TriviaListFragment.OnListFragmentInteractionListener,
        TriviaFragment.OnListFragmentInteractionListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private ActionBarDrawerToggle mDrawerToggle;
    private PeopleService mPeopleService;

    private AppViewModel mAppViewModel;

    private static final String SIGN_IN_TAG = "google-sign-in";
    private static final int PERMISSION_LOCATION = 11;
    private static final int RC_SIGN_IN = 22;

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


        /* ***** Set up Room DB ***** */

        // Get an instance of the created database
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        Log.v("app-db", "Se ha llamado a AppDB.getDatabase()");

        // Get a new or existing ViewModel from the ViewModelProvider
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);


        /* ***** Set up Navigation Drawer ***** */

        // Create variables for the view elements
        Button btnPlay = this.findViewById(R.id.btnPlay);
        Button btnLocation = this.findViewById(R.id.btnLocation);
        SignInButton btnSignIn = findViewById(R.id.sign_in_button);

        DrawerLayout drawer = this.findViewById(R.id.drawer_layout);
        NavigationView navView = this.findViewById(R.id.nav_view);
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        FloatingActionButton fab = this.findViewById(R.id.fab);

        // Set the toolbar
        this.setSupportActionBar(toolbar);

        // Set the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.syncState();

        // Set the navigation view
        navView.setNavigationItemSelectedListener(this);


        /* ***** Event listeners ***** */

        // Google Sign-In button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // "Play" button
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlayFragment();
            }
        });

        // "Register location" button
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMapsFragment();
            }
        });

        // Floating Action Button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign-In account.
        // (If the user is already signed in, 'account' will be non-null.)
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed
            // (no need to attach a listener.)
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
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
        DrawerLayout drawer = this.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {/*TODO?*/}

    @Override
    public void onConnected(@Nullable Bundle bundle) {/*TODO?*/}

    @Override
    public void onConnectionSuspended(int i) {/*TODO?*/}


    /* ***** Event listeners ***** */

    @Override   // Listener method for TriviaListFragment
    public void onListFragmentInteraction(int position) {
        this.loadTriviaFragment(position);
    }

    @Override   // Listener method for TriviaFragment
    public void onListFragmentInteraction(TriviaQuestionWithAnswers qwa) {
        // TODO?
    }

    public void onSendButtonClicked(View view) {
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        TriviaFragment triviaFragment = (TriviaFragment) fragments.get(fragments.size() - 1);

        RecyclerView rvQuestions = view.getRootView().findViewById(R.id.rvQuestions);
        TriviaWithQuestions twq = mAppViewModel.getTrivia(triviaFragment.getSelectedTriviaPosition());

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

    public GoogleSignInOptions setUpSignInApi() {
        // Configure sign-in to request user's ID, email address, and basic profile.
        // (ID and basic profile are included in DEFAULT_SIGN_IN.)
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by 'gso'
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        return gso;
    }

    public void setUpPeopleApi() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        // TODO? Hardcoding credentials seems a bad practice
        // Google API Console -> App's credentials
        String clientId = getString(R.string.google_client_id);
        String clientSecret = getString(R.string.google_client_secret);

        // (Or your redirect URL for web based applications)
        String redirectUrl = getString(R.string.google_redirect_url);
        String scope = getString(R.string.google_scope);

        /* ***** STEP 1: Authorize ***** */
        String authorizationUrl =
            new GoogleBrowserClientRequestUrl(clientId, redirectUrl, Arrays.asList(scope))
                .build();

        Log.v(SIGN_IN_TAG, "authorizationUrl: " + authorizationUrl);

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        // Read the authorization code from the standard input stream.
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String code = in.readLine();

        Log.v(SIGN_IN_TAG, "httpTransport: " + clientId);
        Log.v(SIGN_IN_TAG, "clientSecret: " + clientSecret);
        Log.v(SIGN_IN_TAG, "code: " + code);
        Log.v(SIGN_IN_TAG, "redirectUrl: " + redirectUrl);

        /* ***** STEP 2: Exchange ***** */
//        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
//            httpTransport, jsonFactory, clientId, clientSecret, code, redirectUrl)
//                .execute();

        GoogleCredential credential = new GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
            .setClientSecrets(clientId, clientSecret)
            .build();
//            .setFromTokenResponse(tokenResponse);

        mPeopleService = new PeopleService.Builder(
            httpTransport, jsonFactory, credential)
                .build();

        // FINAL STEP: Get the person for the user
//        Person profile = peopleService.people().get("people/me")
//            .setPersonFields("names,emailAddresses")
//            .execute();
    }

    public void setUpLocationApi() {
        // Initialize the Google Maps location client
        mFusedLocationClient = new FusedLocationProviderClient(this);
    }

    public void setUpGoogleApis() throws IOException {
        // Google Sign-In
        GoogleSignInOptions gso = this.setUpSignInApi();
        // People API
        this.setUpPeopleApi();
        // Google Maps
        this.setUpLocationApi();

        // Initialize the Google Play Services client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API)
                .build();
    }


    /* ***** Methods for Google Sign-In ***** */

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        this.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /** Initiate successful logged in experience */
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            SignInButton signInButton = this.findViewById(R.id.sign_in_button);
            Button btnPlay = this.findViewById(R.id.btnPlay);
            Button btnLocation = this.findViewById(R.id.btnLocation);

            ImageView userPhotoImg = this.findViewById(R.id.userPhotoImg);
            TextView userNameText = this.findViewById(R.id.userNameText);
            TextView userEmailText = this.findViewById(R.id.userEmailText);

            // Hide the sign-in button and unveil the UI buttons
            signInButton.setVisibility(View.INVISIBLE);
            btnPlay.setVisibility(View.VISIBLE);
            btnLocation.setVisibility(View.VISIBLE);

            // Reveal drawer toggle
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();

            // Update the profile section in the Navigation Drawer
            userNameText.setText(account.getDisplayName());
            userEmailText.setText(account.getEmail());
            userPhotoImg.setImageURI(account.getPhotoUrl());

            String welcome = getString(R.string.welcome) + account.getDisplayName();
            Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        } else {
            // TODO: 'account' is null when no user has signed in before
            Log.v(SIGN_IN_TAG, "No user has signed in before");
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(SIGN_IN_TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


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

    public void loadTriviaFragment(int position) {
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.app_bar_main, TriviaFragment.newInstance(position))
                .addToBackStack(null)
                .commit();
    }

    public void loadTriviaResultFragment() {
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.app_bar_main, TriviaResultFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }
}
