# DexCompanion

An Android phone and Wear OS prototype for sending glucose data from a phone to a watch. Both apps use Kotlin and Jetpack Compose.

## Current status

- The phone app displays a greeting and sends a hardcoded glucose value of `147` when its main activity is created.
- Updates use the Wearable Data Layer path `/glucose`, with an integer `glucose` and a long `timestamp`. The timestamp makes each test update distinct.
- The watch app currently displays a starter interface with placeholder buttons.
- The watch tile displays a placeholder greeting.
- The watch face complication supports short text and displays a fixed value of `125`.

Receiving phone updates on the watch and connecting them to the tile or complication are not implemented yet. There is no live glucose data integration; displayed values are test data.

## Project structure

```text
app/       Android phone app and Wearable Data Layer sender
wear/      Wear OS app, tile, and watch face complication
gradle/    Version catalog, Gradle wrapper, and daemon JVM configuration
```

Entry points:

- [Phone activity](app/src/main/java/com/dchung/dexcompanion/MainActivity.kt)
- [Watch activity](wear/src/main/java/com/dchung/dexcompanion/presentation/MainActivity.kt)
- [Watch tile](wear/src/main/java/com/dchung/dexcompanion/tile/GlucoseTileService.kt)
- [Watch complication](wear/src/main/java/com/dchung/dexcompanion/complication/GlucoseComplicationService.kt)

## Setup

1. Open the project root in Android Studio with support for the configured Android Gradle Plugin (`9.4.1`).
2. Install Android SDK Platform 37 through the SDK Manager. Both modules compile and target API 37.
3. Let Android Studio configure the SDK location in `local.properties`, which is ignored by Git.
4. Sync the project with Gradle. The included wrapper uses Gradle `9.6.0`, and the daemon JVM configuration requests Java 25.

The phone app requires API 37 or later. The watch app requires a Wear OS device or emulator running API 30 or later.

## Build and run

Build both debug APKs from the project root:

```sh
./gradlew :app:assembleDebug :wear:assembleDebug
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

In Android Studio, run the `app` configuration on the phone and the `wear` configuration on the watch. To exercise the Data Layer sender, use a paired phone and watch with Google Play services and install both apps using the same signing key. Both modules use the application ID `com.dchung.dexcompanion`.

Launching the phone app submits the test update. The watch UI and complication currently remain at their placeholder values because no watch-side receiver is implemented.

To view the watch surfaces, add the DexCompanion tile from the watch's tile picker or select its complication in a compatible short-text watch face slot.

## Checks

Run the phone module's existing example unit tests and lint both modules:

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :wear:lintDebug
```

Run the phone instrumentation tests with a compatible phone or emulator connected:

```sh
./gradlew :app:connectedDebugAndroidTest
```

The current tests are template examples and do not validate glucose delivery between devices.
