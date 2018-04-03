package com.klinker.droneos.arch.communication.messages;

import com.klinker.droneos.arch.nodes.Node;

import java.io.Serializable;

/**
 * Messages are the base object that are passed between nodes. To send a
 * message from a {@link Node}, call: {@link Node#sendMessage(Message)}.
 * @param <T> The contents of the message. It can be any object.
 */
public class Message<T> implements Serializable {

    ///// Static Variables /////////////////////////////////////////////////////

    /**
     * Static counter variable to give each message unique ID.
     */
    private static long ID_COUNTER = 0;


    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * A unique identifier for this message.
     */
    private long mId;

    /**
     * The class this message is to be sent to.
     */
    private Class<?> mTo;

    /**
     * The class this message was sent from.
     */
    private Class<?> mFrom;

    /**
     * The name of the message used to decide what to do in the receiving Node.
     */
    private String mName;

    /**
     * The content of the message.
     */
    private T mData;


    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * Creates a new message.
     * @param to   The Node's class the message is being sent to. Ex:
     *             <code>CVNode.class</code>
     * @param from The Node's class the message is being sent from. Ex:
     *             <code>this.getClass()</code>
     * @param data The data being sent. This could be text, JSON, an image.
     */
    public Message(Class<? extends Node> to, Class<? extends Node> from,
                   String name, T data) {
        mId = getNextID();
        mTo = to;
        mFrom = from;
        mName = name;
        mData = data;
    }


    ///// Getters //////////////////////////////////////////////////////////////

    /**
     * Increments and returns the next unused ID.
     * @return A unique ID, one more than the previous call to this function.
     */
    private static synchronized long getNextID() {
        ID_COUNTER++;
        return ID_COUNTER;
    }

    /**
     * @return The {@link Node} subclass the message is being sent from.
     */
    public Class<?> getFrom() {
        return mFrom;
    }

    /**
     * @return The {@link Node} subclass the message is being sent to.
     */
    public Class<?> getTo() {
        return mTo;
    }

    /**
     * @return The {@link T} that is being sent in the message.
     */
    public T getData() {
        return mData;
    }

    /**
     * @return The name of the message.
     */
    public String getName() {
        return mName;
    }


    ///// Object Overrides /////////////////////////////////////////////////////

    @Override
    public String toString() {
        return String.format(
                "%s { from: %s, to: %s, data: %s }",
                mFrom.getSimpleName(),
                mTo.getSimpleName(),
                getClass().getSimpleName(),
                mData
        );
    }
}
