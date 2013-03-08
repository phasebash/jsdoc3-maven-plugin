package com.github.phasebash.jsdoc3.maven.tasks;

/**
 * A private interface for an implementation of a class which encapsulates
 * a step in the jsdoc execution process.
 */
interface Task {

    /**
     * Execute the task given a TaskContext.
     *
     * @param context The context containing any necessary bit of information for the task.
     * @throws TaskException If the task cannot complete.
     */
    void execute(TaskContext context) throws TaskException;
}
