package translate.api.yandex.dictionary;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Anton Danshin on 29/11/14.
 */
public interface DictionaryApi {

    public int FLAG_FAMILY = 0x0001;
    public int FLAG_POS = 0x0002;
    public int FLAG_MORPHO = 0x0004;
    public int FLAG_POS_FILTER = 0x0008;

    @GET("/getLangs")
    List<String> getDirections(@Query("key") String apiKey);

    @GET("/lookup")
    DicResult lookup(@Query("key") String key, @Query("text") String text,
                     @Query("lang") String lang, @Query("ui") String ui, @Query("flags") int flags);

}
