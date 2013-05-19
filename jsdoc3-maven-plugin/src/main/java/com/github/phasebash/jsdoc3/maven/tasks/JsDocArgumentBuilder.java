package com.github.phasebash.jsdoc3.maven.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A builder of jsdoc command line arguments.
 */
public class JsDocArgumentBuilder {

    /** the commonjs module directories to include */
    private static final List<String> MODULES = Collections.unmodifiableList(Arrays.asList(
            "node_modules", "rhino", "lib", ""
    ));

    /**
     * Build the command line arguments given a TaskContext.
     *
     * @param context The context containing all relevant information for the given request.
     * @return The list of string arguments.
     */
    public List<String> build(TaskContext context) {
        final List<String> arguments = new ArrayList<String>();

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

        if (context.getConfigFile() != null) {
            arguments.add("-c");
            arguments.add(context.getConfigFile().toString());
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

        return arguments;
    }

    private String asNormalizedFileString(final File file) {
        return replace(file.getAbsolutePath());
    }

    private String asUriString(final File file) {
        return (File.separator.equals("/") ? "file://" : "file:/") + asNormalizedFileString(file);
    }

    private String replace(String string) {
        return string.replace("\\", "/");
    }
}
