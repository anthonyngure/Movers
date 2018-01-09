/*
 * Copyright (c) 2017. Laysan Incorporation
 * Website http://laysan.co.ke
 * Tel +254723203475/+254706356815
 */

package ke.co.thinksynergy.movers.jobqueue;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.thinksynergy.movers.network.Client;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.basecode.log.BeeLog;
import ke.co.toshngure.basecode.networking.ConnectionListener;
import ke.co.toshngure.basecode.networking.ResponseHandler;


/**
 * Created by Anthony Ngure on 07/01/2017.
 * Email : anthonyngure25@gmail.com.
 * Company : Laysan Incorporation
 */

public class SendDeviceTokenJob extends Job implements ConnectionListener {

    private static final String TAG = SendDeviceTokenJob.class.getSimpleName();

    public SendDeviceTokenJob() {
        super(new Params(Priority.HIGH)
                .setSingleId("device_token")
                .requireNetwork()
                .persist()
                .groupBy("firebaseMessagingId")
        );
    }

    @Override
    public void onAdded() {
        BeeLog.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        BeeLog.d(TAG, "onRun");

        String token = FirebaseInstanceId.getInstance().getToken();
        Client.getInstance().getSyncHttpClient()
                .put(Client.absoluteUrl(BackEnd.EndPoints.DEVICE_TOKEN + "/" + token), new ResponseHandler(this));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        BeeLog.d(TAG, "onCancel");
        PrefUtils.getInstance().writeBoolean(R.string.pref_update_device_token, true);
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        BeeLog.d(TAG, "shouldReRunOnThrowable");
        BeeLog.d(TAG, String.valueOf(throwable.getMessage()));
        if (PrefUtils.getInstance().getBoolean(R.string.pref_update_device_token, true)) {
            return RetryConstraint.RETRY;
        } else {
            return RetryConstraint.CANCEL;
        }
    }

    @Override
    public void connect() {

    }

    @Override
    public void onConnectionStarted() {

    }

    @Override
    public void onConnectionFailed(int statusCode, JSONObject response) {
        PrefUtils.getInstance().writeBoolean(R.string.pref_update_device_token, true);
    }

    @Override
    public void onConnectionSuccess(JSONObject response) {
        BeeLog.d(TAG, response.toString());
        PrefUtils.getInstance().writeBoolean(R.string.pref_update_device_token, false);
    }

    @Override
    public void onConnectionProgress(int progress) {

    }

    @Override
    public Activity getListenerContext() {
        return null;
    }
}

