package bgu.spl.mics.application.objects;

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

    /**
     * Constructor for GPSIMU.
     * @param tick
     * @param status
     * @param poses
     */
    public GPSIMU(List<Pose> poses){
        this.currentTick = 0;
        this.currentStatus = STATUS.UP;
        this.prevPoses = poses;
    }
    //TODO: Add a ERROR status to the STATUS enum
    public GPSIMU(){
        this.currentTick = 0;
        this.currentStatus = STATUS.UP;
        this.prevPoses = new ArrayList<>();
    }
    /**
     * Return the current pose of the robot in time tick.
     * @param currentTick
     * @return Pose object, current pose of the robot.
     */
    public Pose getCurrentPose(int currentTick){
        setCurrentTick(currentTick);
        // if the current tick is equle than the size of the prevPoses list, the robot is down.
        if (prevPoses.size() == currentTick){
            this.currentStatus = STATUS.DOWN;
        }
        return prevPoses.get(currentTick-1);
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
