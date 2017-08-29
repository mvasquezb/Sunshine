package com.example.android.sunshine

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        fun onItemClick(weatherDate: Long)
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
        /**
         * Weather date
         */
        val date = cursor.getLong(MainActivity.INDEX_WEATHER_DATE)
        val dateStr = SunshineDateUtils.getFriendlyDateString(
                mContext,
                date
        )
        holder.mDateText.text = dateStr

        /**
         * Weather condition icon
         */
        val weatherConditionId = cursor.getInt(MainActivity.INDEX_WEATHER_WEATHER_ID)
        holder.mWeatherIcon.setImageResource(
                SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherConditionId)
        )
        /**
         * Weather condition text
         */
        val conditionStr = SunshineWeatherUtils.getStringForWeatherCondition(
                mContext,
                weatherConditionId
        )
        holder.mConditionText.text = conditionStr
        /**
         * High and low temperature
         */
        val highTemp = cursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP)
        val lowTemp = cursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP)

        holder.mHighTempText.text = SunshineWeatherUtils.formatTemperature(mContext, highTemp)
        holder.mLowTempText.text = SunshineWeatherUtils.formatTemperature(mContext, lowTemp)

        holder.mForecastDate = date
    }

    override fun getItemCount(): Int {
        if (mCursor == null) {
            return 0
        }
        return mCursor!!.count
    }

    fun swapCursor(cursor: Cursor?) {
        mCursor = cursor
        notifyDataSetChanged()
    }

    inner class ForecastViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

        val mDateText = itemView.findViewById(R.id.dateText) as TextView
        val mConditionText = itemView.findViewById(R.id.weatherDescription) as TextView
        val mHighTempText = itemView.findViewById(R.id.highTempText) as TextView
        val mLowTempText = itemView.findViewById(R.id.lowTempText) as TextView
        val mWeatherIcon = itemView.findViewById(R.id.weatherIcon) as ImageView

        var mForecastDate: Long = System.currentTimeMillis()

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mClickHandler.onItemClick(mForecastDate)
        }
    }
}
