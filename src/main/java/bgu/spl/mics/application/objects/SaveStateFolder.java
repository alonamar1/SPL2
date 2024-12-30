package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TrackedObjectEvent;

/**
 * Represents the folder where the system will save its state.
 * The folder will contain the following files:
 * - Camera - last DetectedObjectsEvent sent to the LiDAR system from each
 * camera.
 * - LiDAR Tracked objects - last TrackedObjectEvent sent to FusionSLAM from
 * evry LiDAR worker.
 * - GPS/IMU - list of poses that sent to the FusionSLAM
 */
public class SaveStateFolder {
    private static class SaveStateFolderHolder {
        private static SaveStateFolder instance = new SaveStateFolder();
    }

    public static SaveStateFolder getInstance() {
        return SaveStateFolderHolder.instance;

    }

    // TODO: Implement the class
    private AtomicReference<List<Pose>> prevPoses;
    private AtomicReference<List<TrackedObjectEvent>> lastLiDarWorkerTracksEvent;
    private AtomicReference<List<DetectedObjectsEvent>> lastCamerasDetectEvent;

    /**
     * Empty Constructor to enforce Singleton pattern
     */
    public SaveStateFolder() {
        this.prevPoses = new AtomicReference<List<Pose>>();
        this.lastCamerasDetectEvent = new AtomicReference<>();
        this.lastLiDarWorkerTracksEvent = new AtomicReference<>();
    }

    public List<Pose> getPrevPoses(){
        return this.prevPoses.get();
    }

    public List<TrackedObjectEvent> getLastLiDarEvent(){
        return this.lastLiDarWorkerTracksEvent.get();
    }

    public List<DetectedObjectsEvent> getLastCameraObjects() {
        return this.lastCamerasDetectEvent.get();
    }

    /**
     * Update the list of poses and locations of the robot.
     * @param pose
     */
    public void updatePose(Pose pose) {
        List<Pose> locaList;
        List<Pose> newList;
        do {
            locaList = this.prevPoses.get();
            newList = new LinkedList<>(locaList);
            newList.add(pose);
        } while(!this.prevPoses.compareAndSet(locaList, newList));
    }

    /**
     * Updates the list of tracked object events for the LiDAR worker with the given event.
     * If an event with the same ID already exists in the list, it is replaced with the new event.
     * Otherwise, the new event is added to the list.
     *
     * This method uses a compare-and-set loop to ensure thread safety when updating the list.
     *
     * @param event the TrackedObjectEvent to be added or updated in the list
     */
    public void updateLidarWorker(TrackedObjectEvent event) {
        List<TrackedObjectEvent> localList;
        List<TrackedObjectEvent> newList;
        do {
            // flag indicate that event object is inside
            boolean hasSameId = false;
            localList = this.lastLiDarWorkerTracksEvent.get();
            newList = new LinkedList<>();
            // fill the new List
            for (TrackedObjectEvent trackedEvent : localList) {
                if (trackedEvent.getId().equals(event.getId())) {
                    newList.add(event);
                    hasSameId = true;
                }
                newList.add(trackedEvent);
            }
            if (!hasSameId) {
                newList.add(event);
            }
        } while (!this.lastLiDarWorkerTracksEvent.compareAndSet(localList, newList));
    }

    /**
     * Updates the list of detected objects events with a new event from a camera.
     * If an event from the same camera ID already exists in the list, it will be replaced with the new event.
     * Otherwise, the new event will be added to the list.
     *
     * @param event the new DetectedObjectsEvent to be added or updated in the list
     */
    public void updateCameraObjects(DetectedObjectsEvent event) {
        List<DetectedObjectsEvent> localList;
        List<DetectedObjectsEvent> newList;
        do {
            // flag indicate that event object is inside
            boolean hasSameId = false;
            localList = this.lastCamerasDetectEvent.get();
            newList = new LinkedList<>();
            // fill the new List
            for (DetectedObjectsEvent detectedEvent : localList) {
                if (detectedEvent.getCameraId() == event.getCameraId()) {
                    newList.add(event);
                    hasSameId = true;
                }
                newList.add(detectedEvent);
            }
            if (!hasSameId) {
                newList.add(event);
            }
        } while (!this.lastCamerasDetectEvent.compareAndSet(localList, newList));
    }
}
