package com.cardiomood.ytranslate.tools;

import com.cardiomood.ytranslate.db.DatabaseHelper;
import com.cardiomood.ytranslate.db.DatabaseHelperFactory;
import com.cardiomood.ytranslate.db.entity.LanguageDao;
import com.cardiomood.ytranslate.db.entity.LanguageEntity;
import com.cardiomood.ytranslate.db.entity.TranslationHistoryDao;
import com.cardiomood.ytranslate.db.entity.TranslationHistoryEntity;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by Anton Danshin on 01/12/14.
 */
public class TranslationHistoryHelper {

    private static final String TAG = TranslationHistoryHelper.class.getSimpleName();

    // because OrmLite doesn't support UNION
    private static final String GET_RECENT_LANGUAGES_SQL =
            "select\n"+
            "   lang,\n"+
            "   max(last_accessed) as last_access \n"+
            "from\n"+
            "   (  select\n"+
            "      src_lang as lang,\n"+
            "      max(last_accessed) as last_accessed \n"+
            "   from\n"+
            "      translation_history \n"+
            "   group by\n"+
            "      src_lang  \n"+
            "   union\n"+
            "   select\n"+
            "      target_lang as lang,\n"+
            "      max(last_accessed) as last_accessed \n"+
            "   from\n"+
            "      translation_history \n"+
            "   group by\n"+
            "      target_lang  \n"+
            ") \n"+
            "group by\n"+
            "lang  \n"+
            "order by\n"+
            "last_access desc limit ?";

    private final DatabaseHelper helper;
    private final TranslationHistoryDao historyDao;
    private final LanguageDao languageDao;

    public TranslationHistoryHelper() throws SQLException {
        helper = DatabaseHelperFactory.getHelper();
        historyDao = helper.getTranslationHistoryDao();
        languageDao = helper.getLanguageDao();
    }

    public Task<TranslationHistoryEntity> saveTranslationAsync(
            final String srcLang,
            final String targetLang,
            final String srcText,
            final String translation,
            final String providerName
    ) {
        return Task.callInBackground(new Callable<TranslationHistoryEntity>() {
            @Override
            public TranslationHistoryEntity call() throws Exception {
                return saveTranslation(srcLang, targetLang, srcText, translation, providerName);
            }
        });
    }

    public TranslationHistoryEntity saveTranslation(
            String srcLang, String targetLang,
            String srcText, String translation, String providerName) throws SQLException {

        synchronized (helper) {
            TranslationHistoryEntity entity = findHistoryItem(srcLang, targetLang, srcText, false);
            if (entity == null) {
                entity = createHistoryItem(srcLang, targetLang, srcText, translation, providerName);
            } else {
                // update translation
                Date now = new Date();
                entity.setTranslation(translation);
                if (now.after(entity.getLastUpdated())) {
                    entity.setLastUpdated(now);
                }
                if (now.after(entity.getLastAccessed())) {
                    entity.setLastAccessed(now);
                }
                historyDao.update(entity);
            }
            return entity;
        }
    }

    public Task<List<TranslationHistoryEntity>> getLastTranslationsAsync(final int offset, final int limit) {
        return Task.callInBackground(new Callable<List<TranslationHistoryEntity>>() {
            @Override
            public List<TranslationHistoryEntity> call() throws Exception {
                return getLastTranslations(offset, limit);
            }
        });
    }

    public List<TranslationHistoryEntity> getLastTranslations(int offset, int limit) throws SQLException {
        synchronized (helper) {
            return historyDao.query(
                    historyDao.queryBuilder()
                            .orderBy("last_accessed", false)
                            .orderBy("src_lang", true)
                            .orderBy("target_lang", true)
                            .offset((long) offset)
                            .limit((long) limit)
                            .prepare()
            );
        }
    }

    public List<TranslationHistoryEntity> getFavoriteTranslations(String query, int offset, int limit) throws SQLException {

        QueryBuilder<TranslationHistoryEntity, Long> qb = historyDao.queryBuilder()
                .orderBy("last_accessed", false)
                .orderBy("src_lang", true)
                .orderBy("target_lang", true)
                .offset((long) offset)
                .limit((long) limit);
        Where where = qb.where();
        where.and(
                where.eq("is_favorite", true),
                where.or(
                        where.like("src_text", new SelectArg("%" + query + "%")),
                        where.like("translation", new SelectArg("%" + query + "%"))
                )
        );
        synchronized (helper) {
            return historyDao.query(qb.prepare());
        }
    }

    public List<TranslationHistoryEntity> getFavoriteTranslations(int offset, int limit) throws SQLException {
        synchronized (helper) {
            return historyDao.query(
                    historyDao.queryBuilder()
                            .orderBy("last_accessed", false)
                            .orderBy("src_lang", true)
                            .orderBy("target_lang", true)
                            .offset((long) offset)
                            .limit((long) limit)
                            .where().eq("is_favorite", true)
                            .prepare()
            );
        }
    }

    public List<TranslationHistoryEntity> getLastTranslations(String query, int offset, int limit) throws SQLException {
        synchronized (helper) {
            return historyDao.query(
                    historyDao.queryBuilder()
                            .orderBy("last_accessed", false)
                            .orderBy("src_lang", true)
                            .orderBy("target_lang", true)
                            .offset((long) offset)
                            .limit((long) limit)
                            .where().like("src_text", new SelectArg("%" + query + "%"))
                            .or().like("translation", new SelectArg("%" + query + "%"))
                            .prepare()
            );
        }
    }

    public Task<Map<String, Date>> getRecentLanguagesAsync(final int limit) {
        return Task.callInBackground(new Callable<Map<String, Date>>() {
            @Override
            public Map<String, Date> call() throws Exception {
                return getRecentLanguages(limit);
            }
        });
    }

    public Task<Map<String, Date>> getRecentSourceLanguagesAsync(final String targetLang, final int limit) {
        return Task.callInBackground(new Callable<Map<String, Date>>() {
            @Override
            public Map<String, Date> call() throws Exception {
                return getRecentSourceLanguages(targetLang, limit);
            }
        });
    }

    public Task<Map<String, Date>> getRecentTargetLanguagesAsync(final String srcLang, final int limit) {
        return Task.callInBackground(new Callable<Map<String, Date>>() {
            @Override
            public Map<String, Date> call() throws Exception {
                return getRecentTargetLanguages(srcLang, limit);
            }
        });
    }

    public Map<String, Date> getRecentSourceLanguages(String targetLang, int limit) throws SQLException {
        // LinkedHashMap is used here to preserve the iteration order
        Map<String, Date> result = new LinkedHashMap<>(limit);

        // construct query:
        //            select src_lang as lang,
        //                   max(last_accessed) as last_access
        //            from translation_history
        //            where target_lang = ? <-- if provided!
        //            group by src_lang
        //            order by last_access desc,
        //                     lang asc
        //            limit ?
        QueryBuilder<TranslationHistoryEntity, Long> builder = historyDao.queryBuilder()
                .selectRaw("src_lang as lang", "max(last_accessed) as last_access")
                .groupByRaw("lang")
                .orderByRaw("last_access desc, lang asc")
                .limit((long) limit);
        if (targetLang != null) {
            builder.where().eq(TranslationHistoryEntity.TARGET_LANG_COLUMN, new SelectArg(targetLang));
        }

        // run query
        synchronized (helper) {
            GenericRawResults<String[]> rows =  (targetLang == null)
                    ? historyDao.queryRaw(builder.prepareStatementString())
                    : historyDao.queryRaw(builder.prepareStatementString(), targetLang);
            for (String[] row: rows) {
                String lang = row[0];
                Date lastAccessed = new Date(Long.parseLong(row[1]));
                result.put(lang, lastAccessed);
            }
            return result;
        }
    }

    public Map<String, Date> getRecentTargetLanguages(String srcLang, int limit) throws SQLException {
        // LinkedHashMap is used here to preserve the iteration order
        Map<String, Date> result = new LinkedHashMap<>(limit);
        // construct query:
        //            select target_lang as lang,
        //                   max(last_accessed) as last_access
        //            from translation_history
        //            where src_lang = ? <-- if provided!
        //            group by target_lang
        //            order by last_access desc,
        //                     lang asc
        //            limit ?
        QueryBuilder<TranslationHistoryEntity, Long> builder = historyDao.queryBuilder()
                .selectRaw("target_lang as lang", "max(last_accessed) as last_access")
                .groupByRaw("lang")
                .orderByRaw("last_access desc, lang asc")
                .limit((long) limit);

        if (srcLang != null) {
            builder.where().eq(TranslationHistoryEntity.SRC_LANG_COLUMN, new SelectArg());
        }

        // run query
        synchronized (helper) {
            GenericRawResults<String[]> rows =  srcLang == null
                    ? historyDao.queryRaw(builder.prepareStatementString())
                    : historyDao.queryRaw(builder.prepareStatementString(), srcLang);
            for (String[] row: rows) {
                String lang = row[0];
                Date lastAccessed = new Date(Long.parseLong(row[1]));
                result.put(lang, lastAccessed);
            }
            return result;
        }
    }

    public Map<String, Date> getRecentLanguages(int limit) throws SQLException {
        // LinkedHashMap is used here to preserve the iteration order
        Map<String, Date> result = new LinkedHashMap<>(limit);

        synchronized (helper) {
            GenericRawResults<String[]> rows = historyDao.queryRaw(
                    GET_RECENT_LANGUAGES_SQL,
                    String.valueOf(limit)
            );
            for (String[] row: rows) {
                String lang = row[0];
                Date lastAccessed = new Date(Long.parseLong(row[1]));
                result.put(lang, lastAccessed);
            }
            return result;
        }
    }

    public TranslationHistoryEntity createHistoryItem(String srcLang, String targetLang,
                                                         String srcText, String translation, String providerName) throws SQLException {
        // create entity
        Date now = new Date();
        TranslationHistoryEntity entity = new TranslationHistoryEntity();
        entity.setCreatedAt(now);
        entity.setLastUpdated(now);
        entity.setLastAccessed(now);
        entity.setSourceLang(srcLang);
        entity.setTargetLang(targetLang);
        entity.setSourceText(srcText);
        entity.setTranslation(translation);
        entity.setTranslationProvider(providerName);

        // TODO: add MD5 calculation for large text

        // persist entity
        synchronized (helper) {
            historyDao.create(entity);
        }
        return entity;
    }


    public TranslationHistoryEntity findHistoryItem(String srcLang, String targetLang,
                                                       String srcText, boolean update) throws SQLException {

        // TODO: add search by MD5 for large text

        // construct query predicate
        Where<TranslationHistoryEntity, Long> predicate = historyDao.queryBuilder()
                .limit(1L)
                .where().eq("target_lang", new SelectArg(targetLang))
                .and().eq("src_text", new SelectArg(srcText));
        if (srcLang != null) {
            predicate.and().eq("src_lang", new SelectArg(srcLang));
        }

        // run query
        synchronized (helper) {
            List<TranslationHistoryEntity> results = historyDao.query(predicate.prepare());
            if (results.isEmpty()) {
                return null;
            }
            if (results.size() == 1) {
                TranslationHistoryEntity item = results.get(0);
                if (update) {
                    Date now = new Date();
                    if (item.getLastAccessed().before(now)) {
                        item.setLastAccessed(new Date());
                    }
                    historyDao.update(item);
                }
                return item;
            }
            return null;
        }
    }


    public Task<TranslationHistoryEntity> findHistoryItemAsync(final String srcLang, final String dstLang, final String text) {
        return Task.callInBackground(new Callable<TranslationHistoryEntity>() {
            @Override
            public TranslationHistoryEntity call() throws Exception {
                return findHistoryItem(srcLang, dstLang, text, true);
            }
        });
    }

    public List<LanguageEntity> getSupportedLanguages() throws SQLException {
        synchronized (helper) {
            return languageDao.queryBuilder()
                    .orderBy(LanguageEntity.NAME_COLUMN, true)
                    .query();
        }
    }

    public LanguageEntity createOrUpdateLanguage(String shortCode, String name) throws SQLException {
        synchronized (helper) {
            LanguageEntity entity = languageDao.findByShortCode(shortCode);
            if (entity == null) {
                entity = new LanguageEntity(shortCode, name);
            }
            helper.getLanguageDao().createOrUpdate(entity);
            return entity;
        }
    }
}
