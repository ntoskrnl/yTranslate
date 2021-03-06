package com.cardiomood.ytranslate.tools;

import com.cardiomood.ytranslate.db.entity.TranslationHistoryEntity;

import java.util.Arrays;

import translate.provider.TranslatedText;

/**
 * Created by Anton Danshin on 05/12/14.
 */
public class SavedTranslatedText extends TranslatedText {

    private TranslationHistoryEntity historyItem;

    public SavedTranslatedText(TranslationHistoryEntity historyItem) {
        super(historyItem.getSourceLang(), historyItem.getTargetLang(),
                Arrays.asList(historyItem.getTranslation().split("\\n")));
        this.historyItem = historyItem;
    }

    public TranslationHistoryEntity getHistoryItem() {
        return historyItem;
    }

    public void setHistoryItem(TranslationHistoryEntity historyItem) {
        this.historyItem = historyItem;
    }
}
