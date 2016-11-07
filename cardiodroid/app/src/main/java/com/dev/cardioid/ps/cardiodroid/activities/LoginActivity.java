package com.dev.cardioid.ps.cardiodroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.network.dtos.ApiError;
import com.dev.cardioid.ps.cardiodroid.utils.NetworkUtils;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

/**
 * A login screen that offers login via Google sign in mechanism.
 * If credentials are present in cache, the activity goes forward without
 * showing any view.
 */
public class LoginActivity extends AppCompatActivity
    implements GoogleApiClient.OnConnectionFailedListener,
    OnClickListener {

  /**
   * For debug purposes.
   */
  public static final String TAG = Utils.makeLogTag(LoginActivity.class);

  /**
   * Used when asking for a result back from the google sign in API.
   */
  private static final int RC_SIGN_IN = 101;

  /**
   * Key used to save the account info inside the intent used to move
   * to the dashboard activity.
   */
  public static final String GOOGLE_SIGN_IN_ACCOUNT_INFO_KEY = "GoogleSignInAccountInfo";

  /**
   * An instance of Application
   */
  private CardioDroidApplication mApp;

  /**
   * The intent used to move forward to the dashboard
   */
  private Intent mDriveToDashboard;



  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "OnCreate Call");
    setContentView(R.layout.activity_login_layout);

    mDriveToDashboard = new Intent(this, DashboardActivity.class);

    mApp = (CardioDroidApplication) getApplicationContext();
    //setupGoogleApiClient();
    SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
    assert signInButton != null;
    signInButton.setSize(SignInButton.SIZE_STANDARD);
    signInButton.setOnClickListener(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      handleSignInResult(result);
    }else{
      //TODO handle this better
      ToastUtils.showShortMessage("Request Code not sign in", this);
    }

  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "OnStart Call");

    //cached sign-in
    OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mApp.getAuthGoogleApiClient());
    if (opr.isDone()) {
      // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
      // and the GoogleSignInResult will be available instantly.
      Log.d(TAG, "Got cached sign-in");
      GoogleSignInResult result = opr.get();
      handleSignInResult(result);
    }

  }

  /**
   * Handle the response given from the Google Sign-In API request.
   * @param result The response for a given authorization request
   */
  private void handleSignInResult(GoogleSignInResult result) {
    Log.d(TAG, "handleSignInResult: " + result.isSuccess());
    if (result.isSuccess()) {
      // Signed in successfully, show authenticated UI.
      GoogleSignInAccount acct = result.getSignInAccount();

      if (acct != null) {
        Log.d(TAG, "" + acct.getDisplayName() + " " + acct.getEmail());
        //String id = acct.getIdToken();
        //add extra to send to another activity
        mDriveToDashboard.putExtra(GOOGLE_SIGN_IN_ACCOUNT_INFO_KEY, acct);
        //put information into the Application instance
        mApp.setPersonalUserAccountInfo(acct);
      }
      mApp.getCardioApiProvider().createUserAsync(mApp.getPersonalUserAccountInfo(),
              new Completion<Void>() {
                @Override
                public void onResult(CallResult<Void> result) {
                  try{
                    result.getResult();
                    startActivity(mDriveToDashboard);
                    finish();
                  }catch(Exception e){
                    Log.e(TAG, "Could not create user inside API");
                    try {
                      ApiError err = (ApiError) e;

                      if (err.status() == NetworkUtils.HttpCodes.CONFLICT) {
                        //means user already existed
                        startActivity(mDriveToDashboard);
                        finish();
                      }
                    }catch(ClassCastException castEx){
                      Log.e(TAG, "ERROR: " + e.getMessage());
                    }
                  }
                }
              });
    }  else {
      // Signed out, show unauthenticated UI.
      String msg = String.format("%s - %s",
          result.getStatus().toString(),
          result.getStatus().getStatusMessage());
      ToastUtils.showShortMessage(msg, this);
    }
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d(TAG, "OnConnectionFailed Call");

  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.sign_in_button:
        signIn();
        break;
    }
  }

  private void signIn() {
    if(NetworkUtils.isConnected(this)) {
      Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mApp.getAuthGoogleApiClient());
      startActivityForResult(signInIntent, RC_SIGN_IN);
    }else{
      ToastUtils.showMessage(getResources().getString(R.string.network_down_warning), this);
    }
  }

}

