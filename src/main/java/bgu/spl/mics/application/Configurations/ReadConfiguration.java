package bgu.spl.mics.application.Configurations;

import org.json.JSONArray;
import org.json.JSONObject;

import bgu.spl.mics.application.objects.Camera;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.objects.LiDarWorkerTracker;

public class ReadConfiguration {

    public static MainConfiguration readConfiguration(String filepath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        JSONObject jsonObject = new JSONObject(content);

        MainConfiguration mainConfig = new MainConfiguration();

        // Extract the directory from the configuration file path
        String configDir = filepath.substring(0, filepath.lastIndexOf("/"));

        // Parse Cameras
        JSONObject camerasObject = jsonObject.getJSONObject("Cameras");
        CameraConfiguration camerasConfig = new CameraConfiguration();
        JSONArray camerasArray = camerasObject.getJSONArray("CamerasConfigurations");
        ReadCameraData r = new ReadCameraData();
        List<Camera> cameraConfigurations = new ArrayList<>();
        for (int i = 0; i < camerasArray.length(); i++) {
            JSONObject cameraObject = camerasArray.getJSONObject(i);
            String cameraDataPath = camerasObject.getString("camera_datas_path");
            if (cameraDataPath.startsWith(".")) {
                cameraDataPath = configDir + cameraDataPath.substring(1);
            }
            Camera cameraConfig = new Camera(cameraObject.getInt("id"), cameraObject.getInt("frequency"),
                    r.readCameraData(r.cameraDataJsonToList(cameraDataPath), cameraObject.getString("camera_key")));
            cameraConfigurations.add(cameraConfig);
        }
        camerasConfig.setCamerasConfigurations(cameraConfigurations);
        camerasConfig.setCameraDatasPath(camerasObject.getString("camera_datas_path"));
        mainConfig.setCameras(camerasConfig);

        // Parse LiDarWorkers
        JSONObject lidarObject = jsonObject.getJSONObject("LiDarWorkers");
        LidarConfigurations lidarConfig = new LidarConfigurations();
        JSONArray lidarArray = lidarObject.getJSONArray("LidarConfigurations");
        List<LiDarWorkerTracker> lidarConfigurations = new ArrayList<>();
        String lidarDataPath = lidarObject.getString("lidars_data_path");
        if (lidarDataPath.startsWith(".")) {
            lidarDataPath = configDir + lidarDataPath.substring(1);
        }
        lidarConfig.setLidarDatasPath(lidarDataPath);
        lidarConfig.setLidarConfigurations(lidarConfigurations);
        mainConfig.setLidars(lidarConfig);
        for (int i = 0; i < lidarArray.length(); i++) {
            JSONObject lidarWorkerObject = lidarArray.getJSONObject(i);
            LiDarWorkerTracker lidarWorkerConfig = new LiDarWorkerTracker(lidarWorkerObject.getInt("id"),
                    lidarWorkerObject.getInt("frequency"), new LinkedList<>(), lidarConfig.getLidarDatasPath());
            lidarConfigurations.add(lidarWorkerConfig);
        }

        // Parse Pose data path
        String poseDataPath = jsonObject.getString("poseJsonFile");
        if (poseDataPath.startsWith(".")) {
            poseDataPath = configDir + poseDataPath.substring(1);
        }
        mainConfig.setPosepath(poseDataPath);
        mainConfig.setTime(jsonObject.getInt("TickTime"));
        mainConfig.setDuration(jsonObject.getInt("Duration"));

        return mainConfig;
    }

    // -------------------Tests--------------------
    public static void main(String[] args) {
        try {
            MainConfiguration config = readConfiguration("C:\\Users\\alona\\SPL2\\example_input_2\\configuration_file.json");
            List<Camera> cameraConfigs = config.getCamera().getCamerasConfigurations();
            for (Camera c : cameraConfigs)
            {
                System.out.println(c.getId());
                System.out.println(c.getFrequency());
            }
            System.out.println();
            List<LiDarWorkerTracker> lidarConfigs = config.getLidar().getlidarConfigurations();
            for (LiDarWorkerTracker l : lidarConfigs)
            {
                System.out.println(l.getID());
                System.out.println(l.getFrequency());
            }
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}