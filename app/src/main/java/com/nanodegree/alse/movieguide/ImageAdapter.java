package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


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
        String imageURL;
        if(convertView == null) {
            view =  mInflater.inflate(mResource, parent, false);
        }
        else{
            view =convertView;
        }
        ImageView imageView = (ImageView)view.findViewById(R.id.movie_poster_image);
        imageURL = IMAGE_BASEURL + getItem(position);
        Picasso.with(mContext).load(imageURL).into(imageView);
        return view;
    }
}
