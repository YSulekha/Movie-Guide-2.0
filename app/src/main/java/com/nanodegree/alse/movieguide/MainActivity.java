package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     //   Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
      //  setSupportActionBar(myToolbar);
        Log.v("Inside OnCreateAcivity", "Main");
        String title = getString(R.string.app_name);
       // this.setTitleColor(Html.fromHtml("<strong>" + "<font color='#B71C1C'>" + "<big>"+ "</big" + "</font>" + "</strong>"));
        setTitle(Html.fromHtml("<strong>" + "<font color='#B71C1C'>" + "<big>"+title+"</big" + "</font>" + "</strong>"));


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

        MenuItem item = menu.findItem(R.id.action_spinner);
        //  item.setActionView(android.widget.Spinner);
        Log.v("main", item.toString());
        //    Log.v("maindff", MenuItemCompat.getActionView(item).toString());
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        if(spinner !=null && spinner.getAdapter() ==null) {
            //Log.v("Inside Oncreate1", .toString());
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pref_sort_entries,R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                    spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            Log.v("Inside Oncreate", spinner.getAdapter().toString());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {

            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }*/
        if(id == R.id.action_spinner){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = getResources().getStringArray(R.array.pref_sort_entryValues)[position];
        Log.v("Inside selection", selection);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_key), selection);
        editor.commit();
        MoviedbFragment fragment = (MoviedbFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.updateMovieList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
