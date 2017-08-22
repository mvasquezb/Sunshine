package com.example.android.sunshine.data

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

/**
 * Manages a local database for weather data.
 */

class WeatherDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        val DATABASE_NAME = "weather.db"
        private val DATABASE_VERSION = 3
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val sqlStatement = "CREATE TABLE ${WeatherContract.WeatherEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${WeatherContract.WeatherEntry.COLUMN_DATE} INTEGER NOT NULL UNIQUE," +
                "${WeatherContract.WeatherEntry.COLUMN_WEATHER_ID} INTEGER NOT NULL,"
                "${WeatherContract.WeatherEntry.COLUMN_MIN_TEMP} REAL NOT NULL," +
                "${WeatherContract.WeatherEntry.COLUMN_MAX_TEMP} REAL NOT NULL," +
                "${WeatherContract.WeatherEntry.COLUMN_HUMIDITY} REAL NOT NULL," +
                "${WeatherContract.WeatherEntry.COLUMN_PRESSURE} REAL NOT NULL," +
                "${WeatherContract.WeatherEntry.COLUMN_WIND_SPEED} REAL NOT NULL,"
                "${WeatherContract.WeatherEntry.COLUMN_DEGREES} REAL NOT NULL);"
        sqLiteDatabase.execSQL(sqlStatement)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }
}