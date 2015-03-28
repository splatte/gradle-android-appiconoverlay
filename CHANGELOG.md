Release notes for gradle-android-appiconoverlay
===============================================

Version 1.2
-----------
* new: when "convert" command could not be found, show the $PATH that was tried
* new: show error message when .git directory could not be found
* fix: don't fail the build when "convert" command could not be found
* fix: work around the problem where an empty or whitespace-only caption would cause ImageMagick to hang and never complete
