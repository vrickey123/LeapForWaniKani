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

### ViewModel

### Repository
#### E-tags

### Remote

### Local

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
