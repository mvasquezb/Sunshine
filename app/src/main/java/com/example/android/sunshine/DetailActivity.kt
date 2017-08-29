package com.example.android.sunshine

import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.app.ShareCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.Menu
import android.view.MenuItem
import com.example.android.sunshine.data.WeatherContract
import com.example.android.sunshine.databinding.ActivityDetailBinding
import com.example.android.sunshine.utilities.SunshineDateUtils
import com.example.android.sunshine.utilities.SunshineWeatherUtils

class DetailActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        val SHARE_HASHTAG = "#SunshineApp"
        @JvmField val FORECAST_DETAIL_LOADER_ID = 50

        @JvmStatic val DETAIL_FORECAST_PROJECTION = arrayOf(
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_DEGREES
        )
        @JvmField val INDEX_WEATHER_DATE = 0
        @JvmField val INDEX_WEATHER_MIN_TEMP = 1
        @JvmField val INDEX_WEATHER_MAX_TEMP = 2
        @JvmField val INDEX_WEATHER_WEATHER_ID = 3
        @JvmField val INDEX_WEATHER_HUMIDITY = 4
        @JvmField val INDEX_WEATHER_PRESSURE = 5
        @JvmField val INDEX_WEATHER_WIND_SPEED = 6
        @JvmField val INDEX_WEATHER_DEGREES = 7
    }

    private lateinit var mDetailBinding: ActivityDetailBinding

    private lateinit var mUri: Uri
    private var mForecastSummary: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        title = getString(R.string.detail_activity_title)

        mUri = intent.data!!

        supportLoaderManager.initLoader(FORECAST_DETAIL_LOADER_ID, null, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.btn_action_share -> {
                shareForecast()
                return true
            }
            R.id.btn_action_settings -> {
                val settings = Intent(this, SettingsActivity::class.java)
                startActivity(settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareForecast() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("$mForecastSummary $SHARE_HASHTAG")
                .startChooser()
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
        when(loaderId) {
            FORECAST_DETAIL_LOADER_ID -> {
                return CursorLoader(
                        this,
                        mUri,
                        DETAIL_FORECAST_PROJECTION,
                        null, null, null
                )
            }
            else -> throw IllegalArgumentException("Loader not implemented: $loaderId")
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data == null || !data.moveToFirst()) {
            return
        }

        /**
         * Weather Icon
         */
        val weatherConditionId = data.getInt(INDEX_WEATHER_WEATHER_ID)
        mDetailBinding.primaryWeatherInfo.weatherIcon.setImageResource(
                SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherConditionId)
        )

        /**
         * Weather date
         */
        val dateStr = SunshineDateUtils.getFriendlyDateString(
                this,
                data.getLong(INDEX_WEATHER_DATE)
        )
        mDetailBinding.primaryWeatherInfo.dateText.text = dateStr

        /**
         * Weather condition
         */
        val weatherConditionStr = SunshineWeatherUtils.getStringForWeatherCondition(
                this,
                weatherConditionId
        )
        mDetailBinding.primaryWeatherInfo.weatherConditionText.text = weatherConditionStr

        /**
         * High and low temperature
         */
        val minTemperatureStr = SunshineWeatherUtils.formatTemperature(
                this,
                data.getDouble(INDEX_WEATHER_MIN_TEMP)
        )
        val maxTemperatureStr = SunshineWeatherUtils.formatTemperature(
                this,
                data.getDouble(INDEX_WEATHER_MAX_TEMP)
        )
        mDetailBinding.primaryWeatherInfo.lowTempText.text = minTemperatureStr
        mDetailBinding.primaryWeatherInfo.lowTempText.text = maxTemperatureStr

        /**
         * Humidity, pressure and wind
         */
        mDetailBinding.extraWeatherDetails.weatherHumidity.text = getString(
                R.string.format_humidity,
                data.getFloat(INDEX_WEATHER_HUMIDITY)
        )
        mDetailBinding.extraWeatherDetails.weatherPressure.text = getString(
                R.string.format_pressure,
                data.getFloat(INDEX_WEATHER_PRESSURE)
        )
        mDetailBinding.extraWeatherDetails.weatherWind.text = SunshineWeatherUtils.getFormattedWind(
                this,
                data.getFloat(INDEX_WEATHER_WIND_SPEED),
                data.getFloat(INDEX_WEATHER_DEGREES)
        )

        mForecastSummary = "$dateStr - $weatherConditionStr - $minTemperatureStr/$maxTemperatureStr"
    }
}
