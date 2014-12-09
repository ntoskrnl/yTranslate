package translate.api.yandex.dictionary;

import java.util.List;

/**
 * Created by Anton Danshin on 29/11/14.
 */
public class Translation extends PosText {

    private List<PosText> syn;
    private List<WrappedText> mean;
    private List<Example> ex;

    public List<PosText> getSyn() {
        return syn;
    }

    public void setSyn(List<PosText> syn) {
        this.syn = syn;
    }

    public List<WrappedText> getMean() {
        return mean;
    }

    public void setMean(List<WrappedText> mean) {
        this.mean = mean;
    }

    public List<Example> getEx() {
        return ex;
    }

    public void setEx(List<Example> ex) {
        this.ex = ex;
    }
}
