package model;


import java.util.Arrays;

/**
 * Defines a task in a project. Task is an atomic element of any project -
 * project consists of tasks that have to be performed to achieve given goal.
 * Task is described by skill required, duration (in hours) and successors
 * (precedence relations).
 * <p>
 * For easier implementation, task also stores
 * information about resource assigned to it, its start time in a project. If id
 * of assigned resource and start time is -1, it means that task is not assigned
 * to any resource and not placed anywhere in the timeline.
 */
public class Task implements Comparable, Cloneable {

    private int id;
    private Skill requiredSkill;
    private int duration;
    private int start;
    private int[] successors;
    private int resourceId;

    public Task(int id, Skill requiredSkill, int duration, int start,
                int[] successors, int resourceId) {
        this.id = id;
        this.requiredSkill = requiredSkill;
        this.duration = duration;
        this.start = start;
        this.successors = successors;
        this.resourceId = resourceId;
    }

    public Task(int id, Skill skill, int duration, int[] successors) {
        this(id, skill, duration, -1, successors, -1);
    }

    public Task(int id, Skill skill, int duration, int[] successors, int resourceId) {
        this(id, skill, duration, -1, successors, resourceId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Skill getRequiredSkills() {
        return requiredSkill;
    }

    public void setRequiredSkills(Skill requiredSkill) {
        this.requiredSkill = requiredSkill;
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

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String toString() {
        /*String p = "";
        for (int i : successors) {
            p += i + " ";
        }
        return id + ", duration: " + duration + ", start: " + start
                + ", required skills: " + requiredSkill
                + ", successors: " + p;*/
        return String.valueOf(resourceId);
    }

    /**
     * Compare two tasks.
     *
     * @param t task to compare to
     * @return true if this task is equal to task t
     */
    @Override
    public boolean equals(Object t) {
        if (!(t instanceof Task)) {
            return false;
        }
        Task task = (Task) t;
        return duration == task.duration &&
                id == task.id &&
                Arrays.equals(successors, task.successors) &&
                requiredSkill == task.requiredSkill;
    }

    /**
     * Compares start times.
     *
     * @param o object to compare
     * @return -1 if this task start earlier, 1 if
     * <code>o</code> start earlier, 0 if they start
     * at the same time
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Task)) {
            throw new IllegalArgumentException("Parameter is not a Task");
        }
        return Integer.compare(start, ((Task) o).start);
    }

    public Task clone() {
        return new Task(id, requiredSkill, duration,start, successors,resourceId);
    }

}
