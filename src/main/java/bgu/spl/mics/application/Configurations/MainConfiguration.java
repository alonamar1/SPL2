package bgu.spl.mics.application.Configurations;

public class MainConfiguration {
    private CameraConfiguration cameras;
    private LidarConfigurations lidarWorkers;
    private String posePath;
    private int tickTime;
    private int duration;
    
    public MainConfiguration()
    {
        cameras = null;
        lidarWorkers = null;
        posePath = null;
        tickTime = 0;
        duration = 0;
    }

    public CameraConfiguration getCamera()
    {return cameras;}

    public LidarConfigurations getLidar()
    {return lidarWorkers;}

    public String getPosepath()
    {return posePath;}

    public int getTicktime()
    {
        return tickTime;
    }
    public int getDuration()
    {
        return duration;
    }

    public void setCameras(CameraConfiguration camerasConfig)
    {
        this.cameras = camerasConfig;
    }
    public void setLidars(LidarConfigurations lidarConfig)
    {
        this.lidarWorkers = lidarConfig;
    }
    public void setPosepath(String path )
    {
        this.posePath = path;
    }
    public void setTime(int time)
    {
        this.tickTime = time;
    }

    public void setDuration(int dur)
    {
        this.duration = dur;
    }


}