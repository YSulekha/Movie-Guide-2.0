package com.nanodegree.alse.movieguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public static final String POSITION = "EXTRA_POSITION";
    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private final String IMAGE_BASEURL = "http://image.tmdb.org/t/p/w185/";
    public Movie movie;
    String  posterPath;
    ImageView mimageView;

    public DetailActivityFragment() {
    }

    public String getPosterPath(){
        return movie.posterUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail1, container, false);
     //   ImageView view = (ImageView)root.findViewById(R.id.tool_bar).findViewById(R.id.imageViewplaces);
       // Log.v("InsideCreateView",view.toString());
   /*     Intent intent = getActivity().getIntent();
        String value = intent.getStringExtra(Intent.EXTRA_TEXT);
        int position = intent.getIntExtra(POSITION, 0);*/


        String title = null;
        String overview = null;
        String releaseDate = null;
        double rating = 0.0;
        String imageURL = null;
        int id = 0;
        String noPosterUrl = "https://assets.tmdb.org/assets/f996aa2014d2ffddfda8463c479898a3/images/no-poster-w185.jpg";
      //  JSONObject object = ((DetailActivity)getActivity()).getjsonObject();
        movie = DetailActivity.movie;
    /*    try {
        //    JSONArray resultArray = new JSONArray(value);
        //    JSONObject object = resultArray.getJSONObject(position);
            JSONObject object = ((DetailActivity)getActivity()).getjsonObject();
            if(object == null){

            }
            id = object.getInt("id");
            title = object.getString("title");
            posterPath = object.getString("poster_path");
            overview = object.getString("overview");
            releaseDate = object.getString("release_date");
            rating = object.getDouble("vote_average");
            Log.v("fdfg",String.valueOf(id));

            movie.date = releaseDate;
            movie.overview=overview;
            movie.date=releaseDate;
            movie.rating=rating;
            movie.title=title;
            movie.movieId=id;
            movie.posterUrl=posterPath;
            Log.v("fdfg",String.valueOf(movie.movieId));

        } catch (JSONException e) {
            Log.e(LOG_TAG,"Error while fetching the value from jsonStr"+e.getMessage());
        }*/
        //Set poster image
   //     mimageView = (ImageView) root.findViewById(R.id.detail_ImageView);
      //  Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.tool_bar);
        ImageView toolImage = (ImageView)getActivity().findViewById(R.id.tool_bar).findViewById(R.id.imageViewplaces);
     //   ImageView toolImage = (ImageView)toolbar.findViewById(R.id.tool_image);


        if (!movie.posterUrl.equals("null")){
            imageURL = IMAGE_BASEURL + movie.posterUrl;

            //  Picasso.with(getActivity()).load(imageURL).into(mimageView);
            Picasso.with(getActivity()).load(imageURL).
                    into(toolImage);

        }
        else {

            Picasso.with(getActivity()).load(noPosterUrl).into(mimageView);
        }
        ((DetailActivity)getActivity()).setposter(movie.posterUrl,toolImage);

        //Set description
        TextView desc = (TextView)root.findViewById(R.id.detail_overview);
        desc.setText(movie.overview);
        //Set release date
        TextView release = (TextView)root.findViewById(R.id.detail_release_date);
        release.setText(movie.date);

        //Set rating
        RatingBar rate = (RatingBar)root.findViewById(R.id.detail_ratingBar);
        //change rating in terms of 5
        rate.setRating((float) ((movie.rating * 5) / 10));


        //Set the Movie title to title of action bar
        getActivity().setTitle(movie.title);


        return root;
    }

}
