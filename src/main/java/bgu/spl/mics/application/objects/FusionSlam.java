package bgu.spl.mics.application.objects;

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

    private LandMark[] landmarks; // צריך לבדוק האם זה מערך או רשימה
    private List<Pose> poses;

    private FusionSlam() {
        landmarks = new LandMark[0]; // 
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
    

    public LandMark[] getLandmarks() {
        return landmarks;
    }   

    public List<Pose> getPoses() {
        return poses;
    }   

    
    // Singleton instance holder
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
