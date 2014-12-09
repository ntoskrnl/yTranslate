package translate.provider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Task;
/**
 * An abstract Translation Provider class.
 *
 * Its subclasses must implement basic operations, such as
 * <ul>
 *  <li>getting the list of supported translation directions;</li>
 *  <li>detecting the language of the text;</li>
 *  <li>translation of simple.</li>
 * </ul>
 * <br/>
 *
 * Created by Anton Danshin on 28/11/14.
 */
public abstract class TranslateProvider {

    private static final String TAG = TranslateProvider.class.getSimpleName();

    /**
     * Translate the provided text in to the target language.
     *
     * @param text text to translate
     * @param targetLanguage the target language (one of the objects returned by
     *                       {@link TranslateProvider#getSupportedLanguages(String)}).
     * @param sourceLanguage the source language. If null the service will usually attempt
     *                       to detect the language (but this might depend on implementation).
     * @return Result of translation containing a translated text and codes
     *         of target and source languages.
     */
    public abstract TranslatedText translate(String text, Language targetLanguage, Language sourceLanguage);

    /**
     * Detect language of the text specified in the argument.
     *
     * @param text the text to analyse
     * @return language code as string (e.g. "en")
     */
    public abstract String detect(String text);

    /**
     * Discover all supported languages.
     *
     * @param uiLanguage the language code of the desired textual representation of language names.
     * @return supported language list as a map.
     */
    public abstract Map<String, Language> getSupportedLanguages(String uiLanguage);

    /**
     * Get supported translation directions.
     *
     * @param uiLanguage the language code of the desired textual representation of language names.
     * @return supported translation directions as a map.
     */
    public abstract Map<Language, List<Language>> getSupportedDirections(String uiLanguage);

    /**
     * Translate the provided text in to the target language. Depending on the implementation,
     * the service might attempt to detect source language automatically.
     *
     * @param text test to translate
     * @param targetLanguage the target language (one of the objects returned by
     *                       {@link TranslateProvider#getSupportedLanguages(String)}).
     * @return Result of translation containing a translated text and codes
     *         of target and source languages.
     */
    public TranslatedText translate(String text, Language targetLanguage) {
        return translate(text, targetLanguage, null);
    }

    /**
     * Translate the provided text in to the target language in background.
     *
     * @param text test to translate
     * @param targetLanguage the target language (one of the objects returned by
     *                       {@link TranslateProvider#getSupportedLanguages(String)}).
     * @param sourceLanguage the source language. If null the service will usually attempt
     *                       to detect the language (but this might depend on implementation).
     * @return A Task containing the result of translation.
     */
    public Task<TranslatedText> translateAsync(final String text, final Language targetLanguage, final Language sourceLanguage) {
        return Task.callInBackground(new Callable<TranslatedText>() {
            @Override
            public TranslatedText call() throws Exception {
                return translate(text, targetLanguage, sourceLanguage);
            }
        });
    }

    /**
     * Detect language of the text in background.
     *
     * @param text the text to analyse
     * @return A Task that contains a detected language code as string (e.g. "en")
     */
    public Task<String> detectAsync(final String text) {
        return Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return detect(text);
            }
        });
    }

    /**
     * Discover all supported languages in background.
     *
     * @param uiLanguage the language code of the desired textual representation of language names.
     * @return A task containing supported language list as a map.
     */
    public Task<Map<String, Language>> getSupportedLanguagesAsync(final String uiLanguage) {
        return Task.callInBackground(new Callable<Map<String, Language>>() {
            @Override
            public Map<String, Language> call() throws Exception {
                return getSupportedLanguages(uiLanguage);
            }
        });
    }

    /**
     * Get supported translation directions in background.
     *
     * @param uiLanguage the language code of the desired textual representation of language names.
     * @return A Task containing supported translation directions as a map.
     */
    public Task<Map<Language, List<Language>>> getSupportedDirectionsAsync(final String uiLanguage) {
        return Task.callInBackground(new Callable<Map<Language, List<Language>>>() {
            @Override
            public Map<Language, List<Language>> call() throws Exception {
                return getSupportedDirections(uiLanguage);
            }
        });
    }

}
