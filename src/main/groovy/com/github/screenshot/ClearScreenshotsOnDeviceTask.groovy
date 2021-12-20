package com.github.screenshot

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ClearScreenshotsOnDeviceTask extends DefaultTask {
    @TaskAction
    def clearScreenshots() {
        def remoteBrandPath = new DeviceMetricsExplorer().determineDeviceBrand(project)
        println "Remove screenshots from device for brand ${remoteBrandPath}"

        // Find device
        def deviceFinder = new DeviceFinder(project)
        def device = deviceFinder.findDevice()

        // Remove screenshots folder of brand from device
        device.executeShellCommand("rm -r /sdcard/screenshots/$remoteBrandPath", new LoggingOutputReceiver())
    }
}
