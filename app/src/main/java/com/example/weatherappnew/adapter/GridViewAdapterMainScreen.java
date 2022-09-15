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

public class GridViewAdapterMainScreen extends BaseAdapter {
    Context context;
    int[] logos;
    String[] titles;
    ArrayList<String> values;
    LayoutInflater inflater;
    public GridViewAdapterMainScreen(Context applicationContext, int[] mLogos, String[] mTitles, ArrayList<String> mValues) {
        this.context = applicationContext;
        this.logos = mLogos;
        this.titles = mTitles;
        this.values = mValues;
        inflater = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return values.size();
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
        view = inflater.inflate(R.layout.single_grid_item_home_screen, null);

        //fetch views by ids
        TextView title = view.findViewById(R.id.singleGridItemTitle);
        TextView value = view.findViewById(R.id.singleGridItemValue);
        ImageView icon = view.findViewById(R.id.singleGridItemIcon);

        //set values to views
        title.setText(titles[i]);
        value.setText(values.get(i));
        icon.setImageResource(logos[i]);

        return view;
    }
}
