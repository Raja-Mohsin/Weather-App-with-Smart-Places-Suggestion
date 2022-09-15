package com.example.weatherappnew.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.weatherappnew.R;
import com.example.weatherappnew.adapter.GridViewAdapterCategoryScreen;

public class CategoriesNearbyPlacesActivity extends AppCompatActivity {

    GridView gridViewCategoryScreen;
    String[] titlesForGridView = {"Parks", "Restaurants", "Cinemas", "Shopping Stores"};
    int[] imagesForGridView = {R.drawable.parks, R.drawable.hotels, R.drawable.cinema, R.drawable.shopping};

    //variables for intent
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_nearby_places);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("lat", 33.3);
        longitude = intent.getDoubleExtra("long", 73.07);

        gridViewCategoryScreen = findViewById(R.id.categoriesNearbyPlacesGridView);
        GridViewAdapterCategoryScreen gridViewAdapterCategoryScreen = new GridViewAdapterCategoryScreen(getApplicationContext(), imagesForGridView, titlesForGridView);
        gridViewCategoryScreen.setAdapter(gridViewAdapterCategoryScreen);

        gridViewCategoryScreen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CategoriesNearbyPlacesActivity.this, CategoryNearbyPlacesDetailActivity.class);
                intent.putExtra("selectedCategory", titlesForGridView[i]);
                intent.putExtra("lat", latitude);
                intent.putExtra("long", longitude);
                startActivity(intent);
            }
        });
    }
}