package com.github.splatte.android

import com.android.builder.core.BuilderConstants

import org.gradle.api.Plugin
import org.gradle.api.Project

class AppIconOverlayPlugin implements Plugin<Project> {
    private static final String TASK_NAME = "overlayicon"

    void apply(Project project) {
        def log = project.logger

        project.extensions.create("appiconoverlay", AppIconOverlayExtension)

        project.android.applicationVariants.all { variant ->
            /* skip release builds */
            if(variant.buildType.name.equals(BuilderConstants.RELEASE)) {
                log.debug("Skipping build type: ${variant.buildType.name}")
                return;
            }

            variant.outputs.each { output ->
                /* set up overlay task */
                def overlayTask = project.task(type:OverlayTask, "${TASK_NAME}${variant.name.capitalize()}") {
                    try {
                        // Android Gradle Plugin < 3.0.0
                        manifestFile = output.processManifest.manifestOutputFile
                    } catch (Exception ignored) {
                        // Android Gradle Plugin >= 3.0.0
                        manifestFile = new File(output.processManifest.manifestOutputDirectory, "AndroidManifest.xml")
                        if (!manifestFile.isFile()) {
                            manifestFile = new File(new File(output.processManifest.manifestOutputDirectory, output.dirName),"AndroidManifest.xml")
                        }
                    }
                    resourcesPath = variant.mergeResources.outputDir
                    buildVariant = "${variant.name.capitalize()}"
                }

                /* hook overlay task into android build chain */
                overlayTask.dependsOn output.processManifest
                output.processResources.dependsOn overlayTask
            }
        }
    }
}

class AppIconOverlayExtension {
    /**
     * Text color in #rrggbbaa format.
     */
    String textColor = "#FFF"

    /**
     * Background color for overlay in #rrggbbaa format.
     */
    String backgroundColor = "#0008"

    /**
     * Format string to be used to create the text in the overlay.
     * Note: Use single quotes, it's a GString.
     * The following variables are available:
     *     - $branch: name of git branch
     *     - $commit: short SHA1 of latest commit in current branch
     *     - $build: the name of the build variant ex. Debug
     */
    String format = '$build->$branch\n$commit'

    /**
     * Command to invoke to run ImageMagick's "convert".
     */
    String imageMagick = "convert"
}
