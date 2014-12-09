package translate.api.yandex.translate;

import java.util.List;

/**
 * Created by antondanhsin on 26/11/14.
 */
public class TranslationResult {

    private int code;
    private String lang;
    private List<String> text;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }
}
