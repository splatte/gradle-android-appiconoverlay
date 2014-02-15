package com.github.splatte.android

import com.android.build.gradle.tasks.PackageApplication
import com.android.builder.BuilderConstants

import groovy.io.FileType

import org.gradle.api.Plugin
import org.gradle.api.Project

class AppIconOverlayPlugin implements Plugin<Project> {
    private static final String TASK_NAME = "overlayicon"

    void apply(Project project) {
        def log = project.logger

        project.android.applicationVariants.all { variant ->
            /* skip release builds */
            if(variant.buildType.name.equals(BuilderConstants.RELEASE)) {
                log.debug("Skipping build type: ${variant.buildType.name}")
                return;
            }

            /* set up overlay task */
            def overlayTask = project.tasks.create(TASK_NAME)
            overlayTask.manifestFile = variant.processManifest.manifestOutputFile
            overlayTask.resourcesPath = variant.mergeResources.outputDir
            overlayTask << {
                /*
                 * parse AndroidManifest.xml
                 * find file name for app icon in <application .. android:icon="@drawable/ic_launcher">
                 */
                def manifestXml = new XmlSlurper().parse(manifestFile)
                def iconFileName = manifestXml.application.@icon.text().split("/")[1]

                /* find the app icon files in all 'drawable' folders */
                resourcesPath.eachDirMatch(~/^drawable.*/) { dir ->
                    dir.eachFileMatch(FileType.FILES, ~"^${iconFileName}.*") { file ->
                        log.debug("found file: ${file}")

                        /* invoke ImageMagick */
                        def imagemagick = ["convert", "-type", "Grayscale", file, file].execute()
                        imagemagick.waitFor()

                        /* print error, if any */
                        if(imagemagick.exitValue() != 0) {
                            log.error("ImageMagick with error code ${imagemagick.exitValue()} and: ${imagemagick.err.text}")
                        }
                    }
                }
            }

            /* hook overlay task into android build chain */
            variant.processResources.dependsOn overlayTask
        }
    }
}
