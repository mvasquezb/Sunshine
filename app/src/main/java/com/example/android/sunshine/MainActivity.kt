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
package com.example.android.sunshine

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.example.android.sunshine.data.FetchWeatherLoader
import com.example.android.sunshine.data.SunshinePreferences
import com.example.android.sunshine.data.WeatherContract
import com.example.android.sunshine.data.WeatherLoaderActions
import com.example.android.sunshine.sync.SunshineSyncUtils

class MainActivity :
        AppCompatActivity(),
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        val TAG = MainActivity.javaClass.simpleName
        val FORECAST_LOADER_ID = 42

        @JvmStatic private val MAIN_FORECAST_PROJECTION = arrayOf(
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
        )
        @JvmField val INDEX_WEATHER_DATE = 0
        @JvmField val INDEX_WEATHER_MAX_TEMP = 1
        @JvmField val INDEX_WEATHER_MIN_TEMP = 2
        @JvmField val INDEX_WEATHER_WEATHER_ID = 3
    }

    private lateinit var mForecastList: RecyclerView
    private lateinit var mForecastAdapter: ForecastAdapter
    private lateinit var mLoadingView: ProgressBar
    private var mPosition: Int = RecyclerView.NO_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        mLoadingView = findViewById(R.id.loading_view) as ProgressBar
        mForecastList = findViewById(R.id.forecast_list) as RecyclerView

        val layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        )
        mForecastList.layoutManager = layoutManager
        mForecastList.setHasFixedSize(true)
        mForecastAdapter = ForecastAdapter(clickHandler = this, context = this)
        mForecastList.adapter = mForecastAdapter

        showLoading()

        supportLoaderManager.initLoader(FORECAST_LOADER_ID, null, this)

        SunshineSyncUtils.initialize(context = this)
    }

    private fun showWeatherDataView() {
        mLoadingView.visibility = View.INVISIBLE
        mForecastList.visibility = View.VISIBLE
    }

    override fun onItemClick(weatherDate: Long) {
        val forecastDetail = Intent(this, DetailActivity::class.java)
        forecastDetail.data = WeatherContract.WeatherEntry.buildWeatherUriWithDate(weatherDate)
        startActivity(forecastDetail)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.forecast, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_action_map -> {
                openLocationInMap()
            }
            R.id.btn_action_settings -> {
                val settings = Intent(this, SettingsActivity::class.java)
                startActivity(settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openLocationInMap() {
        val addressString = SunshinePreferences.getPreferredWeatherLocation(this)
        val geoLocation = Uri.Builder()
                .scheme("geo")
                .authority("0,0")
                .appendQueryParameter("q", addressString)
                .build()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = geoLocation

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.d(TAG, "Couldn't call $geoLocation, no receiving apps installed!")
        }
    }

    /**
     * LoaderManager.LoaderCallbacks implementation
     */
    override fun onLoadFinished(loader: Loader<Cursor>?, weatherData: Cursor?) {
        mForecastAdapter.swapCursor(weatherData)
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0
        }
        mForecastList.smoothScrollToPosition(mPosition)

        if (weatherData != null && weatherData.count != 0) {
            showWeatherDataView()
        }
    }

    override fun onCreateLoader(loaderId: Int, loaderArgs: Bundle?): Loader<Cursor>? {
        when(loaderId) {
            FORECAST_LOADER_ID -> {
                val sortOrder = "${WeatherContract.WeatherEntry.COLUMN_DATE} ASC"
                val selection = WeatherContract.WeatherEntry.getSelectFromTodayOnwards()

                return CursorLoader(
                        this,
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder
                )
            }
            else -> throw IllegalArgumentException("Loader not implemented: $loaderId")
        }
    }

    private fun showLoading() {
        mForecastList.visibility = View.INVISIBLE
        mLoadingView.visibility = View.VISIBLE
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        mForecastAdapter.swapCursor(null)
    }
}