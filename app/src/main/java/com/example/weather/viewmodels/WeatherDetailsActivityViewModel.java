package com.example.weather.viewmodels;


import android.app.Application;
import android.content.Context;

import com.example.weather.models.Location;
import com.example.weather.models.WeatherDetails;
import com.example.weather.repositories.WeatherDetailsRepository;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WeatherDetailsActivityViewModel extends AndroidViewModel {
    private MutableLiveData<Location> mLocationDetails;
    private MutableLiveData<WeatherDetails> mWeatherDetails;
    private WeatherDetailsRepository mWeatherDetailsRepository;
    private Context context = getApplication().getApplicationContext();

    public WeatherDetailsActivityViewModel(Application application) {
        super(application);
    }

    public void initRepository() {
        if (mLocationDetails != null) {
            return;
        }
        mWeatherDetailsRepository = WeatherDetailsRepository.getInstance();
    }

    public LiveData<Location> getLocationDetails() {
        return mLocationDetails;
    }

    public void setLocationDetails() {
        mLocationDetails = mWeatherDetailsRepository.getLocation(context);
    }

    public void setWeatherDetails() {
        mWeatherDetails = mWeatherDetailsRepository.getWeatherDetails();
    }

    public LiveData<WeatherDetails> getWeatherDetails() {
        return mWeatherDetails;
    }

}
