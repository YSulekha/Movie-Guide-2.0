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

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviedbFragment extends Fragment {

    ImageAdapter mImageAdapter;
    JSONArray resultArray;

    public MoviedbFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        mImageAdapter = new ImageAdapter(getActivity(),R.layout.grid_view_movie);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movie);
        gridView.setAdapter(mImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                String value =
                        (String)parent.getItemAtPosition(position);

                intent.putExtra(Intent.EXTRA_TEXT,resultArray.toString());
                intent.putExtra("Position",position);
                startActivity(intent);

            }
        });
        return rootView;
    }

    public void onStart(){
        super.onStart();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String selection = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        Log.v("Inside OnStart",selection);
        updateMovieList();
    }



    public void updateMovieList(){
     /*   SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String selection = shared.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        FetchMovieTask task = new FetchMovieTask();
        task.execute(selection);*/
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String selection = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        FetchMovieTask task = new FetchMovieTask();
        task.execute(selection);
    }

    public String[] formatJSONStr(String jsonStr){
        String RESULTS_ARRAY = "results";
        String POSTER_PATH = "poster_path";
        String [] imageUrls = null;

        JSONObject jsonOutput = null;
        try {
            jsonOutput = new JSONObject(jsonStr);
            resultArray = jsonOutput.getJSONArray(RESULTS_ARRAY);
            imageUrls = new String[resultArray.length()];
            for(int i = 0; i < resultArray.length();i++){
                String imageURL = resultArray.getJSONObject(i).getString(POSTER_PATH);

                imageUrls[i] = imageURL;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return imageUrls;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        final String BASE_URI = "http://api.themoviedb.org/3/movie/";
        final String PARAM_APIKEY = "api_key";

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            String path = params[0];
            String apiKey = BuildConfig.MOVIE_DB_API_KEY;
            try {
                Uri uri = Uri.parse(BASE_URI).buildUpon().appendPath(path).
                        appendQueryParameter(PARAM_APIKEY, apiKey).build();
                Log.v("Inside background",uri.toString());
                URL url = new URL(uri.toString());
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                if(reader == null){
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                String line;
                while((line = reader.readLine())!=null){
                   buffer.append(line+"/n");
                }
                if(buffer.length()==0)
                    return null;
                jsonStr = buffer.toString();
                Log.v(LOG_TAG,jsonStr);

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if(connection != null)
                    connection.disconnect();
                if(reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return formatJSONStr(jsonStr);

        }

        @Override
        protected void onPostExecute(String jsonStr[]){
            if(jsonStr != null){
                mImageAdapter.clear();
                Log.v("InsidePostExecute", "dsfsd");
                for (String str : jsonStr){
                    mImageAdapter.add(str);
                }
            }
        }
    }
}
