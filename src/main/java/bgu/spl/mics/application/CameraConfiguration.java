package bgu.spl.mics.application;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;


public class CameraConfiguration {

    private List<Camera> camerasconfigurations;
    private String camera_datas_path;

    public CameraConfiguration()
    {
        this.camerasconfigurations = new LinkedList<Camera>();
        this.camera_datas_path = null;
    }
    // Getters and setters
    public List<Camera> getCamerasConfigurations() { return camerasconfigurations; }
    public void setCamerasConfigurations(List<Camera> CamerasConfigurations) { this.camerasconfigurations = CamerasConfigurations; }

    public String getCameraDatasPath() { return camera_datas_path; }
    public void setCameraDatasPath(String camera_datas_path) { this.camera_datas_path = camera_datas_path; }
}