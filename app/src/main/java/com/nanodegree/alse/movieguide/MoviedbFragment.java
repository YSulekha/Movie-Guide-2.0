package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
    JSONArray [] resultArray = new JSONArray[2]; //Array to store the result of two pages retrieved from MovieDb API
    final String LOGTAG = MoviedbFragment.class.getSimpleName();

    public MoviedbFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        mImageAdapter = new ImageAdapter(getActivity(),R.layout.grid_view_movie);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movie);
        gridView.setAdapter(mImageAdapter);
        Log.v("Inside OnCreateAcivity", "Frag");

        //On click open detail ACtivity layout
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                String value = (String)parent.getItemAtPosition(position);
                int k = 0;

                //Decide from which array the value should be retrieved
                if(position >= resultArray[0].length() ) {
                    position = position % resultArray[0].length();
                    k++;
                }
                Log.v("Inside Listener", resultArray[k].toString());
                //Sending Jsonstr to detail view to retrive ralated string values
                intent.putExtra(Intent.EXTRA_TEXT, resultArray[k].toString());
                intent.putExtra(DetailActivityFragment.POSITION,position);
                startActivity(intent);

            }
        });
        return rootView;
    }

    public void onStart(){
        super.onStart();
    }
    //Function to call the background task
    public void updateMovieList(){

        //Retrieve the preference value from Shared preference value
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String selection = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        Log.v("InsideUpdate","dsfsd");
        FetchMovieTask task = new FetchMovieTask();
        task.execute(selection);
    }

    //Function to format JSON string retrived from API call
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
                        Log.v("path",path);
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
                    if (buffer.length() == 0)
                        return null;
                    jsonStr = buffer.toString();
                    appendedJson[i-1] = jsonStr;
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to API" + e.getMessage());
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
            return formatJSONStr(appendedJson);
        }

        @Override
        protected void onPostExecute(ArrayList<String> jsonStr){
            if(jsonStr != null){
                mImageAdapter.clear();
                for (int i = 0;i < jsonStr.size();i++){
                    mImageAdapter.add(jsonStr.get(i));
                }
            }
        }
    }
}
