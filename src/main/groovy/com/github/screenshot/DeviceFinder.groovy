package com.github.screenshot

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import org.gradle.api.Project

class DeviceFinder implements AndroidDebugBridge.IDeviceChangeListener {
    private static IDevice device = null
    private AndroidDebugBridge bridge = null
    private Project project

    DeviceFinder(Project currentProject) {
        project = currentProject
    }

    /**
     * Waits for and returns the first device that is found via ADB. This will time out if a device has not been
     * found after 30 seconds. If a device has been found previously and is still online, the previous one is returned.
     * @return The Android device that was found (if one was found within the timeout period)
     */
    IDevice findDevice() {
        println "Finding ADB device"
        if (device != null && device.online) {
            println "Device ${device.version} has already been found before."
            return device
        }

        // Only set up adb if device has not been found already
        setupAdb()

        if (bridge.devices.length == 1) {
            device = bridge.devices.first()
            println "Device ${device.version} is available!"
            return device
        } else {
            println "No devices found. Waiting for devices to be added/recognized..."
            def startTime = new Date().time
            while (device == null) {
                if (new Date().time - startTime > 30000) {
                    throw new IllegalStateException("No device found after 30 seconds - giving up")
                }
                Thread.sleep(1000)
            }
            return device
        }
    }

    private void setupAdb() {
        println "Setup ADB using executable: $project.android.adbExecutable.path"
        AndroidDebugBridge.initIfNeeded(false)
        bridge = AndroidDebugBridge.createBridge(project.android.adbExecutable.path, false)
        bridge.addDeviceChangeListener(this)
    }

    @Override
    void deviceConnected(IDevice connectedDevice) {
        println "Device (${connectedDevice.version}) has been found!"
        device = connectedDevice
    }

    @Override
    void deviceDisconnected(IDevice device) {
    }

    @Override
    void deviceChanged(IDevice changedDevice, int changeMask) {
        println "Changed device (${changedDevice.version}) has been found!"
        device = connectedDevice
    }
}
