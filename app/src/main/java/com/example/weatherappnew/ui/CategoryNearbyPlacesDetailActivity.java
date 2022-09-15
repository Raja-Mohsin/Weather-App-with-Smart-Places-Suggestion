package com.example.weatherappnew.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherappnew.InterfaceCategoryDetailOnItemClick;
import com.example.weatherappnew.R;
import com.example.weatherappnew.adapter.RecyclerViewAdapterCategoryDetail;
import com.example.weatherappnew.model.PlaceModel;
import com.google.android.gms.location.places.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryNearbyPlacesDetailActivity extends AppCompatActivity {

    TextView topText;
    TextView totalResultsText;
    TextView emptyListMessage;
    TextView radiusText;
    ArrayList<PlaceModel> fetchedPlaces;
    RecyclerView recyclerViewCategoryDetailActivity;
    RecyclerViewAdapterCategoryDetail recyclerViewAdapterCategoryDetail;
    double radiusToBePassed;
    int totalResults;

    //variables for intent
    double latitude;
    double longitude;

    //variables for shared prefs
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String rTEXT = "radius";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_nearby_places_detail);

        Intent intent = getIntent();
        String selectedCategory = intent.getStringExtra("selectedCategory");
        //fetch lat long
        latitude = intent.getDoubleExtra("lat", 33.3);
        longitude = intent.getDoubleExtra("long", 73.07);

        //fetch radius
        loadTemperatureUnitData();

        topText = findViewById(R.id.topTextCategoryDetailActivity);
        totalResultsText = findViewById(R.id.totalResultsTextView);
        radiusText = findViewById(R.id.radiusTextView);
        emptyListMessage = findViewById(R.id.emptyListMessage);

        radiusText.setText("Search Radius: " + (int)radiusToBePassed + " meters");
        emptyListMessage.setVisibility(View.INVISIBLE);

        fetchedPlaces = new ArrayList<PlaceModel>();
        recyclerViewCategoryDetailActivity = findViewById(R.id.recyclerViewCategoryDetailActivity);
        recyclerViewAdapterCategoryDetail = new RecyclerViewAdapterCategoryDetail(getApplicationContext(), fetchedPlaces, new InterfaceCategoryDetailOnItemClick() {
            @Override
            public void onItemClick(int position) {
                Intent intent1 = new Intent(CategoryNearbyPlacesDetailActivity.this, PlaceDetailActivity.class);
                PlaceModel selectedPlace = fetchedPlaces.get(position);
                String placeName1 = selectedPlace.getName();
                String latitude1 = selectedPlace.getLatitude();
                String longitude1 = selectedPlace.getLongitude();
                intent1.putExtra("placeName",placeName1);
                intent1.putExtra("latitude",latitude1);
                intent1.putExtra("longitude",longitude1);
                startActivity(intent1);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewCategoryDetailActivity.setAdapter(recyclerViewAdapterCategoryDetail);
        recyclerViewCategoryDetailActivity.setLayoutManager(layoutManager);

        String categoryQueryKeyWord = "";
        switch (selectedCategory)
        {
            case "Parks":
                categoryQueryKeyWord = "park";
                topText.setText("Nearby Parks & Gardens");
                break;
            case "Cinemas":
                categoryQueryKeyWord = "movie_theater";
                topText.setText("Nearby Cinemas & Theatres");
                break;
            case "Restaurants":
                categoryQueryKeyWord = "restaurant";
                topText.setText("Nearby Restaurants & Hotels");
                break;
            case "Shopping Stores":
                categoryQueryKeyWord = "shopping_mall";
                topText.setText("Nearby Shopping & Grocery Stores");
                break;
        }
        sendQueryAndFetchData(categoryQueryKeyWord, latitude, longitude, radiusToBePassed);
    }

    public void loadTemperatureUnitData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        radiusToBePassed = sharedPreferences.getFloat(rTEXT,3000);
    }

    public void sendQueryAndFetchData(String categoryQueryKeyword, double latitude, double longitude, double radius)
    {
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(radius);
        googlePlacesUrl.append("&types=").append(categoryQueryKeyword);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyByVdzDd2TZwqXqFxfoJRPgJJJviizwGdM");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, googlePlacesUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                fetchedPlaces.clear();
                try {
                    JSONObject heroObject = new JSONObject(response);
                    JSONArray heroArray = heroObject.getJSONArray("results");
                    String lat, lng, name, address, icon;
                    for(int i=0; i<heroArray.length(); i++)
                    {
                        JSONObject testObject = heroArray.getJSONObject(i);
                        lat = testObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
                        lng = testObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
                        name = testObject.getString("name");
                        address = testObject.getString("vicinity");
                        icon = testObject.getString("icon");

                        System.out.println("LENGTH OF THE ARRAY IS: " + heroArray.length());
                        System.out.println("THE LATITUDE IS: "+lat);
                        System.out.println("THE LONGITUDE IS: "+lng);
                        System.out.println("THE NAME OF PLACE IS: "+name);
                        System.out.println("THE ADDRESS OF PLACE IS: "+address);
                        System.out.println("THE ICON IS: "+icon);

                        fetchedPlaces.add(new PlaceModel(name, icon, address, lat, lng));
                        totalResults = totalResults + 1;
                        System.out.println("LENGTH OF FETCHED PLACES = "+fetchedPlaces.size());
                    }
                    recyclerViewAdapterCategoryDetail.notifyDataSetChanged();
                    totalResultsText.setText("Total Results = "+totalResults);
                    if(totalResults<1)
                    {
                        recyclerViewCategoryDetailActivity.setVisibility(View.INVISIBLE);
                        emptyListMessage.setText("There are no " + topText.getText().toString() + " to visit in your radius, try increasing the radius in the settings to see some places to visit");
                        emptyListMessage.setVisibility(View.VISIBLE);
                    }
                }
                catch (JSONException exception)
                {
                    exception.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("THERE IS AN ERROR"+error.toString()+"  "+error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}