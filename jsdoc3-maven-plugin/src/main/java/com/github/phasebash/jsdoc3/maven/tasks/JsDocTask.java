package com.github.phasebash.jsdoc3.maven.tasks;

import org.mozilla.javascript.tools.shell.NonExitingMain;

import java.io.File;
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
            "node_modules", "rhino", "lib", null
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

        arguments.add("-opt");
        arguments.add("-1");

        final String basePath = context.getJsDocDir().getAbsolutePath();
        for (final String module : MODULES) {
            arguments.add("-modules");

            final URI uri = URI.create(basePath + (module != null ? "/" + module : "")).normalize();

            arguments.add(uri.toASCIIString());
        }

        arguments.add(basePath + "/jsdoc.js");
        arguments.add("--dirname=" + basePath + "/");

        arguments.add("-d");
        arguments.add(context.getOutputDir().getAbsolutePath());

        for (final File sourceFile : context.getSourceDir()) {
            arguments.add(sourceFile.getAbsolutePath());
        }

        if (context.isDebug()) {
            arguments.add(0, "-debug");

            final String[] arrayArguments = arguments.toArray(new String[arguments.size()]);
            org.mozilla.javascript.tools.debugger.Main.main(arrayArguments);
        } else {
            final String[] arrayArguments = arguments.toArray(new String[arguments.size()]);
            NonExitingMain.main(arrayArguments);
        }
    }
}
