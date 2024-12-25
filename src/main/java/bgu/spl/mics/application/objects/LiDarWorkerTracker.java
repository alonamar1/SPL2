package bgu.spl.mics.application.objects;

import java.util.List;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;

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


    public TrackedObject getTrackedObject(DetectedObject detectedObject){
        LiDarDataBase dataBase = LiDarDataBase.getInstance("path");
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

    public List<TrackedObject> handleTick(int tick){
        // check if the LiDarWorker is up
        // if not, return null
        //TrackedObject trackedObject = getTrackedObject(tick+frequency); // get the tracked object at the next tick

        if (status != status.Up)
        {
            return null;
        }

        return null;
    } 
     public DetectedObjectsEvent handleTick(int tick) {
        // check if the camera is up
        // if not, return null
        StampedDetectedObjects stampedDetectedObjects = this.getDetectedObjects(tick + frequency); // get the detected objects at the next tick
        if (stampedDetectedObjects != null) {
            DetectedObjectsEvent detectedObjectEvent = new DetectedObjectsEvent(id, stampedDetectedObjects.getDetectedObject());
            return detectedObjectEvent;
        }
        return null;

    public int getID(){
        return id;
    }

    public int getFrequency(){
        return frequency;
    }

    public status getStatus(){
        return status;
    }

    public List<TrackedObject> getTrackedObjects(){
        return trackedObjects;
    }
}
