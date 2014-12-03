package com.cardiomood.ytranslate.provider;

import java.util.Locale;

/**
 * Represents a supported language.
 * Contains name in the language specified by <code>uiLanguage</code>.
 *
 * Created by Anton Danshin on 28/11/14.
 */
public class Language implements Comparable<Language> {

    private final String language;
    private final String name;
    private final String uiLanguage;

    /**
     * Creates an instance of the Language.
     *
     * @param language a language code (e.g. "ru").
     * @param name name represented in the language provides as {@param uiLanguage}.
     * @param uiLanguage language code used to provide the name of language.
     */
    public Language(String language, String name, String uiLanguage) {
        if (language == null) {
            throw new IllegalArgumentException("language cannot be null");
        }
        this.language = language;
        this.name = name;
        this.uiLanguage = uiLanguage;
    }

    public Language(String language, String name) {
        this(language, name, Locale.getDefault().getLanguage());
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public String getUiLanguage() {
        return uiLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language1 = (Language) o;

        if (!language.equals(language1.language)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return language.hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + language + ")";
    }

    @Override
    public int compareTo(Language another) {
        if (name == null || another.name == null) {
            return language.compareToIgnoreCase(another.language);
        }
        return name.compareToIgnoreCase(another.name);
    }
}
