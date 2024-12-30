package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;

/**
 * Represents a camera sensor on the robot. Responsible for detecting objects in
 * the environment.
 */
public class Camera {

    private int id;
    private int frequency;
    private String stringID;
    private List<StampedDetectedObjects> detectedObjectsList;
    private STATUS status;

    public Camera(int id, int freq, List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.frequency = freq;
        this.status = STATUS.UP;
        this.stringID = "camera"+id;
        this.detectedObjectsList = detectedObjectsList;
        // try {
        //     detectedObjectsList = cameraData(filepath,"camera"+id);
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     detectedObjectsList = null;
        // }
    }

    public void setStatus(STATUS stat) {
        this.status = stat;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
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

    // // TODO: Move this function to a Main class
    // private List<StampedDetectedObjects> cameraData(String filepath, String cameraID) throws IOException {
    //     Gson gson = new Gson();
    //     try (FileReader reader = new FileReader(filepath)) {
    //         // Convert JSON File to Java Object
    //         Type type = new TypeToken<Map<String, List<List<StampedDetectedObjects>>>>() {}.getType();
    //         Map<String, List<List<StampedDetectedObjects>>> cameras = gson.fromJson(reader, type);
    //         List<List<StampedDetectedObjects>> cameraList = cameras.get(cameraID);
    //         List<StampedDetectedObjects> cameraSDO = cameraList.stream().flatMap(List::stream).toList();
    //         return cameraSDO;
    //     }
    //     catch (JsonSyntaxException e) {
    //         System.err.println("Invalid JSON format: " + e.getMessage());
    //     }
    //     return null;
    // }

    /**
     * Returns the detected objects at a specific time.
     *
     * @param time
     * @return StampedDetectedObjects object, detected objects at a specific
     * time.
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
     *
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
    public boolean checkIfFinish(int nexttTick) {
        // Assuming the list is sorted by time
        if (this.detectedObjectsList.get(this.detectedObjectsList.size() - 1).getTime() < nexttTick) {
            return true;
        }
        return false;
    }

}
