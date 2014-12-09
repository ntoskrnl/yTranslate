package translate.provider;

import java.util.List;

/**
 * Holds the result of translation.
 * Can be subclassed to alter functionality or add more features.
 * <br/>
 * Created by Anton Danshin on 28/11/14.
 */
public class TranslatedText {

    /** Source language code */
    private String sourceLanguage;
    /** Target language code */
    private String targetLanguage;
    /** Translated text **/
    private List<String> text;

    public TranslatedText(String sourceLanguage, String targetLanguage, List<String> text) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.text = text;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    @Override
    public String toString() {
        // TODO: revise this
        return "TranslatedText{" +
                "text=" + text +
                '}';
    }
}
