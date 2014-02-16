gradle-android-appiconoverlay
=============================

Plugin for Android Gradle to automatically overlay the app icon with information about the current build: git branch name and commit SHA1.

## Example
![Example: app icon with overlay](https://github.com/splatte/gradle-android-appiconoverlay/raw/master/example.png "Example: app icon with overlay")

## Usage
1. Checkout this project
2. run ``gradle jar``
3. Reference created ``build/libs/gradle-android-appiconoverlay-1.0.jar`` in your Android project's ``build.gradle`:
```
buildscript {
   ...
   dependencies {
        classpath files('/path/to/gradle-android-appiconoverlay/build/libs/gradle-android-appiconoverlay-1.0.jar')
    }
    ...
}
...
apply plugin: 'app-icon-overlay'
```
The plugin will hook into your build process automatically and overwrite the app icon for debug builds. It will not mess with any files in your repository.
