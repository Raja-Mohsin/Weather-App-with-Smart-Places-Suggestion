package com.example.weatherappnew.ui;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherappnew.model.ChoosenLatLng;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
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
import com.example.weatherappnew.adapter.GridViewAdapterMainScreen;
import com.example.weatherappnew.adapter.RecyclerViewAdapterMainScreen;
import com.example.weatherappnew.model.RecyclerViewItemModelHomeScreen;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //variables for recycler view
    RecyclerView recyclerViewHomeScreen;
    RecyclerViewAdapterMainScreen recyclerViewAdapterMainScreen;
    ArrayList<RecyclerViewItemModelHomeScreen> dataForRecyclerViewHomeScreen;

    //variables for main card
    TextView temperatureTextView;
    TextView conditionTextView;
    ImageView conditionImageView;
    TextView cityTextView;
    ImageView settingsButton;
    ImageView searchButton;
    ImageView refreshWeatherIcon;

    Button nearbyPlacesButton;

    //variables for grid view
    GridView gridViewHomeScreen;
    private String feelsLikeC;
    private String feelsLikeF;
    private String rainPercentage;
    private String humidity;
    private String windSpeed;
    private String latLong;
    private String lastUpdated;
    GridViewAdapterMainScreen gridViewAdapterMainScreen;
    int[] iconsForHomeGrid = {R.drawable.feelslike, R.drawable.rain, R.drawable.humidity, R.drawable.wind, R.drawable.location, R.drawable.update};
    String[] titlesForHomeGrid = {"Feels Like", "Rain %", "Humidity", "Wind Speed", "Lat/Long", "Updated"};
    ArrayList<String> valuesForHomeGrid;

    //variables for shared preferences
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String cTEXT = "tempC";
    public static final String fTEXT = "tempF";
    boolean loadedTempC;
    boolean loadedTempF;

    //variables for location and place picker
    private int PERMISSION_CODE = 1;
    private String cityNameGlobal;
    private LocationManager locationManager;
    private String countryName;
    int REQUEST_CODE = 1;

    //variables for intent
    double latitude, longitude;

    //variables for checking network connectivity
    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTextView = findViewById(R.id.temperatureTextView);
        conditionTextView = findViewById(R.id.conditionTextView);
        conditionImageView = findViewById(R.id.conditionImageView);
        cityTextView = findViewById(R.id.cityTextView);
        settingsButton = findViewById(R.id.settingsButton);
        searchButton = findViewById(R.id.searchButton);
        nearbyPlacesButton = findViewById(R.id.seeNearbyPlacesButton);
        refreshWeatherIcon = findViewById(R.id.refreshButton);

        dataForRecyclerViewHomeScreen = new ArrayList<RecyclerViewItemModelHomeScreen>();

        loadTemperatureUnitData();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        //check the network connection of the device
        connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            //device is connected
            connected = true;
        }
        else
        {
            //device is not connected
            connected = false;
        }

        if(!connected)
        {
            startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            return;
        }

        valuesForHomeGrid = new ArrayList<String>();

        gridViewHomeScreen = findViewById(R.id.gridViewHomeScreen);
        gridViewAdapterMainScreen = new GridViewAdapterMainScreen(getApplicationContext(), iconsForHomeGrid, titlesForHomeGrid, valuesForHomeGrid);
        gridViewHomeScreen.setAdapter(gridViewAdapterMainScreen);

        recyclerViewHomeScreen = findViewById(R.id.recyclerViewHomeScreen);
        recyclerViewAdapterMainScreen = new RecyclerViewAdapterMainScreen(this, dataForRecyclerViewHomeScreen);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHomeScreen.setAdapter(recyclerViewAdapterMainScreen);
        recyclerViewHomeScreen.setLayoutManager(layoutManager);

        nearbyPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoriesNearbyPlacesActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("long", longitude);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchPlaceActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("long", longitude);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        refreshWeatherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location!=null){
                    if(ChoosenLatLng.choosenLatLng == null)
                    {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                    else
                    {
                        latitude = ChoosenLatLng.choosenLatLng.latitude;
                        longitude = ChoosenLatLng.choosenLatLng.longitude;
                    }
                    System.out.println("\nLATITUDE: "+latitude+", LONGITUDE: "+longitude+"\n");
                    cityNameGlobal = getCityName(longitude, latitude);
                    getWeatherInfo(cityNameGlobal);
                }
            }
            else{
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, this);
                double pickedLatitude = place.getLatLng().latitude;
                double pickedLongitude = place.getLatLng().longitude;
                //String pickedCityName = getCityName(pickedLongitude, pickedLongitude);
                Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(pickedLatitude, pickedLongitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String pickedCityName = "City not found";
                if (addresses.size() > 0) {
                    pickedCityName = addresses.get(0).getLocality();
                }
                getWeatherInfo(pickedCityName);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void loadTemperatureUnitData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loadedTempC = sharedPreferences.getBoolean(cTEXT, true);
        loadedTempF = sharedPreferences.getBoolean(fTEXT, false);
    }

    private String getCityName(double longitude, double latitude)
    {
        String cityName = "";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = gcd.getFromLocation(latitude, longitude, 5);
            countryName = addressList.get(0).getCountryName();
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
        cityTextView.setText(new StringBuilder().append(cityName).append(", ").append(countryName).toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dataForRecyclerViewHomeScreen.clear();
                valuesForHomeGrid.clear();

                try {
                    JSONObject heroObject = new JSONObject(response);

                    //get values for grid view items from api response
                    feelsLikeC = heroObject.getJSONObject("current").getString("feelslike_c").toString();
                    feelsLikeF = heroObject.getJSONObject("current").getString("feelslike_f").toString();
                    rainPercentage = heroObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getString("daily_chance_of_rain").toString();
                    humidity = heroObject.getJSONObject("current").getString("humidity").toString();
                    windSpeed = heroObject.getJSONObject("current").getString("wind_kph").toString();
                    latLong = heroObject.getJSONObject("location").getString("lat")+","+heroObject.getJSONObject("location").getString("lon").toString();
                    lastUpdated = heroObject.getJSONObject("current").getString("last_updated").toString();

                    String temperatureC = heroObject.getJSONObject("current").getString("temp_c").toString();
                    String temperatureF = heroObject.getJSONObject("current").getString("temp_f").toString();
                    countryName = heroObject.getJSONObject("location").getString("country").toString();
                    if(loadedTempF)
                    {
                        temperatureTextView.setText(temperatureF+"°F");
                    }
                    else
                    {
                        temperatureTextView.setText(temperatureC+"°C");
                    }
                    String condition = heroObject.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = heroObject.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(conditionImageView);
                    conditionTextView.setText(condition);
                    JSONObject forecastObject = heroObject.getJSONObject("forecast");
                    JSONObject forecastArray = forecastObject.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hoursArray = forecastArray.getJSONArray("hour");
                    String extractedTemperatureToBePassed;
                    for(int i=0; i<hoursArray.length(); i++)
                    {
                        JSONObject object = hoursArray.getJSONObject(i);
                        String extractedTime = object.getString("time");
                        String extractedTemperatureC = object.getString("temp_c");
                        String extractedTemperatureF = object.getString("temp_f");
                        String extractedIcon = object.getJSONObject("condition").getString("icon");
                        String extractedWindSpeed = object.getString("wind_kph");

                        if(loadedTempF)
                        {
                            extractedTemperatureToBePassed = extractedTemperatureF;
                        }
                        else
                        {
                            extractedTemperatureToBePassed = extractedTemperatureC;
                        }

                        dataForRecyclerViewHomeScreen.add(new RecyclerViewItemModelHomeScreen(extractedTime, extractedTemperatureToBePassed, extractedIcon));
                    }
                    if(loadedTempF)
                    {
                        valuesForHomeGrid.add(feelsLikeF+"°F");
                    }
                    else
                    {
                        valuesForHomeGrid.add(feelsLikeC+"°C");
                    }
                    valuesForHomeGrid.add(rainPercentage+"%");
                    valuesForHomeGrid.add(humidity+" g/kg");
                    valuesForHomeGrid.add(windSpeed+" Km/h");
                    valuesForHomeGrid.add(latLong);
                    valuesForHomeGrid.add(lastUpdated);
                    gridViewAdapterMainScreen.notifyDataSetChanged();
                    recyclerViewAdapterMainScreen.notifyDataSetChanged();
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

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}