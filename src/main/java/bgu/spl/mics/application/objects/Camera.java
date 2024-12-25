package bgu.spl.mics.application.objects;

import java.util.List;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;

/**
 * Represents a camera sensor on the robot. Responsible for detecting objects in
 * the environment.
 */
public class Camera {

    private int id;
    private int frequency;
    private List<StampedDetectedObjects> detectedObjectsList;
    private STATUS status;

    public Camera(int id, int freq, STATUS status, List<StampedDetectedObjects> list) {
        id = id;
        frequency = freq;
        this.status = status;
        detectedObjectsList = list;
    }

    public StampedDetectedObjects getDetectedObjects(int time) {
        for (StampedDetectedObjects stampedDetectedObjects : detectedObjectsList) {
            if (stampedDetectedObjects.getTime() == time) {
                return stampedDetectedObjects;
            }
        }
        return null;
    }

    public DetectedObjectsEvent handleTick(int tick) {
        // check if the camera is up
        // if not, return null
        StampedDetectedObjects stampedDetectedObjects = this.getDetectedObjects(tick + frequency); // get the detected objects at the next tick
        if (stampedDetectedObjects != null) {
            DetectedObjectsEvent detectedObjectEvent = new DetectedObjectsEvent(id, stampedDetectedObjects.getDetectedObject(), tick+frequency);
            return detectedObjectEvent;
        }
        return null;
    }

}
