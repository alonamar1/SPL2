package bgu.spl.mics.application;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;


public class LidarConfigurations {

    private List<LiDarWorkerTracker> LiDarConfigurations;
    private String camera_datas_path;

    public LidarConfigurations()
    {
        this.LiDarConfigurations = null;
        this.camera_datas_path = null;
    }

    // Getters and setters
    public List<LiDarWorkerTracker> getlidarConfigurations() { return LiDarConfigurations; }
    public void setLidarConfigurations(List<LiDarWorkerTracker> LidarConfigurations) { this.LiDarConfigurations = LidarConfigurations; }

    public String getLidarDatasPath() { return camera_datas_path; }
    public void setLidarDatasPath(String camera_datas_path) { this.camera_datas_path = camera_datas_path; }
}