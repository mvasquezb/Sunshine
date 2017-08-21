package com.example.android.sunshine.data

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import com.example.android.sunshine.utilities.NetworkUtils
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils
import java.net.URL

/**
 * Created by pmvb on 17-08-20.
 */
class FetchWeatherLoader(context: Context) : AsyncTaskLoader<Array<String>>(context) {

    private var mWeatherData = arrayOf<String>()
    var mActionHandler: WeatherLoaderActions? = null

    constructor(context: Context, actionHandler: WeatherLoaderActions) : this(context) {
        mActionHandler = actionHandler
    }

    override fun onStartLoading() {
        super.onStartLoading()
        if (!mWeatherData.isEmpty()) {
            deliverResult(mWeatherData)
        } else {
            mActionHandler?.onStartLoading()
            forceLoad()
        }
    }

    override fun loadInBackground(): Array<String> {
        val location = SunshinePreferences.getPreferredWeatherLocation(context)
        var weatherRequestUrl: URL
        try {
            weatherRequestUrl = NetworkUtils.buildUrl(location)
        } catch (e: Exception) {
            e.printStackTrace()
            return arrayOf()
        }

        try {
            val jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl)
            val weatherData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(
                    context,
                    jsonWeatherResponse ?: ""
            )
            return weatherData ?: arrayOf()
        } catch (e: Exception) {
            e.printStackTrace()
            return arrayOf()
        }
    }

    override fun deliverResult(data: Array<String>) {
        mWeatherData = data
        super.deliverResult(data)
    }
}