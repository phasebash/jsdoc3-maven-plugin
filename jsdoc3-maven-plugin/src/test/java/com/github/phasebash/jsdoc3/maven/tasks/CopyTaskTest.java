package com.github.phasebash.jsdoc3.maven.tasks;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class CopyTaskTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private CopyTask copyTask;

    @Before
    public void setUp() {
        copyTask = new CopyTask();
    }

    @Test
    public void testCopyDirDoesntExist() {
        final File tempFile = new File(temporaryFolder.getRoot(), "rooty");
        final TaskContext context = new TaskContext(null, null, null, tempFile, false);
        copyTask.execute(context);
        Assert.assertTrue("jsdoc.zip should exist.", new File(tempFile, "jsdoc.zip").exists());
    }

    @Test
    public void testCopyDirExists() {
        final File tempFile = new File(temporaryFolder.getRoot(), "rooty");
        tempFile.mkdirs();
        final TaskContext context = new TaskContext(null, null, null, tempFile, false);
        copyTask.execute(context);
        Assert.assertTrue("jsdoc.zip should exist.", new File(tempFile, "jsdoc.zip").exists());
    }

    @Test
    public void testCopyFileSize() {
        final File tempFile = new File(temporaryFolder.getRoot(), "rooty");
        final TaskContext context = new TaskContext(null, null, null, tempFile, false);
        copyTask.execute(context);
        Assert.assertTrue("jsdoc.zip should exist.", new File(tempFile, "jsdoc.zip").exists());

        final long size = FileUtils.sizeOfDirectory(tempFile);

        // TODO a better test is needed here.
        Assert.assertTrue("should be big.", size > 400000);
    }
}
