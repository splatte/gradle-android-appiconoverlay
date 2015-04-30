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

                /*
                 * caption might end up being only \n, in which case imagemagick will hang and the call never completes
                 * this is most likely due to a missing .git directory, but could also be caused by an erroneous format string
                 */
                if(caption.toString().trim().isEmpty()) {
                    caption = "<no git>"
                }

                boolean isGitDirty = project.appiconoverlay.dirtyCheck && isGitDirty();

                /* invoke ImageMagick */
                try {
                    def imagemagick = ["${project.appiconoverlay.imageMagick}",
                        "-background", "${project.appiconoverlay.backgroundColor}",
                        "-fill", isGitDirty ? "${project.appiconoverlay.dirtyColor}" : "${project.appiconoverlay.textColor}",
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
                } catch(IOException ioe) {
                    logger.error("Could not find ImageMagick's \"convert\". Tried these locations: ${System.getenv('PATH')}")
                }
            }
        }
    }

    boolean isGitDirty() {
        // check if repo has unstaged or uncommitted changes
        def process = executeShell(["git", "diff-index", "--quiet", "HEAD", "--"])
        process.exitValue() != 0
    }

    def queryGit(def command) {
        def process = executeShell(["git", "rev-parse", "--${command}", "HEAD"])
        process.in.text.replaceAll(/\s/, "")
    }

    def executeShell(def args) {
        logger.debug("executing shell: ${args.join(' ')}")
        def process = args.execute(null, project.projectDir)
        process.waitFor()
        process
    }
}
