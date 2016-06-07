package com.nanodegree.alse.movieguide.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by aharyadi on 6/3/16.
 */
public class MovieProvider extends ContentProvider {

    MovieDBHelper mMovieDBHelper;

    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_MOVIEID = 101;
    public static final UriMatcher mUriMatcher = getMatcher();


    public static UriMatcher getMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIE,MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIE+"/#",MOVIE_WITH_MOVIEID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();
        int match = mUriMatcher.match(uri);
        switch (match){
            case MOVIE:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_WITH_MOVIEID:
                String s = MovieContract.MovieEntry.COLUMN_MOV_KEY + "=?";
                String[] sArgs = new String[]{MovieContract.MovieEntry.getMovieIdFromUri(uri)};
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, s, sArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_MOVIEID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = mUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIE:
                SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if(id>=0) {
                    returnUri = MovieContract.MovieEntry.buildMovieWithId(id);
                }
                else{
                    throw new android.database.SQLException("Failed to insert row into " + uri+id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        int returnValue;
        switch (match) {
            case MOVIE:
                SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
                returnValue = db.delete(MovieContract.MovieEntry.TABLE_NAME,selection,selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnValue;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        int returnValue;
        switch (match) {
            case MOVIE:
                SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
                returnValue = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnValue;
    }
    @Override
    @TargetApi(11)
    public void shutdown() {
        mMovieDBHelper.close();
        super.shutdown();
    }
}
