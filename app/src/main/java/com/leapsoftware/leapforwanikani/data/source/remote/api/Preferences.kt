package com.leapsoftware.leapforwanikani.data.source.remote.api

data class Preferences(
    val default_voice_actor_id: Int?,
    val lessons_autoplay_audio:Boolean,
    val lessons_batch_size: Int,
    val lessons_presentation_order: String,
    val reviews_autoplay_audio: Boolean,
    val reviews_display_srs_indicator: Boolean
)