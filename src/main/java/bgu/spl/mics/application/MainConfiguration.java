package bgu.spl.mics.application;

public class MainConfiguration {
    private CameraConfiguration cameras;
    private LidarConfigurations lidarWorkers;
    private PoseConfiguration poses;
    
    public MainConfiguration()
    {
        cameras = null;
    }

    public CameraConfiguration getCamera()
    {return cameras;}

    public LidarConfigurations getLidar()
    {return lidarWorkers;}

    public PoseConfiguration getPose()
    {return poses;}

    public void setCameras(CameraConfiguration camerasConfig)
    {
        this.cameras = camerasConfig;
    }
    public void setLidars(LidarConfigurations lidarConfig)
    {
        this.lidarWorkers = lidarConfig;
    }
    public void setPoses(PoseConfiguration poses)
    {
        this.poses = poses;
    }


}