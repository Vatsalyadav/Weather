package com.example.weather.view;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.weather.R;
import com.example.weather.models.WeatherDetails;
import com.example.weather.viewmodels.WeatherDetailsActivityViewModel;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WeatherDetailsActivityTest {

    @Rule
    public ActivityTestRule<WeatherDetailsActivity> mActivityTestRule = new ActivityTestRule<>(WeatherDetailsActivity.class);
    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");
    WeatherDetails mWeatherDetails;
    private WeatherDetailsActivity weatherDetailsActivity;
    private WeatherDetailsActivityViewModel mWeatherDetailsActivityViewModel;

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    /**
     * Setup activity and fetch Weather Details data that will be tested
     */
    @Before
    public void fetchWeatherDataToBeTested() {
        weatherDetailsActivity = mActivityTestRule.getActivity();
        mWeatherDetailsActivityViewModel = new ViewModelProvider(weatherDetailsActivity, ViewModelProvider.AndroidViewModelFactory.getInstance(weatherDetailsActivity.getApplication())).get(WeatherDetailsActivityViewModel.class);
        mWeatherDetailsActivityViewModel.initRepository();
        mWeatherDetailsActivityViewModel.setWeatherDetails();
        LiveData<WeatherDetails> weatherDetails = mWeatherDetailsActivityViewModel.getWeatherDetails();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (weatherDetails != null && weatherDetails.getValue().getResponseState()) {
            mWeatherDetails = weatherDetails.getValue();
        }
    }

    /*
     * Test whether Wind Speed Text is correct
     */
    @Test
    public void windSpeedTextTest() {
        String windSpeed;
        windSpeed = mWeatherDetails.getCurrent().getWindSpeed() + " km/hr";
        ViewInteraction textView = onView(
                allOf(withId(R.id.wind_speed),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView.check(matches(withText(windSpeed)));
    }

    /*
     * Test whether Humidity Text is correct
     */
    @Test
    public void humidityTextTest() {
        String humidity = mWeatherDetails.getCurrent().getHumidity() + "%";

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.humidity),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText(humidity)));
    }

    /*
     * Test whether UV Index Text is correct
     */
    @Test
    public void uvIndexTextTest() {
        String uvIndex = mWeatherDetails.getCurrent().getUvi() + "";
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.uv_index),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText(uvIndex)));
    }

    /*
     * Test whether Atmospheric Pressure Text is correct
     */
    @Test
    public void pressureTextTest() {
        String pressure = mWeatherDetails.getCurrent().getPressure() + " mb";
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.pressure),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView4.check(matches(withText(pressure)));
    }

    /*
     * Test whether Visibility Text is correct
     */
    @Test
    public void visibilityTextTest() {
        String visibility = mWeatherDetails.getCurrent().getVisibility() / 1000 + " km";
        ViewInteraction textView5 = onView(
                allOf(withId(R.id.visibility),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView5.check(matches(withText(visibility)));
    }

    /*
     * Test whether Cloud Cover Text is correct
     */
    @Test
    public void cloudCoverTextTest() {
        String cloudCover = mWeatherDetails.getCurrent().getClouds() + "%";

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.cloud_cover),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView6.check(matches(withText(cloudCover)));
    }

    /*
     * Test whether Dew Point Text is correct
     */
    @Test
    public void dewPointTextTest() {
        String dewPoint = String.format(Locale.US, "%.1f", mWeatherDetails.getCurrent().getDewPoint()) + "\u00B0C";

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.dew_point),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView7.check(matches(withText(dewPoint)));

    }

    @Test
    public void weeklyForecastButtonTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_forecast), withText("Show Weekly Forecast"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.parent_layout_weather_details),
                                        6),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button_forecast), withText("Hide weekly Forecast"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.parent_layout_weather_details),
                                        6),
                                0),
                        isDisplayed()));
        materialButton2.perform(click());
    }

}
