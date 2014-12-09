package translate.api.yandex.dictionary;

import java.util.List;

/**
 * Created by Anton Danshin on 29/11/14.
 */
public class Example extends WrappedText {

    private List<Translation> tr;

    public List<Translation> getTr() {
        return tr;
    }

    public void setTr(List<Translation> tr) {
        this.tr = tr;
    }
}
