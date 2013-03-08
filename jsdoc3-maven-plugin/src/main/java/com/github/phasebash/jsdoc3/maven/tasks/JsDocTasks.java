package com.github.phasebash.jsdoc3.maven.tasks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The public API for running JSDoc tasks.
 */
public final class JsDocTasks {

    /** the tasks which must be executed, given how jsdoc3 is bundled. */
    private static final List<Task> TASK_LIST = Collections.unmodifiableList(Arrays.asList(
        new CopyTask(), new UnpackTask(), new JsDocTask()
    ));

    /** the task context */
    private final TaskContext taskContext;

    /**
     * Construct.
     *
     * @param taskContext with a taskContext.
     */
    public JsDocTasks(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    /**
     * Run the tasks in the order in which they must occur.
     *
     * @throws TaskException If a task can't execute.
     */
    public void run() throws TaskException {
        for (final Task task : TASK_LIST) {
            task.execute(taskContext);
        }
    }
}
