/*
 * Copyright (c) 2017. Laysan Incorporation
 * Website http://laysan.co.ke
 * Tel +254723203475/+254706356815
 */

package ke.co.thinksynergy.movers.database;

import android.database.sqlite.SQLiteDatabase;

import ke.co.thinksynergy.movers.App;
import ke.co.thinksynergy.movers.model.DaoMaster;
import ke.co.thinksynergy.movers.model.DaoSession;


/**
 * Created by Anthony Ngure on 28/04/2017.
 * Email : anthonyngure25@gmail.com.
 * Company : Laysan Incorporation
 */

public abstract class DatabaseImpl {

    protected String mDbName;
    protected final DaoSession mDaoSession;
    protected static DaoMaster mDaoMaster;

    protected DatabaseImpl(String dbName) {
        this.mDbName = dbName;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(App.getInstance(), dbName, null);
        //MySQLiteOpenHelper helper = new MySQLiteOpenHelper(VCApplication.getInstance(), dbName, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public void clean() {
        App.getInstance().deleteDatabase(mDbName);
        onClean();
    }

    protected abstract void onClean();
}
