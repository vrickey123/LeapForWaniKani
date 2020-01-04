# LeapForWaniKani
<!-- ABOUT -->
## About
[WaniKani](https://www.wanikani.com/) is a Japanese-language-learning app that uses a Spaced Repitition System (SRS) to help users learn Japanese Kanji characters.

Leap For Wanikani is an open-source app developed by the community with three main features:

1. A Dashboard that syncs your current WaniKani lessons and reviews status to your device
2. Push notifications that alert you if you have pending lessons or reviews in your queue
3. An in-app browser that takes you directly to your lessons or reviews then back to the app's Dashboard

### Google Play
Leap For Wanikani is available for download on the [Google Play Store]().

<!-- USAGE EXAMPLES -->
## Technical
The app follows Android's standard MVVM (Model View ViewModel) architecture and implements a **main-safe repository layer with coroutines**. This means that asyncronous functions to request local or remote data use `suspend fun` instead of `LiveData` (or RxJava) in the repository and are only wrapped as obersvable `LiveData` in the `ViewModel`.

Let's look at the data flow for a [Summary](https://docs.api.wanikani.com/20170710/#summary) that backs our lessons and reviews cards as well as push notifications.

### Fragment (Make Request)
Make a request to refresh our data (summary, assignments, etc.) from `DashboardFragment#onResume`.
```
override fun onResume() {
        super.onResume()
        dashboardViewModel.refreshData()
    }
```

### ViewModel (Make Request)
Launch the request using the `DashboardViewModel#viewModelScope` that will cancel the coroutine automatically once the ViewModel's lifecycle owner (the Fragment) is destroyed.
```
fun refreshData() {
        viewModelScope.launch {
            _summary.value = waniKaniRepository.getSummary()
            ...
        }
    }
```

### Repository
The Repository layer is reponsible for returning local or remote data.

```
    override suspend fun getSummary(): LeapResult<WKReport.Summary> {
        return withContext(ioDispatcher) {
            return@withContext fetchSummaryRemoteOrLocal()
        }
    }
    
      private suspend fun fetchSummaryRemoteOrLocal(): LeapResult<WKReport.Summary> {
        val remoteSummary = wkRemoteDataSource.getSummaryAsync()
        when (remoteSummary) {
            is WKApiResponse.ApiError -> Log.w(TAG, "Remote summary source fetch failed")
            is WKApiResponse.ApiNotModified -> {
                return getSummaryFromLocal()
            }
            is WKApiResponse.ApiSuccess -> {
                refreshLocalSummary(remoteSummary.responseData)
                return LeapResult.Success(remoteSummary.responseData)
            }
            is WKApiResponse.NoConnection -> {
                return LeapResult.Offline
            }
            else -> throw IllegalStateException()
        }

        // Local if remote fails
        val localSummary = getSummaryFromLocal()
        if (localSummary is LeapResult.Success) return localSummary
        return LeapResult.Error(Exception("ApiError fetching summary from remote and local"))
    }

    private suspend fun getSummaryFromLocal(): LeapResult<WKReport.Summary> {
        return wkLocalDataSource.getSummary()
    }

    private suspend fun getSummaryRemote(): WKApiResponse<WKReport.Summary> {
        return wkRemoteDataSource.getSummaryAsync()
    }
```

#### Remote
`WKRemoteDataSource` wraps a retrofit `Response` into as `WKApiResponse` depending on the modified/not modified response.
```
interface WKRemoteDataSource {
    suspend fun getSummaryAsync(): WKApiResponse<WKReport.Summary>
}
```

Retrofit API responsible for network requests.
```
interface WaniKaniApi {
    @GET("summary")
    suspend fun getSummaryAsync():Response<WKReport.Summary>
}
```

#### Local

##### E-tags
The WaniKani API supports [conditional requests](https://docs.api.wanikani.com/20170710/#best-practices) with e-tags to determine whether or not a user's data has changed since the last time they made a reponse. 

If their data has not changed, a `304 Not Modified` response is returned to the app which significantly reduces mobile network usage by eliminating unecessary downloads.

### ViewModel (Observe Request)
The `LiveData<Summary` emits changes when the local or remote data source is triggered.
```
    val liveDataSummary: LiveData<LeapResult<WKReport.Summary>> =
        liveData {
            emitSource(_summary)
        }
```

### Fragment (Observe Response)

<!-- ROADMAP -->
## Roadmap
See the [open issues](https://github.com/othneildrew/Best-README-Template/issues) for a list of proposed features (and known issues).

See the [CHANGELOG]() for a summary of recent changes.

<!-- CONTRIBUTING -->
## Contributing
Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- VERSIONING -->
## Versioning
We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

<!-- LICENSE -->
## License
Distributed under the GNU General Public License. 

In short, this copyleft lisence allows you to use this code in your app as long as you also distribute it as a free and open-source project under the same license. If you are Tofugu/WaniKani and would like to use it, please contact us.

See [LICENSE](https://github.com/vrickey123/LeapForWaniKani/blob/feature/readme/LICENSE.md) for more information.

<!-- CONTACT -->
## Contact
Your Name - [@your_twitter](https://twitter.com/your_username) - email@example.com

Project Link: [https://github.com/your_username/repo_name](https://github.com/your_username/repo_name)

## Acknowledgements
* [WaniKani API v2](https://docs.api.wanikani.com/20170710/)
* [Android Architecture Blueprints v2 MVVM/Coroutines, Jose Alcérreca, Google](https://github.com/android/architecture-samples)
* [Coroutines On Android (part III): Real work, Sean McQuillan, Google](https://medium.com/androiddevelopers/coroutines-on-android-part-iii-real-work-2ba8a2ec2f45)
* [Reducing your networking footprint with OkHttp, Etags and If-Modified-Since](https://android.jlelse.eu/reducing-your-networking-footprint-with-okhttp-etags-and-if-modified-since-b598b8dd81a1)
* [Best README Template](https://github.com/othneildrew/Best-README-Template)
