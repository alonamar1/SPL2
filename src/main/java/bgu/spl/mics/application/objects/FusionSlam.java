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
    private List<ReadyToProcessPair<Pose, TrackedObjectEvent>> readyToProcessPairs;

    private FusionSlam() {
        this.landmarks = new LinkedList<>();
        this.poses = new LinkedList<>();
        this.trackedObjectsReciv = new LinkedList<>();
    }

    /**
     * Returns the single instance of FusionSlam.
     *
     * @return The single instance of FusionSlam.
     */

    public static FusionSlam getInstance() {
        return FusionSlamHolder.getInstance();
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

    public List<ReadyToProcessPair<Pose, TrackedObjectEvent>> getReadyToProcessPairs() {
        return readyToProcessPairs;
    }

        /**
     * Processes a ReadyToProcessPair of Pose and TrackedObjectEvent.
     * @param toProcess
     */
    public LandMark ProcessReadyToProcessPair(ReadyToProcessPair<Pose, TrackedObjectEvent> toProcess) {
        // Remove the pose from the list.
        // this.fusionSlam.getPoses().remove(toProcess.getKey());
        // Remove the tracked object from the list.
        this.getTrackedObjectsReciv().remove(toProcess.getValue());
        // Process the pair of pose and every tracked object.
        for (TrackedObject trackedObject : toProcess.getValue().getTrackedObject()) {
            // Check if the tracked object is already a landmark.
            LandMark Prevlandmark = this.isDetectedLandmark(trackedObject.getId());
            // Convert the cloud pointes of the tracked object to global coordinates.
            List<CloudPoint> newLandmarkCloudPoints = this.convertToGlobalCoordinateSys(trackedObject.getCoordinates(), toProcess.getKey());
            // If the tracked object is a new landmark.
            if (Prevlandmark == null) {
                LandMark newLandmark = new LandMark(trackedObject.getId(), trackedObject.getDescription(),
                        newLandmarkCloudPoints);
                this.getLandmarks().add(newLandmark);
                // Increment the number of landmarks detected.
                StatisticalFolder.getInstance().incrementNumLandmarks();
            } else {
                // Update the coordinates of the existing landmark.
                LandMark.updateCoordiLandmark(Prevlandmark, newLandmarkCloudPoints);
            }
        }
        return landmarks.get(landmarks.size()-1);
    }

    /**
     * checks if a LandMark is detected.
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
     * Checks if the list of ReadyToProcessPairs is empty.
     * @return
     */
    public boolean isReadyToProcessPairsEmpty() {
        return readyToProcessPairs.isEmpty();
    }

    /**
     * Converts a list of CloudPoints to the global coordinate system.
     * @param cloudPoints
     * @param pose
     * @return
     */
    public List<CloudPoint> convertToGlobalCoordinateSys(List<CloudPoint> cloudPoints, Pose pose) {
        List<CloudPoint> globalCloudPoints = new LinkedList<>();
        for (CloudPoint cloudPoint : cloudPoints) {
            double radiansYaw = pose.getYaw() * (Math.PI / 180);
            float x = (float) (cloudPoint.getX() * Math.cos(radiansYaw) - cloudPoint.getY() * Math.sin(radiansYaw)
                    + pose.getX());
            float y = (float) (cloudPoint.getX() * Math.sin(radiansYaw) + cloudPoint.getY() * Math.cos(radiansYaw)
                    + pose.getY());
            globalCloudPoints.add(new CloudPoint(x, y));
        }
        return globalCloudPoints;
    }

    /**
     * Checks if a TrackedObjectEvent is ready to be processed.
     * @param trackedObjectEvent
     */
    public void checkReadyToProcess(TrackedObjectEvent trackedObjectEvent) {
        for (Pose pose : this.poses) {
            if (pose.getTime() == trackedObjectEvent.getTime()) {
                this.readyToProcessPairs.add(new ReadyToProcessPair<>(pose, trackedObjectEvent));
            }
        }
    }

    /**
     * Checks if a Pose is ready to be processed.
     * @param pose
     */
    public void checkReadyToProcess(Pose pose) {
        for (TrackedObjectEvent trackedObjectEvent : this.trackedObjectsReciv) {
            if (pose.getTime() == trackedObjectEvent.getTime()) {
                this.readyToProcessPairs.add(new ReadyToProcessPair<>(pose, trackedObjectEvent));
            }
        }
    }

    /*
     * Singleton pattern to ensure a single instance of FusionSlam exists.
     */
    private static class FusionSlamHolder {
        private static FusionSlam instance;

        private FusionSlamHolder() {
            instance = new FusionSlam();
        }

        public static FusionSlam getInstance() {
            return instance;
        }
    }

}
