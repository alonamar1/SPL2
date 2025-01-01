package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.SaveStateFolder;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    private final Camera camera;
    private final CountDownLatch lanch;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera, CountDownLatch lanch) {
        super("CameraService");
        this.camera = camera;
        this.lanch = lanch;
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
                        String reason = detectedList.getDetectedObject().remove(0).getDescreption();
                        sendBroadcast(new CrashedBroadcast("Camera" + this.camera.getId(), reason));
                        terminate();
                        Thread.currentThread().interrupt(); // TODO: Need it ???
                    }
                    // If the camera is not in ERROR state, send the detected objects to the LiDAR
                    // workers.
                    else if (detectedList != null) {
                        sendEvent(detectedList);
                        SaveStateFolder.getInstance().updateCameraObjects(detectedList); // save state
                    }
                }
                // if in the next tick there is not objects to detect end
                if (camera.getStatus() != STATUS.ERROR && this.camera.checkIfFinish(tick.getTick() + 1)) {
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

        lanch.countDown();

    }
}
