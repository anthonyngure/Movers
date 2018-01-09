/*
 * Copyright (c) 2017. Laysan Incorporation
 * Website http://laysan.co.ke
 * Tel +254723203475/+254706356815
 */

package ke.co.thinksynergy.movers.jobqueue.service;

import android.support.annotation.NonNull;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;

import ke.co.thinksynergy.movers.App;


/**
 * Created by yboyar on 3/20/16.
 */
public class MyGcmJobService extends GcmJobSchedulerService {

    @NonNull
    @Override
    protected JobManager getJobManager() {
        return App.getInstance().getJobManager();
    }

}
