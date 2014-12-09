package translate.api.yandex.dictionary;

import java.util.List;
import java.util.Map;

/**
 * Created by Anton Danshin on 29/11/14.
 */
public class DicResult {

    private Map<String, Object> head;

    private List<Definition> def;


    public Map<String, Object> getHead() {
        return head;
    }

    public void setHead(Map<String, Object> head) {
        this.head = head;
    }

    public List<Definition> getDef() {
        return def;
    }

    public void setDef(List<Definition> def) {
        this.def = def;
    }
}
