package com.isluji.travial.ui;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.gson.Gson;
import com.isluji.travial.R;
import com.isluji.travial.data.AppDatabase;
import com.isluji.travial.data.TriviaViewModel;
import com.isluji.travial.misc.TriviaUtils;
import com.isluji.travial.model.TriviaQuestionWithAnswers;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;
import com.jakewharton.threetenabp.AndroidThreeTen;

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
        TriviaFragment.OnListFragmentInteractionListener,
        ResultListFragment.OnListFragmentInteractionListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private ActionBarDrawerToggle mDrawerToggle;
    private PeopleService mPeopleService;

    private TriviaViewModel mViewModel;
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

        // Initialize the timezone information
        AndroidThreeTen.init(this);


        /* ***** Set up Room DB ***** */

        // Get an instance of the created database
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        Log.v(getString(R.string.room_db_name), "Se ha llamado a AppDB.getDatabase()");

        // Get a new or existing ViewModel from the ViewModelProvider
        mViewModel = ViewModelProviders.of(this).get(TriviaViewModel.class);


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
                loadTriviaListFragment();
            }
        });

        // "Register location" button
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMapsActivity();
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
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // account != null
        // -> The user has already signed in to your app with Google.
        // -> Update your UI accordingly for your app.
//        if (account != null) {
//            updateUiOnLogin(account);
//        }

        // account == null
        // -> The user has not yet signed in to your app with Google.
        // -> Display the Google Sign-in button.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed
            // (no need to attach a listener.)
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            this.handleSignInResult(task);
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

        if (id == R.id.nav_tutorial) {
            this.loadTutorialFragment();
        } else if (id == R.id.nav_results) {
            this.loadResultListFragment();
        } else if (id == R.id.nav_rankings) {

        } else if (id == R.id.nav_premium) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            this.logOut();
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

    @Override   // Listener method for ResultListFragment
    public void onListFragmentInteraction(TriviaResult item) {
        // TODO?
    }

    public void onSendButtonClicked(View view) {
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        TriviaFragment triviaFragment = (TriviaFragment) fragments.get(fragments.size() - 1);

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
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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

        Log.v(getString(R.string.google_sign_in_tag),
                "authorizationUrl: " + authorizationUrl);

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        // Read the authorization code from the standard input stream.
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String code = in.readLine();

        Log.v(getString(R.string.google_sign_in_tag), "code: " + code);

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

        mPeopleService = new PeopleService.Builder(
            httpTransport, jsonFactory, credential)
                .build();

        // FINAL STEP: Get the person for the user
//        Person profile = peopleService.people().get("people/me")
//            .setPersonFields("names,emailAddresses")
//            .execute();
    }

    private void setUpGoogleApis() throws IOException {
        // Google Sign-In
        GoogleSignInOptions gso = this.setUpSignInApi();
        // People API
        this.setUpPeopleApi();

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

    private void logOut() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        // Remove the user credentials from Shared Preferences
        editor.remove("user_email");
        editor.remove("user_google_id");
        editor.apply();

        // Logged out successfully, show initial UI.
        this.updateUiOnLogout();
    }

    /** Initiate successful logged in experience */
    private void updateUiOnLogin(GoogleSignInAccount account) {
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
            Log.v(getString(R.string.google_sign_in_tag),
                    "No user has signed in before");
        }
    }

    private void updateUiOnLogout() {
        SignInButton signInButton = this.findViewById(R.id.sign_in_button);
        Button btnPlay = this.findViewById(R.id.btnPlay);
        Button btnLocation = this.findViewById(R.id.btnLocation);

        ImageView userPhotoImg = this.findViewById(R.id.userPhotoImg);
        TextView userNameText = this.findViewById(R.id.userNameText);
        TextView userEmailText = this.findViewById(R.id.userEmailText);

        // Hide the UI buttons and show the sign-in button
        btnPlay.setVisibility(View.INVISIBLE);
        btnLocation.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.VISIBLE);

        // Hide drawer toggle
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.syncState();

        // Update the profile section in the Navigation Drawer
        userNameText.setText(R.string.placeholder_user_name);
        userEmailText.setText(R.string.placeholder_user_email);
        userPhotoImg.setImageResource(R.mipmap.ic_launcher_round);

        String goodbye = getString(R.string.goodbye);
        Toast.makeText(getApplicationContext(), goodbye, Toast.LENGTH_LONG).show();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            this.updateUiOnLogin(account);

            Log.v(getString(R.string.google_sign_in_tag),
                    "ACCOUNT -> " + new Gson().toJson(account));

            // TODO: assert? if/else? requireNonNull?
            assert account != null;
            String email = account.getEmail();
            String googleId = account.getId();

            // Store the User in the Room DB
            assert email != null;
            assert googleId != null;
            mViewModel.insertUser(new User(email, googleId));

            // Store the user ID in the default shared preferences file,
            // so we know who is the logged user in the rest of the app.
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("user_email", email);
            editor.putString("user_google_id", googleId);
            editor.apply();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(getString(R.string.google_sign_in_tag),
                    "signInResult:failed code=" + e.getStatusCode());

            updateUiOnLogin(null);
        }
    }


    /* ***** Methods to load the fragments ***** */

    public void loadTriviaListFragment() {
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, new TriviaListFragment())
            .addToBackStack(null)
            .commit();
    }

    public void loadTriviaFragment(int position) {
        mViewModel.setSelectedTriviaPosition(position);

        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, new TriviaFragment())
            .addToBackStack(null)
            .commit();
    }

    public void loadTriviaResultFragment() {
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, new ResultFragment())
            .addToBackStack(null)
            .commit();
    }

    private void loadResultListFragment() {
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, new ResultListFragment())
            .addToBackStack(null)
            .commit();
    }

    private void loadTutorialFragment() {
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.app_bar_main, new TutorialFragment())
            .addToBackStack(null)
            .commit();
    }

    public void loadMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        this.startActivity(intent);
    }
}
