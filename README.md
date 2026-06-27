# Smile Garden 🌻

A lightweight Android app that watches your face (front camera) and:
- **Smiling** → grows and blooms a field of **sunflowers**
- **Not smiling** → shows a **desert**

It transitions smoothly between the two scenes as your smile is detected,
rather than jump-cutting, so it feels alive.

## Why it's lightweight

- **No image/video assets at all.** Both scenes (desert + sunflower field)
  are drawn procedurally with Jetpack Compose's `Canvas` (vector shapes),
  so there's nothing to bundle or download for the visuals.
- **Face detection runs on-device** via ML Kit's Face Detection API in
  "fast" performance mode with only the smile classifier enabled (no
  landmarks/contours), which is the cheapest configuration.
- Uses the **Google-Play-Services-backed** ML Kit artifact
  (`play-services-mlkit-face-detection`), so the face model is fetched by
  Play Services on first run instead of being bundled into the APK — this
  keeps the app itself only a few MB. (If you need it to work with zero
  internet ever, even on first launch, see the one-line swap noted in
  `app/build.gradle.kts`.)
- No backend, no analytics, no ads, minimal dependency set.

## Requirements

- **minSdk 29 (Android 10)** — meets your "Android 10 minimum" requirement
- targetSdk 34
- A device with a front-facing camera
- Google Play Services available on the device (true for virtually all
  Android 10+ phones)

## Project structure

```
SmileGarden/
├── app/
│   ├── build.gradle.kts                 # dependencies, minSdk=29
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/example/smilegarden/
│           ├── MainActivity.kt          # Compose UI, smoothing/hysteresis logic
│           ├── CameraPreviewOverlay.kt  # CameraX setup (front camera)
│           ├── FaceSmileAnalyzer.kt     # ML Kit smile detection per frame
│           └── ui/
│               ├── DesertScene.kt       # procedurally drawn desert
│               ├── SunflowerScene.kt    # procedurally drawn/animated sunflowers
│               └── Overlays.kt          # status badge + permission prompt
├── build.gradle.kts
└── settings.gradle.kts
```

## Get an installable APK with NO local installs (recommended)

You can have GitHub's free build servers compile a real, installable
`app-debug.apk` for you in the cloud — you only need a free GitHub account
and a browser, nothing installed on your computer or phone.

1. Go to **github.com**, sign in (or create a free account), then click the
   **+** in the top-right → **New repository**. Name it anything (e.g.
   `smile-garden`), keep it **Public**, click **Create repository**.
2. On the new repo's page, click **"uploading an existing file"** (or
   `Add file > Upload files`).
3. Unzip `SmileGarden.zip` on your computer, then drag the **contents** of
   the `SmileGarden` folder (not the folder itself) into the GitHub upload
   box — `app/`, `.github/`, `build.gradle.kts`, etc. Click **Commit changes**.
4. Click the **Actions** tab at the top of your repo. A workflow called
   "Build Installable APK" will already be running (it starts automatically
   on upload). Wait 3–6 minutes for the green checkmark ✅.
5. Click into that finished run, scroll down to **Artifacts**, and click
   **SmileGarden-app-debug** to download a zip containing `app-debug.apk`.
6. Transfer that `.apk` to your Android phone (e.g. email it to yourself,
   or download directly on the phone's browser if you did step 5 on the
   phone). Tap it to install — Android will prompt you to allow
   "install unknown apps" for that source the first time. Then open the app
   and grant the camera permission.

That's it — no Android Studio, no SDK, no command line, ever.

## Alternative: build it yourself with Android Studio

1. **Open the project**
   Android Studio → `File > Open` → select the `SmileGarden` folder.
2. **Let it sync.** First sync needs internet to fetch AndroidX/ML Kit/Compose
   dependencies and the Gradle wrapper jar (Android Studio will offer to
   regenerate the wrapper automatically if it's missing — accept that prompt).
3. **Run it:**
   - Plug in an Android phone (Android 10+) with USB debugging enabled, or
     start an emulator that includes Play Store/Play Services.
   - Click the green ▶ Run button, choose your device.
4. **Or build an installable APK without running from Studio:**
   - `Build > Build App Bundle(s) / APK(s) > Build APK(s)`
   - Studio will show a "locate" link to the generated
     `app/build/outputs/apk/debug/app-debug.apk`
   - Copy that APK to your phone (or `adb install app-debug.apk`) and open it
     — you may need to allow "Install unknown apps" for the source you use.
5. On first launch, grant the **camera permission** when prompted.

### Command line (if you already have a JDK 17 + Android SDK set up)

```bash
cd SmileGarden
./gradlew assembleDebug
# APK will be at app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

(`gradlew`/`gradlew.bat` wrapper scripts aren't included in this export —
run `gradle wrapper` once inside the project folder if you're not opening
it via Android Studio, or just let Android Studio regenerate them.)

## Tuning it

- **Sensitivity** — in `MainActivity.kt`, the hysteresis thresholds
  `smoothedSmile > 0.6f` (start blooming) and `< 0.3f` (back to desert)
  control how easily a smile triggers the change. Lower the first number to
  make it more sensitive.
- **Smoothing** — `smoothedSmile * 0.7f + target * 0.3f` is a simple
  exponential moving average; raise the `0.3f` weight for snappier (but
  jitterier) response.
- **Bloom speed** — `tween(durationMillis = 1400)` in `MainActivity.kt`
  controls how long the desert↔sunflower crossfade/growth takes.
- **More/fewer flowers, layout** — edit `flowerSpecs` in
  `SunflowerScene.kt`.
