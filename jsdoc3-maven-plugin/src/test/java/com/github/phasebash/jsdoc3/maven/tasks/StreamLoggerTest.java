package com.github.phasebash.jsdoc3.maven.tasks;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamLoggerTest {

    @Before
    public void setUp() {

    }

    @Test(timeout = 100)
    public void testSingleLine() {
        Log mockLog = EasyMock.createStrictMock(Log.class);
        mockLog.info("Two peas in a bucket.");
        EasyMock.expectLastCall();
        EasyMock.replay(mockLog);

        InputStream in = new ByteArrayInputStream("Two peas in a bucket.\n".getBytes());
        StreamLogger logger = new StreamLogger(in, mockLog);
        logger.run();

        EasyMock.verify(mockLog);
    }

    @Test(timeout = 100)
    public void testTwoLines() {
        Log mockLog = EasyMock.createStrictMock(Log.class);
        mockLog.info("Two peas in a bucket.");
        mockLog.info("Big ham ham ham.");
        EasyMock.expectLastCall();
        EasyMock.replay(mockLog);

        InputStream in = new ByteArrayInputStream("Two peas in a bucket.\nBig ham ham ham.\n".getBytes());
        StreamLogger logger = new StreamLogger(in, mockLog);
        logger.run();

        EasyMock.verify(mockLog);
    }

    @Test(expected = TaskException.class)
    public void testIoExceptionThrown() {
        InputStream mockStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("oops");
            }
        };

        StreamLogger logger = new StreamLogger(mockStream, new SystemStreamLog());
        logger.run();
    }
}
