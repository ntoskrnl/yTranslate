package com.cardiomood.ytranslate;

import com.cardiomood.ytranslate.db.DatabaseHelperFactory;

/**
 * Created by Anton Danshin on 02/12/14.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseHelperFactory.obtainHelper(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        DatabaseHelperFactory.releaseHelper();
    }
}
