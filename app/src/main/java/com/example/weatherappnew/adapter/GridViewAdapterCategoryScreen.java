package com.example.weatherappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherappnew.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GridViewAdapterCategoryScreen extends BaseAdapter {
    Context context;
    int[] images;
    String[] titles;
    LayoutInflater inflater;
    public GridViewAdapterCategoryScreen(Context applicationContext, int[] mImages, String[] mTitles) {
        this.context = applicationContext;
        this.images = mImages;
        this.titles = mTitles;
        inflater = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return images.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //inflate the layout
        view = inflater.inflate(R.layout.single_categories_nearby_places_grid_item, null);

        //fetch views by ids
        TextView title = view.findViewById(R.id.singleCategoryGridItemTextView);
        ImageView image = view.findViewById(R.id.singleCategoryGridItemImageView);

        //set values to views
        title.setText(titles[i]);
        image.setImageResource(images[i]);

        return view;
    }
}
