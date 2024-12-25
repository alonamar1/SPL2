package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

public class TrackedObjectEvent implements Event<Boolean> {
    
    private List<TrackedObject> trackedObject;

    public TrackedObjectEvent(List<TrackedObject> trackedObject){
        this.trackedObject = trackedObject;
    }    

    public List<TrackedObject> getTrackedObject() {
        return trackedObject;
    }
}
