package com.klinker.droneos.utils.async;

/**
 * An implementation of {@link Runnable} that has callbacks for
 * {@link AsyncTask#onStart()}, {@link AsyncTask#onRun()}, and
 * {@link AsyncTask#onFinish()}.
 * <p>
 *     Instances can only be started once.
 * </p>
 * <p>
 *     During {@link AsyncTask#onRun()}, to see if the task should repeat,
 *     call {@link AsyncTask#isRunning()}, and to stop the task call
 *     {@link AsyncTask#stop()}.
 * </p>
 */
public abstract class AsyncTask implements Runnable {

    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * States whether or not the Task is currently running.
     */
    private boolean mIsRunning;

    /**
     * States whether or not the Task has been started. If the Task is
     * finished, this will still return true.
     */
    private boolean mIsStarted;


    ///// Construction /////////////////////////////////////////////////////////

    /**
     * Instantiates a new {@link AsyncTask}.
     */
    protected AsyncTask() {
        mIsRunning = false;
        mIsStarted = false;
    }


    ///// Overridden Methods ///////////////////////////////////////////////////

    /**
     * DO NOT CALL THIS METHOD. It should only be called from inside the
     * {@link RunnableExecutor} class when executing a set of Tasks in parallel.
     * We call {@link AsyncTask#start(boolean)} with <code>false</code>
     * because the Task group will start it in a new Task.
     */
    @Override
    @Deprecated
    public void run() {
        start(false);
    }


    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * The method that starts the task. Use this to start the task in a new
     * thread.
     */
    public final void start() {
        start(true);
    }

    /**
     * See {@link AsyncTask#start()}.
     *
     * @param startInNewThread This is <code>false</code> if and only if The
     *                         thread is to be ran syncronously. Otherwise it
     *                         should always be <code>true</code>. Example of
     *                         <code>false</code>:
     *                         {@link java.util.concurrent.ExecutorService}
     *                         starts all each runnable in it's own thread,
     *                         so we don't want to start a thread inside
     *                         another thread.
     */
    public final void start(boolean startInNewThread) {
        if (mIsStarted) return;

        mIsRunning = true;
        mIsStarted = true;
        onStart();

        if (startInNewThread) {
            new Thread(() -> {
                onRun();
                onFinish();
                finished();
            }).start();
        } else {
            onRun();
            onFinish();
            finished();
        }
    }

    /**
     * Runs synchronously with call to {@link AsyncTask#start()}.
     * <p>
     * Do not include complex computations here. This method can be used to
     * initialize variables needed for the task, or print log statements.
     */
    protected abstract void onStart();

    /**
     * Runs asynchronously if {@link AsyncTask#start(boolean)} is true,
     * otherwise it will be ran synchronously.
     * <p>
     * Perform all complex computations here.
     */
    protected abstract void onRun();

    /**
     * Runs asynchronously if {@link AsyncTask#start(boolean)} is true,
     * otherwise it will be ran synchronously. It is called directly after
     * {@link AsyncTask#onRun()} returns.
     * <p>
     * This method can handle complex computations, but should just be used
     * to summarize the results of the task.
     */
    protected abstract void onFinish();

    /**
     * Called directly after {@link AsyncTask#onFinish()}, and sets
     * {@link AsyncTask#mIsRunning} to false;
     */
    private synchronized void finished() {
        mIsRunning = false;
    }

    /**
     * Stop the thread by setting mIsRunning to false.
     */
    public synchronized void stop() {

    }


    ///// Getters //////////////////////////////////////////////////////////////

    /**
     * @return Whether or not this thread is being ran.
     */
    public synchronized boolean isRunning() {
        return mIsRunning;
    }

    /**
     * @return Whether or not this thread has been started. Even if the
     * thread is finished, this will return <code>true</code>.
     */
    public synchronized boolean isStarted() {
        return mIsRunning;
    }

}