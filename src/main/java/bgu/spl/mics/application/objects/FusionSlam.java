package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
 */
public class FusionSlam {

    private List<LandMark> landmarks;
    private List<Pose> poses;

    private FusionSlam() {
        landmarks = new LinkedList<>();
        poses = null;
    }

    /**
     * Returns the single instance of FusionSlam.
     *
     * @return The single instance of FusionSlam.
     */

    public static FusionSlam getInstance() {
        return FusionSlamHolder.getInstance();
    }

    public List<LandMark> getLandmarks() {
        return landmarks;
    }

    public List<Pose> getPoses() {
        return poses;
    }

    /*
     * Singleton pattern to ensure a single instance of FusionSlam exists.
     */
    private static class FusionSlamHolder {
        private static FusionSlam instance;

        private FusionSlamHolder() {
            instance = new FusionSlam();
        }

        public static FusionSlam getInstance() {
            return instance;
        }
    }
}
