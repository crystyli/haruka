package com.example.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Crystyle on 2016-03-19.
 */
public class DeviderItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    public static final int HORINZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Drawable mDivider;
    private int mOrientation;

    public DeviderItemDecoration(Context context,int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }
    public void setOrientation(int orientation){
        if(orientation !=HORINZONTAL_LIST && orientation!=VERTICAL_LIST){
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }
}
