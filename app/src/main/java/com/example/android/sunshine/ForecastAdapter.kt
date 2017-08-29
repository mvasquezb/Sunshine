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

    companion object {
        const val VIEW_TYPE_TODAY = 2312
        const val VIEW_TYPE_FUTURE_DAY = 6323
    }

    interface ForecastAdapterOnClickHandler {
        fun onItemClick(weatherDate: Long)
    }

    private val mClickHandler: ForecastAdapterOnClickHandler = clickHandler
    private var mCursor: Cursor? = null
    private val mContext: Context = context
    private val mUseTodayLayout = context.resources.getBoolean(R.bool.use_today_layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        /**
         * Get correct layout for view type
         */
        val layoutId = when(viewType) {
            VIEW_TYPE_TODAY -> R.layout.forecast_list_item_today
            VIEW_TYPE_FUTURE_DAY -> R.layout.forecast_list_item
            else -> throw IllegalArgumentException("Unknown view type")
        }
        val itemView = LayoutInflater.from(parent.context).inflate(
                layoutId,
                parent,
                false
        )
        return ForecastViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY
        }
        return VIEW_TYPE_FUTURE_DAY
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
        val imageResource = when(getItemViewType(position)) {
            VIEW_TYPE_TODAY -> {
                SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherConditionId)
            }
            VIEW_TYPE_FUTURE_DAY -> {
                SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherConditionId)
            }
            else -> throw IllegalArgumentException("Unknown item view type")
        }
        holder.mWeatherIcon.setImageResource(imageResource)
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
        val mConditionText = itemView.findViewById(R.id.weatherConditionText) as TextView
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
