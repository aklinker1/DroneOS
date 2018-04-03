package com.klinker.droneos.arch.communication.messages;

import com.klinker.droneos.arch.nodes.Node;

/**
 * A {@link Message} containing a string.
 */
public class TextMessage extends Message<String> {
    public TextMessage(Class<? extends Node> to, Class<? extends Node> from,
                       String name, String data) {
        super(to, from, name, data);
    }
}
