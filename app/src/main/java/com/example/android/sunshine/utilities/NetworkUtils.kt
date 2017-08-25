/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.utilities

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.android.sunshine.data.SunshinePreferences

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Scanner

/**
 * These utilities will be used to communicate with the weather servers.
 */
object NetworkUtils {

    private val TAG = NetworkUtils.javaClass.simpleName

    private val DYNAMIC_WEATHER_URL = "https://andfun-weather.udacity.com/weather"

    private val STATIC_WEATHER_URL = "https://andfun-weather.udacity.com/staticweather"

    private val FORECAST_BASE_URL = STATIC_WEATHER_URL

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */

    /* The format we want our API to return */
    private val format = "json"
    /* The units we want our API to return */
    private val units = "metric"
    /* The number of days we want our API to return */
    private val numDays = 14

    internal val QUERY_PARAM = "q"
    internal val LAT_PARAM = "lat"
    internal val LON_PARAM = "lon"
    internal val FORMAT_PARAM = "mode"
    internal val UNITS_PARAM = "units"
    internal val DAYS_PARAM = "cnt"

    /**
     * Retrieves the proper URL to query for the weather data. The reason for both this method as
     * well as [.buildUrlWithLocationQuery] is two fold.
     *
     *
     * 1) You should be able to just use one method when you need to create the URL within the
     * app instead of calling both methods.
     * 2) Later in Sunshine, you are going to add an alternate method of allowing the user
     * to select their preferred location. Once you do so, there will be another way to form
     * the URL using a latitude and longitude rather than just a location String. This method
     * will "decide" which URL to build and return it.

     * @param context used to access other Utility methods
     * *
     * @return URL to query weather service
     */
    fun buildUrl(context: Context): URL {
        if (SunshinePreferences.isLocationLatLonAvailable(context)) {
            val preferredCoordinates = SunshinePreferences.getLocationCoordinates(context)
            val latitude = preferredCoordinates[0]
            val longitude = preferredCoordinates[1]
            return buildUrlWithLatitudeLongitude(latitude, longitude)
        } else {
            val locationQuery = SunshinePreferences.getPreferredWeatherLocation(context)
            return buildUrlWithLocationQuery(locationQuery)
        }
    }

    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.

     * @param latitude  The latitude of the location
     * *
     * @param longitude The longitude of the location
     * *
     * @return The Url to use to query the weather server.
     */
    private fun buildUrlWithLatitudeLongitude(latitude: Double, longitude: Double): URL {
        val weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(LAT_PARAM, latitude.toString())
                .appendQueryParameter(LON_PARAM, longitude.toString())
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build()

        val weatherQueryUrl = URL(weatherQueryUri.toString())
        Log.v(TAG, "URL: " + weatherQueryUrl)
        return weatherQueryUrl
    }

    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.

     * @param locationQuery The location that will be queried for.
     * *
     * @return The URL to use to query the weather server.
     */
    private fun buildUrlWithLocationQuery(locationQuery: String): URL {
        val weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build()

        val weatherQueryUrl = URL(weatherQueryUri.toString())
        Log.v(TAG, "URL: " + weatherQueryUrl)
        return weatherQueryUrl
    }

    /**
     * This method returns the entire result from the HTTP response.

     * @param url The URL to fetch the HTTP response from.
     * *
     * @return The contents of the HTTP response.
     * *
     * @throws IOException Related to network and stream reading
     */
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in` = urlConnection.inputStream

            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            if (hasInput) {
                return scanner.next()
            } else {
                return null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}