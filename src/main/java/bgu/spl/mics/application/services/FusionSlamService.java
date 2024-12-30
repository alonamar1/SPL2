package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.messages.PoseEvent;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private FusionSlam fusionSlam; // The FusionSLAM object responsible for managing the global map.
    private int cameraAmount; // represent the number of active cameras
    private int LidarWorkerAmount; // represent the number of active lidar worker
    private boolean poseServiceOn;
    private boolean timeServiceOn;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam, int cameraAmount, int LidarWorkerAmount) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.LidarWorkerAmount = LidarWorkerAmount;
        this.cameraAmount = cameraAmount;
        this.poseServiceOn = true;
        this.timeServiceOn = true;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and
     * TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        // TickEvent - TrackedObjectEvent
        subscribeEvent(TrackedObjectEvent.class, (TrackedObjectEvent TrackedObjectEvent) -> {
            ReadyToProcessPair<Pose, TrackedObjectEvent> toProcess = this.fusionSlam
                    .checkReadyToProcess(TrackedObjectEvent); // Check if the tracked object is ready to be processed.
            if (toProcess != null) {
                // Process the pair of pose and every tracked object.
                this.fusionSlam.ProcessReadyToProcessPair(toProcess);
            } else {
                this.fusionSlam.getTrackedObjectsReciv().add(TrackedObjectEvent); // Add the new tracked object event to
                                                                                  // the list.
            }
        });

        // TickEvent - PoseEvent
        subscribeEvent(PoseEvent.class, (PoseEvent PoseEvent) -> {
            List<ReadyToProcessPair<Pose, TrackedObjectEvent>> toProcess = this.fusionSlam
                    .checkReadyToProcess(PoseEvent.getPose()); // Check if the pose is ready to be processed.
            if (!toProcess.isEmpty()) {
                for (ReadyToProcessPair<Pose, TrackedObjectEvent> pair : toProcess) {
                    this.fusionSlam.ProcessReadyToProcessPair(pair);
                }
            } else {
                this.fusionSlam.getPoses().add(PoseEvent.getPose()); // Add the new pose to the list.
            }
        });

        // TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            // TODO: בודק מתי צריך לסיים את כל התוכנית
            // if need to finish
            this.checkIfRunning(); // צריך לבדוק גם פה ??
            if (!this.fusionSlam.getRunning()) {
                // sendBroadcast(new TerminatedBroadcast("FusionSlam"));
            }
        });

        // TerminatedBroadCast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            if (terminated.getSenderId().equals("Camera")) {
                this.cameraAmount--;
            } else if (terminated.getSenderId().equals("LiDarWorker")) {
                this.LidarWorkerAmount--;
            } else if (terminated.getSenderId().equals( "PoseService")) {
                this.poseServiceOn = false;
            } else if (terminated.getSenderId().equals("TimeService")) {
                this.timeServiceOn = false;
            }
            // TODO: לסיים את התוכנית ואת כל ה fusion אחרי שכל התנאים מתקיימים
            this.checkIfRunning();
            if (!this.fusionSlam.getRunning()) {
                // TODO: make a output File
                terminate();
            }
        });

        // CrashedBroadCast
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            this.fusionSlam.setRunning(false);
            // TODO: Handle the case where other service crashed.
            // TODO: make a ERROR output File
            terminate();
        });
    }

    /**
     * Check if the fusionSlam need to finish the program
     */
    private void checkIfRunning() {
        // All the sensor finishs, finish simulation
        if (cameraAmount == 0 && LidarWorkerAmount == 0 && !this.poseServiceOn)
            this.fusionSlam.setRunning(false);
        // TimeService stops, the program need to terminated
        else if (!this.timeServiceOn)
            this.fusionSlam.setRunning(false);
    }
}
