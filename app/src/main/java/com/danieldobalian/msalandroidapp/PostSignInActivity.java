package com.danieldobalian.msalandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.MsalServiceException;
import com.microsoft.identity.client.MsalUiRequiredException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.lang.String;

public class PostSignInActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    AppSubClass state;

    private String graphURL;

    private AuthenticationResult authResult;
    private PublicClientApplication sampleApp;
    String[] scopes;

    Button refreshButton;
    Button clearCacheButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_sign_in);

        graphURL = getString(R.string.microsoftGraph);
        scopes = getString(R.string.scopes).split("\\s+");

        state = AppSubClass.getInstance();
        sampleApp = state.getPublicClient();
        authResult = state.getAuthResult();

        refreshButton = (Button) findViewById(R.id.refresh);
        clearCacheButton = (Button) findViewById(R.id.clearCache);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hasRefreshToken();
            }
        });

        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearCache();
                finish();
            }
        });

        /* Set Welcome Text */
        Log.d(TAG, "Signed in: " + state.getAuthResult().getUser().getName());
        ((TextView) findViewById(R.id.welcome)).setText("Welcome, "
                + state.getAuthResult().getUser().getName());

        /* Write the token status (whether or not we received each token) */
        this.updateTokenUI();

        /* Calls Graph, dump out response from UserInfo endpoint into UI */
        this.callGraphAPI();

    }

    /* Use volley to request the /me endpoint from MS Graph
    *  Sets the UI to what we get back
    */
    private void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, graphURL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to signedIn activity */
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP GET to Queue");
        Log.d(TAG, "Request URL: " + graphURL);
        Log.d(TAG, "Access Token: " + authResult.getAccessToken());
        Log.d(TAG, "Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    /* Write the token status (whether or not we received each token) */
    private void updateTokenUI() {
        if (authResult != null) {
            TextView it = (TextView) findViewById(R.id.itStatus);
            TextView at = (TextView) findViewById(R.id.atStatus);

            if(authResult.getIdToken() != null) {
                it.setText(it.getText() + " " + getString(R.string.tokenPresent));
            } else {
                it.setText(it.getText() + " " + getString(R.string.noToken));
            }

            if (authResult.getAccessToken() != null) {
                at.setText(at.getText() + " " + getString(R.string.tokenPresent));
            } else {
                at.setText(at.getText() + " " + getString(R.string.noToken));
            }

            hasRefreshToken();

        } else {
            Log.d(TAG, "No authResult, something went wrong.");
        }
    }

    /* Write the token status (whether or not we received each token) */
    private void updateRefreshTokenUI(boolean status) {
            Toast.makeText(getBaseContext(), getString(R.string.refreshing), Toast.LENGTH_SHORT)
                    .show();

            TextView rt = (TextView) findViewById(R.id.rtStatus);

            if (rt.getText().toString().contains(getString(R.string.noToken))
                    || rt.getText().toString().contains(getString(R.string.tokenPresent))) {
                rt.setText(R.string.RT);
            }
            if (status) {
                rt.setText(rt.getText() + " " + getString(R.string.tokenPresent));
                Toast.makeText(getBaseContext(), getString(R.string.refreshed), Toast.LENGTH_SHORT)
                        .show();
            } else {
                rt.setText(rt.getText() + " " + getString(R.string.noToken));
                Toast.makeText(getBaseContext(), getString(R.string.failedRefresh), Toast.LENGTH_SHORT)
                        .show();
            }
    }

    /* Clears a user's tokens from the cache.
     * Logically similar to "signOut" but only signs out of this app.
     */
    private void clearCache() {
        List<User> users = null;
        try {
            Log.d(TAG, "Clearing app cache");
            users = sampleApp.getUsers();

            if (users == null) {
                /* We have no users */

                Log.d(TAG, "Faield to Sign out/clear cache, no user");
            } else if (users.size() == 1) {
                /* We have 1 user */

                /* Remove from token cache */
                sampleApp.remove(users.get(0));

                Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
                        .show();
                Log.d(TAG, "Signed out/cleared cache");

            }
            else {
                /* We have multiple users */

                for (int i = 0; i < users.size(); i++) {
                    sampleApp.remove(users.get(i));
                }

                Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
                        .show();
                Log.d(TAG, "Signed out/cleared cache for multiple users");
            }

        } catch (MsalClientException e) {
            /* No token in cache, proceed with normal unauthenticated app experience */
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    /* Checks if there's a refresh token in the cache.
     * Only way to check is to refresh the tokens and catch Exception.
     * Also is used to refresh the token.
     */
    private void hasRefreshToken() {

        /* Attempt to get a user and acquireTokenSilently
         * If this fails we will do an interactive request
         */
        List<User> users = null;
        try {
            users = sampleApp.getUsers();

            if (users != null && users.size() == 1) {
            /* We have 1 user */
                boolean forceRefresh = true;
                sampleApp.acquireTokenSilentAsync(scopes, users.get(0), null, forceRefresh,
                        getAuthSilentCallback());
            } else {
                /* We have multiple users or none*/

                /* This app does not support multiple users.
                 * Typically, multiple user scenarios depend on app logic to have some
                 * kind of heuristic to determine user to use (or some ui)
                 */
                updateRefreshTokenUI(false);
            }
        } catch (MsalClientException e) {
            /* No token in cache, proceed with normal unauthenticated app experience */
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    /* Calls the Microsoft Graph and dumps response into UI */
    private void updateGraphUI(JSONObject response) {
        TextView graphText = (TextView) findViewById(R.id.graphData);
        graphText.setText(response.toString());
    }

    /* Callback used in for silent acquireToken calls.
     * else errors
     * Sets refresh token ui depending on result
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token */
                updateRefreshTokenUI(true);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
                updateRefreshTokenUI(true);
                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                    assert true;

                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                    assert true;

                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                    assert true;
                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
                updateRefreshTokenUI(true);
            }
        };
    }
}
