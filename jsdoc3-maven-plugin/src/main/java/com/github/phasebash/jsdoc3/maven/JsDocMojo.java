package com.github.phasebash.jsdoc3.maven;

import com.github.phasebash.jsdoc3.maven.tasks.JsDocTasks;
import com.github.phasebash.jsdoc3.maven.tasks.TaskContext;
import com.github.phasebash.jsdoc3.maven.tasks.TaskException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.Set;

/**
 * A Mojo implementation which executs jsdoc3.
 */
@Mojo(name = "jsdoc3", defaultPhase = LifecyclePhase.SITE, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class JsDocMojo extends AbstractMojo {

    /** the working directory */
    @Parameter(defaultValue = "${project.build.directory}/jsdoc3", readonly = true)
    private File workingDirectory;

    /** the directory where source files sit */
    @Parameter(required = false)
    private Set<File> directoryRoots;

    /** the directory where source files sit */
    @Parameter(required = false)
    private Set<File> sourceFiles;

    @Parameter(required = false)
    private boolean recursive = true;

    /** the output directory for jsdoc */
    @Parameter(required = true, defaultValue = "${project.build.directory}/site/jsdoc")
    private File outputDirectory;

    /** if debug mode should be invoked (includes debug rhino console) */
    @Parameter(required = true, defaultValue = "false")
    private boolean debug;

    /** if private symbols should in included in documentation. */
    @Parameter(required = true, defaultValue = "false")
    private boolean includePrivate;

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

        final TaskContext.Builder builder = new TaskContext.Builder();
        builder.withDebug(debug);
        builder.withRecursive(recursive);
        builder.withIncludePrivate(includePrivate);
        builder.withSourceFiles(sourceFiles);
        builder.withDirectoryRoots(directoryRoots);
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
}
