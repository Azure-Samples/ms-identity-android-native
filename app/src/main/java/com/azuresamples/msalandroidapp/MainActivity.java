package com.azuresamples.msalandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.microsoft.identity.client.*;
import com.microsoft.identity.client.exception.*;

public class MainActivity extends AppCompatActivity {

    /* Azure AD v2 Configs */
    final static String[] SCOPES = {"https://graph.microsoft.com/User.Read"};
    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";

    /* UI & Debugging Variables */
    private static final String TAG = MainActivity.class.getSimpleName();
    Button callGraphButton;
    Button signOutButton;

    /* Azure AD Variables */
    private IPublicClientApplication sampleApp;
    private IAuthenticationResult authResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callGraphButton = findViewById(R.id.callGraph);
        signOutButton = findViewById(R.id.clearCache);

        callGraphButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCallGraphClicked();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSignOutClicked();
            }
        });

        /* Configure your sample app and save state for this activity */
        PublicClientApplication.create(this.getApplicationContext(),
            R.raw.auth_config,
            new PublicClientApplication.ApplicationCreatedListener() {
                @Override
                public void onCreated(IPublicClientApplication application) {
                    sampleApp = application;
                    loadAccount();
                }

                @Override
                public void onError(MsalException exception) {
                    /* Fail to initialize PublicClientApplication. */
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccount();
    }

    /*
     * Attempt to get a user and acquireTokenSilent
     * If this fails we do an interactive request
     */
    private void loadAccount(){
        if (sampleApp == null){
            return;
        }

        if (sampleApp instanceof IMultipleAccountPublicClientApplication) {
            final IMultipleAccountPublicClientApplication app = (IMultipleAccountPublicClientApplication)sampleApp;

            app.getAccounts(new PublicClientApplication.LoadAccountCallback() {
                @Override
                public void onTaskCompleted(List<IAccount> result) {
                    /* This sample doesn't support multi-account scenarios, use the first account */
                    if (!result.isEmpty()) {
                        sampleApp.acquireTokenSilentAsync(SCOPES, result.get(0), getAuthSilentCallback());
                    }
                }

                @Override
                public void onError(Exception exception) {
                    /* No accounts or >1 account */
                }
            });
        }
        else if (sampleApp instanceof ISingleAccountPublicClientApplication) {
            final ISingleAccountPublicClientApplication app = (ISingleAccountPublicClientApplication)sampleApp;

            try {
                app.getCurrentAccount(new ISingleAccountPublicClientApplication.CurrentAccountListener() {
                    @Override
                    public void onAccountLoaded(IAccount activeAccount) {
                        if (activeAccount != null) {
                            sampleApp.acquireTokenSilentAsync(SCOPES, activeAccount, getAuthSilentCallback());
                        }
                    }

                    @Override
                    public void onAccountChanged(IAccount priorAccount, IAccount currentAccount) {
                        // Perform a cleanup task as the signed-in account changed.
                        if (currentAccount == null) {
                            updateSignedOutUI();
                        }
                    }
                });
            }
            catch (MsalClientException e) {
                /*  Unexpected error. */
            }
        }
    }


    //
    // Core Identity methods used by MSAL
    // ==================================
    // onCallGraphClicked() - attempts to get tokens for graph, if it succeeds calls graph & updates UI
    // onSignOutClicked() - Signs account out of the app & updates UI
    // callGraphAPI() - called on successful token acquisition which makes an HTTP request to graph
    //

    /* Use MSAL to acquireToken for the end-user
     * Callback will call Graph api w/ access token & update UI
     */
    private void onCallGraphClicked() {
        sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
    }

    /* Clears an account's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     * User will get interactive SSO if trying to sign back-in.
     */
    private void onSignOutClicked() {
        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        if (sampleApp instanceof IMultipleAccountPublicClientApplication) {
            final IMultipleAccountPublicClientApplication app = (IMultipleAccountPublicClientApplication)sampleApp;
            app.getAccounts(new PublicClientApplication.LoadAccountCallback() {
                @Override
                public void onTaskCompleted(List<IAccount> accounts) {
                    /* This sample doesn't support multi-account scenarios, use the first account */
                    for (final IAccount account : accounts) {
                        app.removeAccount(account, new PublicClientApplication.RemoveAccountCallback() {
                            @Override
                            public void onTaskCompleted(Boolean isSuccess) {
                                if (isSuccess) {
                                    /* successfully removed account */
                                    updateSignedOutUI();
                                } else {
                                    /* failed to remove account */
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                /* Failed to remove account due to an exception. */
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception exception) {
                    /* No accounts or >1 account */
                }
            });
        }
        else if (sampleApp instanceof ISingleAccountPublicClientApplication) {
            final ISingleAccountPublicClientApplication app = (ISingleAccountPublicClientApplication)sampleApp;
            try {
                app.removeCurrentAccount(new PublicClientApplication.RemoveAccountCallback() {
                    @Override
                    public void onTaskCompleted(Boolean result) {
                        if (result) {
                            /* successfully removed account */
                            updateSignedOutUI();
                        } else {
                            /* failed to remove account */
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        /* failed to remove account with an exception */
                    }
                });
            }
            catch (MsalClientException e) {
                /*  Unexpected error. */
            }
        }
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MSGRAPH_URL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());

                updateGraphUI(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    //
    // Helper methods manage UI updates
    // ================================
    // updateGraphUI() - Sets graph response in UI
    // updateSuccessUI() - Updates UI when token acquisition succeeds
    // updateSignedOutUI() - Updates UI when app sign out succeeds
    //

    /* Sets the graph response */
    private void updateGraphUI(JSONObject graphResponse) {
        findViewById(R.id.graphData).setVisibility(View.VISIBLE);
        TextView graphText = findViewById(R.id.graphData);
        graphText.setText(graphResponse.toString());
    }

    /* Set the UI for successful token acquisition data */
    private void updateSuccessUI() {
        callGraphButton.setVisibility(View.INVISIBLE);
        signOutButton.setVisibility(View.VISIBLE);
        findViewById(R.id.welcome).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.welcome)).setText("Welcome, " +
                authResult.getAccount().getUsername());
    }

    /* Set the UI for signed out account */
    private void updateSignedOutUI() {
        callGraphButton.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.INVISIBLE);
        findViewById(R.id.welcome).setVisibility(View.INVISIBLE);
        findViewById(R.id.graphData).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.graphData)).setText("No Data");

        Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
                .show();
    }

    //
    // App callbacks for MSAL
    // ======================
    // getActivity() - returns activity so we can acquireToken within a callback
    // getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
    // getAuthInteractiveCallback() - callback defined to handle acquireToken() case
    //

    public Activity getActivity() {
        return this;
    }

    /* Callback used in for silent acquireToken calls.
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                authResult = authenticationResult;

                /* update the UI to post call graph state */
                updateSuccessUI();

                /* call graph */
                callGraphAPI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                /* Store the auth result */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
                updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
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
