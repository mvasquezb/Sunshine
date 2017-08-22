package com.example.android.sunshine.data

import android.provider.BaseColumns

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 */
class WeatherContract {
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
        }
    }
}
