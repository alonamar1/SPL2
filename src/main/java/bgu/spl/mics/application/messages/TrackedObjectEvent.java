package bgu.spl.mics.application.messages;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectEvent implements Event<Boolean> {

    private String LiDarWorkerTrackerid;
    private List<TrackedObject> trackedObject;
    private int time;

    public TrackedObjectEvent(String id, int time) {
        this.LiDarWorkerTrackerid = id;
        this.trackedObject = new LinkedList<TrackedObject>();
        this.time = time;
    }

    public TrackedObjectEvent(List<TrackedObject> trackedObject) {
        this.trackedObject = trackedObject;
    }

    public List<TrackedObject> getTrackedObject() {
        return trackedObject;
    }

    public void addTrackedObject(TrackedObject trackedObject) {
        this.trackedObject.add(trackedObject);
    }

    public int getTime() {
        return time;
    }

    public String getId() {
        return this.LiDarWorkerTrackerid;
    }
}
