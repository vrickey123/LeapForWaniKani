package com.leapsoftware.leapforwanikani.utils

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {

    const val SHARED_PREFS_WK_REMINDERS= "shared_prefs_wanikani_reminders"
    const val PREF_WANIKANI_USER_API_KEY = "wanikani_api_key"
    const val PREF_LEAP_NOTIFICATION_PREF = "leap_notification_pref"
    const val PREF_NEW_FEATURES_ONBOARDING_110 = "leap_new_features_onboarding_110"

    fun saveApiKey(context: Context, key: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_WK_REMINDERS, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(PREF_WANIKANI_USER_API_KEY, key)
            .apply()
    }

    fun getApiKey(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_WK_REMINDERS, Context.MODE_PRIVATE)
        return prefs.getString(PREF_WANIKANI_USER_API_KEY, "")!!
    }

    fun deleteApiKey(context: Context) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_WK_REMINDERS, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(PREF_WANIKANI_USER_API_KEY)
            .apply()
    }

    fun saveNotificationPref(context: Context, hours: Int) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_WK_REMINDERS, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(PREF_LEAP_NOTIFICATION_PREF, hours)
            .apply()
    }

    fun getNotificationPref(context: Context): Int {
        val prefs: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_WK_REMINDERS, Context.MODE_PRIVATE)
        return prefs.getInt(PREF_LEAP_NOTIFICATION_PREF, 1)
    }

    fun setHasUserOnboarded(context: Context, hasUserOnboarded: Boolean, versionCode: Int) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_WK_REMINDERS, Context.MODE_PRIVATE)
        when (versionCode) {
            110 -> {
                prefs.edit()
                    .putBoolean(PREF_NEW_FEATURES_ONBOARDING_110, hasUserOnboarded)
                    .apply()
            }
        }
    }

    fun getHasUserOnboarded(context: Context, versionCode: Int): Boolean {
        val prefs: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_WK_REMINDERS, Context.MODE_PRIVATE)
        return when (versionCode) {
            110 -> {
                prefs.getBoolean(PREF_NEW_FEATURES_ONBOARDING_110, false)
            }
            else -> {
                return true
            }
        }
    }

}