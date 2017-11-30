package model;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private Job[] jobs;

    private Resource[] resources;

    private List<Job> sequence = new ArrayList<>();

    public Schedule(Job[] jobs, Resource[] resources) {
        this.jobs = jobs;
        this.resources = resources;
    }

    public Job[] getJobs() {
        return jobs;
    }

    public void setJobs(Job[] jobs) {
        this.jobs = jobs;
    }

    public Resource[] getResources() {
        return resources;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    public List<Job> getSequence() {
        return sequence;
    }

    public void addJobToSequence(Job job) {
        this.sequence.add(job);
    }
}
