package bgu.spl.mics.application.services;

import java.util.concurrent.Future;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.STATUS;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    private final Camera camera;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService");
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for
     * sending
     * DetectObjectsEvents.
     */
    public Camera getcamera() {
        return camera;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            if (camera.getStatus() != STATUS.DOWN) {
                if (tick.getTick() - camera.getFrequency() >= 1) {
                    DetectedObjectsEvent detectedList = camera.handleTick(tick.getTick() - camera.getFrequency());
                    // If the camera is down, broadcast a Crashed message and terminate the service.
                    if (camera.getStatus() == STATUS.ERROR) {
                        sendBroadcast(new CrashedBroadcast("Camera"));
                        terminate();
                    }
                    // If the camera is not in ERROR state, send the detected objects to the LiDAR workers.
                    else if (detectedList != null) {
                        Future<Boolean> future;
                        future = (Future<Boolean>) sendEvent(detectedList);
                        try {
                            // TODO: Change the time to a constant.
                            Boolean result = future.get(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                            if (result == false) {
                                // TODO: Handle the case where the event was not completed successfully.
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // TODO: Handle the case where the future was interrupted.
                            //sendBroadcast(new CrashedBroadcast("Camera"));
                        }
                    }
                }
                // if in the next tick there is not objects to detect end
                if (this.camera.checkIfFinish(tick.getTick() + 1)) {
                    this.camera.setStatus(STATUS.DOWN);
                    sendBroadcast(new TerminatedBroadcast("Camera"));
                    terminate();
                }
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // if the terminated service is TimeService, terminate the CameraService.
            if (terminated.getSenderId().equals("TimeService")) {
                sendBroadcast(new TerminatedBroadcast("Camera"));
                this.camera.setStatus(STATUS.DOWN);
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            // TODO: Handle the case where other service was Crashed.
            terminate();
        });

    }
}
