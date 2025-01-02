package bgu.spl.mics.application.objects;
import java.util.LinkedList;
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

    public StampedDetectedObjects(int time){
        this.time = time;
        this.DetectedObject = new LinkedList<>();
    }

    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDetectedObject() {
        return DetectedObject;
    }

    public void addDetectedObject(DetectedObject d)
    {
        DetectedObject.add(d);
    }

    /**
     * Checks if an error was detected in the detected objects.
     * @return True if an error was detected, false otherwise.
     */
    public DetectedObject checkError() {
        for (DetectedObject detectedObject : DetectedObject) {
            if (detectedObject.getID().equals("ERROR")) {
                return detectedObject;
            }
        }
        return null;
    }
}
