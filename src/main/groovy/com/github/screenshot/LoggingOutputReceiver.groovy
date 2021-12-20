package com.github.screenshot

import com.android.ddmlib.IShellOutputReceiver


class LoggingOutputReceiver implements IShellOutputReceiver {
    StringBuilder buffer = new StringBuilder()
    boolean writeToDisk = false
    String path

    @Override
    void addOutput(byte[] data, int offset, int length) {
        buffer.append(new String(data, offset, length))
    }
    @Override
    void flush() {
        if (writeToDisk) {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path)))
            bufferedWriter.write(buffer.toString())
            bufferedWriter.flush()
            bufferedWriter.close()
        } else {
            print(buffer)
        }

        buffer = new StringBuilder()
    }

    @Override
    boolean isCancelled() {
        return false
    }
}
