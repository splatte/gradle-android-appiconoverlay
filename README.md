gradle-android-appiconoverlay
=============================


Plugin for Android Gradle to automatically overlay the app icon with information about the current build: git branch name and commit SHA1.


## Example
![Example: app icon with overlay](https://github.com/splatte/gradle-android-appiconoverlay/raw/master/example.png "Example: app icon with overlay")


## Usage

1. This plugin is published on JCenter. Add dependency in your Android project's top-level `build.gradle` file:

    ```groovy
    buildscript {
        dependencies {
            classpath 'com.github.splatte:gradle-android-appiconoverlay:2.0'
        }
    }
    ```

2. Apply the plugin in your app module's ``build.gradle`` file:

    ```groovy
    apply plugin: 'app-icon-overlay'
    appiconoverlay {
        /* then see below for configuration options */
    }

    android {
        /* project config */
    }
    ```

3. For those using Gradle 3.+ disable aapt2 in your app module's ``gradle.properties`` file:

    ```groovy
    android.enableAapt2=false
    ```

4. You will need ``convert`` from the [ImageMagick](http://imagemagick.org/) project to do the image processing. You can install it using a command along the lines of:

    ```bash
    # debian/ubuntu
    sudo apt-get install imagemagick

    # osx
    sudo port install imagemagick
    sudo brew install imagemagick

    # required on osx otherwise imagemagick (silently) goes into an infinite loop complaining about missing fonts
    sudo brew install ghostscript
    ```

The plugin will hook into your build process automatically and overwrite the target app icon files for debug builds. It will not mess with any files in your repository.


## Customization
The plugin offers some options for customizing the appearance of the generated icon. Simply add a block to your app module's ``build.gradle``:

```groovy
appiconoverlay {
    textColor '#FFF'                  /* #rrggbbaa format */
    backgroundColor "#0008"           /* #rrggbbaa format */
    format '$build->$branch\n$commit' /* GString */
    imageMagick 'convert'             /* command to run ImageMagick */
}
```

Option                 | Description
---------------------- | ------------------
`textColor`            | Text color in #rrggbbaa format.
`backgroundColor`      | Background color for overlay in #rrggbbaa format.
`format`               | Format string to be used to create the text in the overlay.<br />*Note*: Use single quotes, it's a GString.<br />The following variables are available: <ul><li>`$branch` name of git branch</li> <li>`$commit` short SHA1 of latest commit in current branch</li> <li>`$build` the name of the build variant ex. Debug</li></ul>
`imageMagick`          | Command to run ImageMagick's "convert".


## Building locally

If you want to make changes to the plugin, here's how you can build it locally and test it with an Android app:
1. Check out the project using git.
2. In the root directory, run ```gradle jar```.
3. Find the generated .jar file in ```./build/libs/```.
4. To reference your local .jar file in your Android project, declare the dependency as follows in the project's top-level `build.gradle` file:
   ```groovy
   buildscript {
       dependencies {
           classpath files('/path/to/gradle-android-appiconoverlay/build/libs/gradle-android-appiconoverlay-2.0.jar')
       }
   }
   ```


## Credits
Idea based on the [IconOverlaying](https://github.com/krzysztofzablocki/IconOverlaying) project by Krzysztof Zabłocki, which implements a similar feature for iOS builds.
