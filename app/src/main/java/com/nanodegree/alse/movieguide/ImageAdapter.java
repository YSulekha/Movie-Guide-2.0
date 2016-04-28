package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by aharyadi on 4/11/16.
 */
public class ImageAdapter extends ArrayAdapter<String> {
    Context mContext;
    LayoutInflater mInflater;
    int mResource;
    int mFieldId;
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
        View v;
        if(convertView == null) {
          v=  mInflater.inflate(mResource, parent, false);
        }
        else{
            v=convertView;
        }
        ImageView imageView = (ImageView)v.findViewById(R.id.movie_poster_image);
        String [] split = getItem(position).split("-");
        String imageURL = IMAGE_BASEURL + split[0];


        Picasso.with(mContext).load(imageURL).into(imageView);


    /*    TextView textView = (TextView)v.findViewById(R.id.movie_poster_rating);
        if(textView!=null){
            Log.v("Inside Addapter", textView.toString());
            textView.setText(split[1]);
        }*/

   //     textView.setText(split[1]);
        return v;
    }



}
