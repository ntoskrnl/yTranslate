package com.cardiomood.ytranslate.db.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Anton Danshin on 01/12/14.
 */
@DatabaseTable(tableName = TranslationHistoryEntity.TABLE_NAME, daoClass = TranslationHistoryDao.class)
public class TranslationHistoryEntity {

    public static final String TABLE_NAME = "translation_history";

    public static final String ID_COLUMN = "_id";
    @DatabaseField(columnName = ID_COLUMN, generatedId = true)
    private Long id;

    public static final String SRC_LANG_COLUMN = "src_lang";
    @DatabaseField(columnName = SRC_LANG_COLUMN, canBeNull = false)
    private String sourceLang;

    public static final String TARGET_LANG_COLUMN = "target_lang";
    @DatabaseField(columnName = TARGET_LANG_COLUMN, canBeNull = false)
    private String targetLang;

    public static final String SRC_TEXT_COLUMN = "src_text";
    @DatabaseField(columnName = SRC_TEXT_COLUMN, index = true, canBeNull = false)
    private String sourceText;

    public static final String TRANSLATION_COLUMN = "translation";
    @DatabaseField(columnName = TRANSLATION_COLUMN, index = true, canBeNull = false)
    private String translation;

    public static final String LAST_ACCESSED_COLUMN = "last_accessed";
    @DatabaseField(columnName = LAST_ACCESSED_COLUMN, dataType = DataType.DATE_LONG, canBeNull = false)
    private Date lastAccessed;

    public static final String LAST_UPDATED_COLUMN = "last_updated";
    @DatabaseField(columnName = LAST_UPDATED_COLUMN, dataType = DataType.DATE_LONG, canBeNull = false)
    private Date lastUpdated;

    public static final String CREATED_AT_COLUMN = "created_at";
    @DatabaseField(columnName = CREATED_AT_COLUMN, dataType = DataType.DATE_LONG, canBeNull = false)
    private Date createdAt;

    public static final String PROVIDER_CLASS_COLUMN = "provider_class";
    @DatabaseField(columnName = PROVIDER_CLASS_COLUMN, canBeNull = false)
    private String translationProvider;

    public static final String IS_FAVORITE_COLUMN = "is_favorite";
    @DatabaseField(columnName = IS_FAVORITE_COLUMN)
    private boolean favorite;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getTranslationProvider() {
        return translationProvider;
    }

    public void setTranslationProvider(String translationProvider) {
        this.translationProvider = translationProvider;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
