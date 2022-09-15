package com.example.weatherappnew.model;

import com.google.android.gms.maps.model.LatLng;

public class ChoosenLatLng {
    public static LatLng choosenLatLng;

    public ChoosenLatLng(LatLng latLng)
    {
        choosenLatLng = new LatLng(latLng.latitude, latLng.longitude);
    }

    public static LatLng getChoosenLatLng() {
        return choosenLatLng;
    }

    public static void setChoosenLatLng(LatLng choosenLatLng) {
        ChoosenLatLng.choosenLatLng = choosenLatLng;
    }
}
