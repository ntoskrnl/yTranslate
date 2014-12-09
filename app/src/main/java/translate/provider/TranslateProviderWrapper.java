package translate.provider;

import java.util.List;
import java.util.Map;

/**
 * Wrapper for Translation provider.
 * Can be subclassed to modify behavior without changing the original implementation.
 *
 * Created by Anton Danshin on 05/12/14.
 */
public class TranslateProviderWrapper extends TranslateProvider {

    private final TranslateProvider mProvider;

    public TranslateProviderWrapper(TranslateProvider provider) {
        this.mProvider = provider;
    }

    /**
     * {@inheritDoc}
     */
    public TranslatedText translate(String text, Language targetLanguage, Language sourceLanguage) {
        return mProvider.translate(text, targetLanguage, sourceLanguage);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Language> getSupportedLanguages(String uiLanguage) {
        return mProvider.getSupportedLanguages(uiLanguage);
    }

    /**
     * {@inheritDoc}
     */
    public Map<Language, List<Language>> getSupportedDirections(String uiLanguage) {
        return mProvider.getSupportedDirections(uiLanguage);
    }

    /**
     * {@inheritDoc}
     */
    public String detect(String text) {
        return mProvider.detect(text);
    }

    /**
     * {@inheritDoc}
     */
    public TranslateProvider getTranslateProvider() {
        return mProvider;
    }
}
