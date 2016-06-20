package com.nanodegree.alse.movieguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class DetailActivity extends AppCompatActivity {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

      //          getWindow().setStatusBarColor(Color.TRANSPARENT);
     getWindow().getDecorView().setSystemUiVisibility(
             View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                     | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);





        if(savedInstanceState==null) {
            Bundle bundle = new Bundle();
            Intent intent = getIntent();
            bundle.putString(FragmentDetail.EXTRATEXT, intent.getStringExtra(FragmentDetail.EXTRATEXT));
            bundle.putInt(FragmentDetail.POSITION, intent.getIntExtra(FragmentDetail.POSITION, 0));
            Log.v("DetailActivity",intent.toString());
            FragmentDetail fragment = new FragmentDetail();

            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v("Inside","Restore");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("Inside","OnSave");
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
