package com.example.android.sunshine.sync

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.android.sunshine.data.WeatherContract
import com.example.android.sunshine.data.WeatherProvider
import com.example.android.sunshine.utilities.NetworkUtils
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils
import com.example.android.sunshine.utilities.SunshineWeatherUtils
import org.json.JSONException
import java.net.URL

/**
 * Created by pmvb on 17-08-25.
 */
object SunshineSyncTask {
    @Synchronized
    fun syncWeather(context: Context) {
        val jsonWeatherData = getLiveWeatherData(context)
        Log.d("SunshineSyncTask", "jsonWeatherData: $jsonWeatherData")
        if (jsonWeatherData.isEmpty()) {
            return
        }
        var weatherValues: Array<ContentValues>? = null
        try {
            weatherValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(
                    context,
                    jsonWeatherData
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.d("SunshineSyncTask", weatherValues.toString())
        if (weatherValues != null) {
            Log.d("SunshineSyncTask", weatherValues.toString())
            context.contentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null)
            context.contentResolver.bulkInsert(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    weatherValues
            )
        }
    }

    private fun getLiveWeatherData(context: Context): String {
        var weatherRequestUrl: URL
        try {
            weatherRequestUrl = NetworkUtils.buildUrl(context)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

        try {
            val jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl)
            return jsonWeatherResponse ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}