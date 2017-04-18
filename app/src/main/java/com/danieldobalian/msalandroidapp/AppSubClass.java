package com.danieldobalian.msalandroidapp;

import android.app.Application;
import android.content.res.Configuration;

import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.PublicClientApplication;

/**
 * Created by dadaboli on 4/17/17.
 * This class is used to maintain auth state across the app
 * This allows us to access the authentication context and result in any activity we want.
 */

public class AppSubClass extends Application {

    private AuthenticationResult authResult;
    private PublicClientApplication sampleApp;
    private static AppSubClass me;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        me = this;

        authResult = null;
        sampleApp = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static AppSubClass getInstance() {
        return me;
    }

    public AuthenticationResult getAuthResult() {
        return authResult;
    }

    public void setAuthResult (AuthenticationResult authResult) {
        this.authResult = authResult;
    }

    public PublicClientApplication getPublicClient() {
        return sampleApp;
    }

    public void setPublicClient (PublicClientApplication app) {
        this.sampleApp = app;
    }

}
