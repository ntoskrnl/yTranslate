package com.cardiomood.ytranslate.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.cardiomood.ytranslate.tools.HistoryAwareTranslateProvider;
import com.cardiomood.ytranslate.tools.ReachabilityTest;
import com.cardiomood.ytranslate.ui.ClickableWordsHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import translate.api.yandex.translate.YandexTranslateProvider;
import translate.provider.Language;
import translate.provider.TranslatedText;

/**
 * Created by Anton Danshin on 28/11/14.
 */
public class TranslationFragment extends Fragment {

    private static final String TAG = TranslationFragment.class.getSimpleName();

    private static final String API_KEY = "trnsl.1.1.20141126T151929Z.2028746c57ef2cb5.29f3fed6a7b663d81c68ca53a58f5eb5e0077b5b";

    public static final String ARG_FROM_HISTORY = "from_history";
    public static final String ARG_SRC_LANG = "src_lang";
    public static final String ARG_TARGET_LANG = "target_lang";
    public static final String ARG_SRC_TEXT = "src_text";
    public static final String ARG_TRANSLATION = "translation";

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

    HistoryAwareTranslateProvider translateProvider;

    Map<String, Language> supportedLanguages = Collections.emptyMap();

    private Timer mTimer = new Timer("typing_timer");
    private TimerTask deferredTranslateTask = null;
    private Handler mHandler;

    public TranslationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        translateProvider = new HistoryAwareTranslateProvider(new YandexTranslateProvider(API_KEY));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().hide();
        } else {
            ((ActionBarActivity) getActivity()).getSupportActionBar().show();
        }

        if (savedInstanceState != null) {
            //Restore the fragment's state
            setSourceLanguage((Language) savedInstanceState.getParcelable("src_lang"));
            setTargetLanguage((Language) savedInstanceState.getParcelable("target_lang"));
            sourceText.setText(savedInstanceState.getString("src_text"));
            translatedText.setText(savedInstanceState.getString("translation"));
            translatedFrom.setText(savedInstanceState.getString("translated_from"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the fragment's state
        outState.putParcelable("src_lang", selectedSourceLanguage);
        outState.putParcelable("target_lang", selectedTargetLanguage);
        outState.putString("src_text", sourceText.getText().toString());
        outState.putString("translation", translatedText.getText().toString());
        outState.putString("translated_from", translatedFrom.getText().toString());
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
                checkInternetConnection();
                translate();
            }
        });

        sourceLanguageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetConnection();
                String targetLang = selectedTargetLanguage == null ? null : selectedTargetLanguage.getLanguage();
                translateProvider.getRecentSourceLanguagesAsync(targetLang, 4)
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
                checkInternetConnection();
                String srcLang = selectedSourceLanguage == null ? null : selectedSourceLanguage.getLanguage();
                translateProvider.getRecentTargetLanguagesAsync(srcLang, 4)
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
                checkInternetConnection();
                Language src = selectedSourceLanguage;
                String targetText = translatedText.getText().toString();
                setSourceLanguage(selectedTargetLanguage);
                setTargetLanguage(src);
                sourceText.setText(targetText);
                translatedText.setText(null);
                translate();
            }
        });

        sourceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    deferredTranslate();
                if (s.toString().trim().isEmpty()) {
                    clear();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLanguages(savedInstanceState);

        checkInternetConnection();
        // TODO: there is a small bug in save/restore. Revise later!
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initLanguages(@Nullable Bundle savedInstanceState) {
        String uiLanguage = Locale.getDefault().getLanguage();
        translateProvider.getSupportedLanguagesAsync(uiLanguage)
                .continueWith(new Continuation<Map<String, Language>, Object>() {
                    @Override
                    public Object then(Task<Map<String, Language>> task) throws Exception {
                        if (task.isCompleted()) {
                            supportedLanguages = task.getResult();
                            Bundle args = getArguments();
                            if (args != null && args.getBoolean(ARG_FROM_HISTORY, false)) {
                                sourceText.setText(args.getString(ARG_SRC_TEXT));
                                onTranslationReady(
                                        new TranslatedText(
                                                args.getString(ARG_SRC_LANG),
                                                args.getString(ARG_TARGET_LANG),
                                                args.getStringArrayList(ARG_TRANSLATION)
                                        ));
                                args.clear();
                            } else if (args == null || args.isEmpty()) {
                                // TODO: load last used languages from prefs
                                setTargetLanguage(new Language("en", "English", "en"));
                            }
                        } else {
                            // handle error
                            if (getActivity() != null) {
                                Log.w(TAG, "initLanguages() failed", task.getError());
                                Toast.makeText(getActivity(), "Failed to load languages. " +
                                        "Check your internet connection and restart app.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    private void clear() {
        cancelDeferredTranslate();

        translatedText.setText(null);
        translatedFrom.setText("N/A");
    }

    private void translate() {
        // cancel any pending task
        cancelDeferredTranslate();

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
//                            Toast.makeText(getActivity(), "Translation failed. Check Internet connection.",
//                                    Toast.LENGTH_SHORT).show();
                        } else if (task.isCompleted()) {
                            onTranslationReady(task.getResult());
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    private void deferredTranslate() {
        if (deferredTranslateTask != null) {
            deferredTranslateTask.cancel();
            mTimer.purge();
        }
        deferredTranslateTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDetached()) {
                            translate();
                        }
                    }
                });
            }
        };

        mTimer.schedule(deferredTranslateTask, 500);
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
        sourceLanguageView.setText(lang == null ? "Detect Language" : lang.toString());
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
        targetLanguageView.setText(lang == null ? "Detect Language" : lang.toString());
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

    /**
     * If there is a scheduled translate timer task - cancel it!.
     */
    private void cancelDeferredTranslate() {
        if (deferredTranslateTask != null) {
            deferredTranslateTask.cancel();
            deferredTranslateTask = null;
            mTimer.purge();
        }
    }

    private void checkInternetConnection() {
        new ReachabilityTest(getActivity(), "api.yandex.ru", 80, new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                // ok!
                if (translateProvider != null) {
                    translateProvider.setStrategy(HistoryAwareTranslateProvider.ONLINE_FIRST);
                }
            }

            @Override
            public void onReachabilityTestFailed() {
                if (translateProvider != null) {
                    translateProvider.setStrategy(HistoryAwareTranslateProvider.HISTORY_FIRST);
                }
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Online translation service is not available.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

}
