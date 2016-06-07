package com.nanodegree.alse.movieguide.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aharyadi on 6/2/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    static final String DB_NAME = "movie.db";



    public MovieDBHelper(Context context) {
        super(context, DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE = "CREATE TABLE "+MovieContract.MovieEntry.TABLE_NAME+"( "+
                MovieContract.MovieEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        MovieContract.MovieEntry.COLUMN_IMG_SRC+ " TEXT,"+
                        MovieContract.MovieEntry.COLUMN_MOV_KEY+ " INTEGER UNIQUE NOT NULL,"+
                        MovieContract.MovieEntry.COLUMN_RATING+ " REAL,"+
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE+" TEXT,"+
                        MovieContract.MovieEntry.COLUMN_TITLE+" TEXT NOT NULL,"+
                        MovieContract.MovieEntry.COLUMN_SHORT_DESC+" TEXT "+
                ");";
        db.execSQL(SQL_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);

    }
}
