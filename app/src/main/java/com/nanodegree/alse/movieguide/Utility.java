package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Created by aharyadi on 6/4/16.
 */
public class Utility {

    public static String getSelectionValue(Context context){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_popular));
    }

    public static Cursor queryFavoriteTable(Context context,Uri uri){
        return context.getContentResolver().query(uri,null,null,null,null);
    }
}
