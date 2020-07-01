package com.leapsoftware.leapforwanikani.data.models

data class ReviewForecast(
    val forecast: Map<String, Int>,
    val totalReviewCount: Int
)