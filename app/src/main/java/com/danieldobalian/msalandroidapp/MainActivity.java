package com.danieldobalian.msalandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.microsoft.identity.client.*;

import org.json.JSONObject;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    Button signInButton;
    Button learnMoreButton;

    /* Azure AD variables */
    private PublicClientApplication sampleApp;

    private User currentUser;
    private AuthenticationResult authResult;

    String[] scopes;

    /* Get the global state */
    AppSubClass state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = (AppSubClass) getApplicationContext();

        signInButton = (Button) findViewById(R.id.signIn);
        learnMoreButton = (Button) findViewById(R.id.learnMore);

        scopes = Constants.SCOPES.split("\\s+");

        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn(scopes);
            }
        });

        learnMoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                learnMore();
            }
        });

        sampleApp = state.getPublicClient();

        /* Initialize the MSAL App context */
        if (sampleApp == null) {
            sampleApp = new PublicClientApplication(
                    this.getApplicationContext(),
                    Constants.CLIENT_ID);
            state.setPublicClient(sampleApp);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }

    private void learnMore() {
        startActivity(new Intent(this, LearnMoreActivity.class));
    }

    /* Use MSAL to acquireToken for the end-user
    *  Call Graph API
    *  Pass UserInfo response data to PostSignInActivity
    */
    private void signIn(String[] scopes) {

        /* Attempt to get a user and acquireTokenSilently
         * If this fails we will do an interactive request
         */
        Log.d(TAG, "Sign In Clicked");

        List<User> users = null;
        try {
            users = sampleApp.getUsers();

            if (users != null && users.size() == 1) {
                /* We have 1 user */
                currentUser = users.get(0);
                sampleApp.acquireTokenSilentAsync(scopes, currentUser, getAuthSilentCallback());
            } else {
                /* We have multiple users or none */

                /* This app does not support multiple users.
                 * Typically, multiple user scenarios depend on app logic to have some
                 * kind of heuristic to determine user to use (or some ui to pick)
                 */
                interactiveAcquireToken();
            }
        } catch (MsalClientException e) {
            /* No token in cache, proceed with normal unauthenticated app experience */
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    private void interactiveAcquireToken() {
        sampleApp.acquireToken(this, scopes, getAuthInteractiveCallback());
    }

    /* Starts post sign in intent */
    private void startPostSignIn() {startActivity(new Intent(this, PostSignInActivity.class));}

    /* Callback used in for silent acquireToken calls.
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                authResult = authenticationResult;
                state.setAuthResult(authResult);

                /* Start post sign in activity */
                startPostSignIn();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                    assert true;

                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                    assert true;

                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                    interactiveAcquireToken();
                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Callback used for interactive request.  If suceeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());
                authResult = authenticationResult;
                state.setAuthResult(authResult);

                /* Start post sign in activity */
                startPostSignIn();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                    assert true;
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                    assert true;

                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }
}
