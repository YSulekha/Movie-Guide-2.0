package com.nanodegree.alse.movieguide;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by aharyadi on 5/23/16.
 */
public class TabFragment extends Fragment {
    public static final String POSITION = "EXTRA_POSITION";
    final String LOG_TAG = TabFragment.class.getSimpleName();
    ArrayAdapter mReviewAdater;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.secondtab, container, false);
        ArrayList<String> data = new ArrayList<String>();
       data.add("Review 1");
        data.add("Review 2");
        mReviewAdater = new ArrayAdapter(getActivity(),R.layout.list_item,R.id.review,data);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_review);
        TextView textView = (TextView)rootView.findViewById(R.id.review_emptyView);
        listView.setEmptyView(textView);
        listView.setAdapter(mReviewAdater);
      /*  Intent intent = getActivity().getIntent();
        String value = intent.getStringExtra(Intent.EXTRA_TEXT);
        int position = intent.getIntExtra(POSITION, 0);*/
      //  Log.v("TabFragment",String.valueOf(position));
        try {
            //JSONArray resultArray = new JSONArray(value);
          //  JSONObject object = resultArray.getJSONObject(position);
         //   JSONObject object = ((DetailActivity_old)getActivity()).getjsonObject();
            int movieId = DetailFragment.movie.movieId;
            FetchReview fetchReview = new FetchReview();
            fetchReview.execute(movieId);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while fetching the value from jsonStr" + e.getMessage());
        }
        return rootView;

    }
    public void onStart(){
        super.onStart();


    }
    public class FetchReview extends AsyncTask<Integer,Void,ArrayList<String>>{

        final String BASE_URI = "http://api.themoviedb.org/3/movie/";
        final String PARAM_APIKEY = "api_key";
        final String LOG_TAG = FetchReview.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {

            int movieId = params[0];
            Log.v("InsideTask",String.valueOf(movieId));
            String jsonStr="";
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            Uri uri = Uri.parse(BASE_URI).buildUpon().appendPath(String.valueOf(movieId)).appendPath("reviews").
                    appendQueryParameter(PARAM_APIKEY, BuildConfig.MOVIE_DB_API_KEY).build();
            try {
                URL url = new URL(uri.toString());
          //      Log.v("InsideTask",uri.toString());
                connection = (HttpURLConnection) url.openConnection();
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
                //Log.v("fdfg",jsonStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(connection != null)
                    connection.disconnect();
                if(reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "Error closing reader" + e.getMessage());
                    }
            }
            return formatJSONStr(jsonStr);



        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
          //  super.onPostExecute(strings);
            if(strings !=null) {
                mReviewAdater.clear();
                for (String review : strings) {
              //      Log.v("InsideTask", "Post");

                    mReviewAdater.add(review);
                }
            }
            else{
                TextView textView = (TextView)getView().findViewById(R.id.review_emptyView);
                textView.setText("No Reviews");
            }
        }
    }
    public ArrayList<String> formatJSONStr(String jsonStr){
        ArrayList<String> reviews = new ArrayList<String>();
     //   Log.v("JSONSTR",jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray results = jsonObject.getJSONArray("results");
            for(int i=0;i<results.length();i++){
                String review = results.getJSONObject(i).getString("content");
             //   Log.v("JSONSTR",review);
                reviews.add(review);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;


    }
}
