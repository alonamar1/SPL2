package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    public enum status {Up, Down, Error;}

    // TODO: Define fields and methods.
    private int id;
    private int frequency;
    private status status;
    private List<TrackedObject> trackedObjects;

    public LiDarWorkerTracker(int id, int frequency, status status, List<TrackedObject> trackedObjects){
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.trackedObjects = trackedObjects;
    }


    public TrackedObject getTrackedObject(String ID){
        LiDarDataBase dataBase = LiDarDataBase.getInstance("path");
        for (List<StampedCloudPoints> cp : dataBase.getCloudPoints()) {
            if (cp.getID().equals(ID)) {
                return new TrackedObject(cp.getID(), cp.getTime(), cp.getDescription(), cp.getCloudPoints());
            }
        }
        return null;
    }
/* 
    public List<TrackedObject> handleTick(int tick){
        // check if the LiDarWorker is up
        // if not, return null
        //TrackedObject trackedObject = getTrackedObject(tick+frequency); // get the tracked object at the next tick
        if(trackedObjects != null){
            return trackedObjects;
        }
        return null;
    } */
}
