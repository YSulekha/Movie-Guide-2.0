package com.nanodegree.alse.movieguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;

import java.util.Arrays;


public class MainActivityDrawer extends AppCompatActivity implements MoviedbFragment.OnClickItemListener,
        FragmentDetail.OnChangeListener{

    DrawerLayout mDrawer;
    NavigationView mNavView;
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawToggle;
    private int SavedSpinnerPosition = -1;
    boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String MOVIEFRAGMENT_TAG = "MFTAG";
    int currPosition = -1;
    int savedDetailposition = -1;
    boolean saved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_drawer);

        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(mDrawToggle);
        mNavView = (NavigationView)findViewById(R.id.drawer_navig);
        setupNavigationDrawerContent(mNavView);

        if (savedInstanceState == null) {
            MoviedbFragment moviedbFragment = new MoviedbFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, moviedbFragment, MOVIEFRAGMENT_TAG).commit();
            //Check if internet connection is present
            if(!Utility.isOnline(this)){
                //If there is no internet,then show favorites
                mNavView.getMenu().getItem(4).setChecked(true);
                selectDrawerItem(mNavView.getMenu().getItem(4));
            }
            else {
                //else show popular movies
                mNavView.getMenu().getItem(0).setChecked(true);
                selectDrawerItem(mNavView.getMenu().getItem(0));

            }
        }
        else{
            //Handle Orientation Change
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SavedSpinnerPosition = sharedPref.getInt("Position", 0);
            selectDrawerItem(mNavView.getMenu().getItem(SavedSpinnerPosition));
            savedDetailposition = savedInstanceState.getInt("detailPosition");
            saved = true;
        }
        //Check to see whether it is a single pane or two pane layout
        if (findViewById(R.id.fragment_container) != null) {
            mTwoPane = true;
            if(savedInstanceState==null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentDetail(), DETAILFRAGMENT_TAG).commit();
            }
        }
        else
            mTwoPane = false;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the detail fragment position for Orientation change
        outState.putInt("detailPosition", currPosition);
    }

    public void setupNavigationDrawerContent(NavigationView navigView){
        navigView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        selectDrawerItem(item);
                        updateList();
                        return true;
                    }
                }
        );
    }
    private ActionBarDrawerToggle setupDrawerToggle(){
        return new ActionBarDrawerToggle(this,mDrawer,toolbar,R.string.drawer_open,R.string.drawer_close);
    }

    public void selectDrawerItem(MenuItem item){
        int position = Arrays.asList(getResources().getStringArray(R.array.pref_sort_entries)).indexOf(item.getTitle());
        String selection = getResources().getStringArray(R.array.pref_sort_entryValues)[position];


        //Store the preference to file to access it fragment
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_key), selection);

        //Store the position to access it in OnSaveInstance - handling Orientation change
        editor.putInt("Position", position);
        editor.commit();

        //Update the grid view when the user selects different option in spinner

        item.setChecked(true);
        setTitle(item.getTitle());
        mDrawer.closeDrawers();
    }

    public void updateList(){
        MoviedbFragment moviedbFragment = (MoviedbFragment)getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
        moviedbFragment.updateMovieList();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public void onClickListen(int position,boolean isFirst,JSONArray[] resultArray) {
        String selection = Utility.getSelectionValue(this);
        String jsonStr;
        int acPosition = position;
        int k=0;

        //If it is two pane then after orientation change retrieve the saved position
        if(mTwoPane){
            if(saved) {
                position = savedDetailposition;
                acPosition = position;
                Log.v("OnClickListen", String.valueOf(position));
                saved=false;
            }
        }
        if (selection.equals(getString(R.string.pref_sort_favorite))) {
            jsonStr = null;
        }
        //else retrive the value from JSON
        else {
            if (position >= resultArray[0].length()) {
                position = position % resultArray[0].length();
                k++;
            }
            jsonStr = resultArray[k].toString();
           // acPosition= position;

        }
        //If two pane send the bundle
        if(mTwoPane){
            Bundle b = new Bundle();
            b.putString(FragmentDetail.EXTRATEXT, jsonStr);
            b.putInt(FragmentDetail.POSITION, position);
            b.putBoolean(FragmentDetail.FLAG, true);
            //Save the current position of detail fragment for Orientation change
            currPosition = acPosition;
            FragmentDetail fragment = new FragmentDetail();
            fragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, DETAILFRAGMENT_TAG).commit();

        }
        //else intent to detail activity
        else if(!isFirst){
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(FragmentDetail.EXTRATEXT, jsonStr);
            intent.putExtra(FragmentDetail.POSITION, position);
            startActivityForResult(intent, 0);

        }
    }
    //This callback is for displaying updated favorite result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            if (data!=null && data.hasExtra("IsChanged")) {

                if(Utility.getSelectionValue(this).equals("favorite")&&
                        data.getStringExtra("IsChanged").equals("true")){
                    MoviedbFragment fragment = (MoviedbFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
                    fragment.displayFavorite(this);
                }
            }
        }
    }

    //This callback is to update the favorite list in two pane layout
    @Override
    public void onChangeListen() {
        MoviedbFragment fragment = (MoviedbFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
        fragment.updateMovieList();
    }


}
