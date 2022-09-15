package com.example.weatherappnew.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherappnew.R;
import com.example.weatherappnew.model.RecyclerViewItemModelHomeScreen;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewAdapterMainScreen extends RecyclerView.Adapter<RecyclerViewAdapterMainScreen.MyViewHolder> {

    private ArrayList<RecyclerViewItemModelHomeScreen> data;
    private Context context;

    //variables for shared preferences
    //variables for shared preferences
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String cTEXT = "tempC";
    public static final String fTEXT = "tempF";
    boolean loadedTempC;
    boolean loadedTempF;

    public RecyclerViewAdapterMainScreen(Context mContext, ArrayList<RecyclerViewItemModelHomeScreen> mData)
    {
        this.context = mContext;
        this.data = mData;
    }

    public void loadTemperatureUnitData()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loadedTempC = sharedPreferences.getBoolean(cTEXT, true);
        loadedTempF = sharedPreferences.getBoolean(fTEXT, false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = LayoutInflater.from(context).inflate(R.layout.single_recycler_view_item_home_screen, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        loadTemperatureUnitData();
        //set values to the item
        RecyclerViewItemModelHomeScreen model = data.get(position);
        if(loadedTempF)
        {
            holder.temperatureTextView.setText(model.getTemperature()+"°F");
        }
        else
        {
            holder.temperatureTextView.setText(model.getTemperature()+"°C");
        }
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.iconImageView);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa");
        try
        {
            Date date = inputFormat.parse(model.getTime());
            holder.timeTextView.setText(outputFormat.format(date));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        //declare items
        TextView timeTextView;
        ImageView iconImageView;
        TextView temperatureTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //find items by their ids
            timeTextView = itemView.findViewById(R.id.singleRecyclerItemTime);
            iconImageView = itemView.findViewById(R.id.singleRecyclerItemIcon);
            temperatureTextView = itemView.findViewById(R.id.singleRecyclerItemTemperature);
        }
    }
}
