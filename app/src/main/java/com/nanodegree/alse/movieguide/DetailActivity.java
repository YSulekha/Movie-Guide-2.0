package com.nanodegree.alse.movieguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by aharyadi on 6/7/16.
 */
public class DetailActivity extends AppCompatActivity {
    private static final String DETAILFRAGMENT_TAG = "DFTAG1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

   /*     getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        Log.v("InsideDetailActivity", "a124");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




     //   android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()). getSupportActionBar();
     //   if (savedInstanceState == null) {

         //   Bundle bundle = new Bundle();
         //   bundle.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            Log.v("InsideDetailActivity","a123");
            Bundle bundle = new Bundle();
            Intent intent = getIntent();
            bundle.putString(DetailFragment.EXTRATEXT,intent.getStringExtra(DetailFragment.EXTRATEXT));
            bundle.putInt(DetailFragment.POSITION, intent.getIntExtra(DetailFragment.POSITION, 0));
            Log.v("DetailIntent", String.valueOf(intent.getIntExtra(DetailFragment.POSITION, 0)));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment,DETAILFRAGMENT_TAG)
                    .commit();
      //  }


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
