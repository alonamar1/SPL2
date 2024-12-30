package bgu.spl.mics.application;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * This class reads camera data from a JSON file and prints the detected objects for each camera and time frame.
 */
public class ReadCameraData {
    public static List<StampedDetectedObjects> readCameraData(String filePath, String id) {
        List<StampedDetectedObjects> output = new LinkedList<>();
        try (InputStream is = new FileInputStream(filePath)) {
            // Create a JSONTokener to parse the JSON file
            JSONTokener tokener = new JSONTokener(is);
            // Create a JSONObject from the tokener
            JSONObject jsonObject = new JSONObject(tokener);

            // Iterate over each camera in the JSON file
            for (String cameraKey : jsonObject.keySet()) {
                System.out.println("Camera: " + cameraKey);
                // Get the data array for the current camera
                JSONArray cameraData = jsonObject.getJSONArray(cameraKey);
                // Iterate over each entry in the camera data array
                for (int i = 0; i < cameraData.length(); i++) {
                    // Get the current entry as a JSONObject
                    JSONObject entry = cameraData.getJSONObject(i);
                    // Get the time value from the entry
                    int time = entry.getInt("time");
                    // Get the detected objects array from the entry
                    JSONArray detectedObjects = entry.getJSONArray("detectedObjects");

                    System.out.println("  Time: " + time);
                    // Iterate over each detected object in the detected objects array
                    for (int j = 0; j < detectedObjects.length(); j++) {
                        // Get the current detected object as a JSONObject
                        JSONObject detectedObject = detectedObjects.getJSONObject(j);
                        // Get the id and description of the detected object
                        String id = detectedObject.getString("id");
                        String description = detectedObject.getString("description");
                        System.out.println("    Detected Object ID: " + id + ", Description: " + description);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static void main(String[] args) {
        // Path to the JSON file containing camera data
        String filePath = "c:/Users/meire/Documents/Third Semster/SPL2/Skeleton/example_input_2/camera_data.json";
        try (InputStream is = new FileInputStream(filePath)) {
            // Create a JSONTokener to parse the JSON file
            JSONTokener tokener = new JSONTokener(is);
            // Create a JSONObject from the tokener
            JSONObject jsonObject = new JSONObject(tokener);

            // Iterate over each camera in the JSON file
            for (String cameraKey : jsonObject.keySet()) {
                System.out.println("Camera: " + cameraKey);
                // Get the data array for the current camera
                JSONArray cameraData = jsonObject.getJSONArray(cameraKey);
                // Iterate over each entry in the camera data array
                for (int i = 0; i < cameraData.length(); i++) {
                    // Get the current entry as a JSONObject
                    JSONObject entry = cameraData.getJSONObject(i);
                    // Get the time value from the entry
                    int time = entry.getInt("time");
                    // Get the detected objects array from the entry
                    JSONArray detectedObjects = entry.getJSONArray("detectedObjects");

                    System.out.println("  Time: " + time);
                    // Iterate over each detected object in the detected objects array
                    for (int j = 0; j < detectedObjects.length(); j++) {
                        // Get the current detected object as a JSONObject
                        JSONObject detectedObject = detectedObjects.getJSONObject(j);
                        // Get the id and description of the detected object
                        String id = detectedObject.getString("id");
                        String description = detectedObject.getString("description");
                        System.out.println("    Detected Object ID: " + id + ", Description: " + description);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
