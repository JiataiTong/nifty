package io.github.cmw025.nifty;
/*
 * CS 193A, Winter 2017, Marty Stepp
 * This activity is a small demonstration of adding Google Sign-in to an Android app.
 * It is tricky to set up, but this gives us the powerful feature of allowing logins
 * and user information in our app.
 *
 * We also demonstrate text-to-speech and speech-to-text here, though it is not really
 * related to signing in a user.
 */

import android.app.Activity;
import android.content.*;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import io.github.cmw025.nifty.R;


public class LoginActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int REQ_CODE_GOOGLE_SIGNIN = 32767 / 2;

    private GoogleApiClient google;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        // updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        SignInButton button = (SignInButton) findViewById(R.id.signin_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClick(v);
            }
        });

        // request the user's ID, email address, and basic profile
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // build API client with access to Sign-In API and options above
        google = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .addConnectionCallbacks(this)
                .build();

    }

    /*
     * This method is called when the Sign in with Google button is clicked.
     * It launches the Google Sign-in activity and waits for a result.
     */
    public void signInClick(View view) {
        // Toast.makeText(this, "Sign in was clicked!", Toast.LENGTH_SHORT).show();

        // connect to Google server to log in
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(google);
        startActivityForResult(intent, REQ_CODE_GOOGLE_SIGNIN);
    }

    /*
     * This method is called when Google Sign-in comes back to my activity.
     * We grab the sign-in results and display the user's name and email address.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQ_CODE_GOOGLE_SIGNIN) {
            // google sign-in has returned
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                // yay; user logged in successfully
                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthWithGoogle(acct);
//                TextView tv = (TextView) findViewById(R.id.results);
//                tv.setText("You signed in as: " + acct.getDisplayName() + " "
//                        + acct.getEmail());
            } else {
                TextView tv = (TextView) findViewById(R.id.results);
                tv.setText("Login fail. :(");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                TextView tv = (TextView) findViewById(R.id.results);
                                tv.setText("You signed in as: " + name + " "
                                        + email);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                String uid = user.getUid();
                                intent.putExtra("uid", uid); //Optional parameters
                                startActivity(intent);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Toast.makeText(LoginActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            TextView tv = (TextView) findViewById(R.id.results);
                            tv.setText("Login fail. :(");
                            // updateUI(null);
                        }
                    }
                });
    }


    // this method is required for the GoogleApiClient.OnConnectionFailedListener above
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
        // log("onConnectionFailed");
    }

    // this method is required for the GoogleApiClient.ConnectionCallbacks above
    public void onConnected(Bundle bundle) {
        // Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        // log("onConnected");
    }

    // this method is required for the GoogleApiClient.ConnectionCallbacks above
    public void onConnectionSuspended(int i) {
        // Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
        // log("onConnectionSuspended");
    }
}