package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.LinkedList;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {

    public enum status {Up, Down, Error;}

    private int ID;
    private int frequency;
    private List<StampedDetectedObjects> detectedObjectsList;
    private status status;

    public Camera(int id, int freq, status status, List<StampedDetectedObjects> list){
        ID = id;
        frequency = freq;
        this.status = status;
        detectedObjectsList = list;
    }

    public StampedDetectedObjects getDetectedObjects(int time){
        for (StampedDetectedObjects stampedDetectedObjects : detectedObjectsList) {
            if(stampedDetectedObjects.getTime() == time){
                return stampedDetectedObjects;
            }
        }
        return null;
    }

    public List<DetectedObjectsEvent> handleTick(int tick){
        // check if the camera is up
        // if not, return null
        StampedDetectedObjects stampedDetectedObjects = getDetectedObjects(tick+frequency); // get the detected objects at the next tick
        if(stampedDetectedObjects != null){
            List<DetectedObject> detectedObjects = stampedDetectedObjects.getDetectedObject();
            List<DetectedObjectsEvent> detectedObjectEvents = new LinkedList<>();
            for (DetectedObject detectedObject : detectedObjects) {
                detectedObjectEvents.add(new DetectedObjectsEvent(ID, detectedObject));
            }
            return detectedObjectEvents;
        }
        return null;
    }
    


}
