package com.example.weatherappnew.ui;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.weatherappnew.R;
import com.example.weatherappnew.model.ChoosenLatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SearchPlaceActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    SupportMapFragment supportMapFragment;
    Button selectLocationButton;
    SearchView searchView;
    LatLng choosenLatlng;
    ChoosenLatLng finalLatLng;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("lat", 33.06);
        longitude = intent.getDoubleExtra("long", 77.07);

        selectLocationButton = findViewById(R.id.selectLocationButtonSearchActivity);
        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchView = findViewById(R.id.searchView);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.searchPlaceMapFragment);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                map.clear();
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if(location!=null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(SearchPlaceActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).title(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    choosenLatlng = new LatLng(latLng.latitude, latLng.longitude);
                    finalLatLng = new ChoosenLatLng(choosenLatlng);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng currentLatLng = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
                map.addMarker(new MarkerOptions().position(latLng));
                choosenLatlng = new LatLng(latLng.latitude, latLng.longitude);
                finalLatLng = new ChoosenLatLng(latLng);
            }
        });
    }
}