package com.nanodegree.alse.movieguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public static final String POSITION = "EXTRA_POSITION";

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        String value = intent.getStringExtra(Intent.EXTRA_TEXT);
        int position = intent.getIntExtra("Position", 0);

        String posterPath = null;
        String title = null;
        String overview = null;
        String releaseDate = null;
        double rating = 0.0;
        try {
            JSONArray resultArray = new JSONArray(value);
            JSONObject object = resultArray.getJSONObject(position);
            title = object.getString("title");
            posterPath = "http://image.tmdb.org/t/p/w185/"+object.getString("poster_path");
            overview = object.getString("overview");
            releaseDate = object.getString("release_date");
            rating = object.getDouble("vote_average");


        } catch (JSONException e) {
            e.printStackTrace();
        }
      //  TextView textView = (TextView) root.findViewById(R.id.detail_moviename);
       // textView.setText(title);
        ImageView imageView = (ImageView) root.findViewById(R.id.detail_ImageView);

        Picasso.with(getActivity()).load(posterPath).
                into(imageView);


        TextView desc = (TextView)root.findViewById(R.id.detail_overview);
        desc.setText(overview);

        TextView release = (TextView)root.findViewById(R.id.detail_release_date);
        release.setText(releaseDate);


        RatingBar rate = (RatingBar)root.findViewById(R.id.detail_ratingBar);
        rate.setRating((float) ((rating * 5) / 10));




      //  getActivity().setTitle(title);
    //    getActivity().setTitleColor(R.color.my_color);
        getActivity().setTitle(Html.fromHtml("<strong>"+"<font color='#B71C1C'>" +"<big>"+ title +"</big" +"</font>"+"</strong>"));
      //  getSupportActionBar().setTitle(Html.fromHtml("<font color='#746E66'>" + titleText + "</font>"));

        return root;
    }

    public void jsonProcessing(String result,int position){

    }
}
