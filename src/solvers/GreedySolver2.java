package solvers;

import model.Job;
import model.Resource;
import model.Schedule;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GreedySolver2 {
    public static int currentTime = 0;

    public static void solve(Schedule schedule){
        Job[] jobs = schedule.getJobs();
        Resource[] resources = schedule.getResources();
        Job starterJob = jobs[0];
        List<Job> nextJobs = completeJob(starterJob);
        List<Job> jobsAux = filterJobs(nextJobs);

        Random r = new Random();
        int total = r.nextInt(3-0) + 0;;


        System.out.println();
    }

    private static List<Job> filterJobs(List<Job> nextJobs) {
        return nextJobs.stream().filter(job -> job.getPredecessors().size() == 0).collect(Collectors.toList());
    }

    private static List<Job> completeJob(Job completedJob) {
        System.out.println("Completed Job: " + completedJob.getId());
        return removePredecessor(completedJob);
    }

    private static List<Job> removePredecessor(Job completedJob) {
        for (Job successor: completedJob.getSuccessors()) {
            successor.removePredecessor(completedJob.getId());
        }
        return completedJob.getSuccessors();
    }
}
