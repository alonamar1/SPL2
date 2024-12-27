package bgu.spl.mics.application.objects;
import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    
    private int time;
    private List<DetectedObject> DetectedObject;

    public StampedDetectedObjects(int time, List<DetectedObject> DetectedObject){
        this.time = time;
        this.DetectedObject = DetectedObject;
    }

    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDetectedObject() {
        return DetectedObject;
    }

    /**
     * Checks if an error was detected in the detected objects.
     * @return True if an error was detected, false otherwise.
     */
    public boolean checkError() {
        for (DetectedObject detectedObject : DetectedObject) {
            if (detectedObject.getID() == "ERROR") {
                return true;
            }
        }
        return false;
    }
}
