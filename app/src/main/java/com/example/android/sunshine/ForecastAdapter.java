package com.example.android.sunshine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by pmvb on 17-08-18.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private String[] mWeatherData;
    private ForecastAdapterOnClickHandler mClickHandler;

    public interface ForecastAdapterOnClickHandler {
        void onItemClick(String weatherData);
    }

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public ForecastAdapter(String[] weatherData) {
        setWeatherData(weatherData);
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.forecast_list_item,
                parent,
                false
        );
        return new ForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        holder.mWeatherText.setText(mWeatherData[position]);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData != null) {
            return mWeatherData.length;
        } else {
            return 0;
        }
    }

    public void setWeatherData(String[] weatherData) {
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final TextView mWeatherText;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            mWeatherText = (TextView) itemView.findViewById(R.id.weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onItemClick(mWeatherText.getText().toString());
        }
    }
}
