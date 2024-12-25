package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.Camera.status;
import bgu.spl.mics.application.objects.Pose;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    // TODO: Define fields and methods.
    private int currentTick;
    private STATUS currentStatus;
    private List<Pose> prevPoses;

    public GPSIMU(int tick, STATUS status, List<Pose> poses){
        this.currentTick = 0;
        this.currentStatus = status;
        this.prevPoses = poses;
    }
    
    public GPSIMU(int tick){
        this.currentTick = 0;
        this.currentStatus = STATUS.UP;
        this.prevPoses = new ArrayList<>();
    }

    public void setCurrentTick(int tick){
        this.currentTick = tick;
    }

    public void setCurrentStatus(STATUS status){
        this.currentStatus = status;
    }

    public int getCurrentTick(){
        return currentTick;
    }

    public STATUS getCurrentStatus(){
        return currentStatus;
    }

    public List<Pose> getPrevPoses(){
        return prevPoses;
    }
}
