package translate.api.yandex.translate;

import java.util.List;
import java.util.Map;

/**
 * Created by antondanhsin on 26/11/14.
 */
public class TranslationDirections {

    private List<String> dirs;
    private Map<String, String> langs;

    public List<String> getDirs() {
        return dirs;
    }

    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }

    public Map<String, String> getLangs() {
        return langs;
    }

    public void setLangs(Map<String, String> langs) {
        this.langs = langs;
    }
}
