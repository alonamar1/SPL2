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

    // TODO: Add private methods and fields as needed.
    public LiDarWorkerTracker(int id, int frequency, status status) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
    }
}
