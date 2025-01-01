package bgu.spl.mics.application.messages;
import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

/**
 * An event that is sent by the CameraService to the LiDarWorkerTracker.
 * The event contains a list of detected objects and the time of arrival to the LiDar.
 */
public class DetectedObjectsEvent implements Event<Boolean> {

    private int cameraId;
    private int time;
    private List<DetectedObject> detectedObject;

    public DetectedObjectsEvent(int cameraId, List<DetectedObject> detectedObject, int time) {
        this.cameraId = cameraId;
        this.detectedObject = detectedObject;
        this.time = time;
    }

    public int getCameraId() {
        return this.cameraId;
    }

    public List<DetectedObject> getDetectedObject() {
        return this.detectedObject;
    }   

    public int getTime() {
        return this.time;
    }


}
