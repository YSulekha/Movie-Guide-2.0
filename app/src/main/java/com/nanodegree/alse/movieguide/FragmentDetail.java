package com.nanodegree.alse.movieguide;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nanodegree.alse.movieguide.data.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class FragmentDetail extends Fragment {

    ViewPagerAdapter mpagerAdapter;
    public static final String EXTRATEXT = "EXTRA_TEXT";
    public static final String POSITION = "EXTRA_POSITION";
    static final String SHARE_TAG = "#MovieGuideApp";
    public static final String FLAG = "EXTRA_FLAG";
    public static Movie movie=new Movie();
    boolean pressed = false;
    boolean dataChanged = false;
    FloatingActionButton FAB;
    FloatingActionButton FABShare;
    OnChangeListener mListener;
    private final String IMAGE_BASEURL = "http://image.tmdb.org/t/p/w185/";
    boolean share = false;


    public FragmentDetail() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Interface for notifying the changes in favorite list to implementing activity
    public interface OnChangeListener{
        public void onChangeListen();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnChangeListener)activity;
        }
        catch(ClassCastException ex){
            Log.d("Fragment",activity.getLocalClassName()+" does not implement listener class"+OnChangeListener.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle bundle = getArguments();

        //Do not set the toolbar for two pane layout
        if(bundle!=null&&!bundle.getBoolean(FLAG)) {
            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_actionbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        TabLayout t = (TabLayout) rootView.findViewById(R.id.tabs);
        t.addTab(t.newTab().setText("Detail"), 0);
        t.addTab(t.newTab().setText("Review"), 1);
        t.setTabTextColors(ContextCompat.getColorStateList(getActivity(), R.color.title_color));
        t.setTabGravity(t.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
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

        FAB = (FloatingActionButton) rootView.findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorite();
            }
        });
        getjsonObject();
        String imageURL = null;
        ImageView toolImage = (ImageView)rootView.findViewById(R.id.imageViewplaces);
        String selection = Utility.getSelectionValue(getActivity());
        String noPosterUrl = "https://assets.tmdb.org/assets/f996aa2014d2ffddfda8463c479898a3/images/no-poster-w185.jpg";
        if (selection.equals(getString(R.string.pref_sort_favorite))) {
            if (movie.posterUrl != null && !movie.posterUrl.equals("null")) {
                File file = new File(movie.posterUrl);
                Picasso.with(getActivity()).load(file).
                        into(toolImage);
            }
        } else if (movie.posterUrl != null && !movie.posterUrl.equals("null")) {
            imageURL = IMAGE_BASEURL + movie.posterUrl;
            Picasso.with(getActivity()).load(imageURL).
                    into(toolImage);
        } else {
            Picasso.with(getActivity()).load(noPosterUrl).into(toolImage);
        }
        if (movie.movieId == -1) {
            return null;
        }
        //When clicked on the image trailer should get launched
        toolImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo(v);
            }
        });
        FABShare = (FloatingActionButton) rootView.findViewById(R.id.share);
        FABShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharevideoUrl();
            }
        });

        return rootView;
    }
    public void sharevideoUrl(){
        share= true;
        onClickVideo(null);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void addToFavorite(){

        String text = getString(R.string.favorite_added);
        String selection = Utility.getSelectionValue(getActivity());
        if(pressed){
            FAB.setImageResource(R.drawable.ic_favorite_border_red_500_48dp);
            pressed=false;
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            String where = MovieContract.MovieEntry.COLUMN_MOV_KEY+"=?";
            String selectionArgs[] = new String[]{String.valueOf(movie.movieId)};
            int rowsDeleted = getActivity().getContentResolver().delete(uri, where, selectionArgs);
            text = getString(R.string.removed_favorite);
            if(selection.equals(getString(R.string.pref_sort_favorite))) {
                if (mListener != null) {
                    mListener.onChangeListen();
                }
            }
            Intent data = new Intent();
            dataChanged = true;
            data.putExtra("IsChanged", String.valueOf(dataChanged));
            getActivity().setResult(0,data);
        }
        else {
            String IMAGE_BASEURL = "http://image.tmdb.org/t/p/w185/";
            String imageURL = IMAGE_BASEURL + movie.posterUrl;
            Picasso.with(getActivity()).load(imageURL).into(picassoImageTarget(getActivity().getApplicationContext(), "imageDir", movie.title + ".jpeg"));
            ContextWrapper cw = new ContextWrapper(getActivity());
            File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(dir,movie.title + ".jpeg");
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOV_KEY, movie.movieId);
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.title);
            values.put(MovieContract.MovieEntry.COLUMN_SHORT_DESC, movie.overview);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.date);
            values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.rating);
            values.put(MovieContract.MovieEntry.COLUMN_IMG_SRC, file.getAbsolutePath());
            movie.posterUrl=file.getAbsolutePath();
            Uri returnUri = getActivity().getContentResolver().insert(uri, values);
            FAB.setImageResource(R.drawable.ic_favorite_red_500_48dp);
            pressed=true;
            if(selection.equals(getString(R.string.pref_sort_favorite))) {
                if (mListener != null) {
                    mListener.onChangeListen();
                }
            }
        }
        Toast t = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        t.show();
    }

    //Referred some blogs to understand how to store images in internal memory
    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); 
        final File imageFile = new File(directory, imageName);
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Create image file

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }


    public void processJSON(JSONObject object){
        try {
            movie.movieId = object.getInt("id");
            boolean isPresent = IsPresentInFavorites(object.getInt("id"));
            if(isPresent == true){
                FAB.setImageResource(R.drawable.ic_favorite_red_500_48dp);
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
        JSONObject resultObject = null;
        Bundle bundle = getArguments();
        if(bundle!=null) {
            String value = bundle.getString(EXTRATEXT);
            int position = bundle.getInt(POSITION);
            if (Utility.getSelectionValue(getActivity()).equals(getString(R.string.pref_favorite_key))) {
                getFavorite(position);
                FAB.setImageResource(R.drawable.ic_favorite_red_500_48dp);
                pressed = true;

            } else if (value != null) {
                try {
                    JSONArray resultArray = new JSONArray(value);
                    resultObject = resultArray.getJSONObject(position);
                    processJSON(resultObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultObject;

    }
    public void getFavorite(int position){
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        Cursor cursor = Utility.queryFavoriteTable(getActivity(), uri);
        if(cursor.getCount()!=0){
            cursor.moveToPosition(position);
            movie.movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOV_KEY));
            movie.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            movie.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SHORT_DESC));
            movie.date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            movie.rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            movie.posterUrl = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMG_SRC));
        }
        else{
            movie.movieId=-1;
        }
    }

    public void onClickVideo(View v){
        int movieId = movie.movieId;
        FetchTrailer fetchTrailer = new FetchTrailer();
        fetchTrailer.execute(movieId);
    }

    public class FetchTrailer extends AsyncTask<Integer,Void,ArrayList<String>> {

        final String BASE_URI = "http://api.themoviedb.org/3/movie/";
        final String PARAM_APIKEY = "api_key";
        final String LOG_TAG = FetchTrailer.class.getSimpleName();


        @Override
        protected ArrayList<String> doInBackground(Integer... params) {

            int movieId = params[0];
            String jsonStr="";
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            Uri uri = Uri.parse(BASE_URI).buildUpon().appendPath(String.valueOf(movieId)).appendPath("videos").
                    appendQueryParameter(PARAM_APIKEY, BuildConfig.MOVIE_DB_API_KEY).build();
            try {
                URL url = new URL(uri.toString());
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
            Uri uri=null;
            for(String videoId:strings){
                String url = "http://img.youtube.com/vi/"+videoId+"/0.jpg";
                uri = Uri.parse("https://www.youtube.com/watch").buildUpon().appendQueryParameter("v",videoId).build();
                break;
            }
            if(uri == null || !Utility.isOnline(getActivity())){
                String text = "No Internet Connection";
                Toast t = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                t.show();
                return;
            }
            if(!share){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(Intent.createChooser(intent,"Play trailer in"));
            }
            else{

                share = false;
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Video Trailer of movie " + movie.title + "-  " + uri+ " "+SHARE_TAG);
                startActivity(Intent.createChooser(shareIntent, "Share via"));

            }
        }
    }
    public ArrayList<String> formatJSONStr(String jsonStr){
        ArrayList<String> trailers = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray results = jsonObject.getJSONArray("results");
            for(int i=0;i<results.length();i++){
                String type = results.getJSONObject(i).getString("type");
                if(type.equals("Trailer")){
                    String videoId = results.getJSONObject(i).getString("key");
                    trailers.add(videoId);
                    break;
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;

    }
}
