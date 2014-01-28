package com.github.phasebash.jsdoc3.maven;

import com.github.phasebash.jsdoc3.maven.tasks.JsDocTasks;
import com.github.phasebash.jsdoc3.maven.tasks.TaskContext;
import com.github.phasebash.jsdoc3.maven.tasks.TaskException;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.util.Locale;

/**
 * Creates a JsDoc Report for the Maven Site.
 *
 */
@Mojo(name = "report", defaultPhase = LifecyclePhase.SITE)
public class JsDocMavenReport extends AbstractMavenReport {

    /**
     * <i>Maven Internal</i>: The Doxia Site Renderer.
     *
     * @component
     */
    private Renderer siteRenderer;

    /**
     * <i>Maven Internal</i>: Project to interact with.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /** the working directory */
    @Parameter(defaultValue = "${project.build.directory}/jsdoc3", readonly = true)
    private File workingDirectory;

    /** the directory where source files sit */
    @Parameter(required = false)
    private File[] directoryRoots;

    /** the directory where source files sit */
    @Parameter(required = false)
    private File[] sourceFiles;

    /** the output directory for jsdoc */
    @Parameter(required = true, defaultValue = "${project.build.directory}/site/jsdoc")
    private File outputDirectory;

    /**
     * If a tutorials directory will be provided to jsdoc to resolve tutorial links.
     * see: http://usejsdoc.org/about-tutorials.html
     */
    @Parameter(required = false)
    private File tutorialsDirectory;

    /**
     * If a configuration file will be provided to jsdoc.
     * see: http://usejsdoc.org/about-configuring-jsdoc.html#configuration-file
     */
    @Parameter(required = false)
    private File configFile;

    /**
     * If a template file will be provided to jsdoc.
     * see: http://usejsdoc.org/about-commandline.html
     */
    @Parameter(required = false)
    private File templateDirectory;

    /**
     * If the processor should recurse into directory roots.
     */
    @Parameter(required = false)
    private boolean recursive = true;

    /**
     * Should errors in jsdoc fail the process (false), or should the generator
     * be lenient to errors (true).
     *
     * See: http://usejsdoc.org/about-commandline.html
     */
    @Parameter(required = false)
    private boolean lenient = false;

    /** if debug mode should be invoked (includes debug rhino console) */
    @Parameter(required = true, defaultValue = "false")
    private boolean debug;

    /** if private symbols should in included in documentation. */
    @Parameter(required = true, defaultValue = "false")
    private boolean includePrivate;

    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    @Override
    protected String getOutputDirectory() {
        return "jsdoc/index";
    }

    @Override
    protected MavenProject getProject() {
        return project;
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        final Log log = getLog();

        workingDirectory.mkdirs();
        outputDirectory.mkdirs();

        final File jsDoc3Dir = new File(workingDirectory, "jsdoc");

        final TaskContext.Builder builder = new TaskContext.Builder();
        builder.withLog(log);
        builder.withDebug(debug);
        builder.withRecursive(recursive);
        builder.withLeniency(lenient);
        builder.withIncludePrivate(includePrivate);
        builder.withSourceFiles(sourceFiles);
        builder.withDirectoryRoots(directoryRoots);
        builder.withOutputDirectory(outputDirectory);
        builder.withTempDirectory(workingDirectory);
        builder.withJsDocDirectory(jsDoc3Dir);
        builder.withTutorialsDirectory(tutorialsDirectory);
        builder.withTemplateDirectory(templateDirectory);
        builder.withConfigFile(configFile);

        try {
            final TaskContext taskContext = builder.build();

            log.info("Generating...");

            new JsDocTasks(taskContext).run();

            log.info("jsdoc3 generation complete, please see " + outputDirectory + ".");

        } catch (IllegalArgumentException e) {
            throw new MavenReportException("Invalid Mojo properties given in the plugin configuration.", e);
        } catch (TaskException e) {
            throw new MavenReportException("Unable to run all JsDoc3 Tasks.", e);
        }
    }

    @Override
    public String getOutputName() {
        return "jsdoc/index";
    }

    @Override
    public String getName(Locale locale) {
        return "JsDocs";
    }

    @Override
    public String getDescription(Locale locale) {
        return "JsDoc API Documentation.";
    }

    @Override
    public boolean isExternalReport() {
        return true;
    }
}
