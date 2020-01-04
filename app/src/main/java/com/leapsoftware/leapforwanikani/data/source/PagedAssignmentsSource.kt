package com.leapsoftware.leapforwanikani.data.source

import android.util.Log
import androidx.lifecycle.*
import com.leapsoftware.leapforwanikani.data.LeapResult
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Creates a composite List<Assignment> by back filling the initially loaded List<Assignment>
 * and additional List<Assignment>'s paged from the network until all responses have been
 * received.
 *
 * [loadedPagedAssignments] Emits "initial" list from database or "load after" single page from
 * network until there are no more pages
 * [pagedListResult] Emits composite List<Assignment> of all successful results
 *
 * Flow
 * 1. _assignments load from page 0
 * 2. initial assignments are emitted
 * 3. starts paged assignments network request with pagerAfterId
 * 4. additional paged assignments are emitted
 * 5. composite list created after all pages return
 *
 * TODO: Add generic type or use List<WKResource> if we add native features and support for more types
 */
class PagedAssignmentsSource(private val waniKaniRepository: WaniKaniRepository) {

    private val TAG by lazy { PagedAssignmentsSource::class.java.simpleName }

    private val _assignments = MutableLiveData<LeapResult<List<WKReport.WKResource.Assignment>>>().apply {
        value = LeapResult.Loading
    }

    private var lastLoadedPageId: Int = -1
    private val pagedList = mutableListOf<WKReport.WKResource.Assignment>()

    private val loadedPagedAssignments: LiveData<LeapResult<List<WKReport.WKResource.Assignment>>> =
        Transformations.switchMap(_assignments) { assignments ->
            liveData {
                val result: LeapResult<List<WKReport.WKResource.Assignment>>? = assignments
                when (result) {
                    is LeapResult.Success -> {
                        if (result.resultData.isNotEmpty()) {
                            val pageAfterId = result.resultData.last().id
                            if (lastLoadedPageId == pageAfterId) {
                                Log.d(TAG, "Pages finished. Last page id = $pageAfterId")
                                emit(LeapResult.Success(emptyList()))
                            } else {
                                Log.d(TAG, "Getting paged assignments after id = $pageAfterId")
                                emitSource(_assignments)
                                lastLoadedPageId = pageAfterId
                                _assignments.value = loadPageAfter(lastLoadedPageId)
                            }
                        } else {
                            // first load of db on install or clear cache is empty
                            emit(LeapResult.Success(emptyList()))
                        }
                    }
                    else -> {
                        emitSource(_assignments)
                    }
                }
            }
        }

    private val pagedListResult: LiveData<LeapResult<List<WKReport.WKResource.Assignment>>> =
        Transformations.switchMap(loadedPagedAssignments) { assignments ->
            liveData {
                val result: LeapResult<List<WKReport.WKResource.Assignment>>? = assignments
                when (result) {
                    is LeapResult.Success -> {
                        pagedList.addAll(result.resultData)
                        val newList = LeapResult.Success(pagedList.toList())
                        val message = String.format(
                            "%s assigments added. Updated paged list size is %s",
                            result.resultData.size,
                            pagedList.size
                        )
                        Log.d(TAG, message)
                        emit(newList)
                    }
                    is LeapResult.Loading -> {
                        emit(LeapResult.Loading)
                    }
                    is LeapResult.Error -> {
                        Log.d(TAG, "Error fetching an assignment page. Returning results thus far.")
                        emit(LeapResult.Success(pagedList.toList()))
                    }
                }
            }
        }

    fun getPagedAssignments(): LiveData<LeapResult<List<WKReport.WKResource.Assignment>>> {
        return pagedListResult
    }

    fun refreshData(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            _assignments.value = loadInitialPage()
        }
    }

    fun clearCache() {
        pagedList.clear()
    }

    private suspend fun loadInitialPage(): LeapResult<List<WKReport.WKResource.Assignment>> {
        return waniKaniRepository.getAssignments(0)
    }

    private suspend fun loadPageAfter(pageAfterId: Int): LeapResult<List<WKReport.WKResource.Assignment>> {
        return waniKaniRepository.getAssignments(pageAfterId)
    }

}