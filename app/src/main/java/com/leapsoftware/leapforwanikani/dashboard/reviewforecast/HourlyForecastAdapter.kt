package com.leapsoftware.leapforwanikani.dashboard.reviewforecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.leapsoftware.leapforwanikani.R
import com.leapsoftware.leapforwanikani.data.models.HourlyForecast

class HourlyForecastAdapter() :
    ListAdapter<HourlyForecast, HourlyForecastAdapter.HourlyViewHolder>(HourlyForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HourlyViewHolder(layoutInflater.inflate(R.layout.review_forecast_hourly, parent, false))
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourlyForecast = getItem(position)
        holder.time.text = hourlyForecast.hour
        holder.additionalCount.text = String.format("+%s", hourlyForecast.additionalReviews.toString())
        holder.totalCount.text = hourlyForecast.totalReviewCount.toString()
        val constraintSet = ConstraintSet()
        val width = HourlyForecast.calculatePercentIncrease(hourlyForecast.additionalReviews, hourlyForecast.totalReviewCount)
        constraintSet.clone(holder.constraintLayout)
        constraintSet.constrainPercentWidth(R.id.review_forecast_hourly_bar, width)
        constraintSet.applyTo(holder.constraintLayout)
    }

    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.review_forecast_hourly_constraint_layout)
        val time = itemView.findViewById<TextView>(R.id.review_forecast_hourly_time)
        val bar = itemView.findViewById<View>(R.id.review_forecast_hourly_bar)
        val additionalCount = itemView.findViewById<TextView>(R.id.review_forecast_hourly_additional)
        val totalCount = itemView.findViewById<TextView>(R.id.review_forecast_hourly_total)
    }
}