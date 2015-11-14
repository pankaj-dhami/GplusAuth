package com.example.pankaj.facebookauth;

import java.io.File;
import java.util.List;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {



    // SocialAuth Component
    SocialAuthAdapter adapter;
    boolean status;

    // Android Components
    Button update;
    EditText edit;
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    TextView textview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = (TextView) findViewById(R.id.text);
// Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
        // Welcome Message
        findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // User clicked the sign-in button, so begin the sign-in process and automatically
                // attempt to resolve any errors that occur.
                mShouldResolve = true;
                mGoogleApiClient.connect();

                // Show a message to the user that we are signing in.
                textview.setText("user sign in");
            }
        });


      //  textview.setText("Welcome to SocialAuth Demo. Connect any provider and then press Update button to Share Update.");

     /*   LinearLayout bar = (LinearLayout) findViewById(R.id.linearbar);
        bar.setBackgroundResource(R.drawable.bar_gradient);

        // Add Bar to library
        adapter = new SocialAuthAdapter(new ResponseListener());

        // Add providers
        adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
        adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
        adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
        adapter.addProvider(Provider.GOOGLE,R.drawable.google);

        // For twitter use add callback method. Put your own callback url here.
        adapter.addCallBack(Provider.TWITTER,
                "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

        // Add keys and Secrets
        try {
            adapter.addConfig(Provider.FACEBOOK, "1192316824117519",
                    "d448e58143a6c292cb851c7622f7d97f", "email");
            adapter.addConfig(Provider.TWITTER, "5jwyYJia583EEczmdAmlOA",
                    "j0rQkJjTjwVdv7HFiE4zz2qKJKzqjksR2aviVU8fSc", null);
            adapter.addConfig(Provider.LINKEDIN, "bh82t52rdos6",
                    "zQ1LLrGbhDZ36fH8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.enable(bar);*/
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        // Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
     //   showSignedInUI();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    // Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //  showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //   showSignedOutUI();
        }
    }

    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

      //  showSignedOutUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    private final class ResponseListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {

            // Variable to receive message status
            Log.d("Share-Bar", "Authentication Successful");

            // Get name of provider after authentication
            final String providerName = values
                    .getString(SocialAuthAdapter.PROVIDER);
            Log.d("Share-Bar", "Provider Name = " + providerName);
            Toast.makeText(MainActivity.this, providerName + " connected",
                    Toast.LENGTH_SHORT).show();

            update = (Button) findViewById(R.id.update);
            edit = (EditText) findViewById(R.id.editTxt);

            // Please avoid sending duplicate message. Social Media Providers
            // block duplicate messages.

            update.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    adapter.updateStatus(edit.getText().toString(),
                            new MessageListener(), true);
                }
            });
          //  adapter.getContactListAsync(new ContactDataListener());
            adapter.getUserProfileAsync( new SocialAuthListener<Profile>(){

                @Override
                public void onExecute(String s, Profile o) {
                    Profile profileMap = (Profile)o;
                    profileMap.getEmail();
                    profileMap.getProfileImageURL();
                }

                @Override
                public void onError(SocialAuthError socialAuthError) {

                }
            });

        }


        @Override
        public void onError(SocialAuthError error) {
            error.printStackTrace();
            Log.d("Share-Bar", error.getMessage());
        }

        @Override
        public void onCancel() {
            Log.d("Share-Bar", "Authentication Cancelled");
        }

        @Override
        public void onBack() {
            Log.d("Share-Bar", "Dialog Closed by pressing Back Key");

        }
    }

    // To get status of message after authentication
    private final class MessageListener implements SocialAuthListener<Integer> {
        @Override
        public void onExecute(String provider, Integer t) {
            Integer status = t;
            if (status.intValue() == 200 || status.intValue() == 201
                    || status.intValue() == 204)
                Toast.makeText(MainActivity.this,
                        "Message posted on " + provider, Toast.LENGTH_LONG)
                        .show();
            else
                Toast.makeText(MainActivity.this,
                        "Message not posted on" + provider, Toast.LENGTH_LONG)
                        .show();
        }

        @Override
        public void onError(SocialAuthError e) {

        }
    }


    // To receive the contacts response after authentication
    private final class ContactDataListener implements SocialAuthListener<List<Contact>> {

        @Override
        public void onExecute(String s, List<Contact> t) {

            Log.d("Custom-UI", "Receiving Data");
            List<Contact> contactsList = t;
            if (contactsList != null && contactsList.size() > 0) {
                for (Contact c : contactsList) {
                    Log.d("Custom-UI", "Contact ID = " + c.getId());
                    Log.d("Custom-UI", "Display Name = " + c.getDisplayName());
                    Log.d("Custom-UI", "First Name = " + c.getFirstName());
                    Log.d("Custom-UI", "Last Name = " + c.getLastName());
                    Log.d("Custom-UI", "Email = " + c.getEmail());
                }
            }
        }
        @Override
        public void onError(SocialAuthError socialAuthError) {

        }


    }
}