package bgu.spl.mics.application.Configurations;

import bgu.spl.mics.application.objects.Pose;

public class PoseConfiguration {

    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    public PoseConfiguration(String path, int time, int duration)
    {
        this.poseJsonFile = path;
        this.TickTime = time;
        this.Duration = duration;
    }
    public int getTime()
    {
        return TickTime;
    }
    public int getDuration()
    {
        return Duration;
    }

    
}
