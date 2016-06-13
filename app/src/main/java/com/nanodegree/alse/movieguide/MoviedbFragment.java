package com.nanodegree.alse.movieguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import com.nanodegree.alse.movieguide.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    JSONArray [] resultArray = new JSONArray[2]; //Array to store the result of two pages retrieved from MovieDb API
    final String LOGTAG = MoviedbFragment.class.getSimpleName();

    public MoviedbFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public interface OnClickItemListener{
        public void onClickListen(String jsonStr,int Position,boolean isFirst);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        mImageAdapter = new ImageAdapter(getActivity(),R.layout.grid_view_movie);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movie);
        TextView emptyView = (TextView) rootView.findViewById(R.id.listview_emptyView);
        gridView.setAdapter(mImageAdapter);
        gridView.setEmptyView(emptyView);
        //On click open detail ACtivity layout
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.v("InsideOnclick", mImageAdapter.getItem(position));
          //      Log.v("ddffd",resultArray.toString());
                String selection = Utility.getSelectionValue(getActivity());
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                String value = (String) parent.getItemAtPosition(position);
                int k = 0;

              /*  if(selection.equals(getString(R.string.pref_sort_favorite)) && cursor != null){
                    cursor.moveToPosition(position);
                    String movieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                    Log.v("InsideOnclick", String.valueOf(movieId));
                }*/
                //  else {

                //Decide from which array the value should be retrieved
                if(selection.equals(getString(R.string.pref_sort_favorite))){
                    intent.putExtra(DetailFragment.EXTRATEXT, "null");
                    intent.putExtra(DetailFragment.POSITION, position);
                    mListener.onClickListen("null", position,isFirst);
                //    startActivityForResult(intent, 0);
                }
                else {
                    if (position >= resultArray[0].length()) {

                        position = position % resultArray[0].length();
                        k++;
                        Log.v("ddffd", resultArray[k].toString());

                    }
                    intent.putExtra(DetailFragment.EXTRATEXT, resultArray[k].toString());
                    intent.putExtra(DetailFragment.POSITION, position);
                    mListener.onClickListen(resultArray[k].toString(),position,isFirst);
                 //   startActivityForResult(intent, 0);
                }

                //  }
                //Sending Jsonstr to detail view to retrive ralated string values

                //  startActivity(intent);


            }
        });
        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            Log.v("onact","insideif");
            if (data!=null && data.hasExtra("IsChanged")) {
                Log.v("onActivityResult",Utility.getSelectionValue(getActivity()));
                if(Utility.getSelectionValue(getActivity()).equals("favorite")&&
                        data.getStringExtra("IsChanged").equals("true")){
                    Log.v("onActivityResult2",Utility.getSelectionValue(getActivity()));
                    displayFavorite(getActivity());
                   // mImageAdapter.notifyDataSetChanged();
                }
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }

    @Override

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnClickItemListener)activity;
        }
        catch(ClassCastException ex){
            Log.d("Fragment",activity.getLocalClassName()+" does not implement listener class"+OnClickItemListener.class);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.v("Inside OnResume", "sff");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("Inside OnPause", "sff");
      //
    }

    //Function to call the background task
    public void updateMovieList(){

        //Retrieve the preference value from Shared preference value
        String selection = Utility.getSelectionValue(getActivity());
        Log.v("updateMovieList",selection+prevSelection);
        if(selection.equals(getString(R.string.pref_sort_favorite))){
            displayFavorite(getActivity());
        }
        else {
         //   if (Utility.isOnline(getActivity())) {
                if(resultArray[0] == null || !selection.equals(prevSelection)){
                    isFirst = true;
                }
                FetchMovieTask task = new FetchMovieTask();
              //  resultArray = new JSONArray[2];
            task.execute(selection);
                Log.v("updateMovieList", String.valueOf(isFirst));

         //   }
            //else {
               // displayFavorite(getActivity());
                //Avoiding the app crash when there is no internet
            /*    String text = "No internet connection. Please enable internet settings and reopen the Application";
                Toast t = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
                t.show();*/
            //   RelativeLayout rl = (RelativeLayout)getActivity().findViewById(R.id.relativeId);
              //  FrameLayout fl = (FrameLayout)getActivity().findViewById(R.id.movie_fragment);
              //  rl.removeAllViews();
              //  fl.removeAllViews();
           /*    Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3500);
                            getActivity().s
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();*/
          //  }
        }
    }


    public void displayFavorite(Context context){
     /*  SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String selection = shared.getString(getString(R.string.pref_favorite_key), null);

        String text = "There is no movie in favorites";

       if(selection==null){
            mImageAdapter.clear();
            Toast t = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
            t.show();

        }
        else {
            String [] posterUrl = selection.split(",");
            mImageAdapter.clear();
            for (String url : posterUrl) {
                mImageAdapter.add(url);

            }
        }*/
     //   resultArray=null;
        cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_IMG_SRC},
        null,null,null,null);

        mImageAdapter.clear();
        if(cursor.getCount()==0){
            TextView emptyView = (TextView) getView().findViewById(R.id.listview_emptyView);
            emptyView.setText("No Favorites in your list");
        }
        while(cursor.moveToNext()){
            String posterUrl = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMG_SRC));
            Log.v("Favorite",posterUrl);
            mImageAdapter.add(posterUrl);
        }
        cursor.close();
        String selection = Utility.getSelectionValue(getActivity());
        if(!selection.equals(prevSelection)) {
            mListener.onClickListen(null, 0, true);
        }

   //     mImageAdapter.clear();
     //   mImageAdapter.add("Bitmap");
     //   readFromInternal();
    }
    public  void readFromInternal() {
        File directory = getActivity().getDir("MovieDb", Context.MODE_PRIVATE);
        File file = new File(directory, "image");
        Bitmap b;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(file));
            mImageAdapter.add(String.valueOf(b));

            Log.v("dsdfs",String.valueOf(b));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


        //Function to format JSON string retrieved from API call
    public ArrayList<String> formatJSONStr(String[] jsonStr){

        String RESULTS_ARRAY = "results";
        String POSTER_PATH = "poster_path";

        ArrayList<String> imageUrls = new ArrayList<String>();
        int j = 0;
        int len = jsonStr.length;
        Log.v("Insideformat",String.valueOf(len));
        JSONObject jsonOutput = null;

        try {
            //process the JSON str for two pages
            while(j < len) {
                jsonOutput = new JSONObject(jsonStr[j]);
                resultArray[j] = jsonOutput.getJSONArray(RESULTS_ARRAY);
              //  Log.v("dfsfd",jsonStr[j]);
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
           // Log.v("insideBack",jsonStr);
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
                       // Log.v("line",line);
                        buffer.append(line + "/n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    jsonStr = buffer.toString();

                    appendedJson[i-1] = jsonStr;
                  //  Log.v("insideBack",jsonStr);
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
                        Log.v("InsideClose","sdsd");
                    } catch (IOException e) {
                        Log.d(LOGTAG, "Error closing reader" + e.getMessage());
                    }
            }
           return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> jsonStr) {
         //   Log.v("Inside PostExecute","hhhh");
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

                mListener.onClickListen(resultArray[0].toString(), 0, true);
             }
            isFirst = false;



        }

    }

}
