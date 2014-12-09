package com.cardiomood.ytranslate.tools;

import android.util.Log;

import com.cardiomood.ytranslate.db.DatabaseHelperFactory;
import com.cardiomood.ytranslate.db.entity.LanguageEntity;
import com.cardiomood.ytranslate.db.entity.TranslationHistoryEntity;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Task;
import translate.provider.Language;
import translate.provider.TranslateProvider;
import translate.provider.TranslateProviderWrapper;
import translate.provider.TranslatedText;

/**
 * Encapsulates translation functionality and uses history database as cache.
 * Provides two different strategies to work with cache.
 *
 * Created by Anton Danshin on 05/12/14.
 */
public class HistoryAwareTranslateProvider extends TranslateProviderWrapper {

    private static final String TAG = HistoryAwareTranslateProvider.class.getSimpleName();

    /**
     * Use local history as a primary translation service.
     * This strategy should be used when there is no Internet or it is unstable.
     * */
    public static final int HISTORY_FIRST = 0;

    /**
     * Use underlying translation provider as primary translation service.
     * This strategy should be used with stable Internet.
     */
    public static final int ONLINE_FIRST = 1;

    private TranslationHistoryHelper mHistoryHelper;
    private boolean historyEnabled = false;
    private int strategy = HISTORY_FIRST;


    public HistoryAwareTranslateProvider(TranslateProvider provider) {
        this(provider, true);
    }

    public HistoryAwareTranslateProvider(TranslateProvider provider, boolean historyEnabled) {
        this(provider, historyEnabled, HISTORY_FIRST);
    }

    public HistoryAwareTranslateProvider(TranslateProvider provider, boolean historyEnabled, int strategy) {
        super(provider);
        if (historyEnabled) {
            try {
                this.mHistoryHelper = new TranslationHistoryHelper();
                this.historyEnabled = true;
                this.strategy = strategy;
            } catch (SQLException ex) {
                this.historyEnabled = false;
                Log.w(TAG, "Failed to initialize TranslationHistoryHelper due to SQL exception.", ex);
                Log.w(TAG, "History will not be used.", ex);
            }
        }
    }

    @Override
    public TranslatedText translate(String text, Language targetLanguage, Language sourceLanguage) {
        TranslatedText result = null;
        if (historyEnabled && strategy == HISTORY_FIRST) {
            // try to find in history
            result = historyLookup(text, targetLanguage, sourceLanguage);
            if (result != null)
                return result;
        }

        // Attempt to translate using underlying provider
        RuntimeException onlineEx = null;
        try {
            result = super.translate(text, targetLanguage, sourceLanguage);
            if (result != null && historyEnabled) {
                // try to save this translation in to history
                try {
                    TranslationHistoryEntity entity = mHistoryHelper.createHistoryItem(
                            result.getSourceLanguage(),
                            result.getTargetLanguage(),
                            text.trim(),
                            getTranslation(result),
                            getTranslateProvider().getClass().getName()
                    );
                    Log.d(TAG, "translate(): history item has been saved, ID=" + entity.getId());
                } catch (SQLException ex) {
                    Log.w(TAG, "translate(): translation hasn't been saved due to SQL exception.", ex);
                }
            }
            return result;

        } catch (RuntimeException ex) {
            Log.w(TAG, "translate(): online translation failed due to network error", ex);
            onlineEx = ex;
        }

        if (historyEnabled && strategy == ONLINE_FIRST) {
            if (result == null) {
                // try to find in history
                result = historyLookup(text, targetLanguage, sourceLanguage);
                if (result != null)
                    return result;
            }
        }

        if (result == null && onlineEx != null) {
            // network error occurred -> notify the client
            throw onlineEx;
        }

        return result;
    }

    @Override
    public Map<String, Language> getSupportedLanguages(String uiLanguage) {
        final Map<String, Language> result = new LinkedHashMap<>();
        if (historyEnabled && strategy == HISTORY_FIRST) {
            // try to find in history
            getSupportedLanguagesLocally(result);
            if (!result.isEmpty())
                return result;
        }

        RuntimeException onlineEx = null;
        try {
            Map<String, Language> onlineResult = super.getSupportedLanguages(uiLanguage);
            if (onlineResult != null) {
                result.putAll(onlineResult);
            }

            if (!result.isEmpty() && historyEnabled) {
                saveLanguagesLocally(result.values());
            }
            return result;
        } catch (RuntimeException ex) {
            Log.w(TAG, "getSupportedLanguages(): failed due to network error", ex);
            onlineEx = ex;
        }

        if (historyEnabled && strategy == ONLINE_FIRST) {
            if (result.isEmpty()) {
                getSupportedLanguagesLocally(result);
            }
            if (!result.isEmpty())
                return result;
        }

        if (result == null && onlineEx != null) {
            // network error occurred -> notify the client
            throw onlineEx;
        }

        return result;
    }

    public boolean isHistoryEnabled() {
        return historyEnabled;
    }

    public void setHistoryEnabled(boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    public int getStrategy() {
        return strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    public Task<Map<String, Date>> getRecentSourceLanguagesAsync(String targetLang, int limit) {
        if (historyEnabled && mHistoryHelper != null) {
            return mHistoryHelper.getRecentSourceLanguagesAsync(targetLang, limit);
        } else {
            Task<Map<String, Date>>.TaskCompletionSource task = Task.create();
            task.setResult(Collections.EMPTY_MAP);
            return task.getTask();
        }
    }

    public Task<Map<String, Date>> getRecentTargetLanguagesAsync(String srcLang, int limit) {
            if (historyEnabled && mHistoryHelper != null) {
                return mHistoryHelper.getRecentTargetLanguagesAsync(srcLang, limit);
            } else {
                Task<Map<String, Date>>.TaskCompletionSource task = Task.create();
                task.setResult(Collections.EMPTY_MAP);
                return task.getTask();
            }
    }

    protected String getTranslation(TranslatedText translatedText) {
        List<String> data = translatedText.getText();
        StringBuilder translation = new StringBuilder();
        if (data != null) {
            for (String line: data) {
                translation.append(line).append("\n");
            }
        }
        return translation.toString().trim();
    }

    protected TranslatedText historyLookup(String text, Language targetLanguage, Language sourceLanguage) {
        try {
            // try to find
            TranslationHistoryEntity entity = mHistoryHelper.findHistoryItem(
                    sourceLanguage == null ? null : sourceLanguage.getLanguage(),
                    targetLanguage.getLanguage(),
                    text.trim(),
                    true
            );

            if (entity != null) {
                // found -> return this data as our translation
                return new SavedTranslatedText(entity);
            }
        } catch (SQLException ex) {
            Log.w(TAG, "translate(): failed to find translation due to SQL exception.", ex);
        }

        // not found
        return null;
    }

    protected void getSupportedLanguagesLocally(Map<String, Language> result) {
        try {
            List<LanguageEntity> langs = mHistoryHelper.getSupportedLanguages();
            if (langs != null && !langs.isEmpty()) {
                for (LanguageEntity entity: langs) {
                    result.put(entity.getShortCode(),
                            new Language(entity.getShortCode(), entity.getName()));
                }
            }
        } catch (SQLException ex) {
            Log.w(TAG, "getSupportedLanguagesLocally(): failed due to SQL exception.", ex);
        }
    }


    protected void saveLanguagesLocally(final Collection<Language> langs) {
        try {
            DatabaseHelperFactory.getHelper().callInTransaction(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (Language lang: langs) {
                        mHistoryHelper.createOrUpdateLanguage(lang.getLanguage(), lang.getName());
                    }
                    return null;
                }
            });
        } catch (SQLException ex) {
            Log.w(TAG, "saveLanguagesLocally(): failed due to SQL exception.", ex);
        }
    }
}

