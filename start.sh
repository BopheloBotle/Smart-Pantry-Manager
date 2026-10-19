#!/usr/bin/env bash
# Start BlushPantry in one command: locate the Android SDK, write
# local.properties, run unit tests, assemble the debug APK, and install
# it on a connected device or emulator when one is available.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

echo "========================================"
echo "  Smart Pantry Manager (BlushPantry)"
echo "========================================"

if ! command -v java >/dev/null 2>&1; then
  echo "Java was not found. Install JDK 17 or newer, then re-run this script."
  exit 1
fi

echo "Java: $(java -version 2>&1 | head -n 1)"

find_sdk() {
  if [ -n "${ANDROID_HOME:-}" ] && [ -d "$ANDROID_HOME" ]; then
    echo "$ANDROID_HOME"
    return
  fi
  if [ -n "${ANDROID_SDK_ROOT:-}" ] && [ -d "$ANDROID_SDK_ROOT" ]; then
    echo "$ANDROID_SDK_ROOT"
    return
  fi

  local candidates=(
    "$HOME/AppData/Local/Android/Sdk"
    "$HOME/Library/Android/sdk"
    "$HOME/Android/Sdk"
    "/c/Users/$USER/AppData/Local/Android/Sdk"
    "/mnt/c/Users/$USER/AppData/Local/Android/Sdk"
  )
  local path
  for path in "${candidates[@]}"; do
    if [ -d "$path" ]; then
      echo "$path"
      return
    fi
  done
  echo ""
}

SDK="$(find_sdk)"
if [ -z "$SDK" ]; then
  echo
  echo "Android SDK not found. The project is complete, but this machine"
  echo "cannot compile it until Android Studio (or command-line tools) is installed."
  echo
  echo "Do this once:"
  echo "  1. Install Android Studio: https://developer.android.com/studio"
  echo "  2. Open this folder as a project and let Gradle sync."
  echo "  3. Create an emulator (API 33 or 34) or plug in a phone with USB debugging."
  echo "  4. Re-run: ./start.sh"
  echo
  echo "If the SDK is already installed, set ANDROID_HOME and re-run."
  exit 1
fi

export ANDROID_HOME="$SDK"
export ANDROID_SDK_ROOT="$SDK"
echo "Android SDK: $SDK"

# Gradle needs a local.properties file pointing at the SDK.
# Use a Windows-friendly path when running from Git Bash.
SDK_PROP="$SDK"
case "$SDK" in
  /c/*|/d/*|/e/*)
    SDK_PROP="$(echo "$SDK" | sed -E 's|^/([a-zA-Z])/|\1:/|; s|/|\\|g')"
    ;;
esac

printf 'sdk.dir=%s\n' "${SDK_PROP//\\/\\\\}" > "$ROOT/local.properties"
echo "Wrote local.properties"

chmod +x "$ROOT/gradlew" 2>/dev/null || true

echo
echo "Running unit tests (strict recipe matching)..."
./gradlew testDebugUnitTest --quiet

echo
echo "Assembling debug APK..."
./gradlew assembleDebug --quiet

APK="$ROOT/app/build/outputs/apk/debug/app-debug.apk"
echo "APK: $APK"

ADB="$SDK/platform-tools/adb"
if [ ! -x "$ADB" ] && [ -f "$ADB.exe" ]; then
  ADB="$ADB.exe"
fi

if [ -x "$ADB" ] || [ -f "$ADB" ]; then
  DEVICE_COUNT="$("$ADB" devices | awk 'NR>1 && $2=="device" {count++} END {print count+0}')"
  if [ "$DEVICE_COUNT" -gt 0 ]; then
    echo "Installing on $DEVICE_COUNT connected device(s)..."
    "$ADB" install -r "$APK"
    "$ADB" shell am start -n com.bophelobotle.blushpantry/.MainActivity || true
    echo "App launched."
  else
    echo "No emulator or device is online."
    echo "Start an emulator in Android Studio (Device Manager), then re-run ./start.sh"
    echo "You can still install later with:"
    echo "  adb install -r \"$APK\""
  fi
else
  echo "adb was not found under the SDK. Open the project in Android Studio and press Run."
fi

echo
echo "Done."
