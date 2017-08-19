package com.example.android.sunshine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    private lateinit var mWeatherDetail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        mWeatherDetail = findViewById(R.id.weather_detail) as TextView
        mWeatherDetail.text = intent.getStringExtra("weatherData")
    }
}
