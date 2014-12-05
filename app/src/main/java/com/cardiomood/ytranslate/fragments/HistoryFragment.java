package com.cardiomood.ytranslate.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cardiomood.ytranslate.R;
import com.cardiomood.ytranslate.db.DatabaseHelperFactory;
import com.cardiomood.ytranslate.db.entity.TranslationHistoryEntity;
import com.cardiomood.ytranslate.ui.TouchEffect;
import com.cardiomood.ytranslate.ui.TranslationHistoryAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class HistoryFragment extends Fragment
        implements AbsListView.OnItemClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    public static final String ARG_FAVORITES = "favorites";

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private TranslationHistoryAdapter mAdapter;

    private boolean favorites = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        favorites = args.getBoolean(ARG_FAVORITES, false);

        // load list from the database
        try {
            ArrayAdapter<TranslationHistoryEntity> arrayAdapter = new HistoryArrayAdapter(
                    getActivity(),
                    new ArrayList<TranslationHistoryEntity>(1000)
            );
            mAdapter = new TranslationHistoryAdapter(
                    getActivity(),
                    arrayAdapter,
                    favorites
            );
        } catch (SQLException ex) {
            Log.e(TAG, "onCreate(): failed to create endless adapter", ex);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        if (mAdapter.getCount() == 0) {
            setEmptyText("Nothing to show");
        } else {
            setEmptyText(null);
        }


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_history, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(getActivity());
        MenuItemCompat.setActionView(menuItem, searchView);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(Color.WHITE);
        txtSearch.setHint("Search in history");

        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();

        // always show action bar
        ((ActionBarActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onHistoryItemSelected(mAdapter.getHistoryItem(position));
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mAdapter.getFilter().filter(s);
        return false;
    }

    public static Fragment newInstance(boolean favorites) {
        Fragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_FAVORITES, favorites);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        public void onHistoryItemSelected(TranslationHistoryEntity historyItem);

    }

    private class HistoryArrayAdapter extends ArrayAdapter<TranslationHistoryEntity> {

        public HistoryArrayAdapter(Context context, List<TranslationHistoryEntity> objects) {
            super(context, R.layout.history_item, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.history_item, parent, false);
            }

            TextView srcText = (TextView) view.findViewById(R.id.src_text);
            TextView translatedText = (TextView) view.findViewById(R.id.translated_text);
            TextView srcLang = (TextView) view.findViewById(R.id.src_lang);
            TextView targetLang = (TextView) view.findViewById(R.id.target_lang);
            ImageButton favButton = (ImageButton) view.findViewById(R.id.favorite_button);

            final TranslationHistoryEntity historyItem = getItem(position);
            srcText.setText(historyItem.getSourceText());
            translatedText.setText(historyItem.getTranslation());
            srcLang.setText(capitalizeString(historyItem.getSourceLang()));
            targetLang.setText(capitalizeString(historyItem.getTargetLang()));

            favButton.setOnTouchListener(TouchEffect.FADE_ON_TOUCH);
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavButtonClicked(historyItem);
                }
            });

            if (historyItem.isFavorite()) {
                favButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
            } else {
                favButton.setBackgroundResource(android.R.drawable.btn_star_big_off);
            }

            return view;
        }

        private void onFavButtonClicked(final TranslationHistoryEntity historyItem) {
            Task.callInBackground(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    historyItem.setFavorite(!historyItem.getFavorite());
                    DatabaseHelperFactory.getHelper()
                            .getTranslationHistoryDao()
                            .update(historyItem);
                    return historyItem;
                }
            }).continueWith(new Continuation<Object, Object>() {
                @Override
                public Object then(Task<Object> task) throws Exception {
                    if (task.isFaulted()) {
                        Toast.makeText(getContext(), "Can't update. Try to refresh the list.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFavButtonClicked() failed", task.getError());
                    } else if (task.isCompleted()) {
//                        if (favorites) {
//                            mAdapter.refresh();
//                        } else {
                            mListView.invalidateViews();
//                        }
                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);
        }

        String capitalizeString(String s) {
            if (s == null) {
                return null;
            }
            return s.substring(0,1).toUpperCase() + s.substring(1);
        }
    }

}
