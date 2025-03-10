package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.messages.TrackedObjectEvent;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
 */
public class FusionSlam {

    private List<LandMark> landmarks;
    private List<Pose> poses;
    private List<TrackedObjectEvent> trackedObjectsReciv; // List of TrackedObjects received from the LiDAR workers.
    private boolean running;

    private FusionSlam() {
        this.landmarks = new LinkedList<>();
        this.poses = new LinkedList<>();
        this.trackedObjectsReciv = new LinkedList<>();
        this.running = true;
    }

    /**
     * Returns the single instance of FusionSlam.
     *
     * @return The single instance of FusionSlam.
     */

    public static FusionSlam getInstance() {
        return FusionSlamHolder.getInstance();
    }

    public void setRunning(boolean state) {
        this.running = state;
    }

    public void setLandmarks(List<LandMark> landmarks) {
        this.landmarks = landmarks;
    }

    public boolean getRunning() {
        return this.running;
    }

    public List<LandMark> getLandmarks() {
        return landmarks;
    }

    public List<Pose> getPoses() {
        return poses;
    }

    public List<TrackedObjectEvent> getTrackedObjectsReciv() {
        return trackedObjectsReciv;
    }

    /**
     * Processes a ReadyToProcessPair of Pose and TrackedObjectEvent.
     * 
     * @pre pose and trackedObjectEvent are not null
     * @post result is not null and contains the correct id, description, and
     *       coordinates
     * @inv landmarks list size is incremented by 1 if a new landmark is added
     * @param toProcess
     */
    public List<LandMark> ProcessReadyToProcessPair(ReadyToProcessPair<Pose, TrackedObjectEvent> toProcess) {
        List<LandMark> newLandmarks = new LinkedList<>();
        // Remove the tracked object from the list.
        this.trackedObjectsReciv.remove(toProcess.getValue());
        // Process the pair of pose and every tracked object.
        for (TrackedObject trackedObject : toProcess.getValue().getTrackedObject()) {
            if (trackedObject.getCoordinates().size() > 0) {
                // Check if the tracked object is already a landmark.
                LandMark Prevlandmark = this.isDetectedLandmark(trackedObject.getId());
                // Convert the cloud pointes of the tracked object to global coordinates.
                List<CloudPoint> newLandmarkCloudPoints = this
                        .convertToGlobalCoordinateSys(trackedObject.getCoordinates(), toProcess.getKey());
                // If the tracked object is a new landmark.
                if (Prevlandmark == null) {
                    LandMark newLandmark = new LandMark(trackedObject.getId(), trackedObject.getDescription(),
                            newLandmarkCloudPoints);
                    this.landmarks.add(newLandmark);
                    newLandmarks.add(newLandmark);
                    // Increment the number of landmarks detected.
                    StatisticalFolder.getInstance().incrementNumLandmarks();
                } else {
                    // Update the coordinates of the existing landmark.
                    LandMark.updateCoordiLandmark(Prevlandmark, newLandmarkCloudPoints);
                }
            }
        }
        if (this.landmarks.size() > 0) {
            return newLandmarks;
        } else {
            return null;
        }
    }

    /**
     * checks if a LandMark is detected.
     * 
     * @param id
     * @return
     */
    public LandMark isDetectedLandmark(String id) {
        for (LandMark landmark : landmarks) {
            if (landmark.getId().equals(id)) {
                return landmark;
            }
        }
        return null;
    }

    /**
     * Converts a list of CloudPoints to the global coordinate system.
     * 
     * @param cloudPoints
     * @param pose
     * @return
     */
    public List<CloudPoint> convertToGlobalCoordinateSys(List<CloudPoint> cloudPoints, Pose pose) {
        List<CloudPoint> globalCloudPoints = new LinkedList<>();
        // Convert the coordinates of the cloud points to the global coordinate system.
        for (CloudPoint cloudPoint : cloudPoints) {
            // Convert the yaw to radians.
            double radiansYaw = Math.toRadians(pose.getYaw());
            // Convert the coordinates to the global coordinate system.
            double x = (cloudPoint.getX() * Math.cos(radiansYaw) - cloudPoint.getY() * Math.sin(radiansYaw)
                    + pose.getX());
            double y = (cloudPoint.getX() * Math.sin(radiansYaw) + cloudPoint.getY() * Math.cos(radiansYaw)
                    + pose.getY());
            // Add the cloud point to the list of global cloud points.
            globalCloudPoints.add(new CloudPoint(x, y));
        }
        return globalCloudPoints;
    }

    /**
     * Checks if a TrackedObjectEvent is ready to be processed.
     * 
     * @param trackedObjectEvent
     */
    public ReadyToProcessPair<Pose, TrackedObjectEvent> checkReadyToProcess(TrackedObjectEvent trackedObjectEvent) {
        // check if there is a Pose with the same time as the TrackedObjectEvent.
        for (Pose pose : this.poses) {
            if (pose.getTime() == trackedObjectEvent.getTime()) {
                return new ReadyToProcessPair<>(pose, trackedObjectEvent);
            }
        }
        return null;
    }

    /**
     * Checks if a Pose is ready to be processed.
     * 
     * @param pose
     */
    public List<ReadyToProcessPair<Pose, TrackedObjectEvent>> checkReadyToProcess(Pose pose) {
        List<ReadyToProcessPair<Pose, TrackedObjectEvent>> listPairs = new LinkedList<>();
        // check if there is a TrackedObjectEvent with the same time as the pose.
        for (TrackedObjectEvent trackedObjectEvent : this.trackedObjectsReciv) {
            if (pose.getTime() == trackedObjectEvent.getTime()) {
                listPairs.add(new ReadyToProcessPair<>(pose, trackedObjectEvent));
            }
        }
        return listPairs;
    }

    /*
     * Singleton pattern to ensure a single instance of FusionSlam exists.
     */
    private static class FusionSlamHolder {
        private static FusionSlam instance = new FusionSlam();

        public static FusionSlam getInstance() {
            return instance;
        }
    }

}
