package com.example.weather.repositories;


import android.content.Context;

import com.example.weather.models.Location;
import com.example.weather.models.WeatherDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherDetailsRepository {

    private static WeatherDetailsRepository instance;
    private static Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final String OPEN_WEATHER_MAP_URL = "https://api.openweathermap.org/";
    private static final String APP_ID = "dac7dbdbaae7162c1e29455b4ae56803";

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

    public MutableLiveData<WeatherDetails> getWeatherDetails() {
        final MutableLiveData<WeatherDetails> weatherData = new MutableLiveData<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OPEN_WEATHER_MAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherApi openWeatherApi = retrofit.create(OpenWeatherApi.class);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("lat", String.valueOf(mLocation.getLatitude()));
        parameters.put("lon", String.valueOf(mLocation.getLongitude()));
        parameters.put("appid", APP_ID);
        parameters.put("units", "metric");
        final WeatherDetails weatherDetails = new WeatherDetails();
        Call<WeatherDetails> call = openWeatherApi.getWeatherDetails(parameters);
        call.enqueue(new Callback<WeatherDetails>() {
            @Override
            public void onResponse(Call<WeatherDetails> call, Response<WeatherDetails> response) {
                if (!response.isSuccessful()) {
                    weatherDetails.setResponseState(false);
                    weatherData.setValue(weatherDetails);
                    return;
                }

                weatherData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<WeatherDetails> call, Throwable t) {
                weatherDetails.setResponseState(false);
                weatherData.setValue(weatherDetails);
            }
        });
        return weatherData;
    }
}