package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;


public class ActivityMain extends AppCompatActivity implements AdapterView.OnItemSelectedListener,MoviedbFragment.OnClickItemListener,
       DetailFragment.OnChangeListener{

    //variable to store the position of spinner for orientation change
    private int SavedSpinnerPosition = -1;
    boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String MOVIEFRAGMENT_TAG = "MFTAG";
    Context mContext;
    int currPosition = -1;
    int savedDetailposition = -1;
    boolean saved = false;
    private String[] mSelectionTitles;

    private ListView mDrawerList;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mSelectionTitles = getResources().getStringArray(R.array.pref_sort_entries);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                null, R.string.app_name, R.string.hello_blank_fragment) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
              //  getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
               // getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.spinner_item, mSelectionTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        if (savedInstanceState == null) {
            Log.v("MainActivity","Saved+null");
            MoviedbFragment moviedbFragment = new MoviedbFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_fragment, moviedbFragment, MOVIEFRAGMENT_TAG).commit();
        }
        //Determine if two pane or single pane layout
        if (findViewById(R.id.fragment_container) != null) {
            mTwoPane = true;
            if(savedInstanceState==null) {
                Log.v("MainActivity","Saved+null");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentDetail(), DETAILFRAGMENT_TAG).commit();
            }
        }
        else
            mTwoPane = false;
        //Restore the spinner value [Orientation change]
        if (savedInstanceState != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SavedSpinnerPosition = sharedPref.getInt("Position", 0);
            Log.v("Spinner", String.valueOf(SavedSpinnerPosition));
            MoviedbFragment fragment=(MoviedbFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            savedDetailposition = savedInstanceState.getInt("detailPosition");
            Log.v("Saved", String.valueOf(savedDetailposition));
            saved = true;


        }
    }
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        return super.onPrepareOptionsMenu(menu);
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        String selection = getResources().getStringArray(R.array.pref_sort_entryValues)[position];
        Log.v("selectItem",selection+position);


        //Store the preference to file to access it fragment
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_key), selection);

        //Store the position to access it in OnSaveInstance - handling Orientation change
        editor.putInt("Position", position);
        editor.commit();
        Log.v("MainActivity","OnItemSelected");

        //Update the grid view when the user selects different option in spinner
        MoviedbFragment moviedbFragment = (MoviedbFragment)getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
        if(moviedbFragment.prevSelection!=null) {
            Log.v("OnItemSelected", moviedbFragment.prevSelection);
        }

        moviedbFragment.updateMovieList();


        // Insert the fragment by replacing any existing fragment

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title.toString();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Save the spinner position for Orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("OnSaveInstance","Main");
        //Save the detail fragment position for Orientation change
        outState.putInt("detailPosition", currPosition);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     /*   getMenuInflater().inflate(R.menu.menu_main, menu);

        //Add Spinner component to Action bar to provide the user choice change the category
        MenuItem item = menu.findItem(R.id.action_spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        //set the adapter to spinner to show the list of values
        if(spinner !=null){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pref_sort_entries,R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }
        Log.v("onCreateOptionsMenu","ddd");
        //If the device is not online then set display favorite by setting the spinner
        //position to favorites
        if(!Utility.isOnline(this)){
            spinner.setSelection(4);
        }
        //To restore the spinner value when orientation changes
        else if(SavedSpinnerPosition >= 0){
            spinner.setSelection(SavedSpinnerPosition);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = getResources().getStringArray(R.array.pref_sort_entryValues)[position];


        //Store the preference to file to access it fragment
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_key), selection);

        //Store the position to access it in OnSaveInstance - handling Orientation change
        editor.putInt("Position", position);
        editor.commit();
        Log.v("MainActivity","OnItemSelected");

        //Update the grid view when the user selects different option in spinner
        MoviedbFragment moviedbFragment = (MoviedbFragment)getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
        if(moviedbFragment.prevSelection!=null) {
            Log.v("OnItemSelected", moviedbFragment.prevSelection);
        }

        moviedbFragment.updateMovieList();

        //store the selection value
        moviedbFragment.prevSelection=selection;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClickListen(int position,boolean isFirst,JSONArray[] resultArray) {
        String selection = Utility.getSelectionValue(this);
        String jsonStr;
        int acPosition;
        int k=0;
        if (selection.equals(getString(R.string.pref_sort_favorite))) {
            jsonStr = null;
            acPosition = position;
        }
        //else retrive the value from JSON
        else {
            if (position >= resultArray[0].length()) {
                position = position % resultArray[0].length();
                k++;
            }
            jsonStr = resultArray[k].toString();
            acPosition= position;

        }
        if(mTwoPane){
            Bundle b = new Bundle();
            b.putString(DetailFragment.EXTRATEXT, jsonStr);
            if(saved) {
                position=savedDetailposition;
                Log.v("MainActivity12",String.valueOf(position));
                saved=false;
            }
            //  else {
            b.putInt(FragmentDetail.POSITION, acPosition);
            //Save the current position of detail fragment for Orientation change
            currPosition = acPosition;
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, DETAILFRAGMENT_TAG).commit();
            //   }

        }
        else if(!isFirst){
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.EXTRATEXT, jsonStr);
            intent.putExtra(DetailFragment.POSITION, acPosition);
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