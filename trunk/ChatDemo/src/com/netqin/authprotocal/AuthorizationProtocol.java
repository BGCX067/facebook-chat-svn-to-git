package com.netqin.authprotocal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public interface AuthorizationProtocol {
    public static final String REDIRECT_URI = "fbconnect://success";
    public static final String CANCEL_URI = "fbconnect://cancel";
    // Strings used in the authorization flow
    public static final String TOKEN = "access_token";
    public static final String EXPIRES = "expires_in";
    public static final String SESSION ="generate_session_secret";
    public static final String SINGLE_SIGN_ON_DISABLED = "service_disabled";

    public static final int FORCE_DIALOG_AUTH = -1;

    /**
     * Full authorize method.
     * 
     * Starts either an Activity or a dialog which prompts the user to log in to Facebook and grant
     * the requested permissions to the given application.
     * 
     * This method will, when possible, use Facebook's single sign-on for Android to obtain an
     * access token. This involves proxying a call through the Facebook for Android stand-alone
     * application, which will handle the authentication flow, and return an OAuth access token for
     * making API calls.
     * 
     * Because this process will not be available for all users, if single sign-on is not possible,
     * this method will automatically fall back to the OAuth 2.0 User-Agent flow. In this flow, the
     * user credentials are handled by Facebook in an embedded WebView, not by the client
     * application. As such, the dialog makes a network request and renders HTML content rather than
     * a native UI. The access token is retrieved from a redirect to a special URL that the WebView
     * handles.
     * 
     * Note that User credentials could be handled natively using the OAuth 2.0 Username and
     * Password Flow, but this is not supported by this SDK.
     * 
     * See http://developers.facebook.com/docs/authentication/ and http://wiki.oauth.net/OAuth-2 for
     * more details.
     * 
     * Note that this method is asynchronous and the callback will be invoked in the original
     * calling thread (not in a background thread).
     * 
     * Also note that requests may be made to the API without calling authorize first, in which case
     * only public information is returned.
     * 
     * IMPORTANT: Note that single sign-on authentication will not function correctly if you do not
     * include a call to the authorizeCallback() method in your onActivityResult() function! Please
     * see below for more information. single sign-on may be disabled by passing FORCE_DIALOG_AUTH
     * as the activityCode parameter in your call to authorize().
     * 
     * @param activity
     *            The Android activity in which we want to display the authorization dialog.
     * @param applicationId
     *            The Facebook application identifier e.g. "350685531728"
     * @param permissions
     *            A list of permissions required for this application: e.g. "read_stream",
     *            "publish_stream", "offline_access", etc. see
     *            http://developers.facebook.com/docs/authentication/permissions This parameter
     *            should not be null -- if you do not require any permissions, then pass in an empty
     *            String array.
     * @param activityCode
     *            Single sign-on requires an activity result to be called back to the client
     *            application -- if you are waiting on other activities to return data, pass a
     *            custom activity code here to avoid collisions. If you would like to force the use
     *            of legacy dialog-based authorization, pass FORCE_DIALOG_AUTH for this parameter.
     *            Otherwise just omit this parameter and Facebook will use a suitable default. See
     *            http://developer.android.com/reference/android/ app/Activity.html for more
     *            information.
     * @param listener
     *            Callback interface for notifying the calling application when the authentication
     *            dialog has completed, failed, or been canceled.
     */
    public void authorize(
            Activity activity, boolean facebookNeedReAuth, int dialogId, String[] permissions, int activityCode,
            final LoginListener listener);

    /**
     * IMPORTANT: This method must be invoked at the top of the calling activity's
     * onActivityResult() function or Facebook authentication will not function properly!
     * 
     * If your calling activity does not currently implement onActivityResult(), you must implement
     * it and include a call to this method if you intend to use the authorize() method in this SDK.
     * 
     * For more information, see http://developer.android.com/reference/android/app/
     * Activity.html#onActivityResult(int, int, android.content.Intent) public void
     * authorizeCallback(int requestCode, int resultCode, Intent data); /** Refresh OAuth access
     * token method. Binds to Facebook for Android stand-alone application application to refresh
     * the access token. This method tries to connect to the Facebook App which will handle the
     * authentication flow, and return a new OAuth access token. This method will automatically
     * replace the old token with a new one. Note that this method is asynchronous and the callback
     * will be invoked in the original calling thread (not in a background thread).
     * 
     * @param context
     *            The Android Context that will be used to bind to the Facebook RefreshToken Service
     * @param serviceListener
     *            Callback interface for notifying the calling application when the refresh request
     *            has completed or failed (can be null). In case of a success a new token can be
     *            found inside the result Bundle under Facebook.ACCESS_TOKEN key.
     * @return true if the binding to the RefreshToken Service was created
     */
    public void authorizeCallback(int requestCode, int resultCode, Intent data);

    /**
     * Check if the access token requires refreshing.
     * 
     * @return true if the last time a new token was obtained was over 24 hours ago.
     */
    public boolean shouldExtendAccessToken();

    /**
     * Invalidate the current user session by removing the access token in memory, clearing the
     * browser cookie, and calling auth.expireSession through the API.
     * 
     * Note that this method blocks waiting for a network response, so do not call it in a UI
     * thread.
     * 
     * @param context
     *            The Android context in which the logout should be called: it should be the same
     *            context in which the login occurred in order to clear any stored cookies
     * @throws IOException
     * @throws MalformedURLException
     * @return JSON string representation of the auth.expireSession response ("true" if successful)
     */
    public String logout(Context context) throws MalformedURLException, IOException;

    /**
     * Synchronously make a request to the Facebook Graph API with the given HTTP method and string
     * parameters. Note that binary data parameters (e.g. pictures) are not yet supported by this
     * helper function.
     * 
     * See http://developers.facebook.com/docs/api
     * 
     * Note that this method blocks waiting for a network response, so do not call it in a UI
     * thread.
     * 
     * @param graphPath
     *            Path to resource in the Facebook graph, e.g., to fetch data about the currently
     *            logged authenticated user, provide "me", which will fetch
     *            http://graph.facebook.com/me
     * @param params
     *            Key-value string parameters, e.g. the path "search" with parameters {"q" :
     *            "facebook"} would produce a query for the following graph resource:
     *            https://graph.facebook.com/search?q=facebook
     * @param httpMethod
     *            http verb, e.g. "GET", "POST", "DELETE"
     * @throws IOException
     * @throws MalformedURLException
     * @return JSON string representation of the response
     */
    public String request(String graphPath, Bundle params, String httpMethod)
            throws FileNotFoundException, MalformedURLException, IOException;

    /**
     * Generate a UI dialog for the request action in the given Android context with the provided
     * parameters.
     * 
     * Note that this method is asynchronous and the callback will be invoked in the original
     * calling thread (not in a background thread).
     * 
     * @param context
     *            The Android context in which we will generate this dialog.
     * @param action
     *            String representation of the desired method: e.g. "feed" ...
     * @param parameters
     *            String key-value pairs to be passed as URL parameters.
     * @param listener
     *            Callback interface to notify the application when the dialog has completed.
     */
    public void dialog(Activity context, String action, Bundle parameters, final LoginListener listener);

    /**
     * @return boolean - whether this object has an non-expired session token
     */
    public boolean isSessionValid();

    /**
     * 设置用户头像url
     * 
     * @param url
     */
    public void setPicUrl(String url);

    /**
     * 获取用户头像
     * 
     * @return
     */
    public String getPicUrl();

    /**
     * 设置用户昵称
     * 
     * @param nickName
     */
    public void setNickName(String nickName);

    /**
     * 获取用户昵称
     */
    public String getNickName();

    /**
     * 设置用户uid
     * 
     * @param userUID
     */
    public void setUserUID(String userUID);

    /**
     * 获取用户uid
     */
    public String getUserUID();

    /**
     * Retrieve the OAuth 2.0 access token for API access: treat with care. Returns null if no
     * session exists.
     * 
     * @return String - access token
     */
    public String getAccessToken();

    /**
     * Retrieve the current session's expiration time (in milliseconds since Unix epoch), or 0 if
     * the session doesn't expire or doesn't exist.
     * 
     * @return long - session expiration time
     */
    public long getAccessExpires();

    /**
     * Set the OAuth 2.0 access token for API access.
     * 
     * @param token
     *            - access token
     */
    public void setAccessToken(String token);

    /**
     * Set the current session's expiration time (in milliseconds since Unix epoch), or 0 if the
     * session doesn't expire.
     * 
     * @param time
     *            - timestamp in milliseconds
     */
    public void setAccessExpires(long time);

    /**
     * Set the current session's duration (in seconds since Unix epoch), or "0" if session doesn't
     * expire.
     * 
     * @param expiresIn
     *            - duration in seconds (or 0 if the session doesn't expire)
     */
    public void setAccessExpiresIn(String expiresIn);

    public String getAppId();

    public void setAppId(String appId);

    /**
     * Callback interface for service requests.
     */
    public static interface ServiceListener {

        /**
         * Called when a service request completes.
         * 
         * @param values
         *            Key-value string pairs extracted from the response.
         */
        public void onComplete(Bundle values);

        /**
         * Called when a Facebook server responds to the request with an error.
         */
        public void onFacebookError(AuthorizationError e);

        /**
         * Called when a Facebook Service responds to the request with an error.
         */
        public void onError(Error e);

    }

}
