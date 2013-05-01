package com.github.phasebash.jsdoc3.maven.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A Task which runs the jsdoc3 executable via Rhino.
 */
final class JsDocTask implements Task {

    /** the commonjs module directories to include */
    private static final List<String> MODULES = Collections.unmodifiableList(Arrays.asList(
        "node_modules", "rhino", "lib"
    ));

    /**
     * Execute the jsdoc3 runner.
     *
     * @param context The context.
     * @throws TaskException If we're unable to run the task.
     */
    @Override
    public void execute(TaskContext context) throws TaskException {
        final List<String> arguments = new LinkedList<String>();

        final File basePath = context.getJsDocDir();
        final String javaHome = System.getProperty("java.home");
        final File java = new File(javaHome, "bin" + File.separator + "java");

        arguments.add(java.getAbsolutePath());
        arguments.add("-classpath");
        arguments.add(new File(basePath, "rhino" + File.separator + "js.jar").getAbsolutePath());
        arguments.add("org.mozilla.javascript.tools.shell.Main");

        for (final String module : MODULES) {
            arguments.add("-modules");
            final File file = new File(basePath, module);
            arguments.add(file.getAbsolutePath());
        }

        arguments.add("-modules");
        arguments.add(basePath.getAbsolutePath());

        arguments.add(basePath + File.separator + "jsdoc.js");
        arguments.add("--dirname=" + basePath + File.separator);

        if (context.isRecursive()) {
            arguments.add("-r");
        }
		
        if (context.isIncludePrivate()) {
            arguments.add("-p");
        }

        arguments.add("-d");
        arguments.add(context.getOutputDir().getAbsolutePath());

        for (final File sourceFile : context.getSourceDir()) {
            arguments.add(sourceFile.getAbsolutePath());
        }

        Process process;

        System.err.println(arguments);

        if (context.isDebug()) {
            throw new UnsupportedOperationException("Debug mode not currently supported.");
        } else {
            final ProcessBuilder processBuilder = new ProcessBuilder(arguments);

            try {
                process = processBuilder.start();
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
}
