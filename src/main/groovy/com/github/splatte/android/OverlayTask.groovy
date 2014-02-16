package com.github.splatte.android

import groovy.io.FileType

import javax.imageio.ImageIO

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class OverlayTask extends DefaultTask {
    File manifestFile
    File resourcesPath

    String gitCommit = queryGit("short")
    String gitBranch = queryGit("abbrev-ref")

    @TaskAction
    def overlay() {
        /*
         * parse AndroidManifest.xml
         * find file name for app icon in <application .. android:icon="@drawable/ic_launcher">
         */
        def manifestXml = new XmlSlurper().parse(manifestFile)
        def iconFileName = manifestXml.application.@icon.text().split("/")[1]

        /* find the app icon files in all 'drawable' folders */
        resourcesPath.eachDirMatch(~/^drawable.*/) { dir ->
            dir.eachFileMatch(FileType.FILES, ~"^${iconFileName}.*") { file ->
                logger.debug("found file: ${file}")

                def img = ImageIO.read(file);

                /* invoke ImageMagick */
                def imagemagick = ["convert",
                    "-background", "#0008",
                    "-fill", "white",
                    "-gravity", "center",
                    "-size", "${img.width}x${img.height / 2}",
                    "caption:${gitBranch}\n${gitCommit}",
                    file,
                    "+swap",
                    "-gravity", "south",
                    "-composite",
                    file]
                .execute()
                imagemagick.waitFor()

                /* print error, if any */
                if(imagemagick.exitValue() != 0) {
                    logger.error("ImageMagick with error code ${imagemagick.exitValue()} and: ${imagemagick.err.text}")
                }
            }
        }
    }

    private def queryGit(def command) {
        def git = ["git", "rev-parse", "--${command}", "HEAD"].execute()
        git.waitFor()
        git.in.text.replaceAll(/\s/, "")
    }
}
