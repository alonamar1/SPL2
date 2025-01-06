package bgu.spl.mics.application;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.application.Configurations.MainConfiguration;
import bgu.spl.mics.application.Configurations.PoseData;
import bgu.spl.mics.application.Configurations.ReadConfiguration;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;

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
     * @param args Command-line arguments. The first argument is expected to be the
     *             path to the configuration file.
     */
    public static void main(String[] args) {
        MainConfiguration configFile = null;
        String path = null;
        
        // Check if the user gave a path to the configuration file
        try {
            path = args[0];
        } catch (Exception e) {
            System.out.println("No ARGUMENTS were given!!, USING DEFAULT CONFIGURATION FILE");
            path = "example_input_with_error\\configuration_file.json";
        }

        // Read the configuration file
        try {
            configFile = ReadConfiguration.readConfiguration(path);
        } catch (Exception e) {
            System.out.println("Can't Find the  config file!! :(");
        }
        if (configFile == null) {
            throw new IllegalAccessError("Can't Read config file!! :(");
        }

        // Get the directory of the configuration file
        String configDir = path.substring(0, path.lastIndexOf("\\"));

        // General SETUP:
        int cameraAmount = configFile.getCamera().getCamerasConfigurations().size();
        int LiDarWorkerAmount = configFile.getLidar().getlidarConfigurations().size();
        // make sure wait for initializing
        CountDownLatch lanch = new CountDownLatch(cameraAmount + LiDarWorkerAmount + 2);

        // initializing to Pose Service
        PoseData dateposinition = new PoseData();
        dateposinition.loadData(configFile.getPosepath());

        GPSIMU gpsimu = new GPSIMU(dateposinition.getPoses());
        PoseService poseService = new PoseService(gpsimu, cameraAmount, LiDarWorkerAmount, lanch);
        Thread poseThread = new Thread(poseService, "PoseService");
        poseThread.start();

        // initializing to Fusion Slam Service
        FusionSlam fusionObject = FusionSlam.getInstance();
        FusionSlamService fusionslam = new FusionSlamService(fusionObject, cameraAmount, LiDarWorkerAmount, configDir,
                lanch);
        Thread fusionThread = new Thread(fusionslam, "FusionSlam");
        fusionThread.start();

        // initializing to Cameras Services
        List<CameraService> listCameraServices = new LinkedList<>();
        List<Thread> threadsCamerasService = new LinkedList<>();
        // Create Cameras Services
        for (Camera cam : configFile.getCamera().getCamerasConfigurations()) {
            listCameraServices.add(new CameraService(cam, lanch));
        }
        // Create Threads
        for (CameraService camSer : listCameraServices) {
            threadsCamerasService.add(new Thread(camSer, "Camera" + camSer.getName()));
        }
        // start Threads
        for (Thread th : threadsCamerasService) {
            th.start();
        }

        // initializing to LiDar Services
        List<LiDarService> listLidarServices = new LinkedList<>();
        List<Thread> threadsLiDarsService = new LinkedList<>();
        // Create LiDars Services
        for (LiDarWorkerTracker liDar : configFile.getLidar().getlidarConfigurations()) {
            listLidarServices.add(new LiDarService(liDar, cameraAmount, lanch));
        }
        // Create Threads
        for (LiDarService liDarSer : listLidarServices) {
            threadsLiDarsService.add(new Thread(liDarSer, "LidarWorker" + liDarSer.getName()));
        }
        // start Threads
        for (Thread th : threadsLiDarsService) {
            th.start();
        }

        // waits for all the microservices to initialize, except Time Service
        try {
            lanch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // initializing to Time Service
        TimeService timeService = new TimeService(configFile.getTicktime(), configFile.getDuration());
        Thread timeServiceThread = new Thread(timeService, "TimeService");
        timeServiceThread.start();

        try {
            timeServiceThread.join();
        } catch (Exception e) {
            System.out.println("Time not finish");
        }

    }
}
