package com.example.weather.repositories;


import android.content.Context;

import com.example.weather.models.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.lifecycle.MutableLiveData;

public class WeatherDetailsRepository {

    private static WeatherDetailsRepository instance;
    private static Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    public static WeatherDetailsRepository getInstance() {
        if (instance == null) {
            instance = new WeatherDetailsRepository();
        }
        return instance;
    }

    public MutableLiveData<Location> getLocation(Context context) {
        final MutableLiveData<Location> data = new MutableLiveData<>();
        mLocation = new Location();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if (location != null) {
                    mLocation.setLatitude(location.getLatitude());
                    mLocation.setLongitude(location.getLongitude());
                    data.setValue(mLocation);
                }
            }
        });
        data.setValue(mLocation);
        return data;
    }
}