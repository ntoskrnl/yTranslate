package translate.api.yandex.translate;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;

/**
 * Created by Anton Danshin on 26/11/14.
 */
public class YandexTranslate {

    private static String TAG = YandexTranslate.class.getSimpleName();

    private static final String SERVICE_END_POINT = "https://translate.yandex.net/api/v1.5/tr.json";

    private final TranslateApi service;
    private final String apiKey;

    public YandexTranslate(String apiKey, ErrorHandler errorHandler) {
        this.apiKey = apiKey;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVICE_END_POINT)
                .setErrorHandler(errorHandler)
                .build();
        service = restAdapter.create(TranslateApi.class);
    }

    public YandexTranslate(String apiKey) {
        this(apiKey, ErrorHandler.DEFAULT);
    }

    public TranslationDirections getLangs(String ui) {
        return service.getLangs(apiKey, ui);
    }

    public TranslationResult translate(String text, String lang, TranslateApi.TextFormat format) {
        return service.translate(apiKey, text, lang, format);
    }

    public DetectionResult detectLanguage(String text) {
        return service.detectLanguage(apiKey, text);
    }

}
