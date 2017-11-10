package model;

public class Job {
    private int id;
    private int duration;
    private int start;
    private int[] successors;
    private int[] resources;

    public Job(int id, int duration, int start, int[] successors, int[] resources) {
        this.id = id;
        this.duration = duration;
        this.start = start;
        this.successors = successors;
        this.resources = resources;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int[] getSuccessors() {
        return successors;
    }

    public void setSuccessors(int[] successors) {
        this.successors = successors;
    }
}
