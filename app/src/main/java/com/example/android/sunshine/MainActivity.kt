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
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.example.android.sunshine.data.FetchWeatherLoader
import com.example.android.sunshine.data.WeatherLoaderActions

class MainActivity :
        AppCompatActivity(),
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Array<String>> {

    companion object {
        val TAG = MainActivity.javaClass.simpleName
        val FORECAST_LOADER_ID = 42
    }

    private lateinit var mForecastList: RecyclerView
    private lateinit var mForecastAdapter: ForecastAdapter
    private lateinit var mErrorMessage: TextView
    private lateinit var mLoadingView: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        mErrorMessage = findViewById(R.id.error_message) as TextView
        mLoadingView = findViewById(R.id.loading_view) as ProgressBar
        mForecastList = findViewById(R.id.forecast_list) as RecyclerView

        val layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        )
        mForecastList.layoutManager = layoutManager
        mForecastList.setHasFixedSize(true)
        mForecastAdapter = ForecastAdapter(this)
        mForecastList.adapter = mForecastAdapter

        supportLoaderManager.initLoader(FORECAST_LOADER_ID, null, this)
    }

    private fun showWeatherDataView() {
        mErrorMessage.visibility = View.INVISIBLE
        mForecastList.visibility = View.VISIBLE
    }

    private fun showErrorMessage() {
        mForecastList.visibility = View.INVISIBLE
        mErrorMessage.visibility = View.VISIBLE
    }

    override fun onItemClick(weatherData: String) {
        val forecastDetail = Intent(this, DetailActivity::class.java)
        forecastDetail.putExtra("weatherData", weatherData)
        startActivity(forecastDetail)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.forecast, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_action_refresh -> {
                mForecastAdapter.setWeatherData(arrayOf<String>())
                supportLoaderManager.restartLoader(FORECAST_LOADER_ID, null, this)
            }
            R.id.btn_action_map -> {
                openLocationInMap()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openLocationInMap() {
        val addressString = "1600 Ampitheatre Parkway, CA"
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
    override fun onLoadFinished(loader: Loader<Array<String>>?, weatherData: Array<String>?) {
        mLoadingView.visibility = View.INVISIBLE

        if (weatherData != null) {
            mForecastAdapter.setWeatherData(weatherData)
            showWeatherDataView()
        } else {
            showErrorMessage()
        }
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<String>> {
        return FetchWeatherLoader(this@MainActivity, object : WeatherLoaderActions {
            override fun onStartLoading() {
                mLoadingView.visibility = View.VISIBLE
            }
        })
    }

    override fun onLoaderReset(p0: Loader<Array<String>>?) {
    }
}