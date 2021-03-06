package com.cardiomood.ytranslate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cardiomood.ytranslate.db.entity.TranslationHistoryEntity;
import com.cardiomood.ytranslate.fragments.HistoryFragment;
import com.cardiomood.ytranslate.fragments.TranslationFragment;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, HistoryFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    private boolean suppressBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            Fragment translateFragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, "translateFragment");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, translateFragment)
                    .commit();
            PlaceholderFragment.translationFragment = translateFragment;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment translateFragment = getSupportFragmentManager().findFragmentByTag("content_fragment");
        //Save the fragment's instance
        if (translateFragment instanceof TranslationFragment) {
            getSupportFragmentManager().putFragment(outState, "translateFragment", translateFragment);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, boolean fromSavedState) {
        // update the main content by replacing fragments
        if (!fromSavedState) {
            Fragment fragment = PlaceholderFragment.newInstance(position + 1);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, "content_fragment")
                    .commit();
            updateTitle(position + 1);
        }
    }

    public void updateTitle(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mNavigationDrawerFragment.selectItem(4, false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHistoryItemSelected(TranslationHistoryEntity historyItem) {
        Bundle args = new Bundle();
        args.putBoolean(TranslationFragment.ARG_FROM_HISTORY, true);
        args.putString(TranslationFragment.ARG_SRC_LANG, historyItem.getSourceLang());
        args.putString(TranslationFragment.ARG_TARGET_LANG, historyItem.getTargetLang());
        args.putString(TranslationFragment.ARG_SRC_TEXT, historyItem.getSourceText());
        args.putStringArrayList(TranslationFragment.ARG_TRANSLATION, new ArrayList<>(Arrays.asList(historyItem.getTranslation())));
        PlaceholderFragment.translationFragment.setArguments(args);

        mNavigationDrawerFragment.selectItem(0, false);
    }

    @Override
    public void onBackPressed() {
        if (!suppressBackButton) {
            super.onBackPressed();
        }
    }

    public void suppressBackButton(boolean suppress) {
        suppressBackButton = suppress;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private static Fragment translationFragment = null;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            if (sectionNumber == 1) {
                if (translationFragment == null) {
                    translationFragment = new TranslationFragment();
                }
                return translationFragment;
            }
            if (sectionNumber == 2) {
                return HistoryFragment.newInstance(true);
            }
            if (sectionNumber == 3) {
                return HistoryFragment.newInstance(false);
            }
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

    }

}
