package com.github.phasebash.jsdoc3.maven.tasks;

import org.codehaus.plexus.util.Expand;

import java.io.File;

/**
 * Unpack the jsdoc.zip
 */
final class UnpackTask implements Task {

    /**
     * Unpack the jsdoc.zip
     *
     * @param context The context containing any necessary bit of information for the task.
     * @throws TaskException
     */
    @Override
    public void execute(TaskContext context) throws TaskException {
        final Expand expander = new Expand();

        expander.setSrc(new File(context.getTempDir(), "jsdoc.zip"));
        expander.setDest(context.getJsDocDir());

        try {
            expander.execute();
        } catch (Exception e) {
            throw new TaskException("Unable to unpack jsdoc.zip.", e);
        }
    }
}
