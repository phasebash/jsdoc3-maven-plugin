package com.github.phasebash.jsdoc3.maven.tasks;

import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A Task which runs the jsdoc3 executable via Rhino.
 */
final class JsDocTask implements Task {

    /** a builder of jsdoc command line arguments */
    private static final JsDocArgumentBuilder JS_DOC_ARGUMENT_BUILDER = new JsDocArgumentBuilder();

    /**
     * Execute the jsdoc3 runner.
     *
     * @param context The context.
     * @throws TaskException If we're unable to run the task.
     */
    @Override
    public void execute(TaskContext context) throws TaskException {
        final List<String> arguments = JS_DOC_ARGUMENT_BUILDER.build(context);
        final ExecutorService executorService = Executors.newFixedThreadPool(3);

        Process process;

        if (context.isDebug()) {
            throw new UnsupportedOperationException("Debug mode not currently supported.");
        } else {
            final ProcessBuilder processBuilder = new ProcessBuilder(arguments);

            try {
                final Log log = context.getLog();

                process = processBuilder.start();
                executorService.submit(new StreamLogger(process.getErrorStream(), log));
                executorService.submit(new StreamLogger(process.getInputStream(), log));
            } catch (IOException e) {
                throw new TaskException("Unable to execute jsdoc tasks in new JVM.", e);
            }
        }

        try {
            final int exitCode = process.waitFor();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
            if (exitCode != 0) {
                throw new TaskException("Process died with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            throw new TaskException("Interrupt while waiting for jsdoc task to complete.", e);
        }
    }
}
