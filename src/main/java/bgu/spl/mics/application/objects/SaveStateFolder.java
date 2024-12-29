package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

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
    // TODO: Implement the class
    // TODO: להחליט האם הולכים על זה או על איוונט חדש מסוג אחר ששולח את כל הקבצים
    // האחרונים
    private List<Pose> prevPoses;
    private List<TrackedObjectEvent> lastLiDarWorkerTracksEvent;
    private List<DetectedObjectsEvent> lastCamerasDetectEvent;

    public SaveStateFolder(int amoutCamera, int amountLidarWorker) {
        this.prevPoses = new LinkedList<>();
        //this.lastCamerasDetectEvent =
    }

}
