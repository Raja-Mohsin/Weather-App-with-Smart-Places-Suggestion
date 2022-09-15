package com.example.weatherappnew.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Placeholder;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherappnew.InterfaceCategoryDetailOnItemClick;
import com.example.weatherappnew.R;
import com.example.weatherappnew.model.PlaceModel;
import com.example.weatherappnew.model.RecyclerViewItemModelHomeScreen;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewAdapterCategoryDetail extends RecyclerView.Adapter<RecyclerViewAdapterCategoryDetail.MyViewHolder2>{

    private ArrayList<PlaceModel> fetchedPlacesList;
    private Context context;
    private InterfaceCategoryDetailOnItemClick onItemClick;

    public RecyclerViewAdapterCategoryDetail(Context mContext, ArrayList<PlaceModel> fetchedPlaces, InterfaceCategoryDetailOnItemClick mOnItemClick)
    {
        this.context = mContext;
        this.fetchedPlacesList = fetchedPlaces;
        this.onItemClick = mOnItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = LayoutInflater.from(context).inflate(R.layout.single_recycler_view_item_category_detail_screen, parent, false);
        MyViewHolder2 viewHolder = new MyViewHolder2(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
        //set values to the items
        String extractedTitle = fetchedPlacesList.get(position).getName();
        holder.title.setText(extractedTitle);

        String extractedAddress = fetchedPlacesList.get(position).getAddress();
        holder.address.setText(extractedAddress);

        String extractedIcon = fetchedPlacesList.get(position).getImage();
        Picasso.get().load(extractedIcon).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return fetchedPlacesList.size();
    }


    public class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //declare items
        CardView parentCardView;
        TextView title, address;
        ImageView icon;
        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);
            //find items by their ids
            title = itemView.findViewById(R.id.single_recycler_view_category_detail_title);
            address = itemView.findViewById(R.id.single_recycler_view_category_detail_address);
            icon = itemView.findViewById(R.id.single_recycler_view_category_detail_icon);
            parentCardView = itemView.findViewById(R.id.singleParentCardViewCategoryDetailScreen);
            parentCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.singleParentCardViewCategoryDetailScreen:
                    onItemClick.onItemClick(getAdapterPosition());
                    break;
            }
        }
    }
}
