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

    fun openApiKey(context: Context) {
        chromeIntent.launchUrl(context, Uri.parse("https://www.wanikani.com/settings/personal_access_tokens"))
    }

    fun openTerms(context: Context) {
        chromeIntent.launchUrl(context, Uri.parse("https://www.wanikani.com/terms"))
    }

    fun openWaniKaniForum(context: Context) {
        chromeIntent.launchUrl(context, Uri.parse("https://community.wanikani.com/t/android-leap-for-wanikani-demo-native-offline-no-web/38276"))
    }

    fun openGitHub(context: Context) {
        chromeIntent.launchUrl(context, Uri.parse("https://github.com/vrickey123/LeapForWaniKani"))
    }

    fun openLicense(context: Context) {
        chromeIntent.launchUrl(context, Uri.parse("https://github.com/vrickey123/LeapForWaniKani/blob/develop/LICENSE.md"))
    }
}