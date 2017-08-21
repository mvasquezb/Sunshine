package com.example.android.sunshine

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    companion object {
        val SHARE_HASHTAG = "#SunshineApp"
    }

    private lateinit var mWeatherDetail: TextView
    private lateinit var mForecast: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        mForecast = intent.getStringExtra("weatherData") ?: ""

        mWeatherDetail = findViewById(R.id.weather_detail) as TextView
        mWeatherDetail.text = mForecast
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.btn_action_refresh -> {
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
                .setText("$mForecast $SHARE_HASHTAG")
                .startChooser()
    }
}
