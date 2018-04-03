package com.klinker.droneos.arch.communication;

import com.klinker.droneos.arch.communication.messages.Message;

public interface OnMessageReceivedListener {
    void onReceiveMessage(Message message);
}
