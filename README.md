# Leap For WaniKani
<img src="https://github.com/vrickey123/LeapForWaniKani/blob/develop/docs/dashboard.png" width="360">

<!-- ABOUT -->
## About
[WaniKani](https://www.wanikani.com/) is a Japanese-language-learning app that uses a Spaced Repetition System (SRS) to help users learn Japanese Kanji characters.

Leap For Wanikani is an open-source app developed by the community with three main features:

1. A Dashboard that syncs your current WaniKani lessons and reviews status to your device
2. Push notifications that alert you if you have pending lessons or reviews in your queue
3. An in-app browser that takes you directly to your lessons or reviews then back to the app's Dashboard

Leap for WaniKani also has a [WaniKani Community](https://community.wanikani.com/t/android-leap-for-wanikani-demo-native-offline-no-web/38276) forum post.

### Google Play
Leap For Wanikani is available for download on the [Google Play Store](https://play.google.com/store/apps/details?id=com.leapsoftware.leapforwanikani&hl=en_US).

<!-- USAGE EXAMPLES -->
## Technical
#### Main-Safe Repository With Coroutines
The app follows Android's standard **MVVM** ([Model View ViewModel](https://developer.android.com/jetpack/docs/guide#recommended-app-arch)) architecture and implements a **main-safe repository layer with coroutines**. This means that asyncronous functions to request local or remote data use `suspend fun` instead of `LiveData` (or RxJava) in the repository and are only wrapped as observable `LiveData` in the `ViewModel`.

Let's look at the data flow for a [Summary](https://docs.api.wanikani.com/20170710/#summary) that backs our lessons and reviews cards as well as push notifications.

### Fragment (Make Request)
Make a request to refresh our data (summary, assignments, etc.) from `DashboardFragment#onResume`.
```kotlin
override fun onResume() {
        super.onResume()
        dashboardViewModel.refreshData()
    }
```

### ViewModel (Make Request)
Launch the request using the `DashboardViewModel#viewModelScope` that will cancel the coroutine automatically once the ViewModel's lifecycle owner (the Fragment) is destroyed.
```kotlin
fun refreshData() {
        viewModelScope.launch {
            _summary.value = waniKaniRepository.getSummary()
            ...
        }
    }
```

### Repository (Get Local or Remote)
The Repository layer is reponsible for returning local or remote data. Note that `WKApiResponse.ApiNotModified` returns local data. (See E-tags and Conditional Requests)

```kotlin
    override suspend fun getSummary(): LeapResult<WKReport.Summary> {
        return withContext(ioDispatcher) {
            return@withContext fetchSummaryRemoteOrLocal()
        }
    }
    
    private suspend fun fetchSummaryRemoteOrLocal(updatedAfter: Long): LeapResult<WKReport.Summary> {
        val remoteSummary = wkRemoteDataSource.getSummaryAsync(updatedAfter)
        when (remoteSummary) {
            is WKApiResponse.ApiError -> {
                Log.w(TAG, "Remote summary source fetch failed")
                // Try Local if remote fails
                val localSummary = getSummaryFromLocal()
                if (localSummary is LeapResult.Success) return localSummary
                val exception = createException(remoteSummary.code, "ApiError fetching summary from remote and local")
                return LeapResult.Error(exception)
            }
            is WKApiResponse.ApiNotModified -> {
                Log.d(TAG, "Remote summary not modified. Returning local.")
                return getSummaryFromLocal()
            }
            is WKApiResponse.ApiSuccess -> {
                Log.d(TAG, "Remote summary success. Returning latest remote.")
                refreshLocalSummary(remoteSummary.responseData)
                return LeapResult.Success(remoteSummary.responseData)
            }
            is WKApiResponse.NoConnection -> {
                Log.e(TAG, "No connection. Could not fetch fresh summary.")
                return LeapResult.Offline
            }
            else -> throw IllegalStateException()
        }
    }

    private suspend fun getSummaryFromLocal(): LeapResult<WKReport.Summary> {
        return wkLocalDataSource.getSummary()
    }

    private suspend fun getSummaryRemote(): WKApiResponse<WKReport.Summary> {
        return wkRemoteDataSource.getSummaryAsync()
    }
```

#### Remote
`WKRemoteDataSource` wraps a retrofit `Response` with `WKApiResponse` to handle modified/not modified API responses.
```kotlin
interface WKRemoteDataSource {
    suspend fun getSummaryAsync(): WKApiResponse<WKReport.Summary>
}
```

The Retrofit `WaniKaniApi` implements network requests.
```kotlin
interface WaniKaniApi {
    @GET("summary")
    suspend fun getSummaryAsync():Response<WKReport.Summary>
}
```

#### Local
Async requests to a Room database are routed through `WKLocalDataSource` using `suspend` functions.
```kotlin
interface WKLocalDataSource {
    suspend fun getSummary(): LeapResult<WKReport.Summary>
}
```

```kotlin
interface WKReportDao {
    @Query("SELECT * FROM summary")
    suspend fun getSummary(): WKReport.Summary?
```

##### E-tags and Conditional Requests
The WaniKani API supports [conditional requests](https://docs.api.wanikani.com/20170710/#best-practices) with e-tags to determine whether or not a user's data has changed since the last time they made a reponse. 

If their data has not changed, a `304 Not Modified` response is returned to the app which significantly reduces mobile network usage by eliminating unecessary downloads.

### ViewModel (Observe Response)
The `LiveData<Summary>` emits changes when the local or remote data source is triggered.
```kotlin
    val liveDataSummary: LiveData<LeapResult<WKReport.Summary>> =
        liveData {
            emitSource(_summary)
        }
```

### Fragment (Observe Response)
The `DashboardFragment` observes the `LiveData<WKReport.Summary>` and reacts when a new summary is emitted. It updates the UI based on a `LeapResult.Success`,`LeapResult.Error`, `LeapResult.Loading`, or `LeapResult.Offline` response so that a user's state is accurately represented.

```kotlin
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
                    adapter.bindAvailableStatus(availableStatus, null)
                    adapter.bindLessonsCount(lessonsCardView, 0)
                    adapter.bindReviewsCount(reviewsCardView, 0)
                    progressBar.visibility = View.GONE
                    errorSnackbar.show()
                }
                is LeapResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LeapResult.Offline -> {
                    progressBar.visibility = View.GONE
                }
            }
        })
```

<!-- ROADMAP -->
## Roadmap
See the [open issues](https://github.com/vrickey123/LeapForWaniKani/issues) for a list of proposed features (and known issues).

See the [CHANGELOG](https://github.com/vrickey123/LeapForWaniKani/blob/chore/update-readme/CHANGELOG.md) for a summary of recent changes.

<!-- CONTRIBUTING -->
## Contributing
Before starting work, please the see the [open issues](https://github.com/vrickey123/LeapForWaniKani/issues) so that work is not accidentally duplicated.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- VERSIONING -->
## Versioning
We use [SemVer](http://semver.org/) for versioning. For the versions available, see [Releases](https://github.com/vrickey123/LeapForWaniKani/releases).

<!-- LICENSE -->
## License
Distributed under the GNU General Public License. 

In short, this copyleft lisence allows you to use this code in your app as long as you also distribute it as an open-source project under the same GNU GPLv3 license. If you are Tofugu/WaniKani and would like to use it, please contact us.

See [LICENSE](https://github.com/vrickey123/LeapForWaniKani/blob/develop/LICENSE.md) for more information.

<!-- CONTACT -->
## Contact
team@leapsoftware.io

## Acknowledgements
* [WaniKani API v2](https://docs.api.wanikani.com/20170710/)
* [Android Architecture Blueprints v2 MVVM/Coroutines, Jose Alcérreca, Google](https://github.com/android/architecture-samples)
* [Coroutines On Android (part III): Real work, Sean McQuillan, Google](https://medium.com/androiddevelopers/coroutines-on-android-part-iii-real-work-2ba8a2ec2f45)
* [Reducing your networking footprint with OkHttp, Etags and If-Modified-Since](https://android.jlelse.eu/reducing-your-networking-footprint-with-okhttp-etags-and-if-modified-since-b598b8dd81a1)
* [Best README Template](https://github.com/othneildrew/Best-README-Template)
