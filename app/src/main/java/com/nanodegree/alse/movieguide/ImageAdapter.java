package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;


public class ImageAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;
    private int mResource;
    private final String IMAGE_BASEURL = "http://image.tmdb.org/t/p/w185/";


    public ImageAdapter(Context context, int layoutId) {
        super(context, layoutId);
        mContext = context;
        mResource = layoutId;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public void add(String object) {
        super.add(object);
    }

    @Override
    public View getView(int position, View convertView ,ViewGroup parent){
        View view;
        String imageURL = null;
        String noPosterUrl = "https://assets.tmdb.org/assets/f996aa2014d2ffddfda8463c479898a3/images/no-poster-w185.jpg";
        if(convertView == null) {
            view =  mInflater.inflate(mResource, parent, false);
        }
        else{
            view =convertView;
        }
        ImageView imageView = (ImageView)view.findViewById(R.id.movie_poster_image);
        String url = getItem(position);

        if(Utility.getSelectionValue(mContext).equals(mContext.getString(R.string.pref_sort_favorite))){

            File file = new File(url);
            Picasso.with(mContext).load(file).into(imageView);
        }

       else if (!url.equals("null")){
            imageURL = IMAGE_BASEURL + url;
            Picasso.with(mContext).load(imageURL).into(imageView);
        }

        else {
            //When there is no poster image display "No poster" image.
            Picasso.with(mContext).load(noPosterUrl).into(imageView);
        }

        return view;

    }
}
