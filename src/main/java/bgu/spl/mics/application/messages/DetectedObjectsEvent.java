package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

public class DetectedObjectsEvent implements Event<Boolean> {

    private int cameraId;
    private DetectedObject detectedObject;

    public DetectedObjectsEvent(int cameraId, DetectedObject detectedObject) {
        this.cameraId = cameraId;
        this.detectedObject = detectedObject;
    }

    public int getCameraId() {
        return this.cameraId;
    }

    public DetectedObject getDetectedObject() {
        return this.detectedObject;
    }

}
