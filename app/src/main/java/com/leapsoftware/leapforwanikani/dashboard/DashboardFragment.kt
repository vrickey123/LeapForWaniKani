package com.leapsoftware.leapforwanikani.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.card.MaterialCardView
import com.leapsoftware.leapforwanikani.MainViewModel
import com.leapsoftware.leapforwanikani.R
import com.leapsoftware.leapforwanikani.ViewModelFactory
import com.leapsoftware.leapforwanikani.data.LeapResult
import com.leapsoftware.leapforwanikani.data.source.WaniKaniRepository
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport

class DashboardFragment : Fragment() {

    private val TAG by lazy { DashboardViewModel::class.java.simpleName }

    companion object {
        fun newInstance() = DashboardFragment()
    }

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var availableStatus: TextView
    private lateinit var lessonsCardView: MaterialCardView
    private lateinit var reviewsCardView: MaterialCardView
    private lateinit var stageProgressCardView: MaterialCardView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = WaniKaniRepository.getInstance(context!!)
        val factory = ViewModelFactory(repository)
        dashboardViewModel = ViewModelProviders.of(this, factory).get(DashboardViewModel::class.java)
        mainViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        availableStatus = view.findViewById<TextView>(R.id.available_status_textview)
        lessonsCardView = view.findViewById<MaterialCardView>(R.id.lessons_card_included)
        reviewsCardView = view.findViewById<MaterialCardView>(R.id.reviews_card_included)
        stageProgressCardView = view.findViewById<MaterialCardView>(R.id.stage_progress_card_included)
        progressBar = view.findViewById<ProgressBar>(R.id.dashboard_progress_bar)

        val dashboardViewAdapter = DashboardViewAdapter(view.context)
        dashboardViewAdapter.bindLessonsTitle(lessonsCardView, getString(R.string.card_lessons_title))
        dashboardViewAdapter.bindReviewsTitle(reviewsCardView, getString(R.string.card_reviews_title))

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener({
            mainViewModel.refreshData()
            dashboardViewModel.refreshData()
            swipeRefreshLayout.isRefreshing = false
        })

        lessonsCardView.setOnClickListener {
            WebDelegate.openLessons(it.context)
        }

        reviewsCardView.setOnClickListener {
            WebDelegate.openReviews(it.context)
        }

        subscribeToUi(
            dashboardViewAdapter, availableStatus, lessonsCardView, reviewsCardView,
            stageProgressCardView, progressBar
        )
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.refreshData()
    }

    private fun subscribeToUi(adapter: DashboardViewAdapter, availableStatus: TextView,
                              lessonsCardView: MaterialCardView, reviewsCardView: MaterialCardView,
                              stageProgressCardView: MaterialCardView, progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE

        dashboardViewModel.liveDataSummary.observe(viewLifecycleOwner, Observer { summary ->
            when (summary) {
                is LeapResult.Success<WKReport.Summary> -> {
                    // Lessons are grouped by the hour.
                    // [0] are the lessons available now, [1] are the lessons in an hour, etc. 24 hours provided.
                    adapter.bindAvailableStatus(availableStatus, summary.resultData.data.next_reviews_at)
                    adapter.bindLessonsCount(lessonsCardView, summary.resultData.data.lessons[0].subject_ids.size)
                    adapter.bindReviewsCount(reviewsCardView, summary.resultData.data.reviews[0].subject_ids.size)
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Error -> {
                    progressBar.visibility = View.GONE
                    adapter.bindAvailableStatus(availableStatus, null)
                    adapter.bindLessonsCount(lessonsCardView, 0)
                    adapter.bindReviewsCount(reviewsCardView, 0)
                }
                is LeapResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Offline -> {
                    progressBar.visibility = View.GONE
                }
            }
        })

        dashboardViewModel.liveDataAssignments.observe(viewLifecycleOwner, Observer { assignments ->
            when (assignments) {
                is LeapResult.Success<List<WKReport.WKResource.Assignment>> -> {
                    Log.d(TAG, "assignments size = " + assignments.resultData.size)
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Error -> {
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Offline -> {
                    progressBar.visibility = View.GONE
                }
            }
        })

        dashboardViewModel.liveDataCountApprentice.observe(viewLifecycleOwner, Observer { countApprentice ->
            when (countApprentice) {
                is LeapResult.Success<Int> -> {
                    adapter.bindStageApprenticeTextView(stageProgressCardView, countApprentice.resultData)
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Error -> {
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Offline -> {
                    progressBar.visibility = View.GONE
                }
            }
        })

        dashboardViewModel.liveDataCountGuru.observe(viewLifecycleOwner, Observer { countGuru ->
            when (countGuru) {
                is LeapResult.Success<Int> -> {
                    adapter.bindStageGuruTextView(stageProgressCardView, countGuru.resultData)
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Error -> {
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Offline -> {
                    progressBar.visibility = View.GONE
                }
            }
        })

        dashboardViewModel.liveDataCountMaster.observe(viewLifecycleOwner, Observer { countMaster ->
            when (countMaster) {
                is LeapResult.Success<Int> -> {
                    adapter.bindStageMasterTextView(stageProgressCardView, countMaster.resultData)
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Error -> {
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Offline -> {
                    progressBar.visibility = View.GONE
                }
            }
        })

        dashboardViewModel.liveDataCountEnlightened.observe(viewLifecycleOwner, Observer { countEnlightened ->
            when (countEnlightened) {
                is LeapResult.Success<Int> -> {
                    adapter.bindStageEnlightenedTextView(stageProgressCardView, countEnlightened.resultData)
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Error -> {
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Offline -> {
                    progressBar.visibility = View.GONE
                }
            }
        })

        dashboardViewModel.liveDataCountBurned.observe(viewLifecycleOwner, Observer { countBurned ->
            when (countBurned) {
                is LeapResult.Success<Int> -> {
                    adapter.bindStageBurnedTextView(stageProgressCardView, countBurned.resultData)
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Error -> {
                    progressBar.visibility = View.GONE
                }
                is LeapResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Offline -> {
                    progressBar.visibility = View.GONE
                }
            }
        })

        mainViewModel.onClearCache.observe(viewLifecycleOwner, Observer {
            dashboardViewModel.clearAssignmentsSource()
            dashboardViewModel.refreshData()
        })

        mainViewModel.onLogin.observe(viewLifecycleOwner, Observer {
            dashboardViewModel.refreshData()
        })
    }

}
