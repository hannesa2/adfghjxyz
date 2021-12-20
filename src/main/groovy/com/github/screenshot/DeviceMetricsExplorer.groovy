package com.github.screenshot

import org.gradle.api.Project
import com.android.ddmlib.IShellOutputReceiver

class DeviceMetricsExplorer implements IShellOutputReceiver {
    private ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private String deviceBrand = null;

    /**
     * Determines what brand of device is currently running.
     * @param project The current project
     * @return The current brand. Either bmw, mini or bmw_2x1.
     */
    String determineDeviceBrand(Project project) {
        def device = new DeviceFinder(project).findDevice()
        device.executeShellCommand("wm size", this)
        def startTime = new Date().time

        while (deviceBrand == null) {
            if (new Date().time - startTime > 30000) {
                throw new IllegalStateException("No device brand identified after 30 seconds - giving up")
            }
            Thread.sleep(1000)
        }
        return deviceBrand;
    }

    @Override
    void addOutput(byte[] data, int offset, int length) {
        bos.write(data, offset, length);
    }

    @Override
    void flush() {
        bos.flush();
        String msg = new String(bos.toByteArray())
        if (msg.contains("1920x720")) {
            deviceBrand = "bmw"
        } else if (msg.contains("1600x1600")) {
            deviceBrand = "mini"
        } else if (msg.contains("1920x960")) {
            deviceBrand = "bmw_2x1"
        }
    }

    @Override
    boolean isCancelled() {
        return false
    }
}
