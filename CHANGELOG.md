# CHANGELOG
## v1.2.1
_2020-08-19_

- Removed passed and srs_stage_name from Assignment model. These were removed without an API version update so the API version pin didn't catch it.
- Destructive database migration used for deprecated fields since the WK server maintains user info and app can resync.
- Room Database schema version 2

## v1.2.0
_2020-07-01_

- Review forecast, notification categories in nav drawer, design update

## v1.1.1
_2020-05-30_

- The default push frequency is every 15 minutes, not 0.

## v1.1.0
_2020-05-28_

- Added Notification Preferences and New Features Snackbar

## v1.0.1
_2020-05-28_

- Fixed notification bugs using next reviews at and available at Summary fields.

## v1.0.0
_2020-01-09_

- A Dashboard that syncs your current WaniKani lessons and reviews status to your device
- Push notifications that alert you if you have pending lessons or reviews in your queue
- An in-app browser that takes you directly to your lessons or reviews then back to the app's Dashboard
- Main-safe repository layer with coroutines
- Etags support _not modified_ reponses from the WaniKani API V2
