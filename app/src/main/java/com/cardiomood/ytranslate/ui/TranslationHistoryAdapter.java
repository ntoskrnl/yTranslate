package com.cardiomood.ytranslate.ui;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

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
public class TranslationHistoryAdapter extends EndlessAdapter implements Filterable {

    private static final String TAG = TranslationHistoryAdapter.class.getSimpleName();

    private final TranslationHistoryHelper historyHelper;
    private final List<TranslationHistoryEntity> cachedItems = Collections.synchronizedList(new ArrayList<TranslationHistoryEntity>());

    private Filter mFilter;
    private String query = null;
    private boolean favorites = false;


    public TranslationHistoryAdapter(Context context, ArrayAdapter<TranslationHistoryEntity> wrapped, boolean favorites) throws SQLException{
        super(context, wrapped, android.R.layout.simple_list_item_1);

        // initialize adapter
        this.favorites = favorites;
        historyHelper = new TranslationHistoryHelper();
        setSerialized(true);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        // get more items in background
        cachedItems.clear();
        try {
            List<TranslationHistoryEntity> items = null;
            if (favorites) {
                items = (query == null)
                        ? historyHelper.getFavoriteTranslations(getWrappedAdapter().getCount(), 30)
                        : historyHelper.getFavoriteTranslations(query, getWrappedAdapter().getCount(), 30);
            } else {
                items = (query == null)
                        ? historyHelper.getLastTranslations(getWrappedAdapter().getCount(), 30)
                        : historyHelper.getLastTranslations(query, getWrappedAdapter().getCount(), 30);
            }
            cachedItems.addAll(items);
        } catch (SQLException ex) {
            Log.e(TAG, "cacheInBackground() failed", ex);
        }
        return !cachedItems.isEmpty();
    }

    @Override
    protected void appendCachedData() {
        @SuppressWarnings("unchecked")
        ArrayAdapter<TranslationHistoryEntity> adapter = (ArrayAdapter<TranslationHistoryEntity>) getWrappedAdapter();
        for (TranslationHistoryEntity entity: cachedItems) {
            adapter.add(entity);
        }
    }

    public void refresh() {
        restartAppending();
        @SuppressWarnings("unchecked")
        ArrayAdapter<TranslationHistoryEntity> adapter = (ArrayAdapter<TranslationHistoryEntity>) getWrappedAdapter();
        adapter.clear();
    }


    public TranslationHistoryEntity getHistoryItem(int position) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<TranslationHistoryEntity> wrapped = (ArrayAdapter<TranslationHistoryEntity>) getWrappedAdapter();
        return wrapped.getItem(position);
    }


    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new DataFilter();
        }
        return mFilter;
    }

    private class DataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (constraint == null || constraint.length() == 0) {
                if (query == null)
                    return;
                query = null;
            } else {
                if (constraint.toString().equals(query))
                    return;
                query = constraint.toString();
            }
            refresh();
        }
    }


}
