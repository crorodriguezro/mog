package solvers;

import model.Job;
import model.Resource;
import model.Schedule;

import java.util.Arrays;

public class GreedySolver {
    public static int currentTime = 0;

    public static void solve(Schedule schedule){
        Job[] jobs = schedule.getJobs();
        Resource[] resources = schedule.getResources();
        Job starterJob = jobs[0];
        Job[] nextJobs = completeJob(starterJob, jobs);
        Job[] nextJobs2 = completeJob(nextJobs[0], jobs);
        Job[] nextJobs3 = completeJob(nextJobs[1], jobs);
        Job[] nextJobs4 = completeJob(nextJobs[2], jobs);
        System.out.println();
    }

    private static Job[] completeJob(Job completedJob, Job[] jobs) {
        System.out.println("Completed Job: " + completedJob.getId());
        return removePredecessor(completedJob, jobs);

    }

    private static Job[] removePredecessor(Job completedPredecesor, Job[] jobs) {
        for (Job successor: completedPredecesor.getSuccessors()) {
            successor.removePredecessor(completedPredecesor.getId());
        }
        return Arrays.stream(jobs).filter(j -> j.getPredecessors().size()==0).toArray(Job[]::new);
    }
}
