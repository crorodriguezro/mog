package solvers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import model.Activity;
import model.Resource;
import model.Schedule;

import java.util.List;
import java.util.stream.Collectors;

public class GreedySolver implements Solver {

  /**
   *
   */

  static int worldTime = 0;
  List<Activity> jobsInProgresses = new ArrayList<>();
  Set<Activity> availableActivities = new HashSet<>();
  Resource[] resources;


  private List<Activity> sequence = new ArrayList<>();

  public List<Activity> solve(Schedule schedule) {
    List<Activity> activities = Arrays.asList(schedule.getActivities());
    resources = schedule.getResources();
    Activity currentActivity = activities.get(0);

    availableActivities.add(currentActivity);
    while (availableActivities.size() > 0) {
      startNextJobs();
      pushWorld();
    }
    return sequence;
  }

  List<Activity> getDoableJobs(Collection<Activity> activities) {
    return activities.stream()
            .filter(job -> job.getPredecessors().size() == 0)
            .filter(availableResourcesPredicate)
            .collect(Collectors.toList());
  }

  Predicate<Activity> availableResourcesPredicate = job -> {
    int[] jobResources = job.getResources();
    for (int j = 0; j < jobResources.length; j++) {
      if (jobResources[j] > resources[j].getAmount()) {
        return false;
      }
    }
    return true;
  };

  void startNextJobs() {
    /*List<Activity> doableJobs = getDoableJobs(availableActivities);
    while (doableJobs.size() > 0){
      startJob(doableJobs.get(0));
      doableJobs = getDoableJobs(availableActivities);
    }*/
  }

  void startJob(Activity activity) {
    availableActivities.remove(activity);
    jobsInProgresses.add(activity);
    activity.start(worldTime);
    sequence.add(activity);
//    System.out.println("Available Resources:");
//    Arrays.asList(resources).stream().forEach(System.out::println);
    //System.out.println("Started Activity: " + activity);
//    System.out.println();
    int[] jobResources = activity.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources[i].getAmount() - jobResources[i];
      resources[i].setAmount(resourceAmount);
    }
  }

  static List<Activity> removePredecessor(Activity completedActivity) {
    for (Activity successor : completedActivity.getSuccessors()) {
      successor.removePredecessor(completedActivity.getId());
    }
    return completedActivity.getSuccessors();
  }

  Consumer<Activity> completeJob = completedJob -> {
//    System.out.println("Completed Activity: " + completedJob);
    int[] jobResources = completedJob.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources[i].getAmount() + jobResources[i];
      resources[i].setAmount(resourceAmount);
    }
    jobsInProgresses.remove(completedJob);
    List<Activity> successorActivities = removePredecessor(completedJob);
    availableActivities.addAll(successorActivities);
  };

  private void pushWorld() {
    Activity nextActivityToFinish = jobsInProgresses.stream()
        .min(Comparator.comparingInt(Activity::getFinishTime))
        .get();
    worldTime = nextActivityToFinish.getFinishTime();
    List<Activity> finishedActivities = jobsInProgresses.stream()
        .filter(job -> job.getFinishTime() == worldTime)
        .collect(Collectors.toList());
    finishedActivities.forEach(completeJob);
  }
}
