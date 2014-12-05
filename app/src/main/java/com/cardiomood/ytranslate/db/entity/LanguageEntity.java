package com.cardiomood.ytranslate.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Anton Danshin on 05/12/14.
 */
@DatabaseTable(tableName = LanguageEntity.TABLE_NAME, daoClass = LanguageDao.class)
public class LanguageEntity {

    public static final String TABLE_NAME = "languages";

    public static final String ID_COLUMN = "_id";
    @DatabaseField(columnName = ID_COLUMN, generatedId = true)
    private Long id;

    public static final String SHORT_CODE_COLUMN = "short_code";
    @DatabaseField(columnName = SHORT_CODE_COLUMN, unique = true, canBeNull = false, index = true)
    private String shortCode;


    public static final String NAME_COLUMN = "name";
    @DatabaseField(columnName = NAME_COLUMN)
    private String name;

    public LanguageEntity() {
    }

    public LanguageEntity(String shortCode, String name) {
        this.shortCode = shortCode;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
