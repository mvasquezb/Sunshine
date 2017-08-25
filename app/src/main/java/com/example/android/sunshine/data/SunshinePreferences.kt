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
package com.example.android.sunshine.data

import android.content.Context
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.example.android.sunshine.R

object SunshinePreferences {

    /*
     * Human readable location string, provided by the API.  Because for styling,
     * "Mountain View" is more recognizable than 94043.
     */
    val PREF_CITY_NAME = "city_name"

    /*
     * In order to uniquely pinpoint the location on the map when we launch the
     * map intent, we store the latitude and longitude.
     */
    val PREF_COORD_LAT = "coord_lat"
    val PREF_COORD_LONG = "coord_long"

    /*
     * Before you implement methods to return your REAL preference for location,
     * we provide some default values to work with.
     */
    private val defaultWeatherLocation = "94043,USA"

    val defaultWeatherCoordinates = doubleArrayOf(37.4284, 122.0724)

    private val DEFAULT_MAP_LOCATION = "1600 Amphitheatre Parkway, Mountain View, CA 94043"

    /**
     * Helper method to handle setting location details in Preferences (city name, latitude,
     * longitude)
     *
     *
     * When the location details are updated, the database should to be cleared.

     * @param context  Context used to get the SharedPreferences
     * *
     * @param lat      the latitude of the city
     * *
     * @param lon      the longitude of the city
     */
    fun setLocationDetails(context: Context, lat: Double, lon: Double) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()

        editor.putLong(PREF_COORD_LAT, java.lang.Double.doubleToRawLongBits(lat))
        editor.putLong(PREF_COORD_LONG, java.lang.Double.doubleToRawLongBits(lon))
        editor.apply()
    }

    /**
     * Resets the location coordinates stores in SharedPreferences.

     * @param context Context used to get the SharedPreferences
     */
    fun resetLocationCoordinates(context: Context) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()

        editor.remove(PREF_COORD_LAT)
        editor.remove(PREF_COORD_LONG)
        editor.apply()
    }

    /**
     * Returns the location currently set in Preferences. The default location this method
     * will return is "94043,USA", which is Mountain View, California. Mountain View is the
     * home of the headquarters of the Googleplex!

     * @param context Context used to get the SharedPreferences
     * *
     * @return Location The current user has set in SharedPreferences. Will default to
     * * "94043,USA" if SharedPreferences have not been implemented yet.
     */
    fun getPreferredWeatherLocation(context: Context): String {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val location = sharedPrefs.getString(
                context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default_value)
        )
        return location
    }

    /**
     * Returns true if the user has selected metric temperature display.

     * @param context Context used to get the SharedPreferences
     * *
     * @return true If metric display should be used
     */
    fun isMetric(context: Context): Boolean {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val defaultPref = context.getString(R.string.pref_units_default_value)
        val metricPref = context.getString(R.string.pref_units_metric)

        return sharedPrefs.getString(
                context.getString(R.string.pref_units_key),
                defaultPref
        ) == metricPref
    }

    /**
     * Returns the location coordinates associated with the location.  Note that these coordinates
     * may not be set, which results in (0,0) being returned. (conveniently, 0,0 is in the middle
     * of the ocean off the west coast of Africa)

     * @param context Used to get the SharedPreferences
     * *
     * @return An array containing the two coordinate values.
     */
    fun getLocationCoordinates(context: Context): DoubleArray {
        return defaultWeatherCoordinates
    }

    /**
     * Returns true if the latitude and longitude values are available. The latitude and
     * longitude will not be available until the lesson where the PlacePicker API is taught.

     * @param context used to get the SharedPreferences
     * *
     * @return true if lat/long are saved in SharedPreferences
     */
    fun isLocationLatLonAvailable(context: Context): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)

        val spContainLatitude = sp.contains(PREF_COORD_LAT)
        val spContainLongitude = sp.contains(PREF_COORD_LONG)

        var spContainBothLatitudeAndLongitude = false
        if (spContainLatitude && spContainLongitude) {
            spContainBothLatitudeAndLongitude = true
        }

        return spContainBothLatitudeAndLongitude
    }

    /**
     * Returns the last time that a notification was shown (in UNIX time)

     * @param context Used to access SharedPreferences
     * *
     * @return UNIX time of when the last notification was shown
     */
    fun getLastNotificationTimeInMillis(context: Context): Long {
        /* Key for accessing the time at which Sunshine last displayed a notification */
        val lastNotificationKey = context.getString(R.string.pref_last_notification)

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        val sp = PreferenceManager.getDefaultSharedPreferences(context)

        /*
         * Here, we retrieve the time in milliseconds when the last notification was shown. If
         * SharedPreferences doesn't have a value for lastNotificationKey, we return 0. The reason
         * we return 0 is because we compare the value returned from this method to the current
         * system time. If the difference between the last notification time and the current time
         * is greater than one day, we will show a notification again. When we compare the two
         * values, we subtract the last notification time from the current system time. If the
         * time of the last notification was 0, the difference will always be greater than the
         * number of milliseconds in a day and we will show another notification.
         */
        val lastNotificationTime = sp.getLong(lastNotificationKey, 0)

        return lastNotificationTime
    }

    /**
     * Returns the elapsed time in milliseconds since the last notification was shown. This is used
     * as part of our check to see if we should show another notification when the weather is
     * updated.

     * @param context Used to access SharedPreferences as well as use other utility methods
     * *
     * @return Elapsed time in milliseconds since the last notification was shown
     */
    fun getEllapsedTimeSinceLastNotification(context: Context): Long {
        val lastNotificationTimeMillis = SunshinePreferences.getLastNotificationTimeInMillis(context)
        val timeSinceLastNotification = System.currentTimeMillis() - lastNotificationTimeMillis
        return timeSinceLastNotification
    }

    /**
     * Saves the time that a notification is shown. This will be used to get the ellapsed time
     * since a notification was shown.

     * @param context Used to access SharedPreferences
     * *
     * @param timeOfNotification Time of last notification to save (in UNIX time)
     */
    fun saveLastNotificationTime(context: Context, timeOfNotification: Long) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        val lastNotificationKey = context.getString(R.string.pref_last_notification)
        editor.putLong(lastNotificationKey, timeOfNotification)
        editor.apply()
    }
}