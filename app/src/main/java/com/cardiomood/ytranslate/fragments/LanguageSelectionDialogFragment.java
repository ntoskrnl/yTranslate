package com.cardiomood.ytranslate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.cardiomood.ytranslate.R;
import com.cardiomood.ytranslate.provider.Language;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Anton Danshin on 01/12/14.
 */
public class LanguageSelectionDialogFragment extends DialogFragment {

    private static final String TAG = LanguageSelectionDialogFragment.class.getSimpleName();

    @InjectView(R.id.favorite_langs_block)
    LinearLayout favoriteLangsBlock;
    @InjectView(R.id.favorite_langs)
    LinearLayout favoriteLangsListView;

    @InjectView(R.id.other_langs_block)
    LinearLayout otherLangsBlock;
    @InjectView(R.id.other_langs)
    LinearLayout otherLangsListView;

    private List<ItemHolder> favoriteLanguages = new ArrayList<>();
    private List<ItemHolder> otherLanguages = new ArrayList<>();

    private LanguageListAdapter favoriteAdapter;
    private LanguageListAdapter otherAdapter;

    private Callback callback;

    public LanguageSelectionDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_language_selection, null);
        inflater = getActivity().getLayoutInflater();

        // inject views
        ButterKnife.inject(this, dialogView);

        favoriteAdapter = new LanguageListAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, favoriteLanguages);
        otherAdapter = new LanguageListAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, otherLanguages);

        getDialog().setTitle("Select language");

        updateLists();

        return dialogView;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public void setFavoriteLanguages(List<Language> langs) {
        favoriteLanguages.clear();
        for (Language lang: langs) {
            favoriteLanguages.add(new ItemHolder(lang));
        }
        updateLists();
    }

    public void setOtherLanguages(List<Language> langs) {
        otherLanguages.clear();
        for (Language lang: langs) {
            otherLanguages.add(new ItemHolder(lang));
        }
        updateLists();
    }

    private void updateLists() {
        if (favoriteLangsListView != null) {
            favoriteLangsListView.removeAllViews();
            for (int i=0; i<favoriteAdapter.getCount(); i++) {
                View itemView = favoriteAdapter.getView(i, null, favoriteLangsListView);
                favoriteLangsListView.addView(itemView);
                final Language lang = favoriteAdapter.getItem(i).getItem();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLanguageSelected(lang);
                        getDialog().dismiss();
                    }
                });
            }
            if (favoriteAdapter.getCount() > 0) {
                favoriteLangsBlock.setVisibility(View.VISIBLE);
            } else {
                favoriteLangsBlock.setVisibility(View.GONE);
            }
        }

        if (otherLangsListView != null) {
            otherLangsListView.removeAllViews();
            for (int i = 0; i < otherAdapter.getCount(); i++) {
                View itemView = otherAdapter.getView(i, null, otherLangsListView);
                otherLangsListView.addView(itemView);
                final Language lang = otherAdapter.getItem(i).getItem();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLanguageSelected(lang);
                        getDialog().dismiss();
                    }
                });
            }
            if (otherAdapter.getCount() > 0) {
                otherLangsBlock.setVisibility(View.VISIBLE);
            } else {
                otherLangsBlock.setVisibility(View.GONE);
            }
        }
    }

    private void onLanguageSelected(Language lang) {
        if (callback != null) {
            callback.onLanguageSelected(lang);
        }
    }

    private static class LanguageListAdapter extends ArrayAdapter<ItemHolder> {

        public LanguageListAdapter(Context context, int resource, int textViewResourceId, List<ItemHolder> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            v.setBackgroundResource(android.R.drawable.list_selector_background);
            v.setClickable(true);
            return v;
        }
    }

    private static class ItemHolder implements Comparable<ItemHolder> {

        private Language item;

        private ItemHolder(Language item) {
            this.item = item;
        }

        public Language getItem() {
            return item;
        }

        @Override
        public String toString() {
            return item == null ? "Detect" : item.getName();
        }

        @Override
        public int compareTo(ItemHolder another) {
            if (item ==null)
                return 1;
            if (another.item == null)
                return -1;
            return toString().compareToIgnoreCase(another.toString());
        }
    }

    public static interface Callback {

        void onLanguageSelected(Language lang);

    }

}
