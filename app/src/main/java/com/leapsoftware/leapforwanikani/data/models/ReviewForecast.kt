package com.leapsoftware.leapforwanikani.data.models

data class ReviewForecast(
    val forecast: List<HourlyForecast>,
    val totalReviewCount: Int
)