package com.nanodegree.alse.movieguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.nanodegree.alse.movieguide.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviedbFragment extends Fragment {

    ImageAdapter mImageAdapter;
    Cursor cursor;
    OnClickItemListener mListener;
    boolean isFirst = false;
    String prevSelection;
    int aPosition;
    TextView memptyView;

    JSONArray [] resultArray = new JSONArray[2]; //Array to store the result of two pages retrieved from MovieDb API
    final String LOGTAG = MoviedbFragment.class.getSimpleName();

    public MoviedbFragment() {
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public interface OnClickItemListener{
        public void onClickListen(int Position,boolean isFirst,JSONArray [] resultArray);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        View rootView =  inflater.inflate(R.layout.main_fragment, container, false);
            Log.v("Inside","MovieDBFragment");
           // mImageAdapter = new ImageAdapter(getActivity(), R.layout.grid_view_movie);
        mImageAdapter = new ImageAdapter(getActivity(), R.layout.staggered_imageview);
        StaggeredGridView gridView = (StaggeredGridView) rootView.findViewById(R.id.grid_view_movie);
      //  GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movie);
     //   gridView.setColumnCountLandscape(2);
       // gridView.setColumnCountPortrait(2);
       // gridView.setColumnCount(2);


        
            memptyView = (TextView) rootView.findViewById(R.id.listview_emptyView);
            gridView.setAdapter(mImageAdapter);
            gridView.setEmptyView(memptyView);
            //On click open detail Activity layout
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String selection = Utility.getSelectionValue(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    String value = (String) parent.getItemAtPosition(position);
                    int k = 0;
                    aPosition = position;

                    //Decide from which array the value should be retrieved
                    //If the spinner value is favorite,retrieve the value from database
                    if (selection.equals(getString(R.string.pref_sort_favorite))) {
                        intent.putExtra(DetailFragment.EXTRATEXT, "null");
                        intent.putExtra(DetailFragment.POSITION, position);
                        mListener.onClickListen(position, isFirst,null);
                    }
                    //else retrive the value from JSON
                    else {
                     /*   if (position >= resultArray[0].length()) {
                            position = position % resultArray[0].length();
                            k++;
                        }*/
                        intent.putExtra(DetailFragment.EXTRATEXT, resultArray[k].toString());
                        intent.putExtra(DetailFragment.POSITION, position);
                        mListener.onClickListen(position, isFirst,resultArray);
                    }

                }
            });
        updateMovieList();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v("OnAttach","Fragment");
        try{
            //Lisener to know when the Grid view item is clicked in two pane layout
            mListener = (OnClickItemListener)activity;
        }
        catch(ClassCastException ex){
            Log.d("Fragment",activity.getLocalClassName()+" does not implement listener class"+OnClickItemListener.class);
        }
       // updateMovieList();

    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //Function to call the background task
    public void updateMovieList(){
        //Retrieve the preference value from Shared preference value

        String selection = Utility.getSelectionValue(getActivity());


        if(selection.equals(getString(R.string.pref_sort_favorite))){
            displayFavorite(getActivity());
        }
        else {

            if(resultArray[0] == null || !selection.equals(prevSelection)){
                //This flag is to display the movie details in detail activity when the main activity
                // is opened for first time and also when spinner valus are selected for first time
               // in two pane layout
                isFirst = true;
            }
            FetchMovieTask task = new FetchMovieTask();
            task.execute(selection);
        }
    }

    //Function to display favorites from database
    public void displayFavorite(Context context){
        //Query the database

        cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_IMG_SRC},
        null,null,null,null);

        mImageAdapter.clear();
        if(cursor.getCount()==0){
            //When there is no data in db,display the empty view
            View view = getView();
            if(memptyView!=null) {
                //TextView emptyView = (TextView) view.findViewById(R.id.listview_emptyView);
                memptyView.setText(getString(R.string.empty_no_favorites));
            }
        }
        while(cursor.moveToNext()){
            String posterUrl = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMG_SRC));
            mImageAdapter.add(posterUrl);
        }
        cursor.close();

        //This is display the first item in favorites in detail fragment in two pane layout
        //so that old detail view is replaced with new movie detail from favorites
        String selection = Utility.getSelectionValue(getActivity());
        if(!selection.equals(prevSelection)) {
            mListener.onClickListen(0, true,null);
        }
    }

    //Function to format JSON string retrieved from API call
    public ArrayList<String> formatJSONStr(String[] jsonStr){

        String RESULTS_ARRAY = "results";
        String POSTER_PATH = "poster_path";
        ArrayList<String> imageUrls = new ArrayList<String>();
        int j = 0;
        int len = jsonStr.length;
        JSONObject jsonOutput = null;

        try {
            //process the JSON str for two pages
            while(j < len) {
                jsonOutput = new JSONObject(jsonStr[j]);
                resultArray[j] = jsonOutput.getJSONArray(RESULTS_ARRAY);
                for (int i = 0; i < resultArray[j].length(); i++) {
                    String imageURL = resultArray[j].getJSONObject(i).getString(POSTER_PATH);
                    imageUrls.add(imageURL);
                    if(imageURL.equals("null")) {
                        String path = resultArray[j].getJSONObject(i).getString("backdrop_path");
                   }
                }
               j++;
            }
        } catch (JSONException e) {
            Log.e(LOGTAG,"Error formating JSON string"+e.getMessage());
        }

        return imageUrls;
    }

    //Class to execute the background task - fetching data from Moviedb API
    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<String>> {

        final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        final String BASE_URI = "http://api.themoviedb.org/3/movie/";
        final String PARAM_APIKEY = "api_key";
        final String PARAM_PAGE_NO = "page";


        @Override
        protected ArrayList<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            String path = params[0];
            String apiKey = BuildConfig.MOVIE_DB_API_KEY;
            String [] appendedJson = new String[2];

            try {
                for(int i = 1;i < 3;i++) {
                    Uri uri = Uri.parse(BASE_URI).buildUpon().appendPath(path).
                            appendQueryParameter(PARAM_APIKEY, apiKey).
                            appendQueryParameter(PARAM_PAGE_NO, String.valueOf(i)).build();

                    URL url = new URL(uri.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    if (reader == null) {
                        return null;
                    }

                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "/n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    jsonStr = buffer.toString();

                    appendedJson[i-1] = jsonStr;
                }
                return formatJSONStr(appendedJson);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to API" + e.getMessage());
                appendedJson=null;
            }
            finally {
                if(connection != null)
                    connection.disconnect();
                if(reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.d(LOGTAG, "Error closing reader" + e.getMessage());
                    }
            }
           return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> jsonStr) {
            if (jsonStr != null) {
                mImageAdapter.clear();
                for (int i = 0; i < jsonStr.size(); i++) {
                    mImageAdapter.add(jsonStr.get(i));
                }
            }
            else{
                mImageAdapter.clear();
                TextView emptyView = (TextView) getView().findViewById(R.id.listview_emptyView);
                emptyView.setText("No Internet Connection");
            }
            if (resultArray[0]!=null && isFirst == true){
                mListener.onClickListen( 0, true,resultArray);
             }
            isFirst = false;
        }

    }
}
