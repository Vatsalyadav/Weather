package com.example.weather.repositories;

import com.example.weather.models.WeatherDetails;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface OpenWeatherApi {

    @GET("data/2.5/onecall")
    Call<WeatherDetails> getWeatherDetails(
            @QueryMap Map<String, String> params
    );

}
