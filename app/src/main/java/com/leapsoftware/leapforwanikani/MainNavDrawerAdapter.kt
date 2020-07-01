package com.leapsoftware.leapforwanikani

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.work.ExistingPeriodicWorkPolicy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.leapsoftware.leapforwanikani.utils.PreferencesManager
import com.leapsoftware.leapforwanikani.workers.SummaryNotifyWorker

class MainNavDrawerAdapter(
    private val navigationView: NavigationView,
    private val mainViewModel: MainViewModel
) {

    private val TAG by lazy { MainNavDrawerAdapter::class.java.simpleName }

    fun setOnItemSelectedListener(onNavigationItemSelectedListener: NavigationView.OnNavigationItemSelectedListener) {
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    fun setLoginStatus(activity: Activity, apiKey: String) {
        if (apiKey.isEmpty()) {
            showLoginView(activity)
        } else {
            showLogoutView(activity)
        }
    }

    fun login(activity: Activity) {
        val builder = MaterialAlertDialogBuilder(activity)

        builder.setTitle(activity.getString(R.string.dialog_api_key_title))
        builder.setMessage(activity.getString(R.string.dialog_api_key_message))

        val editText = EditText(activity)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        editText.layoutParams = lp
        builder.setView(editText)

        builder.setPositiveButton(activity.getString(R.string.dialog_api_key_positive_button)) { dialog, whichButton ->
            val input = editText.text.toString()
            PreferencesManager.saveApiKey(activity, input)
            activity.invalidateOptionsMenu()
            mainViewModel.refreshData()
            mainViewModel.onLogin.postValue(Unit)
        }

        builder.show()
    }

    fun logout(activity: Activity) {
        PreferencesManager.deleteApiKey(activity)
        activity.invalidateOptionsMenu()
        mainViewModel.clearCache()
        mainViewModel.refreshData()
        mainViewModel.onLogout.postValue(Unit)
    }

    fun showNotificationPrefs(context: Context) {
        val notifiPrefOptions = Array<String>(25) {
            if (it == 0) {
                "Every 15 minutes" // the PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS
            } else {
                String.format("Every %d hours", it)
            }
        }
        var checkedItem = PreferencesManager.getNotificationPref(context)

        MaterialAlertDialogBuilder(context)
            .setTitle(context.resources.getString(R.string.dialog_notification_prefs))
            .setNeutralButton(context.resources.getString(android.R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(context.resources.getString(android.R.string.ok)) { dialog, which ->
                // Respond to positive button press
                PreferencesManager.saveNotificationPref(context, checkedItem)
                SummaryNotifyWorker.enqueueUniquePeriodicWork(context, ExistingPeriodicWorkPolicy.REPLACE)
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(notifiPrefOptions, checkedItem) { dialog, which ->
                // Respond to item chosen
                checkedItem = which
            }
            .show()
    }

    fun openChannelAppSystemSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
            context.startActivity(intent)
        }
    }

    fun showLoginView(activity: Activity) {
        activity.runOnUiThread({
            navigationView.menu.findItem(R.id.nav_login).isVisible = true
            navigationView.menu.findItem(R.id.nav_logout).isVisible = false
        })
    }

    fun showLogoutView(activity: Activity) {
        activity.runOnUiThread({
            navigationView.menu.findItem(R.id.nav_login).isVisible = false
            navigationView.menu.findItem(R.id.nav_logout).isVisible = true
        })
    }

    fun bindUserName(userName: String) {
        val navHeader = navigationView.getHeaderView(0)
        navHeader.findViewById<TextView>(R.id.nav_header_title).text = userName
    }

    fun bindUserLevel(userLevel: String) {
        val navHeader = navigationView.getHeaderView(0)
        navHeader.findViewById<TextView>(R.id.nav_header_subtitle).text = userLevel
    }

    fun bindVersionName(versionName: String) {
        navigationView.menu.findItem(R.id.nav_version_name).title =
            String.format("v%s", versionName)
    }

}