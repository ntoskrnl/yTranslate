package com.cardiomood.ytranslate.db.entity;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Created by Anton Danshin on 02/12/14.
 */
public class TranslationHistoryDao extends BaseDaoImpl<TranslationHistoryEntity, Long> {

    public TranslationHistoryDao(ConnectionSource connectionSource, Class<TranslationHistoryEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    
}
