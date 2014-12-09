package translate.api.yandex.dictionary;

import java.util.List;

/**
 * Created by Anton Danshin on 29/11/14.
 */
public class Definition extends WrappedText {

    private String pos;
    private String ts;
    private List<Translation> tr;

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public List<Translation> getTr() {
        return tr;
    }

    public void setTr(List<Translation> tr) {
        this.tr = tr;
    }
}
