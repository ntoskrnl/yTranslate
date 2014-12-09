package translate.api.yandex.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import translate.provider.Language;
import translate.provider.TranslateProvider;
import translate.provider.TranslatedText;

/**
 * An implementation of {@link translate.provider.TranslateProvider}
 * that works with Yandex.Translate API.
 * <br/>
 *
 * Created by Anton Danshin on 28/11/14.
 */
public class YandexTranslateProvider extends TranslateProvider {

    private static final TranslateApi.TextFormat FORMAT = TranslateApi.TextFormat.PLAIN;

    private static final String[] SUPPORTED_UI_LANGS = new String[] {"en", "ru", "uk", "tr"};

    private YandexTranslate service;

    public YandexTranslateProvider(String apiKey) {
        // TODO: provide a sane ErrorHandler in order to wrap Retrofit exceptions
        this.service = new YandexTranslate(apiKey /**, myErrorHandler */);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TranslatedText translate(String text, Language targetLanguage, Language sourceLanguage) {
        String dir = targetLanguage.getLanguage();
        if (sourceLanguage != null) {
            dir = sourceLanguage.getLanguage() + "-" + targetLanguage.getLanguage();
        }
        TranslationResult result = service.translate(text, dir, FORMAT);
        // TODO: do all necessary checks!
        String srcLang = result.getLang().substring(0, result.getLang().indexOf('-'));
        return new TranslatedText(srcLang, targetLanguage.getLanguage(), result.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String detect(String text) {
        DetectionResult result = service.detectLanguage(text);
        return result.getLang();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Language> getSupportedLanguages(String uiLanguage) {
        uiLanguage = getValidUiLang(uiLanguage);
        TranslationDirections response = service.getLangs(uiLanguage);
        Map<String, String> langs = response.getLangs();
        Map<String, Language> result = new HashMap<>();
        for (Map.Entry<String, String> entry: langs.entrySet()) {
            result.put(entry.getKey(), new Language(entry.getKey(), entry.getValue(), uiLanguage));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Language, List<Language>> getSupportedDirections(String uiLanguage) {
        uiLanguage = getValidUiLang(uiLanguage);
        TranslationDirections response = service.getLangs(uiLanguage);
        List<String> dirs = response.getDirs();
        Map<String, String> langs = response.getLangs();
        Map<Language, List<Language>> result = new HashMap<>();
        for (String dir: dirs) {
            String langA = dir.substring(0, dir.indexOf('-'));
            String langB = dir.substring(dir.indexOf('-')+1);
            Language language = new Language(langA, langs.get(langA), uiLanguage);
            List<Language> list = result.get(language);
            if (list == null) {
                list = new ArrayList<>();
                result.put(language, list);
            }
            list.add(new Language(langB, langs.get(langB), uiLanguage));
        }
        return result;
    }

    private String getValidUiLang(String uiLang) {
        if (uiLang == null) {
            return SUPPORTED_UI_LANGS[0];
        }
        for (String lang: SUPPORTED_UI_LANGS) {
            if (lang.equals(uiLang)) {
                return lang;
            }
        }
        return SUPPORTED_UI_LANGS[0];
    }
}
