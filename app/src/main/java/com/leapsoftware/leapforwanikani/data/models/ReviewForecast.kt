package com.leapsoftware.leapforwanikani.data.models

import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import java.text.SimpleDateFormat
import java.util.*

data class ReviewForecast(
    val forecast: List<HourlyForecast>,
    val totalReviewCount: Int
) {
    companion object {
        fun create(wkSummary: WKReport.Summary): ReviewForecast {
            val forecast = mutableListOf<HourlyForecast>()
            val calendar = Calendar.getInstance(Locale.getDefault())
            var totalReviewCount: Int = 0
            for ((i, reviewsAtHour) in wkSummary.data.reviews.withIndex()) {
                // Terrible Java Data Formatting API for the user-facing hourString
                val sdf = SimpleDateFormat("h a", Locale.getDefault())
                val hourOfTheDay = calendar.get(Calendar.HOUR)
                val hourString: String = sdf.format(calendar.time)
                // Round down to the nearest hour so that the hourString reads 11:00pm rather than 11:37pm
                calendar.set(Calendar.HOUR, hourOfTheDay)
                calendar.set(Calendar.MINUTE, 0)
                // [0] are the reviews available now, [1] are the reviews in an hour, etc. 24 hours provided.
                // Skip the first [0] reviews array since we want our forecast to begin one hour from now rather than the current review status
                // Keep track of the hourString, additionalReviews added that hour, and totalReviewCount to create an HourlyForecase; i.e. a row in the UI
                totalReviewCount += reviewsAtHour.subject_ids.size
                if ((i > 0) && reviewsAtHour.subject_ids.isNotEmpty()) {
                    val hourlyForecast = HourlyForecast(hourString, reviewsAtHour.subject_ids.size, totalReviewCount)
                    forecast.add(hourlyForecast)
                }
                calendar.add(Calendar.HOUR, 1)
            }
            return ReviewForecast(forecast, totalReviewCount)
        }
    }
}