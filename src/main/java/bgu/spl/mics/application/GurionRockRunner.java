package bgu.spl.mics.application;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.ReadyToProcessPair;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.services.CameraService;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        String configPath = "C:\\Users\\alona\\SPL2\\example_input_2\\configuration_file.json";
        try {
            MainConfiguration config = ReadConfiguration.readConfiguration(configPath);
            List<CameraConfiguration> cameraConfigs = config.getCameras().getCamerasConfigurations();
            List<CameraService> camerasList = new LinkedList<>();
            for (CameraConfiguration cameraConfig : cameraConfigs) {
                camerasList.add(new CameraService(new Camera()));
                System.out.println("Camera ID: " + cameraConfig.getId());
                System.out.println("Frequency: " + cameraConfig.getFrequency());
                System.out.println("Camera Key: " + cameraConfig.getCameraKey());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    