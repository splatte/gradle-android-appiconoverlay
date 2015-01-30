package com.github.splatte.android

import groovy.io.FileType
import groovy.text.SimpleTemplateEngine

import javax.imageio.ImageIO

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class OverlayTask extends DefaultTask {
    File manifestFile
    File resourcesPath

    @TaskAction
    def overlay() {
        /*
         * parse AndroidManifest.xml
         * find file name for app icon in <application .. android:icon="@drawable/ic_launcher">
         */
        def manifestXml = new XmlSlurper().parse(manifestFile).declareNamespace('android':'http://schemas.android.com/apk/res/android')
        def iconFileName = manifestXml.application.@'android:icon'.text().split("/")[1]

        /* find the app icon files in all 'drawable' folders */
        // TODO: use ..split("/")[0] to determine if drawable or mipmap
        resourcesPath.eachDirMatch(~/^drawable.*|^mipmap.*/) { dir ->
            dir.eachFileMatch(FileType.FILES, ~"^${iconFileName}.*") { file ->
                logger.debug("found file: ${file}")

                def img = ImageIO.read(file);
                def formatBinding = ['branch': queryGit("abbrev-ref"), 'commit': queryGit("short")]
                def caption = new SimpleTemplateEngine().createTemplate(project.appiconoverlay.format).make(formatBinding)

                /* invoke ImageMagick */
                // TODO: when there is no .git, `caption` will be empty and the imagemagick call will never complete
                def imagemagick = ["${project.appiconoverlay.imageMagick}",
                    "-background", "${project.appiconoverlay.backgroundColor}",
                    "-fill", "${project.appiconoverlay.textColor}",
                    "-gravity", "center",
                    "-size", "${img.width}x${img.height / 2}",
                    "caption:${caption}",
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

    def queryGit(def command) {
        def args = ["git", "rev-parse", "--${command}", "HEAD"]
        logger.debug("executing git: ${args.join(' ')}")

        def git = args.execute(null, project.projectDir)
        git.waitFor()
        git.in.text.replaceAll(/\s/, "")
    }
}
