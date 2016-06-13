package com.nanodegree.alse.movieguide;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
 * Created by aharyadi on 5/24/16.
 */
public class TrailerFragment extends Fragment {
    public static final String POSITION = "EXTRA_POSITION";
    final String LOG_TAG = TabFragment.class.getSimpleName();
    int movieId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.thirdtab, container, false);
     /*   Intent intent = getActivity().getIntent();
        String value = intent.getStringExtra(Intent.EXTRA_TEXT);
        int position = intent.getIntExtra(POSITION, 0);*/
        try {
         //   JSONArray resultArray = new JSONArray(value);
           // JSONObject object = resultArray.getJSONObject(position);
            JSONObject object = null;
                    //((DetailActivity_old)getActivity()).getjsonObject();
            movieId = object.getInt("id");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while fetching the value from jsonStr" + e.getMessage());
        }
      //  Log.v("TabFragment",String.valueOf(position));
        Button button = (Button)rootView.findViewById(R.id.trailerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchReview fetchReview = new FetchReview();
                fetchReview.execute(movieId);
            }
        });
        return rootView;
    }
    public class FetchReview extends AsyncTask<Integer,Void,ArrayList<String>> {

        final String BASE_URI = "http://api.themoviedb.org/3/movie/";
        final String PARAM_APIKEY = "api_key";
        final String LOG_TAG = FetchReview.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {

            int movieId = params[0];
            Log.v("InsideTask", String.valueOf(movieId));
            String jsonStr="";
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            Uri uri = Uri.parse(BASE_URI).buildUpon().appendPath(String.valueOf(movieId)).appendPath("videos").
                    appendQueryParameter(PARAM_APIKEY, BuildConfig.MOVIE_DB_API_KEY).build();
            try {
                URL url = new URL(uri.toString());
                Log.v("InsideTask",uri.toString());
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
                Log.v("fdfg",jsonStr);
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
            for(String videoId:strings){
                String url = "http://img.youtube.com/vi/"+videoId+"/0.jpg";

             //   ImageView toolImage = (ImageView) getActivity().findViewById(R.id.tool_bar).findViewById(R.id.imageViewplaces);
               // Picasso.with(getActivity()).load(url).into(toolImage);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("https://www.youtube.com/watch").buildUpon().appendQueryParameter("v",videoId).build();
                intent.setData(uri);
                startActivity(intent);
                break;
            }


        }
    }
    public ArrayList<String> formatJSONStr(String jsonStr){
        ArrayList<String> reviews = new ArrayList<String>();
        Log.v("JSONSTR", jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray results = jsonObject.getJSONArray("results");
            for(int i=0;i<results.length();i++){
                String type = results.getJSONObject(i).getString("type");
                if(type.equals("Trailer")){
                    String videoId = results.getJSONObject(i).getString("key");
                    reviews.add(videoId);
                    break;
                }
                Log.v("JSONSTR","review");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;


    }
}
