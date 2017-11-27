package solvers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import model.Job;
import model.Resource;
import model.Schedule;

import java.util.List;
import java.util.stream.Collectors;

public class GreedySolver2 {

  private static int worldTime = 0;
  private List<Job> jobsInProgress = new ArrayList<>();
  private Set<Job> availableJobs = new HashSet<>();
  private Resource[] resources;

  public void solve(Schedule schedule) {
    List<Job> jobs = Arrays.asList(schedule.getJobs());
    resources = schedule.getResources();
    Job currentJob = jobs.get(0);

    availableJobs.add(currentJob);
    while (availableJobs.size() > 0) {
      startNextJobs();
      pushWorld();
    }
  }

  private List<Job> getDoableJobs() {
    return availableJobs.stream()
            .filter(job -> job.getPredecessors().size() == 0)
            .filter(availableResourcesPredicate)
            .collect(Collectors.toList());
  }

  private Predicate<Job> availableResourcesPredicate = job -> {
    int[] jobResources = job.getResources();
    for (int j = 0; j < jobResources.length; j++) {
      if (jobResources[j] > resources[j].getAmount()) {
        return false;
      }
    }
    return true;
  };

  private void startNextJobs() {
    List<Job> doableJobs = getDoableJobs();
    while (doableJobs.size() > 0){
      startJob(doableJobs.get(0));
      availableJobs.remove(doableJobs.get(0));
      doableJobs = getDoableJobs();
    }
  }

  private void startJob(Job job) {
    jobsInProgress.add(job);
    job.start(worldTime);
//    System.out.println("Available Resources:");
//    Arrays.asList(resources).stream().forEach(System.out::println);
    System.out.println("Started Job: " + job);
//    System.out.println();
    int[] jobResources = job.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources[i].getAmount() - jobResources[i];
      resources[i].setAmount(resourceAmount);
    }
  }

  private static List<Job> removePredecessor(Job completedJob) {
    for (Job successor : completedJob.getSuccessors()) {
      successor.removePredecessor(completedJob.getId());
    }
    return completedJob.getSuccessors();
  }

  private Consumer<Job> completeJob = completedJob -> {
//    System.out.println("Completed Job: " + completedJob);
    int[] jobResources = completedJob.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources[i].getAmount() + jobResources[i];
      resources[i].setAmount(resourceAmount);
    }
    jobsInProgress.remove(completedJob);
    List<Job> successorJobs = removePredecessor(completedJob);
    availableJobs.addAll(successorJobs);
  };

  private void pushWorld() {
    Job nextJobToFinish = jobsInProgress.stream()
        .min(Comparator.comparingInt(Job::getFinishTime))
        .get();
    worldTime = nextJobToFinish.getFinishTime();
    List<Job> finishedJobs = jobsInProgress.stream()
        .filter(job -> job.getFinishTime() == worldTime)
        .collect(Collectors.toList());
    finishedJobs.forEach(completeJob);
  }
}
