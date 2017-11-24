package model;

import java.util.ArrayList;
import java.util.List;

public class Job {
    private int id;
    private int duration;
    private int start;
    private int[] resources;
    private List<Job> predecessors = new ArrayList<>();
    private List<Job> successors = new ArrayList<>();

    public Job(int id) {
        this.id = id;
    }

    public Job(int id, int duration, int start, int[] resources) {
        this.id = id;
        this.duration = duration;
        this.start = start;
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

    public int[] getResources() {
        return resources;
    }

    public void setResources(int[] resources) {
        this.resources = resources;
    }

    public List<Job> getPredecessors() {
        return predecessors;
    }

    public List<Job> getSuccessors() {
        return successors;
    }

    public void addPredecessor(Job predecessor) {
        predecessors.add(predecessor);
    }

    public void addSuccessor(Job successor) {
        successors.add(successor);
    }

    public void removePredecessor(int idPredecesor) {
        for (int i = 0; i < predecessors.size(); i++) {
            if(predecessors.get(i).getId() == idPredecesor){
                predecessors.remove(predecessors.get(i));
            }
        }
    }
}
