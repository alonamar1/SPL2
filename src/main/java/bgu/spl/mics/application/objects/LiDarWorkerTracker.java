package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    // TODO: Define fields and methods.
    private int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> trackedObjects;

    public LiDarWorkerTracker(int id, int frequency, STATUS status, List<TrackedObject> trackedObjects){
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.trackedObjects = trackedObjects;
    }

    /**
     * Returns a TrackedObject object for a given DetectedObject.
     * @param detectedObject
     * @return TrackedObject object for the detected object.
     */
    public TrackedObject getTrackedObject(DetectedObject detectedObject){
        LiDarDataBase dataBase = LiDarDataBase.getInstance("path"); //לשנות את הפאט
        // get the cloud points of the detected object
        for (StampedCloudPoints cp : dataBase.getCloudPoints()) {
            // if the ID of the cloud points is the same as the ID of the detected object
            if (cp.getID().equals(detectedObject.getID())) {
                CloudPoint[] cloudPoints = new CloudPoint[cp.getCloudPoints().size()]; // create an array of cloud points
                for (int i = 0; i < cp.getCloudPoints().size(); i++) {
                    // create a cloud point object for each cloud point, without the z coordinate
                     cloudPoints[i] = new CloudPoint(cp.getCloudPoints().get(i).get(0), cp.getCloudPoints().get(i).get(1));
                }
                TrackedObject trackedObject = new TrackedObject(cp.getID(), cp.getTime(), detectedObject.getDescreption(), cloudPoints);
                this.trackedObjects.add(trackedObject); // add the tracked object to the list of tracked objects
                StatisticalFolder.getInstance().incrementNumTrackedObjects(); // add the tracked object to the statistical folder
                return trackedObject;
            }
        }
        return null;
    }

    public int getID(){
        return id;
    }

    public int getFrequency(){
        return frequency;
    }

    public STATUS getStatus(){
        return status;
    }

    public List<TrackedObject> getTrackedObjects(){
        return trackedObjects;
    }
}
