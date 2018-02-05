package model;

import project.Activity;
import project.Resource;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private Activity[] activities;

    private Resource[] resources;

    private List<Activity> sequence = new ArrayList<>();

    public Schedule(Activity[] activities, Resource[] resources) {
        this.activities = activities;
        this.resources = resources;
    }

    public Activity[] getActivities() {
        return activities;
    }

    public void setActivities(Activity[] activities) {
        this.activities = activities;
    }

    public Resource[] getResources() {
        return resources;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    public List<Activity> getSequence() {
        return sequence;
    }

    public void addActivityToSequence(Activity activity) {
        this.sequence.add(activity);
    }
}
