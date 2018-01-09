/*
 * Copyright (c) 2016. VibeCampo Social Network
 *
 * Website : http://www.vibecampo.com
 */

package ke.co.thinksynergy.movers.network;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import ke.co.thinksynergy.movers.BuildConfig;
import ke.co.thinksynergy.movers.model.User;
import ke.co.thinksynergy.movers.utils.PrefUtils;


/**
 * Created by Anthony Ngure on 4/17/2016.
 * Email : anthonyngure25@gmail.com.
 * http://www.toshngure.co.ke
 */
public class Client {

    private static final String TAG = Client.class.getSimpleName();
    public static Client mInstance;
    private AsyncHttpClient mClient;
    private SyncHttpClient syncHttpClient;

    private Client() {
        mClient = getClient();
    }

    @Deprecated
    public static synchronized Client getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Client();
        }
        return mInstance;
    }

    public static synchronized Client getInstance() {
        if (mInstance == null) {
            mInstance = new Client();
        }
        return mInstance;
    }

    public static String absoluteUrl(String relativeUrl) {
        String url = BackEnd.BASE_URL + relativeUrl;
        User user = PrefUtils.getInstance().getUser();
        if (user != null) {
            url = url + "?token=" + user.getToken();
        }
        Log.i(TAG, "Connecting to " + url);
        return url;
    }

    public AsyncHttpClient getClient() {
        if (mClient == null) {
            /**
             * Client setup
             */
            mClient = new AsyncHttpClient();
            setUpClient(mClient);
        }
        return mClient;
    }

    public SyncHttpClient getSyncHttpClient() {
        if (syncHttpClient == null) {
            syncHttpClient = new SyncHttpClient();
            setUpClient(syncHttpClient);
        }
        return syncHttpClient;
    }

    private void setUpClient(AsyncHttpClient client) {
        client.setUserAgent(BuildConfig.APPLICATION_ID);
        client.setEnableRedirects(false, true);
        client.setLoggingEnabled(BuildConfig.DEBUG);
        client.addHeader("Accept-Encoding", "gzip");
        client.addHeader("Accept", "application/json");



        /*client.setBasicAuth(
                PrefUtils.getInstance().getString(R.string.pref_email),
                PrefUtils.getInstance().getString(R.string.pref_password)
        );*/

        /*client.setTimeout(30000);
        client.setResponseTimeout(60000);*/

        client.setTimeout(10000);
        client.setResponseTimeout(20000);
        client.setMaxRetriesAndTimeout(1, 10000);
    }


    public void invalidate() {
        mInstance = null;
        mClient = null;
    }
}
