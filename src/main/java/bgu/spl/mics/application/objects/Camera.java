package bgu.spl.mics.application.objects;
import java.util.List;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {

    public enum status {Up, Down, Error;}

    private int ID;
    private int frequency;
    private List<StampedDetectedObjects> detectedObjectsList;
    private status status;

    public Camera(int id, int freq, status status, List<StampedDetectedObjects> list){
        ID=id;
        frequency=freq;
        this.status=status;
        detectedObjectsList = list;
    }
    


}
