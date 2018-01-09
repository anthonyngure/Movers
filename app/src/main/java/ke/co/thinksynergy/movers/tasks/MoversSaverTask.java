package ke.co.thinksynergy.movers.tasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ke.co.thinksynergy.movers.database.Database;
import ke.co.thinksynergy.movers.model.Mover;
import ke.co.thinksynergy.movers.model.MoverDao;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 11/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class MoversSaverTask extends AsyncTask<JSONArray, Void, ArrayList<Mover>> {

    private OnTaskFinishListener<ArrayList<Mover>> mTaskFinishListener;

    public MoversSaverTask(OnTaskFinishListener<ArrayList<Mover>> listener) {
        this.mTaskFinishListener = listener;
    }

    @Override
    protected ArrayList<Mover> doInBackground(JSONArray... data) {

        Database.getInstance().getDaoSession().getMoverDao().deleteAll();

        try {
            for (int i = 0; i < data[0].length(); i++) {
                JSONObject serviceObject = data[0].getJSONObject(i);
                Mover provider = BaseUtils.getSafeGson()
                        .fromJson(serviceObject.toString(), Mover.class);
                Database.getInstance().getDaoSession().getMoverDao()
                        .insertOrReplace(provider);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<Mover> nearMostTrucks = Database.getInstance().getDaoSession().getMoverDao()
                .queryBuilder().where(MoverDao.Properties.Type.eq(Mover.TRUCK))
                .orderAsc(MoverDao.Properties.Distance).limit(2).list();

        List<Mover> nearMostPickUps = Database.getInstance().getDaoSession().getMoverDao()
                .queryBuilder().where(MoverDao.Properties.Type.eq(Mover.PICK_UP))
                .orderAsc(MoverDao.Properties.Distance).limit(2).list();

        List<Mover> nearMostMotorBikes = Database.getInstance().getDaoSession().getMoverDao()
                .queryBuilder().where(MoverDao.Properties.Type.eq(Mover.MOTOR_BIKE))
                .orderAsc(MoverDao.Properties.Distance).limit(2).list();

        List<Mover> nearMostPersons = Database.getInstance().getDaoSession().getMoverDao()
                .queryBuilder().where(MoverDao.Properties.Type.eq(Mover.PERSON))
                .orderAsc(MoverDao.Properties.Distance).limit(2).list();

        ArrayList<Mover> topMovers = new ArrayList<>();
        topMovers.addAll(nearMostTrucks);
        topMovers.addAll(nearMostPickUps);
        topMovers.addAll(nearMostMotorBikes);
        topMovers.addAll(nearMostPersons);

        return topMovers;
    }

    @Override
    protected void onPostExecute(ArrayList<Mover> movers) {
        super.onPostExecute(movers);
        mTaskFinishListener.onTaskFinish(movers);
    }
}
