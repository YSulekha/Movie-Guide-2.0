package com.nanodegree.alse.movieguide.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aharyadi on 6/2/16.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.nanodegree.alse.movieguide.data";
    public static final String PATH_MOVIE = "favorite";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;

        public static Uri buildMovieWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovieWithMovieId(int id){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME = "favorite";

        // Column with the foreign key into the location table.
        public static final String COLUMN_MOV_KEY = "movie_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_IMG_SRC = "src";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_TITLE = "title";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "desc";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }
}
