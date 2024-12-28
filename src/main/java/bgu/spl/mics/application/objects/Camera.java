package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;

/**
 * Represents a camera sensor on the robot. Responsible for detecting objects in
 * the environment.
 */


public class Camera {

    private int id;
    private int frequency;
    private List<StampedDetectedObjects> detectedObjectsList;
    private STATUS status;

    public Camera(int id, int freq, String filepath) {
        this.id = id;
        this.frequency = freq;
        this.status = STATUS.UP;
        this.detectedObjectsList = cameraData(filepath);
        
    }

    public STATUS getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    // TODO: Move this function to a Main class
    private List<StampedDetectedObjects> cameraData(String filepath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filepath)) {
            // Convert JSON File to Java Object
            return gson.fromJson(reader, new TypeToken<List<StampedCloudPoints>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the detected objects at a specific time.
     * @param time
     * @return StampedDetectedObjects object, detected objects at a specific time.
     */
    public StampedDetectedObjects getDetectedObjects(int time) {
        for (StampedDetectedObjects stampedDetectedObjects : detectedObjectsList) {
            if (stampedDetectedObjects.getTime() == time) {
                return stampedDetectedObjects;
            }
        }
        return null;
    }

    /**
     * Handles a tick event.
     * @param tick
     * @return DetectedObjectsEvent object, detected objects event.
     */
    public DetectedObjectsEvent handleTick(int tick) {
        StampedDetectedObjects stampedDetectedObjects = this.getDetectedObjects(tick); // get the detected objects at the next tick
        if (stampedDetectedObjects != null) {
            // check if an error was detected in the detected objects
            if (stampedDetectedObjects.checkError()) {
                this.status = STATUS.ERROR; // TODO: Handle the case where an error was detected.
                return null;
            }
            DetectedObjectsEvent detectedObjectEvent = new DetectedObjectsEvent(id, stampedDetectedObjects.getDetectedObject(), tick);
            StatisticalFolder.getInstance().incrementNumDetectedObjects(stampedDetectedObjects.getDetectedObject().size()); // increment the number of detected objects in the statistical folder
            return detectedObjectEvent;
        }
        return null;
    }

}
