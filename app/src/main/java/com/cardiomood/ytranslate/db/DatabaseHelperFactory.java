package com.cardiomood.ytranslate.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Provides global access to the
 *
 * Actually, this is not a factory. :)
 *
 * Created by Anton Danshin on 01/12/14.
 */
public class DatabaseHelperFactory {

    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getHelper() {
        return databaseHelper;
    }

    public synchronized static void obtainHelper(Context context) {
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public synchronized static void releaseHelper() {
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }

}
