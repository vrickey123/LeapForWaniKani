package com.leapsoftware.leapforwanikani.data.models

data class HourlyForecast(
    val hour: String,
    val additionalReviews: Int,
    val totalReviewCount: Int
) {
    companion object {
        fun calculatePercentIncrease(additionalReviews: Int, totalReviewCount: Int): Float {
            return additionalReviews.toFloat() / totalReviewCount
        }
    }
}