package com.cardiomood.ytranslate;

import com.cardiomood.ytranslate.provider.Language;
import com.cardiomood.ytranslate.provider.TranslateProvider;
import com.cardiomood.ytranslate.provider.TranslatedText;
import com.cardiomood.ytranslate.provider.YandexTranslateProvider;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.yandex.translate.DetectionResult;


/**
 * Created by antondanhsin on 26/11/14.
 */
public class Main {

//    public static void main(String[] args) {
//        long t0 = System.currentTimeMillis(), t = 0;
//        TranslateProvider provider = new YandexTranslateProvider("trnsl.1.1.20141126T151929Z.2028746c57ef2cb5.29f3fed6a7b663d81c68ca53a58f5eb5e0077b5b");
//        Map<String, Language> langs = provider.getSupportedLanguages(Locale.getDefault().getLanguage());
//        t = System.currentTimeMillis();
//        System.out.println(t - t0);
//
//        String msg = "5G (เครือข่ายโทรศัพท์มือถือรุ่นหรือระบบไร้สายรุ่นที่ 5) เป็นคำที่ใช้ในบางงานวิจัยและโครงการเพื่อแสดงถึงขั้นตอนที่สำคัญต่อไปของมาตรฐานการสื่อสารโทรคมนาคมมือถือเกินกว่ามาตรฐาน 4G/IMT-Advanced ปัจจุบัน 5G เป็นเทคโนโลยีการสื่อสารที่ยังไม่ได้มีสเปคที่แท้จริงโดยเฉพาะอย่างยิ่งในเอกสารอย่างเป็นทางการใด ๆ ที่เผยแพร่โดยร่างมาตรฐานการสื่อสารโทรคมนาคม";
//        String lang = provider.detect(msg);
//        System.out.println(langs.get(lang));
//        t = System.currentTimeMillis();
//        System.out.println(t - t0);
//
//        TranslatedText translation = provider.translate("ыатфыоатжылат фавлыатж выфалываф ывв ывафыж", new Language("vi", "Vietnamese", "en"));
//        System.out.println(langs.get(translation.getSourceLanguage()) + " translated to " + langs.get(translation.getTargetLanguage()));
//        System.out.println(translation.getText());
//        t = System.currentTimeMillis();
//        System.out.println(t - t0);
//
//        Map<Language, List<Language>> dirs = provider.getSupportedDirections("ru");
//        for (Map.Entry<Language, List<Language>> entry: dirs.entrySet()) {
//            System.out.println(entry.getKey() + " -> " + entry.getValue());
//        }
//        t = System.currentTimeMillis();
//        System.out.println(t - t0);
//    }

//    public static void main(String[] args) {
//        String text = "Le Requin gris de récif est un prédateur agile et rapide, qui se nourrit principalement de poissons osseux et de céphalopodes.";
//
//        TranslateProvider provider = new YandexTranslateProvider("trnsl.1.1.20141126T151929Z.2028746c57ef2cb5.29f3fed6a7b663d81c68ca53a58f5eb5e0077b5b");
//        Map<String, Language> langs = provider.getSupportedLanguages("ru");
//
//        TranslatedText translatedText = provider.translate(text, langs.get("ru"));
//        System.out.println(translatedText.getText());
//        System.out.println("Translated from " + langs.get(translatedText.getSourceLanguage()));
//
//    }

    public static void main(String[] args) {
        String msg = "Hello world!!!! I want to say thanks\n to you all bla-bla-bla! do you understand me? Let's play! Fucking shit!\n";
        final String[] aEach = msg.split(String.format("((?<=%1$s)|(?=%1$s))", "[\\s;|)(!?*_+=><.,:]+"));
        for (String each: aEach) {
            System.out.println(each);
        }
    }
}
