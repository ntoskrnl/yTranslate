package com.cardiomood.ytranslate.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cardiomood.ytranslate.db.entity.TranslationHistoryDao;
import com.cardiomood.ytranslate.db.entity.TranslationHistoryEntity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Anton Danshin on 01/12/14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FILE_NAME = "ytranslate.db";

    private Context mContext;
    private TranslationHistoryDao translationHistoryDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        // create tables
        try {
            TableUtils.createTable(connectionSource, TranslationHistoryEntity.class);
        } catch (SQLException ex) {
            Log.e(TAG, "onCreate() filed", ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // perform upgrade
    }

    public synchronized TranslationHistoryDao getTranslationHistoryDao() throws SQLException {
        if (translationHistoryDao == null) {
            translationHistoryDao = new TranslationHistoryDao(getConnectionSource(), TranslationHistoryEntity.class);
        }
        return translationHistoryDao;
    }
}
