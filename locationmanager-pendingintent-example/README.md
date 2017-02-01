Android LocationManager Example using PendingIntents with a BroadcastReceiver and IntentService
=============================

This example shows how to use Android's [LocationManager](https://developer.android.com/reference/android/location/LocationManager.html) API to
request location updates using both the Provider and [Criteria](https://developer.android.com/reference/android/location/Criteria.html) request calls,
and demonstrating how to handle location updates from a [PendingIntent](https://developer.android.com/reference/android/app/PendingIntent.html) to a
[BroadcastReceiver](https://developer.android.com/reference/android/content/BroadcastReceiver.html) (to handle location updates in the main thread) and with an
[IntentService](https://developer.android.com/reference/android/app/IntentService.html) (to handle location updates in a background thread).