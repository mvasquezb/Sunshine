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

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.example.android.sunshine.data.SunshinePreferences
import com.example.android.sunshine.utilities.NetworkUtils
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils

import java.net.URL

class MainActivity : AppCompatActivity(), ForecastAdapter.ForecastAdapterOnClickHandler {

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

        loadWeatherData()
    }

    private fun loadWeatherData() {
        showWeatherDataView()
        val location = SunshinePreferences.getPreferredWeatherLocation(this)
        FetchWeatherTask().execute(location)
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
        Toast.makeText(this, weatherData, Toast.LENGTH_SHORT).show()
    }

    inner class FetchWeatherTask : AsyncTask<String, Void, Array<String>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            mLoadingView.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): Array<String>? {
            if (params.isEmpty()) {
                return null
            }
            val location = params[0]
            val weatherRequestUrl = NetworkUtils.buildUrl(location)!!

            try {
                val jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl)
                val weatherData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(
                        this@MainActivity,
                        jsonWeatherResponse ?: ""
                )
                return weatherData
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(weatherData: Array<String>?) {
            mLoadingView.visibility = View.INVISIBLE

            if (weatherData != null) {
                mForecastList.visibility = View.VISIBLE
                mForecastAdapter.setWeatherData(weatherData)
            } else {
                showErrorMessage()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.forecast, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_action_refresh -> {
                mForecastAdapter.setWeatherData(arrayOf<String>())
                loadWeatherData()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}