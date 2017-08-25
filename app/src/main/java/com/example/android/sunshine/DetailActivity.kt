package com.example.android.sunshine

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.app.ShareCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.android.sunshine.data.WeatherContract
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

    private lateinit var mWeatherDate: TextView
    private lateinit var mWeatherCondition: TextView
    private lateinit var mWeatherMinTemp: TextView
    private lateinit var mWeatherMaxTemp: TextView
    private lateinit var mWeatherHumidity: TextView
    private lateinit var mWeatherPressure: TextView
    private lateinit var mWeatherWind: TextView

    private lateinit var mUri: Uri
    private var mForecastSummary: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        mWeatherDate = findViewById(R.id.weather_detail_date) as TextView
        mWeatherCondition = findViewById(R.id.weather_detail_condition) as TextView
        mWeatherMinTemp = findViewById(R.id.weather_detail_min_temp) as TextView
        mWeatherMaxTemp = findViewById(R.id.weather_detail_max_temp) as TextView
        mWeatherHumidity = findViewById(R.id.weather_detail_humidity) as TextView
        mWeatherPressure = findViewById(R.id.weather_detail_pressure) as TextView
        mWeatherWind = findViewById(R.id.weather_detail_wind) as TextView

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
                        null,
                        null,
                        null
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
        mWeatherDate.text = SunshineDateUtils.getFriendlyDateString(
                this,
                data.getLong(INDEX_WEATHER_DATE)
        )
        mWeatherCondition.text = SunshineWeatherUtils.getStringForWeatherCondition(
                this,
                data.getInt(INDEX_WEATHER_WEATHER_ID)
        )
        mWeatherMinTemp.text = SunshineWeatherUtils.formatTemperature(
                this,
                data.getDouble(INDEX_WEATHER_MIN_TEMP)
        )
        mWeatherMaxTemp.text = SunshineWeatherUtils.formatTemperature(
                this,
                data.getDouble(INDEX_WEATHER_MAX_TEMP)
        )
        mWeatherHumidity.text = getString(
                R.string.format_humidity,
                data.getFloat(INDEX_WEATHER_HUMIDITY)
        )
        mWeatherPressure.text = getString(
                R.string.format_pressure,
                data.getFloat(INDEX_WEATHER_PRESSURE)
        )
        mWeatherWind.text = SunshineWeatherUtils.getFormattedWind(
                this,
                data.getFloat(INDEX_WEATHER_WIND_SPEED),
                data.getFloat(INDEX_WEATHER_DEGREES)
        )
        mForecastSummary = "${mWeatherDate.text} - ${mWeatherCondition.text} - " +
                "${mWeatherMaxTemp.text}/${mWeatherMinTemp.text}"
    }
}
