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


    public TrackedObject getTrackedObject(DetectedObject detectedObject){
        LiDarDataBase dataBase = LiDarDataBase.getInstance("path"); //לשנות את הפאט
        for (StampedCloudPoints cp : dataBase.getCloudPoints()) {
            if (cp.getID().equals(detectedObject.getID())) {
                CloudPoint[] cloudPoints = new CloudPoint[cp.getCloudPoints().size()];
                for (int i = 0; i < cp.getCloudPoints().size(); i++) {
                     cloudPoints[i] = new CloudPoint(cp.getCloudPoints().get(i).get(0), cp.getCloudPoints().get(i).get(1));
                }
                return (new TrackedObject(cp.getID(), cp.getTime(), detectedObject.getDescreption(), cloudPoints));
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
