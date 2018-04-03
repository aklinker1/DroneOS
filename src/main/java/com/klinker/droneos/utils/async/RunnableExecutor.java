package com.klinker.droneos.utils.async;

import com.klinker.droneos.utils.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunnableExecutor {

    public static final int MAX_PARALLEL_COUNT = 15;

    /**
     * The mode the executor will run in.
     */
    public enum Mode {
        /**
         * Run tasks one after another.
         */
        SERIES,
        /**
         * Run tasks at the same time.
         */
        PARALLEL
    }

    private LinkedList<Runnable> mRunnableList;
    private Mode mMode;
    private ExecutorService mExecutor;

    public static RunnableExecutor newParallel(int maxThreads) {
        return new RunnableExecutor(Mode.PARALLEL, maxThreads);
    }

    public static RunnableExecutor newSeries() {
        return new RunnableExecutor(Mode.SERIES, 0);
    }

    /**
     * Creates an instance that runs tasks based on a mode.
     *
     * @param mode Either {@link Mode#SERIES} or {@link Mode#PARALLEL}.
     */
    private RunnableExecutor(Mode mode, int parallelCount) {
        mRunnableList = new LinkedList<>();
        mMode = mode;
        if (mode == Mode.PARALLEL) {
            mExecutor = Executors.newFixedThreadPool(parallelCount);
        } else {
            mExecutor = Executors.newSingleThreadExecutor();
        }
    }

    /**
     * Adds a runnable to the queue. If the Task is
     * {@link RunnableExecutor.Mode#PARALLEL}, the order of the runnables are
     * added does not matter.
     * <p>
     * If the mode is {@link RunnableExecutor.Mode#SERIES}, the each runnable
     * will be executed in the order they were submitted.
     *
     * @param runnable The runnable to add to the group.
     */
    public void addRunnable(Runnable runnable) {
        mRunnableList.addLast(runnable);
    }

    public void executeRunnable(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    /**
     * Starts all the tasks either in {@link Mode#PARALLEL} or as a
     * {@link Mode#SERIES}.
     */
    public void start() {
        while (!mRunnableList.isEmpty()) {
            Runnable task = mRunnableList.removeFirst();
            mExecutor.execute(task);
        }
    }

    /**
     * Immediately cancels all the tasks in the group.
     *
     * @return The tasks that did not begin execution.
     */
    public List<Runnable> cancel() {
        return mExecutor.shutdownNow();
    }

    /**
     * Awaits all the tasks in the current instance to finish.
     */
    public void join() {
        mExecutor.shutdown();
        try {
            mExecutor.awaitTermination(
                    Long.MAX_VALUE,
                    TimeUnit.NANOSECONDS
            );
        } catch (InterruptedException e) {
            Log.e(
                    "arch",
                    "Could not await termination of "
                            + mExecutor.toString()
            );
        }
    }

}
