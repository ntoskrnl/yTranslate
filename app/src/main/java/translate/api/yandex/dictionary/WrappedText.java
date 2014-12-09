package translate.api.yandex.dictionary;

/**
 * Created by Anton Danshin on 29/11/14.
 */
public class WrappedText {

    private String text;

    public WrappedText() {
    }

    public WrappedText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
