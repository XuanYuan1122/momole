package com.app.common.task;

/**
 * Created by Haru on 2016/4/12 0012.
 */
class PriorityRunnable implements Runnable{

    /*package*/ long SEQ;
    public final Priority priority;
    private final Runnable runnable;

    public PriorityRunnable(Priority priority, Runnable runnable) {
        this.priority = priority == null ? Priority.DEFAULT : priority;
        this.runnable = runnable;
    }

    @Override
    public final void run() {
        this.runnable.run();
    }
}
