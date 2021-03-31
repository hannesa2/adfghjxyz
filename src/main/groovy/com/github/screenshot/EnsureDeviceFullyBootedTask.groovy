package com.github.screenshot

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class EnsureDeviceFullyBootedTask extends DefaultTask {

    @Input
    boolean waitForDeviceToSettle = true

    @TaskAction
    def blockUntilDeviceFullyBooted() {

        println "Wait for device to be fully booted ..."

        // Find device
        def deviceFinder = new DeviceFinder(project)
        def device = deviceFinder.findDevice()

        def startTime = new Date().time
        def isFullyBooted = device.getProperty("sys.boot_completed")

        while (isFullyBooted != "1") {
            if (new Date().time - startTime > 10 * 60 * 1000) {
                throw new IllegalStateException("Device not fully booted after 4 minutes - giving up")
            }
            sleep(10 * 1000)
            isFullyBooted = device.getProperty("sys.boot_completed")
        }

        if (waitForDeviceToSettle) {
            println "Device (${device.version}) is fully booted! Waiting additional 90s to let it settle."
            sleep(90 * 1000)
        } else {
            println "Device (${device.version}) is fully booted!"
        }
    }
}
