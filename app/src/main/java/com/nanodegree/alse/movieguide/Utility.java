package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

public class Utility {

    public static String getSelectionValue(Context context){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_popular));
    }

    public static Cursor queryFavoriteTable(Context context,Uri uri){
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    //Function to check if the user is connected to network
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
