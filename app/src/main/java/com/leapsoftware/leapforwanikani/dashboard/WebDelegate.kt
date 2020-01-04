package com.leapsoftware.leapforwanikani.dashboard

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object WebDelegate {

    private val builder = CustomTabsIntent.Builder()
    private val chromeIntent = builder.build()

    fun openLessons(context: Context) {
        chromeIntent.launchUrl(context, Uri.parse("https://www.wanikani.com/lesson"))
    }

    fun openReviews(context: Context) {
        chromeIntent.launchUrl(context, Uri.parse("https://www.wanikani.com/review"))
    }
}