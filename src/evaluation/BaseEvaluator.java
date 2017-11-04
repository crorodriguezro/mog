package evaluation;

import model.Resource;
import model.Schedule;
import model.Task;

/**
 * Abstract evaluator class. All evaluators inherit from it.
 * Has cost, duration functions along with abstract <code>evaluate()</code>
 */
abstract public class BaseEvaluator {

    private Schedule schedule;

    public BaseEvaluator(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * Abstract <code>evaluate()</code>. Each evaluator
     * must define a body for this function.
     *
     * @return evaluation value
     */
    abstract public double evaluate();

    /**
     * Creates a copy of this evaluator with a new schedule
     *
     * @param schedule schedule to use
     * @return new evaluator of the same type, but
     * with the new schedule
     */
    abstract public BaseEvaluator getCopy(Schedule schedule);

    /**
     * Allows to differentiate evaluators
     *
     * @return type from <code>EvaluatorType</code>
     */
    abstract public EvaluatorType getType();

    /**
     * Gets total duration of the project, which is the latest finish
     * date of all resources.
     *
     * @return total duration of the project
     */
    public int getDuration() {
        int result = 0;
        Resource[] resources = schedule.getResources();
        for (Resource r : resources) {
            if (r.getFinish() > result) {
                result = r.getFinish();
            }
        }
        return result;
    }

    /**
     * Sums duration of all task of the schedule.
     *
     * @return maximum possible duration of the schedule
     */
    public int getMaxDuration() {
        int duration = 0;
        for (Task t : schedule.getTasks()) {
            duration += t.getDuration();
        }
        return duration;
    }

    /**
     * Calculates normalized duration by dividing duration by max duration.
     *
     * @return normalized duration of the schedule
     */
    public double getDurationNormalized() {
        return (double) getDuration() / (double) getMaxDuration();
    }
}
