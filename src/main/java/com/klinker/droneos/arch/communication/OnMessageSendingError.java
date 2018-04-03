package com.klinker.droneos.arch.communication;

import com.klinker.droneos.arch.communication.messages.Message;

/**
 * An interface that handles callbacks for the sending
 * {@link com.klinker.droneos.arch.nodes.Node}. It will be triggered and the
 * node sending the message can be notified.
 */
public interface OnMessageSendingError {
    /**
     * The callback for when there is an error sending a message.
     * @param message The message that failed to send.
     * @param e The error that caused the failure.
     */
    void onMessageSendingError(Message message, Exception e);
}
