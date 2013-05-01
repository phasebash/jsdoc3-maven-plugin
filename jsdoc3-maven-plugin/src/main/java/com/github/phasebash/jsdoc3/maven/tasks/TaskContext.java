package com.github.phasebash.jsdoc3.maven.tasks;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A context object representing the necessary values required to run jsdoc3.
 */
public final class TaskContext {

    /** the source directory where sources can be found */
    private final Collection<File> sourceDir;

    /** where jsdoc should be written */
    private final File outputDir;

    /** the directory where jsdoc source files can be found */
    private final File jsDocDir;

    /** a temp dir for scratch files */
    private final File tempDir;

    /** if jsdoc should run in debug mode */
    private final boolean debug;

    /** if we shall pass the -r flag to jsdoc. */
    private final boolean recursive;

    /** if we shall pass the -p flag to jsdoc. */
    private final boolean includePrivate;

    /**
    /**
     * Private constructor.
     *
     * @param sourceDir the source dir.
     * @param outputDir the output dir.
     * @param jsDocDir  the jsdoc dir.
     * @param tempDir   the temp dir.
     * @param debug     if debug mode should be used.
     * @param recursive if the jsdoc task should recursively look for jsfiles.
     * @param includePrivate if private symbols should be included.
     */
    TaskContext(Collection<File> sourceDir, File outputDir, File jsDocDir,
                File tempDir, boolean debug, boolean recursive, boolean includePrivate) {
        this.sourceDir = sourceDir;
        this.jsDocDir  = jsDocDir;
        this.outputDir = outputDir;
        this.tempDir   = tempDir;
        this.debug     = debug;
        this.recursive = recursive;
		this.includePrivate = includePrivate;
    }

    /**
     * Get the JSDoc source directory.  It may not exist.
     *
     * @return The directory.
     */
    public File getJsDocDir() {
        return jsDocDir;
    }

    /**
     * Get the output directory.  It may not exist at the time of this call.
     *
     * @return The directory.
     */
    public File getOutputDir() {
        return outputDir;
    }

    /**
     * Get the temp directory.  It will exist at the time of this call.
     *
     * @return The temp directory.
     */
    public File getTempDir() {
        return tempDir;
    }

    /**
     * Get the source directory.  If will exist at the time of this call.
     *
     * @return The source directory.
     */
    public Collection<File> getSourceDir() {
        return sourceDir;
    }

    /**
     * Debug mode?
     *
     * @return true for yes, false for no.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Recursive mode?
     *
     * @return true for yes, false for no.
     */
    public boolean isRecursive() {
        return recursive;
    }
	
	/**
	 * Should private symbols be included.
	 * 
	 * @return true for yes, false for no.
	 */
	public boolean isIncludePrivate() {
		return includePrivate;
	}

    /**
     * The way in which a TaskContext should be built.
     */
    public static class Builder {

        private Set<File> sourceFiles = new LinkedHashSet<File>();

        private Set<File> directoryRoots = new LinkedHashSet<File>();

        private File outputDirectory;

        private File jsDocDirectory;

        private File tempDirectory;

        private boolean debug = false;

        private boolean recursive = false;
		
		private boolean includePrivate = false;

        public Builder withSourceFiles(final Collection<File> sourceFiles) {
            if (sourceFiles != null) {
                this.sourceFiles.addAll(sourceFiles);
            }
            return this;
        }

        public Builder withOutputDirectory(final File outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public Builder withJsDocDirectory(final File jsDocDirectory) {
            this.jsDocDirectory = jsDocDirectory;
            return this;
        }

        public Builder withTempDirectory(final File tempDirectory) {
            this.tempDirectory = tempDirectory;
            return this;
        }

        public Builder withRecursive(final boolean recursive) {
            this.recursive = recursive;
            return this;
        }

        /**
         * An optional attribute, if jsdoc should run in debug mode.
         *
         * @param debug the value.
         * @return The builder.
         */
        public Builder withDebug(final boolean debug) {
            this.debug = debug;
            return this;
        }
		
		public Builder withIncludePrivate(final boolean includePrivate) {
			this.includePrivate = includePrivate;
			return this;
		}

        public void withDirectoryRoots(final Set<File> directoryRoots) {
            if (directoryRoots != null) {
                this.directoryRoots.addAll(directoryRoots);
            }
        }

        public TaskContext build() {
            if (sourceFiles.size() == 0 && directoryRoots.size() == 0) {
                throw new IllegalArgumentException("sourceFiles and/or directoryRoots are required.");
            }

            final Collection<File> sourceRoots = new LinkedHashSet<File>();
            sourceRoots.addAll(sourceFiles);
            sourceRoots.addAll(directoryRoots);

            if (!outputDirectory.exists()) {
                throw new IllegalStateException("Output directory must exist.");
            }

            if (jsDocDirectory == null) {
                throw new IllegalStateException("jsdoc directory must not be null.");
            }

            if (tempDirectory == null) {
                throw new IllegalStateException("Temp directory must not be null.");
            }

            return new TaskContext(sourceRoots, outputDirectory, jsDocDirectory, tempDirectory, debug, recursive, includePrivate);
        }
    }
}
