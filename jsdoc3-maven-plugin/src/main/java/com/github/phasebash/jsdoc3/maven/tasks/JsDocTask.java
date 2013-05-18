package com.github.phasebash.jsdoc3.maven.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * A Task which runs the jsdoc3 executable via Rhino.
 */
final class JsDocTask implements Task {

    /** the commonjs module directories to include */
    private static final List<String> MODULES = Collections.unmodifiableList(Arrays.asList(
        "node_modules", "rhino", "lib", ""
    ));

    /**
     * Execute the jsdoc3 runner.
     *
     * @param context The context.
     * @throws TaskException If we're unable to run the task.
     */
    @Override
    public void execute(TaskContext context) throws TaskException {
        final Log log = context.getLog();

        final List<String> arguments = new LinkedList<String>();

        final File basePath = context.getJsDocDir();

        arguments.add("java");
        arguments.add("-classpath");
        arguments.add(replace(new File(basePath, "rhino" + File.separator + "js.jar").toString()));
        arguments.add("org.mozilla.javascript.tools.shell.Main");

        for (final String module : MODULES) {
            arguments.add("-modules");
            arguments.add(asUriString(new File(basePath, module)));
        }

        arguments.add(replace(new File(basePath, "jsdoc.js").toString()));

        arguments.add("--dirname=" + replace(basePath.toString()));

        if (context.isRecursive()) {
            arguments.add("-r");
        }
		
        if (context.isIncludePrivate()) {
            arguments.add("-p");
        }

        if (context.getTutorialsDirectory() != null) {
            arguments.add("-u");
            arguments.add(context.getTutorialsDirectory().toString());
        }

        arguments.add("-d");
        arguments.add(context.getOutputDir().toString());

        for (final File sourceFile : context.getSourceDir()) {
            arguments.add(sourceFile.toString());
        }

        if (log.isDebugEnabled()) {
            int idx = 0;
            for (String arg: arguments) {
                log.debug("Rhino arg[" + (idx++) + "]" + arg);
            }
        }

        Process process;

        if (context.isDebug()) {
            throw new UnsupportedOperationException("Debug mode not currently supported.");
        } else {
            final ProcessBuilder processBuilder = new ProcessBuilder(arguments);

            try {
                process = processBuilder.start();
                new StreamLogger(process.getErrorStream(), context.getLog()).run();
                new StreamLogger(process.getInputStream(), context.getLog()).run();
            } catch (IOException e) {
                throw new TaskException("Unable to execute jsdoc tasks in new JVM.", e);
            }
        }

        try {
            final int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new TaskException("Process died with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            throw new TaskException("Interrupt while waiting for jsdoc task to complete.", e);
        }
    }

    private String asNormalizedFileString(final File file) {
        return file.getAbsolutePath().replace("\\", "/");
    }

    private String asUriString(final File file) {
        return (File.separator.equals("/") ? "file://" : "file:/") + asNormalizedFileString(file);
    }

    private String replace(String string) {
        return string.replace("\\", "/");
    }
}
