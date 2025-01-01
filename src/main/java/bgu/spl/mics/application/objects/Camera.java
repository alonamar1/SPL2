package bgu.spl.mics.application.objects;
import java.util.LinkedList;
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

    public Camera(int id, int freq, List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.frequency = freq;
        this.status = STATUS.UP;
        this.detectedObjectsList = detectedObjectsList;
    }

    public void setStatus(STATUS stat) {
        this.status = stat;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    public STATUS getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    /**
     * Returns the detected objects at a specific time.
     *
     * @param time
     * @return StampedDetectedObjects object, detected objects at a specific
     * time.
     */
    public StampedDetectedObjects getDetectedObjects(int time) {
        for (StampedDetectedObjects stampedDetectedObjects : detectedObjectsList) {
            if (stampedDetectedObjects.getTime() == time) {
                return stampedDetectedObjects;
            }
        }
        return null;
    }

    /**
     * Handles a tick event.
     *
     * @param tick
     * @return DetectedObjectsEvent object, detected objects event.
     */
    public DetectedObjectsEvent handleTick(int tick) {
        StampedDetectedObjects stampedDetectedObjects = this.getDetectedObjects(tick); // get the detected objects at the next tick
        if (stampedDetectedObjects != null) {
            // check if an error was detected in the detected objects
            DetectedObject errorObject = stampedDetectedObjects.checkError();
            if (errorObject != null) {
                this.status = STATUS.ERROR; // TODO: Handle the case where an error was detected.
                List<DetectedObject> errorlist = new LinkedList<DetectedObject>();
                errorlist.add(errorObject);
                return new DetectedObjectsEvent(id, errorlist, tick);
            }
            DetectedObjectsEvent detectedObjectEvent = new DetectedObjectsEvent(id, stampedDetectedObjects.getDetectedObject(), tick);
            StatisticalFolder.getInstance().incrementNumDetectedObjects(stampedDetectedObjects.getDetectedObject().size()); // increment the number of detected objects in the statistical folder
            return detectedObjectEvent;
        }
        return null;
    }
    public boolean checkIfFinish(int nexttTick) {
        // Assuming the list is sorted by time
        if (this.detectedObjectsList.get(this.detectedObjectsList.size() - 1).getTime() < nexttTick) {
            return true;
        }
        return false;
    }

}
