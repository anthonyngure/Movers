/*
 * Copyright (c) 2016. VibeCampo Social Network
 *
 * Website : http://www.vibecampo.com
 */

package ke.co.thinksynergy.movers;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import ke.co.thinksynergy.movers.jobqueue.service.MyGcmJobService;
import ke.co.thinksynergy.movers.jobqueue.service.MyJobService;
import ke.co.toshngure.basecode.log.BeeLog;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();

    private static App mInstance;
    private JobManager jobManager;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        BeeLog.init(BuildConfig.DEBUG, "ToshNgure");

        mInstance = this;

        getJobManager();
    }

    private void configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {

                    @Override
                    public boolean isDebugEnabled() {
                        return BeeLog.DEBUG;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {

                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120);//wait 2 minute
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(this,
                    MyJobService.class), true);
        } else {
            int enableGcm = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            if (enableGcm == ConnectionResult.SUCCESS) {
                builder.scheduler(GcmJobSchedulerService.createSchedulerFor(this,
                        MyGcmJobService.class), true);
            }
        }

        jobManager = new JobManager(builder.build());
    }

    public synchronized JobManager getJobManager() {
        if (jobManager == null) {
            configureJobManager();
        }
        return jobManager;
    }

}
