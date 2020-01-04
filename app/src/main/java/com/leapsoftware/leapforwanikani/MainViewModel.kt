package com.leapsoftware.leapforwanikani

import android.util.Log
import androidx.lifecycle.*
import com.leapsoftware.leapforwanikani.data.LeapResult
import com.leapsoftware.leapforwanikani.data.source.WaniKaniRepository
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import kotlinx.coroutines.launch

/**
 * The MainViewModel can be used as a shared view model to communicate across fragments
 * https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
 */
class MainViewModel(private val waniKaniRepository: WaniKaniRepository) : ViewModel() {

    private val TAG by lazy { MainViewModel::class.java.simpleName }

    private val _user = MutableLiveData<LeapResult<WKReport.User>>().apply {
        value = LeapResult.Loading
    }

    val onClearCache = MutableLiveData<Unit>()
    val onLogin = MutableLiveData<Unit>()

    val liveDataUser: LiveData<LeapResult<WKReport.User>> =
        liveData {
            Log.d(TAG, "emitting user")
            emitSource(_user)
        }

    fun clearCache() {
        viewModelScope.launch {
            waniKaniRepository.clearCache()
            onClearCache.postValue(Unit)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _user.value = waniKaniRepository.getUser()
        }
    }

}