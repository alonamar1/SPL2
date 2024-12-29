package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using
 * data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    // TODO: Define fields and methods.
    private int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> trackedObjects;
    LiDarDataBase dataBase; // the LiDarDataBase object

    public LiDarWorkerTracker(int id, int frequency, STATUS status, List<TrackedObject> trackedObjects) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.trackedObjects = trackedObjects;
        this.dataBase = LiDarDataBase.getInstance("path"); // לשנות את הפאט
    }

    /**
     * Returns a TrackedObject object for a given DetectedObject.
     * 
     * @param detectedObject
     * @return TrackedObject object for the detected object.
     */
    public TrackedObject getTrackedObject(DetectedObject detectedObject, int timeSeeingObject) {
        // get the cloud points of the detected object
        for (StampedCloudPoints cp : dataBase.getCloudPoints()) {
            if (cp.getTime() <= timeSeeingObject) {
                // if the ID of the cloud points is "ERROR"
                if (cp.getID() == "ERROR") {
                    this.status = STATUS.ERROR;
                    return null;
                }
                // if the ID of the cloud points is the same as the ID of the detected object
                else if (cp.getID().equals(detectedObject.getID())) {
                    List<CloudPoint> cloudPoints = new LinkedList<>(); // create an empty list of cloud points
                    for (int i = 0; i < cp.getCloudPoints().size(); i++) {
                        // create a cloud point object for each cloud point, without the z coordinate
                        cloudPoints.add(new CloudPoint(cp.getCloudPoints().get(i).get(0),
                                cp.getCloudPoints().get(i).get(1)));
                    }
                    TrackedObject trackedObject = new TrackedObject(cp.getID(), cp.getTime(),
                            detectedObject.getDescreption(), cloudPoints);
                    this.trackedObjects.add(trackedObject); // add the tracked object to the list of tracked objects
                    return trackedObject;
                }
            }
        }
        return null;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
    
    public int getID() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }
}
