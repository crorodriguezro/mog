package schedule;

import model.Schedule;
import model.Solution;
import project.Activity;
import project.Resource;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Sequence {

  private static int worldTime;
  private List<Activity> activitiesInProgresses;
  Set<Activity> availableActivities;
  Resource[] resources;

  private List<Activity> sequence;

  public List<Activity> solve(Schedule schedule) {
    return generateSequence(schedule);
  }

  public List<Activity> generateSequence(Schedule schedule) {
    worldTime = 0;
    sequence = new ArrayList<>();
    activitiesInProgresses = new ArrayList<>();
    availableActivities = new HashSet<>();
    List<Activity> activities = Arrays.asList(schedule.getActivities());
    resources = schedule.getResources();
    Activity currentActivity = activities.get(0);

    availableActivities.add(currentActivity);
    while (availableActivities.size() > 0) {
      startNextActivities();
      pushWorld();
    }
    return sequence;
  }

  List<Activity> getDoableActivities(Collection<Activity> activities) {
    return activities.stream()
            .filter(activity -> activity.getPredecessors().size() == 0)
            .filter(availableResourcesPredicate)
            .collect(Collectors.toList());
  }

  Predicate<Activity> availableResourcesPredicate = activity -> {
    int[] activityResources = activity.getResources();
    for (int j = 0; j < activityResources.length; j++) {
      if (activityResources[j] > resources[j].getAmount()) {
        return false;
      }
    }
    return true;
  };

  abstract void startNextActivities();

  void startActivity(Activity activity) {
    availableActivities.remove(activity);
    activitiesInProgresses.add(activity);
    activity.start(worldTime);
    sequence.add(activity);
//    System.out.println("Available Resources:");
//    Arrays.asList(resources).stream().forEach(System.out::println);
    //System.out.println("Started Activity: " + activity);
//    System.out.println();
    int[] activityResources = activity.getResources();
    for (int i = 0; i < activityResources.length; i++) {
      int resourceAmount = resources[i].getAmount() - activityResources[i];
      resources[i].setAmount(resourceAmount);
    }
  }

  static List<Activity> removePredecessor(Activity completedActivity) {
    for (Activity successor : completedActivity.getSuccessors()) {
      successor.removePredecessor(completedActivity.getId());
    }
    return completedActivity.getSuccessors();
  }

  Consumer<Activity> completeActivity = completedActivity -> {
    int[] activityResources = completedActivity.getResources();
    for (int i = 0; i < activityResources.length; i++) {
      int resourceAmount = resources[i].getAmount() + activityResources[i];
      resources[i].setAmount(resourceAmount);
    }
    activitiesInProgresses.remove(completedActivity);
    List<Activity> successorActivities = removePredecessor(completedActivity);
    availableActivities.addAll(successorActivities);
  };

  private void pushWorld() {
    Activity nextActivityToFinish = activitiesInProgresses.stream()
        .min(Comparator.comparingInt(Activity::getFinishTime))
        .get();
    worldTime = nextActivityToFinish.getFinishTime();
    List<Activity> finishedActivities = activitiesInProgresses.stream()
        .filter(activity -> activity.getFinishTime() == worldTime)
        .collect(Collectors.toList());
    finishedActivities.forEach(completeActivity);
  }

  public static List<Solution> getNonDominated(List<Solution> allSolutions) {
    List<Solution> bestSolutions;
    List<Solution> listWithoutDuplicates = allSolutions.stream()
            .distinct()
            .collect(Collectors.toList());

    bestSolutions = listWithoutDuplicates.stream().filter(candidateSolution -> {
      boolean nonDominant = listWithoutDuplicates.stream()
              .filter(solution -> !solution.equals(candidateSolution))
              .anyMatch(solution -> {
                boolean bestOrEqualCmax = solution.getcMax() <= candidateSolution.getcMax();
                boolean bestOrEqualTwst = solution.getTwst() <= candidateSolution.getTwst();
                return bestOrEqualCmax && bestOrEqualTwst;
              });
      return !nonDominant;
    }).collect(Collectors.toList());

    bestSolutions.forEach(solution -> {
      System.out.println(solution.getcMax() + "\t" + solution.getTwst());
    });
    return bestSolutions;
  }
}
