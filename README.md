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
            classpath 'com.github.splatte:gradle-android-appiconoverlay:1.2'
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

3. You will need ``convert`` from the [ImageMagick](http://imagemagick.org/) project to do the image processing. You can install it using a command along the lines of:

    ```bash
    # debian/ubuntu
    sudo apt-get install imagemagick

    # osx
    sudo port install imagemagick
    sudo brew install imagemagick
    ```

The plugin will hook into your build process automatically and overwrite the target app icon files for debug builds. It will not mess with any files in your repository.


## Customization
The plugin offers some options for customizing the appearance of the generated icon. Simply add a block to your app module's ``build.gradle``:

```groovy
appiconoverlay {
    textColor '#FFF'           /* #rrggbbaa format */
    backgroundColor "#0008"    /* #rrggbbaa format */
    format '$branch\n$commit'  /* GString */
    imageMagick 'convert'      /* command to run ImageMagick */
    dirtyCheck false           /* flag to enabled/disabled dirty check */
    dirtyColor '#F00'          /* #rrggbbaa format */
}
```

Option                 | Description
---------------------- | ------------------
`textColor`            | Text color in #rrggbbaa format.
`backgroundColor`      | Background color for overlay in #rrggbbaa format.
`format`               | Format string to be used to create the text in the overlay.<br />*Note*: Use single quotes, it's a GString.<br />The following variables are available: <ul><li>`$branch` name of git branch</li> <li>`$commit` short SHA1 of latest commit in current branch</li></ul>
`imageMagick`          | Command to run ImageMagick's "convert".
`dirtyCheck`           | Flag to enable/disable check if repo has unstaged or uncommitted files (is dirty)
`dirtyColor`           | Text color in #rrggbbaa format used if repo is dirty (see dirtyCheck)


## Credits
Idea based on the [IconOverlaying](https://github.com/krzysztofzablocki/IconOverlaying) project by Krzysztof Zabłocki, which implements a similar feature for iOS builds.
