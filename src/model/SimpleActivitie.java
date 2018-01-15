package model;

public class SimpleActivitie {
    private int id;
    private int duration;
    private int startTime;
    private int finishTime;
    private int weight;

    public SimpleActivitie(Activity activity) {
        this.id = activity.getId();
        this.duration = activity.getDuration();
        this.startTime = activity.getStartTime();
        this.finishTime = activity.getFinishTime();
        this.weight = activity.getWeight();
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
        return "SimpleActivitie{" +
            "id=" + id +
            ", duration=" + duration +
            ", startTime=" + startTime +
            ", finishTime=" + finishTime +
            ", weight=" + weight +
            '}';
    }
}
