package com.cardiomood.ytranslate.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cardiomood.ytranslate.R;
import com.cardiomood.ytranslate.provider.Language;
import com.cardiomood.ytranslate.provider.TranslateProvider;
import com.cardiomood.ytranslate.provider.TranslatedText;
import com.cardiomood.ytranslate.provider.YandexTranslateProvider;
import com.cardiomood.ytranslate.tools.HistoryAwareTranslateProvider;
import com.cardiomood.ytranslate.tools.TranslationHistoryHelper;
import com.cardiomood.ytranslate.ui.ClickableWordsHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Anton Danshin on 28/11/14.
 */
public class TranslationFragment extends Fragment {

    private static final String TAG = TranslationFragment.class.getSimpleName();

    private static final String API_KEY = "trnsl.1.1.20141126T151929Z.2028746c57ef2cb5.29f3fed6a7b663d81c68ca53a58f5eb5e0077b5b";

    @InjectView(R.id.src_lang)
    TextView sourceLanguageView;
    @InjectView(R.id.dst_lang)
    TextView targetLanguageView;
    @InjectView(R.id.translate_button)
    Button translateButton;
    @InjectView(R.id.swap_button)
    Button swapButton;
    @InjectView(R.id.src_text)
    EditText sourceText;
    @InjectView(R.id.translated_text)
    TextView translatedText;
    @InjectView(R.id.translated_from)
    TextView translatedFrom;

    ClickableWordsHelper wordClickHelper;

    Language selectedSourceLanguage;
    Language selectedTargetLanguage;

    TranslateProvider translateProvider = new HistoryAwareTranslateProvider(new YandexTranslateProvider(API_KEY));
    TranslationHistoryHelper historyHelper;

    Map<String, Language> supportedLanguages = Collections.emptyMap();
    Map<Language, List<Language>> supportedDirections = Collections.emptyMap();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().hide();
        } else {
            ((ActionBarActivity) getActivity()).getSupportActionBar().show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

        ButterKnife.inject(this, view);

        wordClickHelper = new ClickableWordsHelper(translatedText);
        wordClickHelper.setCallback(new ClickableWordsHelper.Callback() {
            @Override
            public void onWordClicked(View widget, String word) {
                if (selectedSourceLanguage != null) {
                    sourceText.setText(word);
                    Language target = selectedTargetLanguage;
                    setTargetLanguage(selectedSourceLanguage);
                    setSourceLanguage(target);
                    translate();
                }
            }

            @Override
            public void onWordTouchEvent(View widget, String word, MotionEvent event) {

            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate();
            }
        });

        sourceLanguageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetLang = selectedTargetLanguage == null ? null : selectedTargetLanguage.getLanguage();
                if (historyHelper == null) {
                    return;
                }
                historyHelper.getRecentSourceLanguagesAsync(targetLang, 4)
                        .continueWith(new Continuation<Map<String, Date>, Object>() {
                            @Override
                            public Object then(Task<Map<String, Date>> task) throws Exception {
                                FragmentManager fm = getChildFragmentManager();
                                LanguageSelectionDialogFragment fragment = new LanguageSelectionDialogFragment();
                                fragment.setCallback(new LanguageSelectionDialogFragment.Callback() {
                                    @Override
                                    public void onLanguageSelected(Language lang) {
                                        setSourceLanguage(lang);
                                    }
                                });

                                List<Language> favoriteLangs = new ArrayList<>();
                                favoriteLangs.add(null);
                                if (task.getResult() != null) {
                                    Map<String, Date> result = task.getResult();
                                    for (String lang: result.keySet()) {
                                        if (supportedLanguages.containsKey(lang)) {
                                            favoriteLangs.add(supportedLanguages.get(lang));
                                        }
                                    }
                                }
                                fragment.setFavoriteLanguages(favoriteLangs);

                                List<Language> otherLanguages = new ArrayList<>(supportedLanguages.values());
                                Collections.sort(otherLanguages);
                                fragment.setOtherLanguages(otherLanguages);
                                fragment.show(fm, "fragment_select_src_lang");
                                return null;
                            }
                        }, Task.UI_THREAD_EXECUTOR);
            }
        });

        targetLanguageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String srcLang = selectedSourceLanguage == null ? null : selectedSourceLanguage.getLanguage();
                if (historyHelper == null) {
                    return;
                }
                historyHelper.getRecentTargetLanguagesAsync(srcLang, 4)
                        .continueWith(new Continuation<Map<String, Date>, Object>() {
                            @Override
                            public Object then(Task<Map<String, Date>> task) throws Exception {
                                FragmentManager fm = getChildFragmentManager();
                                LanguageSelectionDialogFragment fragment = new LanguageSelectionDialogFragment();
                                fragment.setCallback(new LanguageSelectionDialogFragment.Callback() {
                                    @Override
                                    public void onLanguageSelected(Language lang) {
                                        setTargetLanguage(lang);
                                    }
                                });
                                if (task.getResult() != null) {
                                    Map<String, Date> result = task.getResult();
                                    List<Language> favoriteLangs = new ArrayList<>(result.size());
                                    for (String lang: result.keySet()) {
                                        if (supportedLanguages.containsKey(lang)) {
                                            favoriteLangs.add(supportedLanguages.get(lang));
                                        }
                                    }
                                    fragment.setFavoriteLanguages(favoriteLangs);
                                }
                                List<Language> otherLanguages = new ArrayList<>(supportedLanguages.values());
                                Collections.sort(otherLanguages);
                                fragment.setOtherLanguages(otherLanguages);
                                fragment.show(fm, "fragment_select_dst_lang");
                                return null;
                            }
                        }, Task.UI_THREAD_EXECUTOR);
                    }
                });

        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Language src = selectedSourceLanguage;
                String targetText = translatedText.getText().toString();
                setSourceLanguage(selectedTargetLanguage);
                setTargetLanguage(src);
                sourceText.setText(targetText);
                translatedText.setText(null);
                translate();
            }
        });

        setTargetLanguage(new Language("en", "English", "en"));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLanguages();

        // TODO: decouple from history helper!
        try {
            historyHelper = new TranslationHistoryHelper();
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "Failed to initialize translation history", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "onViewCreated(): failed to create TranslationHistoryHelper", ex);
        }

        // TODO: implement save/restore instance state to provide better UX
    }

    private void initLanguages() {
        Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
        String uiLanguage = Locale.getDefault().getLanguage();
        translateProvider.getSupportedLanguagesAsync(Locale.getDefault().getLanguage())
                .continueWith(new Continuation<Map<String, Language>, Object>() {
                    @Override
                    public Object then(Task<Map<String, Language>> task) throws Exception {
                        if (task.isCompleted()) {
                            supportedLanguages = task.getResult();
                        } else {
                            // handle error
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
        translateProvider.getSupportedDirectionsAsync(uiLanguage)
                .continueWith(new Continuation<Map<Language, List<Language>>, Object>() {
                    @Override
                    public Object then(Task<Map<Language, List<Language>>> task) throws Exception {
                        if (task.isCompleted()) {
                            onSupportedDirectionsLoaded(task.getResult());
                        } else {
                            // handle error
                            Log.w(TAG, "getSupportedDirectionsAsync() failed", task.getError());
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    private void onSupportedDirectionsLoaded(Map<Language, List<Language>> directions) {
        supportedDirections = directions;

    }

    private void translate() {
        final String text = sourceText.getText().toString().trim();
        final Language srcLang = selectedSourceLanguage;
        final Language targetLang = selectedTargetLanguage;

        if (text.isEmpty() || targetLang == null)
            return;

        translateButton.setEnabled(false);
        translateProvider.translateAsync(text, targetLang, srcLang)
                .continueWith(new Continuation<TranslatedText, Object>() {
                    @Override
                    public Object then(Task<TranslatedText> task) throws Exception {
                        translateButton.setEnabled(true);
                        if (task.isFaulted()) {
                            // handle error
                            Log.e(TAG, "translate() failed with exception", task.getError());
                            Toast.makeText(getActivity(), "Translation failed. Check Internet connection.",
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.isCompleted()) {
                            onTranslationReady(task.getResult());
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    private void onTranslationReady(TranslatedText text) {
        StringBuilder sb = new StringBuilder();
        for (String translation : text.getText()) {
            sb.append(translation).append("\n");
        }
        wordClickHelper.setText(sb.toString().trim());

        Language lang = supportedLanguages.get(text.getSourceLanguage());
        if (lang != null) {
            translatedFrom.setText(lang.getName());
            setSourceLanguage(lang);
        } else {
            translatedFrom.setText("N/A");
        }

        lang = supportedLanguages.get(text.getTargetLanguage());
        if (lang != null) {
            setTargetLanguage(lang);
        }
    }

    private void setSourceLanguage(Language lang) {
        if (selectedSourceLanguage == null && lang == null) {
            // nothing changed
            return;
        }
        if (selectedSourceLanguage != null && selectedSourceLanguage.equals(lang)) {
            // nothing changed
            return;
        }

        // save previous value
        Language srcLang = selectedSourceLanguage;

        // update source language value
        selectedSourceLanguage = lang;
        sourceLanguageView.setText(lang == null ? "Detect Language" : lang.toString());

        // swap languages if necessary
        if (lang != null && srcLang != null && lang.equals(selectedTargetLanguage)) {
            setTargetLanguage(srcLang);
        }

        if (selectedSourceLanguage == null || selectedSourceLanguage.equals(selectedTargetLanguage)) {
            // make swap languages disabled
            swapButton.setEnabled(false);
        } else {
            swapButton.setEnabled(true);
        }
    }

    private void setTargetLanguage(Language lang) {
        if (lang == null) {
            return;
        }

        if (lang.equals(selectedTargetLanguage)) {
            // nothing changed
            return;
        }

        // save previous value
        Language targetLang = selectedTargetLanguage;

        // update target language
        selectedTargetLanguage = lang;
        targetLanguageView.setText(lang == null ? "Detect Language" : lang.toString());

        // swap languages if necessary
        if (lang != null && lang.equals(selectedSourceLanguage)) {
            setSourceLanguage(targetLang);
        }

        if (selectedSourceLanguage == null || selectedTargetLanguage.equals(selectedSourceLanguage)) {
            // make swap languages disabled
            swapButton.setEnabled(false);
        } else {
            swapButton.setEnabled(true);
        }
    }

}
