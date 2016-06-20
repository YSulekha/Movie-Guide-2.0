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

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {


    private final String IMAGE_BASEURL = "http://image.tmdb.org/t/p/w185/";
    public Movie movie;
    String imageURL = null;
    String noPosterUrl = "https://assets.tmdb.org/assets/f996aa2014d2ffddfda8463c479898a3/images/no-poster-w185.jpg";

    public DetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.detail_card, container, false);

        movie = FragmentDetail.movie;
        if (movie.movieId == -1) {
            return null;
        }
        ImageView imageView = (ImageView)root.findViewById(R.id.detail_poster);
        String selection = Utility.getSelectionValue(getActivity());
        if (selection.equals(getString(R.string.pref_sort_favorite))) {
            if (movie.posterUrl != null && !movie.posterUrl.equals("null")) {
                File file = new File(movie.posterUrl);
                Picasso.with(getActivity()).load(file).
                        into(imageView);
            }
        }
        else if (movie.posterUrl != null && !movie.posterUrl.equals("null")){
            imageURL = IMAGE_BASEURL + movie.posterUrl;
            Picasso.with(getActivity()).load(imageURL).
                    into(imageView);
        }
        else {
            Picasso.with(getActivity()).load(noPosterUrl).into(imageView);
        }

        TextView desc = (TextView) root.findViewById(R.id.detail_overview);
        desc.setText(movie.overview);

        TextView titleText = (TextView) root.findViewById(R.id.detail_title);
        titleText.setText(movie.title);

        TextView release = (TextView) root.findViewById(R.id.detail_release_date);
        release.setText(movie.date);


        RatingBar rate = (RatingBar) root.findViewById(R.id.detail_ratingBar);
        //change rating in terms of 5
        rate.setRating((float) ((movie.rating * 5) / 10));

        return root;
    }

}
