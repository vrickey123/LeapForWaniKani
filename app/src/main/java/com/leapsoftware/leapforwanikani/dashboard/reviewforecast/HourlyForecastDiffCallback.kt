package com.leapsoftware.leapforwanikani.dashboard.reviewforecast

import androidx.recyclerview.widget.DiffUtil
import com.leapsoftware.leapforwanikani.data.models.HourlyForecast
import com.leapsoftware.leapforwanikani.data.models.ReviewForecast

class HourlyForecastDiffCallback: DiffUtil.ItemCallback<HourlyForecast>() {
    override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return false
    }
}