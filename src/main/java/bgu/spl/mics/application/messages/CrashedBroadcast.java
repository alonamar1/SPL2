package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast sent by the any of the Microservices to notify that a microservice has crashed.
 */
public class CrashedBroadcast implements Broadcast {
    private String senderId;
    private String reasonForCrash; // The reason For Crash to be sent in the broadcast.

    // TODO: לשנות את זה בכל המחלקו הרלוונטיות ולהתאים את התקלות לפי זה
    public CrashedBroadcast(String senderId, String reasonForCrash) {
        this.senderId = senderId;
        this.reasonForCrash = reasonForCrash;
    }

    // Constructor for the case where the reason for the crash is unknown.
    /*public CrashedBroadcast(String senderId) {
        this.senderId = senderId;
        this.reasonForCrash = "Unknown";
    }*/

    public String getSenderId() {
        return senderId;
    }

    public String getreasonForCrash() {
        return reasonForCrash;
    }
}
