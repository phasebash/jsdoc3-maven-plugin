package com.github.phasebash.jsdoc3.maven.tasks;

import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * A Task which copies the minimal jsdoc3 zip into a target directory.
 */
final class CopyTask implements Task {

    /** the jsdoc3 zip classpath reference */
    private static final String JSDOC_ARCHIVE_PATH = "com/github/jsdoc3/jsdoc.zip";

    /**
     * Copy the zip.
     *
     * @param context The context object which tells us where to put the zip.
     * @throws TaskException If we're unable to copy the zip.
     */
    @Override
    public void execute(final TaskContext context) throws TaskException {
        try {
            final URL resource = findJsDocArchive();
            copyTo(resource, context.getTempDir(), "jsdoc.zip");
        } catch (IOException e) {
            throw new TaskException("Unable to copy jsdoc zip to temp dir.", e);
        }
    }

    /**
     * Returns the classpath URL of where the zip is located.
     *
     * @return The URL.
     * @throws IOException If we can't find the zip.
     */
    private URL findJsDocArchive() throws IOException {
        URL resource = getClass().getClassLoader().getResource(JSDOC_ARCHIVE_PATH);

        if (resource == null) {
            throw new IOException(JSDOC_ARCHIVE_PATH + " not found.");
        }

        return resource;
    }

    /**
     * Utility method to copy a URL to a directory with a file name.
     *
     * @param source The source URL
     * @param destinationDir The destination directory to hold the file.
     * @param fileName The file name.
     * @throws IOException If the file can't be written.
     */
    private void copyTo(final URL source, final File destinationDir, final String fileName) throws IOException {
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        final File file = new File(destinationDir, fileName);

        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            IOUtil.copy(source.openStream(), fos);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
}
