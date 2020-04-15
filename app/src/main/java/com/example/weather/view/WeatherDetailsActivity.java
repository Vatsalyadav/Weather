package com.example.weather.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.models.Daily;
import com.example.weather.models.Hourly;
import com.example.weather.models.Location;
import com.example.weather.models.WeatherDetails;
import com.example.weather.viewmodels.WeatherDetailsActivityViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    private Boolean showWeeklyForecast = true;

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

    private LineChart hourlyTempChart, weeklyTempChart;

    private void fetchWeatherData() {
        mWeatherDetailsActivityViewModel.setWeatherDetails();
        mWeatherDetailsActivityViewModel.getWeatherDetails().observe(this, new Observer<WeatherDetails>() {
            @Override
            public void onChanged(@Nullable WeatherDetails weatherDetails) {
                if (weatherDetails.getResponseState()) {
                    updateUI(weatherDetails);
                }
            }
        });
    }

    public void weeklyForecast(View view) {
        View redLayout = findViewById(R.id.hidden_forecast_layout);
        ViewGroup parent = findViewById(R.id.parent_layout_picture_details);
        MaterialButton buttonForecast = findViewById(R.id.button_forecast);

        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(R.id.hidden_forecast_layout);

        TransitionManager.beginDelayedTransition(parent, transition);
        redLayout.setVisibility(showWeeklyForecast ? View.VISIBLE : View.GONE);
        buttonForecast.setText(showWeeklyForecast ? "Hide weekly Forecast" : "Show weekly Forecast");
        showWeeklyForecast = !showWeeklyForecast;
    }

    private void updateUI(WeatherDetails weatherDetails) {
        setWeatherDetailsUI(weatherDetails);

        hourlyTempChart = findViewById(R.id.hourly_temp_chart);
        List<Hourly> hourlyData = weatherDetails.getHourly();
        ArrayList<String> xLabelHour = new ArrayList<>();
        ArrayList<Entry> yAxisValHour = new ArrayList<>();
        int i = 0;
        for (Hourly hour : hourlyData) {
            yAxisValHour.add(new Entry(i, (hour.getTemp()).floatValue()));
            xLabelHour.add(formatTime(hour.getDt(), "HH:mm"));
            i++;
        }
        setTemperatureChart(xLabelHour, yAxisValHour, null, hourlyTempChart);

        weeklyTempChart = findViewById(R.id.weekly_temp_chart);
        List<Daily> dailyData = weatherDetails.getDaily();
        ArrayList<String> xLabelWeek = new ArrayList<>();
        ArrayList<Entry> yaxisValMin = new ArrayList<>();
        ArrayList<Entry> yaxisValMax = new ArrayList<>();
        i = 0;
        for (Daily daily : dailyData) {
            yaxisValMin.add(new Entry(i, (daily.getTemp().getMin()).floatValue()));
            yaxisValMax.add(new Entry(i, (daily.getTemp().getMax()).floatValue()));
            xLabelWeek.add(formatTime(daily.getDt(), "EEE"));
            i++;
        }
        setTemperatureChart(xLabelWeek, yaxisValMin, yaxisValMax, weeklyTempChart);

    }

    private void setWeatherDetailsUI(WeatherDetails weatherDetails) {
        TextView currentTemperatureText = findViewById(R.id.current_temperature);
        TextView weatherDescText = findViewById(R.id.weather_desc);
        TextView windSpeedText = findViewById(R.id.wind_speed);
        TextView humidityText = findViewById(R.id.humidity);
        TextView uvText = findViewById(R.id.uv_index);
        TextView pressureText = findViewById(R.id.pressure);
        TextView visibilityText = findViewById(R.id.visibility);
        TextView cloudCoverText = findViewById(R.id.cloud_cover);
        TextView dewPointText = findViewById(R.id.dew_point);
        TextView refreshTimeText = findViewById(R.id.refresh_time);

        String currentTemperature, weatherDesc, windSpeed, humidity, uvIndex, pressure,
                visibility, cloudCover, dewPoint, refreshTime;
        currentTemperature = weatherDetails.getCurrent().getTemp() + "\u00B0";
        weatherDesc = weatherDetails.getCurrent().getWeather().get(0).getMain() +
                ", Feels Like " + String.format(Locale.US, "%.1f", weatherDetails.getCurrent().getFeelsLike())
                + "\u00B0C";
        windSpeed = weatherDetails.getCurrent().getWindSpeed() + " km/hr";
        humidity = weatherDetails.getCurrent().getHumidity() + "%";
        uvIndex = weatherDetails.getCurrent().getUvi() + "";
        pressure = weatherDetails.getCurrent().getPressure() + " mb";
        visibility = weatherDetails.getCurrent().getVisibility() / 1000 + " km";
        cloudCover = weatherDetails.getCurrent().getClouds() + "%";
        dewPoint = String.format(Locale.US, "%.1f", weatherDetails.getCurrent().getDewPoint()) + "\u00B0C";
        refreshTime = "Refresh at " + formatTime(weatherDetails.getCurrent().getDt(), "hh:mm aa");

        currentTemperatureText.setText(currentTemperature);
        weatherDescText.setText(weatherDesc);
        windSpeedText.setText(windSpeed);
        humidityText.setText(humidity);
        uvText.setText(uvIndex);
        pressureText.setText(pressure);
        visibilityText.setText(visibility);
        cloudCoverText.setText(cloudCover);
        dewPointText.setText(dewPoint);
        refreshTimeText.setText(refreshTime);
    }


    private void setTemperatureChart(ArrayList<String> xLabel, ArrayList<Entry> yAxisVal,
                                     ArrayList<Entry> yAxisVal2, LineChart tempLineChart) {

        LineDataSet lineDataSet = new LineDataSet(yAxisVal, "Temp");
        lineDataSet.setColor(Color.GRAY);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawFilled(false);
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextSize(13f);
        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(lineDataSet);


        if (yAxisVal2 != null) {
            LineDataSet lineDataSet2 = new LineDataSet(yAxisVal2, "Max Temp");
            lineDataSet2.setColor(Color.GRAY);
            lineDataSet2.setDrawCircles(false);
            lineDataSet2.setDrawFilled(false);
            lineDataSet2.setDrawValues(true);
            lineDataSet2.setValueTextSize(13f);
            lineDataSet2.setLineWidth(2.5f);
            lineDataSet2.setValueTextColor(Color.WHITE);
            lineDataSet2.setCubicIntensity(0.2f);
            lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            sets.add(lineDataSet2);
        }

        LineData data = new LineData(sets);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.format(Locale.US, "%.1f", entry.getY()) + "\u00B0";
            }
        });

        tempLineChart.setDescription(null);
        tempLineChart.setData(data);
        tempLineChart.setDoubleTapToZoomEnabled(false);
        tempLineChart.setPinchZoom(false);
        tempLineChart.disableScroll();
        tempLineChart.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
        tempLineChart.setDescription(null);

        tempLineChart.getAxisLeft().setDrawGridLines(false);
        tempLineChart.getXAxis().setDrawGridLines(false);
        tempLineChart.setDrawGridBackground(false);
        tempLineChart.getAxisRight().setEnabled(false);
        tempLineChart.getAxisLeft().setEnabled(false);

        tempLineChart.getLegend().setEnabled(false);
        tempLineChart.setVisibleXRangeMaximum(5F); // allow 6 values
        tempLineChart.moveViewToX(0F);
        tempLineChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        tempLineChart.invalidate();
        XAxis xAxis = tempLineChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xLabel));
        xAxis.setGranularity(1f);
        xAxis.setAvoidFirstLastClipping(true);
    }

    private String formatTime(long time, String dateFormat) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format(dateFormat, cal).toString();
    }

}
