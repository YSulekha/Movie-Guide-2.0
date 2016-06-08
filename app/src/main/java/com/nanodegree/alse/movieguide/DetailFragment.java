package com.nanodegree.alse.movieguide;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nanodegree.alse.movieguide.data.MovieContract;

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


public class DetailFragment extends Fragment {

    private Toolbar toolbar;
    ViewPagerAdapter mpagerAdapter;
    public static final String EXTRATEXT = "EXTRA_TEXT";
    public static final String POSITION = "EXTRA_POSITION";
    ImageView poster = null;
    String posterUrl = null;
    public static Movie movie=new Movie();
    boolean pressed = false;
    boolean dataChanged = false;
    MyContentObserver ob;
    FloatingActionButton FAB;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_detail2, container, false);
        toolbar = (Toolbar)rootView.findViewById(R.id.tool_bar).findViewById(R.id.toolbar_actionbar);
        Log.v("InsideOnCreate", "DetailFrgament");

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()). getSupportActionBar();
        TabLayout t = (TabLayout)rootView.findViewById(R.id.tabs);
        t.addTab(t.newTab().setText("Detail"), 0);
        t.addTab(t.newTab().setText("Review"), 1);
        //  t.addTab(t.newTab().setText("Trailer"), 2);
        t.setTabTextColors(ContextCompat.getColorStateList(getActivity(), R.color.title_color));
        t.setTabGravity(t.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.viewpager);

        mpagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(mpagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(t));
        t.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
     //   ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        FAB = (FloatingActionButton) rootView. findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorite();
            }
        });
        String value=null;
        Intent intent = getActivity().getIntent();
        if(intent.hasExtra(Intent.EXTRA_TEXT)) {
            value = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        int position = intent.getIntExtra(POSITION, 0);
        ob = new MyContentObserver(null);
        getjsonObject();
    //    movie = new Movie();
        ImageView poster = (ImageView)rootView.findViewById(R.id.tool_bar).findViewById(R.id.imageViewplaces);
        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo(v);
            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    public void addToFavorite(){

        String text = "Added to favorite";


        if(pressed){
            FAB.setImageResource(R.drawable.ic_favorite_red_500_36dp);
            pressed=false;
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            String where = MovieContract.MovieEntry.COLUMN_MOV_KEY+"=?";
            String selectionArgs[] = new String[]{String.valueOf(movie.movieId)};
            int rowsDeleted = getActivity().getContentResolver().delete(uri, where, selectionArgs);
            Log.v("ssgsgg", String.valueOf(rowsDeleted));
            text = "Removed from favorite";
            Intent data = new Intent();
            dataChanged = true;
            data.putExtra("IsChanged", String.valueOf(dataChanged));
            getActivity().setResult(0,data);
         //   getActivity().finish();
            //   getParent().getContentResolver().notify();

        }
        else {

            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOV_KEY, movie.movieId);
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.title);
            values.put(MovieContract.MovieEntry.COLUMN_SHORT_DESC, movie.overview);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.date);
            values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.rating);
            values.put(MovieContract.MovieEntry.COLUMN_IMG_SRC, movie.posterUrl);
            Uri returnUri = getActivity().getContentResolver().insert(uri, values);
            Log.v("ssgsgg", returnUri.toString());


            FAB.setImageResource(R.drawable.ic_movie_filter_red_600_18dp);
            pressed=true;
            Log.v("ssgsgg", String.valueOf(FAB.isPressed()));
        }
        Toast t = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
        t.show();

        //      ImageView imageView = fragment.mimageView;

        //  mpagerAdapter.getItem(0);
        //   String posterUrl = getPosterPath(getjsonObject());


     /*   if(favoriteList !=null){
            favoriteList = favoriteList+","+posterUrl;
        }
        else
            favoriteList = posterUrl;
        Log.v("PosterUrl", "dffff" + favoriteList);
        editor.putString(getString(R.string.pref_favorite_key), favoriteList);
        editor.commit();
        String text = "Added to favorite";
        Toast t = Toast.makeText(this, text, Toast.LENGTH_LONG);
        t.show();
        try{
            addToStorage(poster);
        }
        catch (Exception e){
            e.printStackTrace();
        }*/

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }



    public void processJSON(JSONObject object){
        try {
            movie.movieId = object.getInt("id");
            Log.v("InsideprocessJSON",String.valueOf(movie.movieId));
            boolean isPresent = IsPresentInFavorites(object.getInt("id"));
            if(isPresent == true){
                FAB.setImageResource(R.drawable.ic_movie_filter_red_600_18dp);
                pressed = true;
            }
            movie.title = object.getString("title");
            movie.posterUrl = object.getString("poster_path");
            movie.overview = object.getString("overview");
            movie.date = object.getString("release_date");
            movie.rating = object.getDouble("vote_average");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean IsPresentInFavorites(int id){
        Uri uri = MovieContract.MovieEntry.buildMovieWithMovieId(id);
        Cursor c = Utility.queryFavoriteTable(getActivity(), uri);
        if(c.moveToFirst()){
            return true;
        }
        else
            return false;
    }
    public JSONObject getjsonObject(){
        Log.v("Insidegetjson","dsffd");
        Intent intent = getActivity().getIntent();
        JSONObject resultObject = null;
        String value = intent.getStringExtra(EXTRATEXT);
   //     Log.v("Insidegetjson",value);
        int position = intent.getIntExtra(POSITION, 0);
        if(Utility.getSelectionValue(getActivity()).equals(getString(R.string.pref_favorite_key))){
            getFavorite(position);
            FAB.setImageResource(R.drawable.ic_movie_filter_red_600_18dp);
            pressed=true;

        }
        else if(value!=null){
       //else{

            try {
                JSONArray resultArray = new JSONArray(value);
                resultObject = resultArray.getJSONObject(position);
                processJSON(resultObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return resultObject;

    }
    public void getFavorite(int position){
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        Cursor cursor = Utility.queryFavoriteTable(getActivity(), uri);
        if(cursor!=null){
            cursor.moveToPosition(position);
            movie.movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOV_KEY));
            movie.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            Log.v("dffdfd",movie.title);
            movie.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SHORT_DESC));
            movie.date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            movie.rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            movie.posterUrl = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMG_SRC));

        }
    }
    public String getPosterPath(JSONObject o){
        String posterPath = null;
        try {
            posterPath = o.getString("poster_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return posterPath;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

    /*    if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        }*/
        if(id == R.id.action_favorite){
            addToFavorite();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

 /*   @Override
    public void finish() {
        Log.v("Insidefinish", String.valueOf(dataChanged));

        Intent data = new Intent();
        data.putExtra("IsChanged", String.valueOf(dataChanged));
        getActivity().setResult(0, data);
        getActivity().finish();

    }*/

    public void onClickVideo(View v){
        int movieId = 0;
    /*    try {
           // movieId = getjsonObject().getInt("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        movieId = movie.movieId;
        FetchReview fetchReview = new FetchReview();
        fetchReview.execute(movieId);
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
                startActivity(Intent.createChooser(intent,"Play trailer in"));
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
    public class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            //  super.onChange(selfChange);
            onChange(selfChange,null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {

            Log.v("sdff", "Inside Onchange");
            dataChanged = true;
            //   super.onChange(selfChange, uri);

        }
    }



}
