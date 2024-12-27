package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast sent by the any of the Microservices to notify that a microservice has crashed.
 */
public class CrashedBroadcast implements Broadcast {
    private String senderId;

    public CrashedBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}
