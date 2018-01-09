package ke.co.thinksynergy.movers.tasks;

import android.app.Activity;

import java.lang.ref.WeakReference;

import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.activity.SignInActivity;
import ke.co.thinksynergy.movers.database.Database;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.basecode.app.DialogAsyncTask;

/**
 * Created by Anthony Ngure on 31/10/2017.
 * Email : anthonyngure25@gmail.com.
 */

public  class SignOutAsyncTask extends DialogAsyncTask<Void, Void, Void>  {

    private WeakReference<Activity> mActivityWeakReference;

    public SignOutAsyncTask(Activity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        PrefUtils.getInstance().writeString(R.string.pref_user, "");
        Database.getInstance().clean();
        return null;
    }

    @Override
    protected Activity getActivity() {
        return mActivityWeakReference.get();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Activity activity = mActivityWeakReference.get();
        if (activity != null){
            SignInActivity.start(activity);
            activity.finish();
        }
    }
}
