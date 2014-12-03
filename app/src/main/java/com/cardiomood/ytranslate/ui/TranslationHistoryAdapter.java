package com.cardiomood.ytranslate.ui;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.cardiomood.ytranslate.db.entity.TranslationHistoryEntity;
import com.cardiomood.ytranslate.tools.TranslationHistoryHelper;
import com.commonsware.cwac.endless.EndlessAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anton Danshin on 03/12/14.
 */
public class TranslationHistoryAdapter extends EndlessAdapter {

    private static final String TAG = TranslationHistoryAdapter.class.getSimpleName();

    private final TranslationHistoryHelper historyHelper;
    private final List<TranslationHistoryEntity> cachedItems = Collections.synchronizedList(new ArrayList<TranslationHistoryEntity>());


    public TranslationHistoryAdapter(Context context, ListAdapter wrapped) throws SQLException{
        super(context, wrapped, android.R.layout.simple_list_item_1);

        // initialize adapter
        setSerialized(true);
        historyHelper = new TranslationHistoryHelper();
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        // get more items in background
        cachedItems.clear();
        try {
            List<TranslationHistoryEntity> items = historyHelper.getLastTranslations(getWrappedAdapter().getCount(), 5);
            cachedItems.addAll(items);
        } catch (SQLException ex) {
            Log.e(TAG, "cacheInBackground() failed", ex);
        }
        return !cachedItems.isEmpty();
    }

    @Override
    protected void appendCachedData() {
        ArrayAdapter<String> adapter = (ArrayAdapter) getWrappedAdapter();
        for (TranslationHistoryEntity entity: cachedItems) {
            adapter.add(entity.getSourceText());
        }
    }
}
