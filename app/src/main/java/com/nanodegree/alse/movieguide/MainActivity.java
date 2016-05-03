package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //variable to store the position of spinner for Orientation change
    private int SavedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("Inside OnCreateAcivity", "Main");
        //Restore the spinner value [Orientation change]
        if (savedInstanceState != null) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SavedPosition = sharedPref.getInt("Position", 0);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Store the spinner position when activity is destroyed
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int position = sharedPref.getInt("Position", 0);
        outState.putInt("position", position);
    }

    @Override
    protected void onResume() {

        super.onResume();
        MoviedbFragment fragment = (MoviedbFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.updateMovieList();
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
        //To restore the spinner value when orientation changes
        if(SavedPosition >= 0){
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
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_key), selection);

        //Store the position to access it in OnSaveInstance - handling Orientation change
        editor.putInt("Position",position);
        editor.commit();

        //Update the grid view when the user selects different option in spinner
        MoviedbFragment fragment = (MoviedbFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.updateMovieList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
