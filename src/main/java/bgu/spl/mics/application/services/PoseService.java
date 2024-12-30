package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * PoseService is responsible for maintaining the robot's current pose (position
 * and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    private GPSIMU gpsimu;
    private final CountDownLatch lanch;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu, CountDownLatch lanch) {
        super("PoseService");
        this.gpsimu = gpsimu;
        this.lanch = lanch;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the
     * current pose.
     */
    @Override
    protected void initialize() {
        // TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            if (this.gpsimu.getCurrentStatus() != STATUS.DOWN) {
                // Create a new PoseEvent with the current pose
                PoseEvent poseEvent = new PoseEvent(this.gpsimu.getCurrentPose(tick.getTick()));
                sendEvent(poseEvent); // Send the PoseEvent
                SaveStateFolder.getInstance().updatePose(poseEvent.getPose()); // save status
            }
            if (this.gpsimu.getCurrentStatus() == STATUS.DOWN){
                sendBroadcast(new TerminatedBroadcast("PoseService"));
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // if the terminated service is TimeService, terminate the PoseService.
            if (terminated.getSenderId().equals("TimeService")) {
                sendBroadcast(new TerminatedBroadcast("PoseService"));
                this.gpsimu.setCurrentStatus(STATUS.DOWN);
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            // TODO: Handle the case where other service was Crashed.
            terminate();
        });
        lanch.countDown();
    }
}
