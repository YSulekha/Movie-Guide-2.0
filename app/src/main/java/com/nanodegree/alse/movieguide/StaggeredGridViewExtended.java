package com.nanodegree.alse.movieguide;

import android.content.Context;
import android.util.AttributeSet;

import com.etsy.android.grid.StaggeredGridView;

/**
 * Created by aharyadi on 6/17/16.
 */
public class StaggeredGridViewExtended extends StaggeredGridView {
    public StaggeredGridViewExtended(Context context) {
        super(context);
    }
    public StaggeredGridViewExtended(Context context, AttributeSet attributes) {
        super(context, attributes);
    }
    boolean columnCountChanged = false;
    @Override
    protected void onSizeChanged(int w, int h) {
        super.onSizeChanged(w, h);
        if(!columnCountChanged && getWidth() > 0) {
            columnCountChanged = true;
            this.setColumnCount(4);
        }
    }
}
