package bgu.spl.mics.application.services;

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
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        // TODO Implement this
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
            // TODO Implement this
        });

        // TickEvent - PoseEvent
        subscribeEvent(PoseEvent.class, (PoseEvent PoseEvent) -> {
            // TODO Implement this
        });

        // TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            // TODO Implement this
        });

        // TerminatedBroadCast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // TODO: Handle the case where the service was terminated.
            // terminate();
        });

        // CrashedBroadCast
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            //
            terminate();
        });
    }
}
