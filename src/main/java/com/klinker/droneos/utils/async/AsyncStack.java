package com.klinker.droneos.utils.async;

import java.util.LinkedList;

/**
 * This is a wrapper class is meant to help handle the async modification of a stack.
 * This is used in the Architecture due to queues being modified from different
 * threads. Use this if your application involves multi-threading.
 *
 * @param <T> The model you wish to store.
 */
public class AsyncStack<T> {

    private LinkedList<T> stack;

    /**
     * Default constructor,
     */
    public AsyncStack() {
        stack = new LinkedList<>();
    }

    public synchronized void push(T t) {
        stack.addLast(t);
    }

    public synchronized int size() {
        return stack.size();
    }

    public synchronized T pull(T t) {
        return stack.removeLast();
    }

}
