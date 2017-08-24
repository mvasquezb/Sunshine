package com.example.android.sunshine

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.android.sunshine.data.WeatherContract
import com.example.android.sunshine.utilities.SunshineDateUtils
import com.example.android.sunshine.utilities.SunshineWeatherUtils

/**
 * Created by pmvb on 17-08-18.
 */

class ForecastAdapter(
        clickHandler: ForecastAdapterOnClickHandler,
        context: Context
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private val mClickHandler: ForecastAdapterOnClickHandler = clickHandler
    private var mCursor: Cursor? = null
    private val mContext: Context = context

    interface ForecastAdapterOnClickHandler {
        fun onItemClick(weatherData: String)
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
        if (mCursor == null) {
            return
        }
        val cursor = mCursor!!
        if (!cursor.moveToPosition(position)) {
            return
        }

        val dateStr = SunshineDateUtils.getFriendlyDateString(
                mContext,
                cursor.getLong(MainActivity.INDEX_WEATHER_DATE)
        )
        val descStr = SunshineWeatherUtils.getStringForWeatherCondition(
                mContext,
                cursor.getInt(MainActivity.INDEX_WEATHER_WEATHER_ID)
        )
        val maxMinTempStr = SunshineWeatherUtils.formatHighLows(
                mContext,
                cursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP),
                cursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP)
        )

        var weatherSummary = "$dateStr - $descStr - $maxMinTempStr"
        holder.mWeatherText.text = weatherSummary
    }

    override fun getItemCount(): Int {
        if (mCursor == null) {
            return 0
        }
        return mCursor!!.count
    }

    fun setWeatherData(weatherData: Array<String>) {
//        mWeatherData = weatherData
        notifyDataSetChanged()
    }

    fun swapCursor(cursor: Cursor?) {
        mCursor = cursor
        notifyDataSetChanged()
    }

    inner class ForecastViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

        val mWeatherText: TextView = itemView.findViewById(R.id.weather_data) as TextView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mClickHandler.onItemClick(mWeatherText.text.toString())
        }
    }
}
