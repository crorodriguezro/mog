package model;

public class Schedule {

    private Job[] jobs;

    private Resource[] resources;

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
}