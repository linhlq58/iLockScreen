package com.tetstudio.ilockscreen.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 10/14/2016.
 */
public class GalleryAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<Integer> imgId;

    public GalleryAdapter(Activity context, ArrayList<Integer> imgId) {
        this.context = context;
        this.imgId = imgId;
    }

    @Override
    public int getCount() {
        return imgId.size();
    }

    @Override
    public Object getItem(int i) {
        return imgId.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(imgId.get(i));
        imageView.setLayoutParams(new Gallery.LayoutParams(500, 500));
        return imageView;
    }
}
