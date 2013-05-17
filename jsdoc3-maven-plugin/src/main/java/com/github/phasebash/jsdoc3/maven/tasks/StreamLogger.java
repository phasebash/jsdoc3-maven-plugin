package com.github.phasebash.jsdoc3.maven.tasks;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamLogger extends Thread {

    private final BufferedReader bufferedReader;

    private final InputStreamReader inputStreamReader;

    private final Log log;

    public StreamLogger(InputStream in, Log log) {
        this.inputStreamReader = new InputStreamReader(in);
        this.bufferedReader = new BufferedReader(inputStreamReader);
        this.log = log;
    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new TaskException("Exception while intercepting stream.", e);
        }

        IOUtils.closeQuietly(bufferedReader);
        IOUtils.closeQuietly(inputStreamReader);
    }
}
