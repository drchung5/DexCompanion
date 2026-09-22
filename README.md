# DexCompanion

DexCompanion is an Android phone and Wear OS companion app that forwards glucose values from Dexcom G7 notifications to a watch face complication. It is built with Kotlin and Jetpack Compose and currently implements the notification-to-complication flow, with starter screens still present in both apps.

## How it works

1. The phone's notification listener watches notifications from the Dexcom G7 package, `com.dexcom.g7`.
2. It inspects the notification's expanded and regular custom views for the first text value that parses as an integer between `40` and `400`.
3. It sends that value and a send timestamp to the watch through the Wearable Data Layer at `/glucose`.
4. The watch listener saves the received value locally and requests an update for the watch face complication.
5. The **Dex Companion** complication displays the saved value in a compatible short-text slot.

The integration reads notifications; it does not sign in to a Dexcom account or connect directly to a sensor.

## Current behavior and limitations

| Surface | Current behavior |
| --- | --- |
| Phone background service | Extracts and forwards matching Dexcom G7 notification values after notification access is granted. |
| Watch face complication | Displays the latest value received by the watch and requests a refresh when new data arrives. |
| Phone app screen | Displays a starter greeting. Every time the activity is created, it also sends the hardcoded test value `147`. |
| Watch app screen | Displays a starter greeting and placeholder buttons. Its launcher label is currently `wear`. |
| Watch tile | Displays a placeholder greeting and appears as **Example tile** in the tile picker. |

The complication uses `125` for its preview and as the fallback before any value has been received. Opening the phone app can replace a received glucose value with the test value `147` until another notification update arrives.

Notification parsing depends on Dexcom G7's custom notification layout. Only integer text from `40` through `400` is accepted; decimal values, `LOW`/`HIGH` text, and other package names are not handled. There is no unit detection or conversion, trend arrow, glucose history, or alert implementation.

The watch saves only the numeric value. It does not use the transmitted timestamp or display reading age, connection status, or a stale-data indicator, so a saved value can remain visible after updates stop.

## Set up on a phone and watch

1. Use an Android phone running API 37 or later and a paired Wear OS watch running API 30 or later, with Google Play services available.
2. Install the `app` module on the phone and the `wear` module on the watch using the same signing key. Both use the application ID `com.dchung.dexcompanion`.
3. On the phone, open Android Settings, find **Notification access**, and enable access for **DexCompanion**. The app does not yet provide an in-app shortcut or onboarding for this permission.
4. Ensure Dexcom G7 is posting glucose notifications on the phone. The listener processes notifications as they are posted or updated.
5. Edit a watch face with a short-text complication slot and select **Dex Companion** as its data source.

The watch needs the phone app for notification-derived updates, even though its manifest currently declares it as standalone. The watch app screen and tile do not display received glucose data yet.

## Development

Open the project root in Android Studio with support for the configured Android Gradle Plugin (`9.4.1`). Install Android SDK Platform 37 and sync Gradle. Both modules compile and target API 37; the included wrapper uses Gradle `9.6.0`, and the daemon JVM configuration requests Java 25. Let Android Studio configure the SDK path in the Git-ignored `local.properties` file.

Build both debug APKs:

```sh
./gradlew :app:assembleDebug :wear:assembleDebug
```

On Windows, use `gradlew.bat` instead of `./gradlew`. In Android Studio, run `app` on the phone and `wear` on the watch.

### Verify the data flow

- Launch the phone app to send the test value `147`, then check the **Dex Companion** complication on the paired watch.
- With notification access enabled, wait for a Dexcom G7 notification containing a supported value and check that the complication updates.
- Filter Logcat by `DexCompanion` to inspect notification extraction, send success or failure, and watch reception.

The test value verifies the phone-to-watch path; a Dexcom notification update is needed to verify notification parsing as well.

### Checks

Run the existing phone example unit tests and lint both modules:

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :wear:lintDebug
```

Run the phone instrumentation tests with a compatible phone or emulator connected:

```sh
./gradlew :app:connectedDebugAndroidTest
```

The existing tests are template examples. They do not validate notification parsing, delivery between devices, or complication updates.

## Project structure

```text
app/       Android phone app, notification listener, and Data Layer sender
wear/      Wear OS app, Data Layer receiver, complication, and placeholder tile
gradle/    Version catalog, Gradle wrapper, and daemon JVM configuration
```

Key implementation files:

- [Dexcom notification listener](app/src/main/java/com/dchung/dexcompanion/DexcomNotificationListener.kt)
- [Phone activity and test sender](app/src/main/java/com/dchung/dexcompanion/MainActivity.kt)
- [Watch data listener](wear/src/main/java/com/dchung/dexcompanion/GlucoseDataListenerService.kt)
- [Watch complication](wear/src/main/java/com/dchung/dexcompanion/complication/GlucoseComplicationService.kt)
- [Watch activity](wear/src/main/java/com/dchung/dexcompanion/presentation/MainActivity.kt)
- [Watch tile](wear/src/main/java/com/dchung/dexcompanion/tile/GlucoseTileService.kt)
- [Watch labels](wear/src/main/res/values/strings.xml)
