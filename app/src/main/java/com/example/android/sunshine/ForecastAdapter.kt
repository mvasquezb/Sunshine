package com.example.android.sunshine

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by pmvb on 17-08-18.
 */

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private var mWeatherData: Array<String>? = null
    private lateinit var mClickHandler: ForecastAdapterOnClickHandler

    interface ForecastAdapterOnClickHandler {
        fun onItemClick(weatherData: String)
    }

    constructor(clickHandler: ForecastAdapterOnClickHandler) {
        mClickHandler = clickHandler
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.forecast_list_item,
                parent,
                false
        )
        return ForecastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.mWeatherText.text = mWeatherData!![position]
    }

    override fun getItemCount(): Int {
        if (mWeatherData != null) {
            return mWeatherData!!.size
        } else {
            return 0
        }
    }

    fun setWeatherData(weatherData: Array<String>) {
        mWeatherData = weatherData
        notifyDataSetChanged()
    }

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val mWeatherText: TextView

        init {
            mWeatherText = itemView.findViewById(R.id.weather_data) as TextView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mClickHandler.onItemClick(mWeatherText.text.toString())
        }
    }
}
