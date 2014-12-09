package translate.api.yandex.translate;


import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interface of Yandex Translate API.
 *
 * Created by antondanhsin on 26/11/14.
 */
public interface TranslateApi {

    @GET("/getLangs")
    TranslationDirections getLangs(@Query("key") String key, @Query("ui") String ui);

    @GET("/detect")
    DetectionResult detectLanguage(@Query("key") String key, @Query("text") String text);

    @GET("/translate")
    TranslationResult translate(@Query("key") String key, @Query("text") String text, @Query("lang") String lang, @Query("format") TextFormat format);

    public static enum TextFormat {
        PLAIN, HTML;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

}
