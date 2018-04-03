package com.klinker.droneos.utils.async;

import java.util.LinkedList;

/**
 * This is a wrapper class is meant to help handle the async modification of a queue.
 * This is used in the Architecture due to queues being modified from different
 * threads. Use this if your application involves multi-threading.
 *
 * @param <T> The model you wish to store.
 */
public class AsyncQueue<T> {

    private LinkedList<T> queue;

    /**
     * Default constructor,
     */
    public AsyncQueue() {
        queue = new LinkedList<>();
    }

    /**
     * Adds an item to the queue
     *
     * @param t The item to add.
     */
    public synchronized void enqueue(T t) {
        queue.addLast(t);
    }

    /**
     * @return The next item in the queue.
     */
    public synchronized T dequeue() {
        if (queue.size() == 0) return null;
        return queue.removeFirst();
    }

    /**
     * @return The size of the queue.
     */
    public synchronized int size() {
        return queue.size();
    }

}
