package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {

    private String ID;
    private String descreption;

    public DetectedObject(String ID, String descreption){
        this.ID = ID;
        this.descreption = descreption;
    }

    public String getID(){
        return ID;
    }

    public String getDescreption(){
        return descreption;
    }
    
}
