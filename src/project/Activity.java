package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Esta clase almacena cada actividad con todas sus caracteristicas, id, duracion, tiempo de inicio, tiempo de finalizacion,
 * recursos, peso, sucesores y predecesores
 */
public class Activity {
    private int id;
    private int duration;
    private int startTime;
    private int finishTime;
    private int[] resources;
    private int weight;
    private List<Activity> predecessors = new ArrayList<>();
    private List<Activity> successors = new ArrayList<>();
    public Activity(int id) {
        this.id = id;
//        weight = random.nextInt(1000);
    }
    private static Random random = new Random();

    public Activity(int id, int duration, int startTime, int[] resources) {
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.resources = resources;
//        weight = random.nextInt(1000);
    }

    public Activity(Activity activity) {
        this.id = activity.id;
        this.duration = activity.duration;
        this.startTime = activity.startTime;
        this.finishTime = activity.finishTime;
        this.resources = activity.resources;
        this.weight = activity.weight;
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

    public List<Activity> getPredecessors() {
        return predecessors;
    }

    public List<Activity> getSuccessors() {
        return successors;
    }

    public void addSuccessor(Activity successor) {
        successors.add(successor);
        successor.predecessors.add(this);
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void removePredecessor(int idPredecessor) {
        for (int i = 0; i < predecessors.size(); i++) {
            if (predecessors.get(i).getId() == idPredecessor) {
                predecessors.remove(predecessors.get(i));
            }
        }
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Activity{" +
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
        Activity activity = (Activity) o;
        return id == activity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
