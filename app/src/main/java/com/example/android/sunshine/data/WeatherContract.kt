package com.example.android.sunshine.data

import android.net.Uri
import android.provider.BaseColumns
import com.example.android.sunshine.utilities.SunshineDateUtils

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 */
class WeatherContract {

    companion object {
        val CONTENT_AUTHORITY = "com.example.android.sunshine"
        val BASE_CONTENT_URI = "content://$CONTENT_AUTHORITY"
    }

    class WeatherEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "weather"
            val COLUMN_DATE = "date"
            val COLUMN_WEATHER_ID = "weather_id"
            val COLUMN_MIN_TEMP = "min"
            val COLUMN_MAX_TEMP = "max"
            val COLUMN_HUMIDITY = "humidity"
            val COLUMN_PRESSURE = "pressure"
            val COLUMN_WIND_SPEED = "wind"
            val COLUMN_DEGREES = "degrees"

            val PATH_WEATHER = "weather"
            val CONTENT_URI = Uri.parse("$BASE_CONTENT_URI/$PATH_WEATHER")

            @JvmStatic
            fun buildWeatherUriWithDate(date: Long): Uri {
                return CONTENT_URI.buildUpon().appendPath(date.toString()).build()
            }

            @JvmStatic
            fun getSelectFromTodayOnwards(): String {
                val normalizedNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis())
                return "${WeatherEntry.COLUMN_DATE} >= $normalizedNow"
            }
        }
    }
}
