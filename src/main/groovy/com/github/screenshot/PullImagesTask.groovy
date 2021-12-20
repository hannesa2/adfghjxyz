package com.github.screenshot

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PullImagesTask extends DefaultTask {

    String localImagesFolder = ""
    boolean clearOldScreenshots = false

    @TaskAction
    def pullImages() {
        def brandTarget = new DeviceMetricsExplorer().determineDeviceBrand(project)
        def localPathToImages = localImagesFolder + "/" + brandTarget
        def remoteBrandPath = new DeviceMetricsExplorer().determineDeviceBrand(project)
        println "Pulling screenshots from device for brand ${remoteBrandPath} to local path ${localPathToImages}"

        // Create a fresh local screenshots folder
        LocalFileManager.createLocalScreenshotsFolder(project, localPathToImages, clearOldScreenshots)

        // Find device
        def deviceFinder = new DeviceFinder(project)
        def device = deviceFinder.findDevice()

        // Download screenshots
        def screenshots = DeviceExplorer.findScreenshots(device, remoteBrandPath)
        ImageGrabber.grabImages(device, screenshots, localPathToImages)
    }
}