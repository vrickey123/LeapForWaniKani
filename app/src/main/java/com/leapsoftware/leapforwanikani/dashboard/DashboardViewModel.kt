package com.leapsoftware.leapforwanikani.dashboard

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.leapsoftware.leapforwanikani.data.LeapResult
import com.leapsoftware.leapforwanikani.data.models.HourlyForecast
import com.leapsoftware.leapforwanikani.data.models.ReviewForecast
import com.leapsoftware.leapforwanikani.data.source.PagedAssignmentsSource
import com.leapsoftware.leapforwanikani.data.source.WaniKaniRepository
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKSrsStageType
import com.leapsoftware.leapforwanikani.data.succeeded
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Based on MVVM Android Architecture V2 Blueprint
 * https://github.com/android/architecture-samples/blob/master/app/src/main/java/com/example/android/architecture/blueprints/todoapp/tasks/TasksViewModel.kt
 */
class DashboardViewModel(
    private val waniKaniRepository: WaniKaniRepository
) : ViewModel() {

    private val TAG by lazy { DashboardViewModel::class.java.simpleName }

    private val assignmentsSource = PagedAssignmentsSource(waniKaniRepository)

    private val _summary = MutableLiveData<LeapResult<WKReport.Summary>>().apply {
        value = LeapResult.Loading
    }

    private val _assignments: LiveData<LeapResult<List<WKReport.WKResource.Assignment>>> =
        assignmentsSource.getPagedAssignments()

    // Emits changes when local or remote responseData source is triggered
    val liveDataSummary: LiveData<LeapResult<WKReport.Summary>> =
        liveData {
            Log.d(TAG, "emitting summary")
            emitSource(_summary)
        }

    // Composite PagedList result of WKCollection<Assignment>
    val liveDataAssignments: LiveData<LeapResult<List<WKReport.WKResource.Assignment>>> =
        liveData {
            Log.d(TAG, "emitting assignments")
            emitSource(_assignments)
        }

    val liveDataCountApprentice: LiveData<LeapResult<Int>> = Transformations.switchMap(_assignments) {
        liveData {
            val count = waniKaniRepository.getCountAssignmentsBySrsStage(WKSrsStageType.apprentice)
            emit(count)
        }
    }

    val liveDataCountGuru: LiveData<LeapResult<Int>> = Transformations.switchMap(_assignments) {
        liveData {
            val count = waniKaniRepository.getCountAssignmentsBySrsStage(WKSrsStageType.guru)
            emit(count)
        }
    }

    val liveDataCountMaster: LiveData<LeapResult<Int>> = Transformations.switchMap(_assignments) {
        liveData {
            val count = waniKaniRepository.getCountAssignmentsBySrsStage(WKSrsStageType.master)
            emit(count)
        }
    }

    val liveDataCountEnlightened: LiveData<LeapResult<Int>> = Transformations.switchMap(_assignments) {
        liveData {
            val count = waniKaniRepository.getCountAssignmentsBySrsStage(WKSrsStageType.enlightened)
            emit(count)
        }
    }

    val liveDataCountBurned: LiveData<LeapResult<Int>> = Transformations.switchMap(_assignments) {
        liveData {
            val count = waniKaniRepository.getCountAssignmentsBySrsStage(WKSrsStageType.burned)
            emit(count)
        }
    }

    val liveDataReviewForecast: LiveData<LeapResult<ReviewForecast>> = Transformations.switchMap(_summary) {
        liveData {
            when (it) {
                is LeapResult.Success<WKReport.Summary> -> {
                    val reviewForecast = ReviewForecast.create(it.resultData)
                    emit(LeapResult.Success(reviewForecast))
                }
                is LeapResult.Error -> {
                    emit(LeapResult.Error(it.exception))
                }
                is LeapResult.Loading -> {
                    emit(LeapResult.Loading)
                }
                is LeapResult.Offline -> {
                    emit(LeapResult.Offline)
                }
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _summary.value = waniKaniRepository.getSummary(Date().time)
            assignmentsSource.refreshData(this)
        }
    }

    fun clearAssignmentsSource() {
        assignmentsSource.clearCache()
    }


}
