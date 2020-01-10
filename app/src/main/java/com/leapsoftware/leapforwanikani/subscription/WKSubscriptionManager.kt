package com.leapsoftware.leapforwanikani.subscription

import android.util.Log
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport

/**
 * If we add native lessons or reviews, use the [WKSubscriptionManager] to unlock premium content.
 * For now, the web will handle it.
 */
object WKSubscriptionManager {
    private val TAG by lazy { WKSubscriptionManager::class.java.simpleName }

    private const val SUBSCRIPTION_TYPE_FREE = "free"
    private const val SUBSCRIPTION_TYPE_RECURRING = "recurring"
    private const val SUBSCRIPTION_TYPE_LIFETIME = "lifetime"

    private const val MAX_LEVEL_FREE = 3
    private const val MAX_LEVEL_SUBSCRIBED = 60

    /*
    * Unlocks premium content based on user type and max level.
    * https://docs.api.wanikani.com/20170710/#user-data-structure
    */
    fun isEntitled(user: WKReport.User, targetLevel: Int): Boolean {
        when (user.data.subscription.type) {
            SUBSCRIPTION_TYPE_FREE -> {
                return targetLevel < MAX_LEVEL_FREE
            }
            SUBSCRIPTION_TYPE_RECURRING, SUBSCRIPTION_TYPE_LIFETIME -> {
                return isActive(user)
            }
            else -> {
                Log.e(TAG, "Unknown subscription type " + user.data.subscription.type)
                return false
            }
        }
    }

    /**
     * @return [true] if the user currently has a paid subscription
     */
    private fun isActive(user: WKReport.User): Boolean {
        return user.data.subscription.active
    }
}