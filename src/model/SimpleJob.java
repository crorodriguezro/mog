package model;

public class SimpleJob {
    private int id;
    private int duration;
    private int startTime;
    private int finishTime;
    private int weight;

    public SimpleJob(Job job) {
        this.id = job.getId();
        this.duration = job.getDuration();
        this.startTime = job.getStartTime();
        this.finishTime = job.getFinishTime();
        this.weight = job.getWeight();
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

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "SimpleJob{" +
            "id=" + id +
            ", duration=" + duration +
            ", startTime=" + startTime +
            ", finishTime=" + finishTime +
            ", weight=" + weight +
            '}';
    }
}
