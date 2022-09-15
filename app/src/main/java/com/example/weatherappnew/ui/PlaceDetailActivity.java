package com.example.weatherappnew.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherappnew.R;
import com.example.weatherappnew.model.RecyclerViewItemModelHomeScreen;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PlaceDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    //variables for views
    SupportMapFragment supportMapFragment;
    TextView placeNameTextView, temperatureTextView, conditionTextView, windSpeedTextView, humidityTextView, rainPercentageTextView;

    //variables for shared preferences
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String cTEXT = "tempC";
    public static final String fTEXT = "tempF";
    boolean loadedTempC;
    boolean loadedTempF;

    //variables for api data fetching
    String placeName, cityName;
    double latitude, longitude;
    String rainPercentage, humidity, windSpeed;
    String temperatureC, temperatureF, condition, conditionIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        //initialize the view variables
        placeNameTextView = findViewById(R.id.placeNameInCardView);
        temperatureTextView = findViewById(R.id.tempInCardView);
        conditionTextView = findViewById(R.id.conditionTextInCardView);
        windSpeedTextView = findViewById(R.id.windSpeedInCardView);
        humidityTextView = findViewById(R.id.humidityInCardView);
        rainPercentageTextView = findViewById(R.id.rainPercentageInCardView);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentPlaceDetail);
        supportMapFragment.getMapAsync(PlaceDetailActivity.this);

        //get data from intent
        Intent intent = getIntent();
        placeName = intent.getStringExtra("placeName");
        latitude = Double.parseDouble(intent.getStringExtra("latitude"));
        longitude = Double.parseDouble(intent.getStringExtra("longitude"));

        //load data from shared prefs
        loadTemperatureUnitData();

        //get city name from lat long
        cityName = getCityName(longitude, latitude);

        //get weather info
        getWeatherInfo(cityName);
    }

    private String getCityName(double longitude, double latitude)
    {
        String cityName = "";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = gcd.getFromLocation(latitude, longitude, 5);
            //countryName = addressList.get(0).getCountryName();
            cityName = addressList.get(0).getLocality();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=17094eb917ca46689d575935212508&q="+cityName+"&days=1&aqi=no&alerts=no";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject heroObject = new JSONObject(response);

                    //get values from api response
                    rainPercentage = heroObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getString("daily_chance_of_rain").toString();
                    humidity = heroObject.getJSONObject("current").getString("humidity").toString();
                    windSpeed = heroObject.getJSONObject("current").getString("wind_kph").toString();
                    temperatureC = heroObject.getJSONObject("current").getString("temp_c").toString();
                    temperatureF = heroObject.getJSONObject("current").getString("temp_f").toString();
                    condition = heroObject.getJSONObject("current").getJSONObject("condition").getString("text");
                    conditionIcon = heroObject.getJSONObject("current").getJSONObject("condition").getString("icon");

                    //set values to the views
                    if(loadedTempC)
                    {
                        temperatureTextView.setText(temperatureC+"°C");
                    }
                    else
                    {
                        temperatureTextView.setText(temperatureF+"°F");
                    }
                    windSpeedTextView.setText("Wind Speed: "+windSpeed+"Km/h");
                    conditionTextView.setText(condition);
                    humidityTextView.setText("Humidity: "+humidity+"g");
                    rainPercentageTextView.setText("Rain%"+rainPercentage);
                    placeNameTextView.setText(placeName);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error"+error.toString());
                Toast.makeText(getApplicationContext(), "Failed to load weather info", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(placeName);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);
    }

    public void loadTemperatureUnitData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loadedTempC = sharedPreferences.getBoolean(cTEXT, true);
        loadedTempF = sharedPreferences.getBoolean(fTEXT, false);
    }
}