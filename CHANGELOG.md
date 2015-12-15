Release notes for gradle-android-appiconoverlay
===============================================

Version 1.3
-----------
* new: support for product flavors. name of the current build variant can be used (e.g. "paidRelease" or "alphaDebug") (thanks to [rocket0423](https://github.com/rocket0423))
* fix: compatibility problems between java7 and java8 resolved

Version 1.2
-----------
* new: when "convert" command could not be found, show the $PATH that was tried
* new: show error message when .git directory could not be found
* fix: don't fail the build when "convert" command could not be found
* fix: work around the problem where an empty or whitespace-only caption would cause ImageMagick to hang and never complete
