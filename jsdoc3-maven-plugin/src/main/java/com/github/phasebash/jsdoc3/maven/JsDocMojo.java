package com.github.phasebash.jsdoc3.maven;

import com.github.phasebash.jsdoc3.maven.tasks.JsDocTasks;
import com.github.phasebash.jsdoc3.maven.tasks.TaskContext;
import com.github.phasebash.jsdoc3.maven.tasks.TaskException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A Mojo implementation which executs jsdoc3.
 */
@Mojo(name = "jsdoc3", defaultPhase = LifecyclePhase.SITE, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class JsDocMojo extends AbstractMojo {

    /** the file include extensions */
    private static final String[] INCLUDE_EXTENSIONS = new String[]{"js"};

    /** the working directory */
    @Parameter(defaultValue = "${project.build.directory}/jsdoc3", readonly = true)
    private File workingDirectory;

    /** the directory where source files sit */
    @Parameter(required = true)
    private Set<File> directoryRoots;

    /** the directory where source files sit */
    @Parameter(required = false)
    private Set<File> sourceFiles;

    /** the output directory for jsdoc */
    @Parameter(required = true, defaultValue = "${project.build.directory}/site/jsdoc")
    private File outputDirectory;

    /** if debug mode should be invoked (includes debug rhino console) */
    @Parameter(required = true, defaultValue = "false")
    private boolean debug;

    /**
     * Execute the jsdoc3 Main.
     *
     * @throws MojoExecutionException If parameters aren't correct or if an error is occurred while running jsdoc.
     */
    public void execute() throws MojoExecutionException {
        final Log log = getLog();

        workingDirectory.mkdirs();
        outputDirectory.mkdirs();

        final File jsDoc3Dir = new File(workingDirectory, "jsdoc");
        final Set<File> scannedFiles = scanFiles(directoryRoots);

        if (sourceFiles != null) {
            scannedFiles.addAll(sourceFiles);
        }

        for (File file : scannedFiles) {
            log.info(file.toString());
        }

        final TaskContext.Builder builder = new TaskContext.Builder();
        builder.withDebug(debug);
        builder.withSourceFiles(scannedFiles);
        builder.withOutputDirectory(outputDirectory);
        builder.withTempDirectory(workingDirectory);
        builder.withJsDocDirectory(jsDoc3Dir);

        try {
            final TaskContext taskContext = builder.build();

            log.info("Generating...");

            new JsDocTasks(taskContext).run();

            log.info("jsdoc3 generation complete, please see " + outputDirectory + ".");

        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException("Invalid Mojo properties given in the plugin configuration.", e);
        } catch (TaskException e) {
            throw new MojoExecutionException("Unable to run all JsDoc3 Tasks.", e);
        }
    }

    /**
     * Scan a set of directory roots and return a set of all files contained within them.
     *
     * @param directoryRoots The root directories.
     * @return The set of all files contained within the input directories.
     */
    private Set<File> scanFiles(Set<File> directoryRoots) {
        final Set<File> walkedFiles = new LinkedHashSet<File>();
        for (final File baseDir : directoryRoots) {
            final Collection<File> files = FileUtils.listFiles(baseDir, INCLUDE_EXTENSIONS, true);
            walkedFiles.addAll(files);
        }

        return walkedFiles;
    }
}
