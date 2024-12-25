package bgu.spl.mics.application.messages;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectEvent implements Event<Boolean> {
    
    private List<TrackedObject> trackedObject;

    public TrackedObjectEvent(){
        this.trackedObject = new LinkedList<TrackedObject>();
    }
    
    public TrackedObjectEvent(List<TrackedObject> trackedObject){
        this.trackedObject = trackedObject;
    }    

    public List<TrackedObject> getTrackedObject() {
        return trackedObject;
    }

    public void addTrackedObject(TrackedObject trackedObject) {
        this.trackedObject.add(trackedObject);
    }
}
