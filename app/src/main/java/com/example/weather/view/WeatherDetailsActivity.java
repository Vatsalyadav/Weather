package com.example.weather.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.models.Location;
import com.example.weather.models.WeatherDetails;
import com.example.weather.viewmodels.WeatherDetailsActivityViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class WeatherDetailsActivity extends AppCompatActivity {
    private static final String TAG = "WeatherDetailsActivity";
    private int locationRequestCode = 1000;
    private WeatherDetailsActivityViewModel mWeatherDetailsActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        displayToolbar();
        initWeatherDetailsViewModel();
        initLocation();
    }

    private void displayToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_weather_details);
        setSupportActionBar(toolbar);
    }

    private void initLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted
            fetchLocation();
        }
    }

    private void fetchLocation() {
        mWeatherDetailsActivityViewModel.setLocationDetails();
        mWeatherDetailsActivityViewModel.getLocationDetails().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                Log.e(TAG, "onChanged: " + location.getLatitude() + "  " + location.getLongitude());
                if (location.getLatitude() != 0.0 && location.getLongitude() != 0)
                    fetchWeatherData();
            }
        });
    }

    private void fetchWeatherData() {
        mWeatherDetailsActivityViewModel.setWeatherDetails();
        mWeatherDetailsActivityViewModel.getWeatherDetails().observe(this, new Observer<WeatherDetails>() {
            @Override
            public void onChanged(@Nullable WeatherDetails weatherDetails) {
                Log.e(TAG, "fetchWeatherData: " + weatherDetails.getResponseState() + "  ");
            }
        });
    }

    private void initWeatherDetailsViewModel() {
        try {
            mWeatherDetailsActivityViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(WeatherDetailsActivityViewModel.class);
            mWeatherDetailsActivityViewModel.initRepository();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to get UI Data, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

}
