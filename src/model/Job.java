package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Job {
    private int id;
    private int duration;
    private int startTime;
    private int finishTime;
    private int[] resources;
    private int weight;
    private List<Job> predecessors = new ArrayList<>();
    private List<Job> successors = new ArrayList<>();
    public Job(int id) {
        this.id = id;
    }

    private static Random random = new Random();

    public Job(int id, int duration, int startTime, int[] resources) {
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.resources = resources;
        weight = random.nextInt(1000);
    }

    public void start(int t) {
        this.startTime = t;
        this.finishTime = t + duration;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getWeight() {
        return weight;
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
            if (predecessors.get(i).getId() == idPredecesor) {
                predecessors.remove(predecessors.get(i));
            }
        }
    }

    @Override
    public String toString() {
        return "Job{" +
            "id=" + id +
            ", startTime=" + startTime +
            ", finishTime=" + finishTime +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Job job = (Job) o;
        return id == job.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
