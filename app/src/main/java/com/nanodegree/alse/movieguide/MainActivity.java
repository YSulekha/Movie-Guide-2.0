package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        MoviedbFragment.OnClickItemListener,DetailFragment.OnChangeListener{

    //variable to store the position of spinner for orientation change
    private int SavedPosition = -1;
    boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String MOVIEFRAGMENT_TAG = "MFTAG";
    MyContentObserver ob;
    Context mContext;
    Boolean isChanged = false;
    int currPosition = -1;
    int savedDposition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if(savedInstanceState==null) {

        mContext = this;
        if (savedInstanceState == null) {
            Log.v("Inside Oncreate","hhh");
            MoviedbFragment moviedbFragment = new MoviedbFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_fragment, moviedbFragment, MOVIEFRAGMENT_TAG).commit();
        }

            //   moviedbFragment.updateMovieList();
            //     Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
            Log.v("InsideOnCreate", "MainActivity");

            //      setSupportActionBar(toolbar);
            //     getSupportActionBar().setTitle("Movie");
            if (findViewById(R.id.fragment_container) != null) {
                mTwoPane = true;
                ob = new MyContentObserver(null);
               // getSupportActionBar().se
         /*       View view = getLayoutInflater().inflate(R.layout.action_bar_style, null);
                android.support.v7.app.ActionBar.LayoutParams layout = new  android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
                getSupportActionBar().setCustomView(view, layout);*/
              //  getSupportActionBar().setCustomView(R.layout.action_bar_style);
                if(savedInstanceState==null) {
                    Log.v("Inside Oncreate","hhh");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DetailFragment(), DETAILFRAGMENT_TAG).commit();
                }
            } else
                mTwoPane = false;
      //  }
        //Restore the spinner value [Orientation change]
       if (savedInstanceState != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SavedPosition = sharedPref.getInt("Position", 0);
           MoviedbFragment fragment=(MoviedbFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
           String selection = getResources().getStringArray(R.array.pref_sort_entryValues)[SavedPosition];
      //     fragment.prevSelection=selection;
          // fragment.resultArray[0]=new JSONArray();
           savedDposition = savedInstanceState.getInt("detailPosition");
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  getContentResolver().unregisterContentObserver(ob);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("fggg", "OnResume");
      //  getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, ob);
    }

    //Save the spinner position for Orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Store the spinner position when activity is destroyed
     /* SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int position = sharedPref.getInt("Position", 0);
        outState.putInt("position", position);*/
        outState.putInt("detailPosition", currPosition);

    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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

        if(!Utility.isOnline(this)){
            spinner.setSelection(4);
        }
        //To restore the spinner value when orientation changes
        else if(SavedPosition >= 0){
            Log.v("Insideif", String.valueOf(SavedPosition));
            spinner.setSelection(SavedPosition);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

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



        //Update the grid view when the user selects different option in spinner
        MoviedbFragment moviedbFragment = (MoviedbFragment)getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
     //   if(!selection.equals(moviedbFragment.prevSelection)) {
            moviedbFragment.updateMovieList();
      //  }
        moviedbFragment.prevSelection=selection;
        Log.v("AFter","updateMovieList");
     /*   if(mTwoPane) {
            JSONArray[] resultArray = moviedbFragment.resultArray;
            Bundle b = new Bundle();
            b.putString(DetailFragment.EXTRATEXT, resultArray[0].toString());
            b.putInt(DetailFragment.POSITION, 0);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailFragment, DETAILFRAGMENT_TAG).commit();
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClickListen(String jsonStr, int position,boolean isFirst) {
        if(mTwoPane){
            Log.v("InsideOnClick", String.valueOf(isFirst));
            Bundle b = new Bundle();
            b.putString(DetailFragment.EXTRATEXT, jsonStr);
            if(this.savedDposition !=-1) {
                position=savedDposition;
                savedDposition=-1;
            }
                b.putInt(DetailFragment.POSITION, position);

            currPosition = position;
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment,DETAILFRAGMENT_TAG).commit();

        }
        else if(!isFirst){
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.EXTRATEXT, jsonStr);
            //  }
            //Sending Jsonstr to detail view to retrive ralated string values

            intent.putExtra(DetailFragment.POSITION, position);
            startActivityForResult(intent, 0);

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            Log.v("onact", "insideif");
            if (data!=null && data.hasExtra("IsChanged")) {
                Log.v("onActivityResult",Utility.getSelectionValue(this));
                if(Utility.getSelectionValue(this).equals("favorite")&&
                        data.getStringExtra("IsChanged").equals("true")){
                    Log.v("onActivityResult2", Utility.getSelectionValue(this));
                    MoviedbFragment fragment = (MoviedbFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
                    fragment.displayFavorite(this);
                  //  displayFavorite();
                    // mImageAdapter.notifyDataSetChanged();
                }
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }
    public void reloadFragment(){
        MoviedbFragment fragment = (MoviedbFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
        fragment.updateMovieList();

    }

    @Override
    public void onChangeListen() {
        MoviedbFragment fragment = (MoviedbFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
        Log.v("Inside OnChangeListen","adad");
        fragment.updateMovieList();
    }

    public class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            //  super.onChange(selfChange);
            onChange(selfChange,null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {

            Log.v("sdff", "Inside Onchange");
            if(Utility.getSelectionValue(getApplicationContext()).equals(getString(R.string.pref_sort_favorite))){
                isChanged = true;
              //  reloadFragment();
            }
         //   MoviedbFragment fragment = (MoviedbFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            //getSupportFragmentManager().beginTransaction().replace(R.id.movie_fragment,new MoviedbFragment(),MOVIEFRAGMENT_TAG).commit();

            //   super.onChange(selfChange, uri);

        }
    }
}
