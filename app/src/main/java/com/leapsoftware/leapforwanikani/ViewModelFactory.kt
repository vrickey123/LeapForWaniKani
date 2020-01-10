package com.leapsoftware.leapforwanikani

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leapsoftware.leapforwanikani.dashboard.DashboardViewModel
import com.leapsoftware.leapforwanikani.data.source.WaniKaniRepository

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val waniKaniRepository: WaniKaniRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DashboardViewModel::class.java) -> {
                    DashboardViewModel(waniKaniRepository)
                }
                isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel(waniKaniRepository)
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}