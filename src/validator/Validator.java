package validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import model.Activity;
import model.Resource;
import model.SimpleActivitie;

/**
 * Esta clase valida las secuencias "S*" que se obtienen, basado en las restricciones que presenta cada
 * actividad, se deben cumplir las restricciones de precedencia y de recursos.
 */

public class Validator {

  private List<SimpleActivitie> simpleActivities;

  private List<Resource> resources;
  private List<Activity> sequence;
  private List<Activity> jobsInProgresses;
  private int worldTime;

  public Validator(Resource[] resources, List<Activity> sequence) {
    this.resources = Arrays.stream(resources)
        .map(Resource::new)
        .collect(Collectors.toList());
    this.sequence = sequence;
    this.jobsInProgresses = new ArrayList<>();
    this.worldTime = 0;
  }

  public List<SimpleActivitie> validate() {
//    System.out.println("Inicio");
    simpleActivities = new ArrayList<>();
    while (sequence.size() > 0) {
      if (sequence.get(0).getStartTime() != -1) {
        List<Activity> test = sequence.stream()
            .filter(job -> {
              return sequence.get(0).getStartTime() == job.getStartTime()
                  && job.getStartTime() >= worldTime;
            })
            .collect(Collectors.toList());
        for (Activity activity : test) {
          startJob(activity);
        }
        pushWorld();
      } else {
        break;
      }
    }
    sequence.forEach(job -> {
      job.setStartTime(-1);
      job.setFinishTime(-1);
    });

    while (sequence.size() > 0) {
      List<Activity> doableActivities = getDoableJobsByPredecessors(sequence).stream()
          .filter(doableJob -> jobsInProgresses.stream()
              .noneMatch(jip -> jip.getId() == doableJob.getId()))
          .collect(Collectors.toList());

      List<Activity> sequenceWithoutJobsInProgresses = sequence.stream()
          .filter(doableJob -> jobsInProgresses.stream()
              .noneMatch(jip -> jip.getId() == doableJob.getId()))
          .collect(Collectors.toList());

      boolean hasPredecessorsInProgress = false;
      for (int i = 0; i < doableActivities.size(); i++) {
        if (doableActivities.get(i).getId() != sequenceWithoutJobsInProgresses.get(i).getId()) {
          List<Activity> nextActivityPredecessors = sequenceWithoutJobsInProgresses.get(i).getPredecessors();
          int predecessorsInProgress = nextActivityPredecessors.stream()
              .filter(jobsInProgresses::contains)
              .toArray().length;

          if( predecessorsInProgress > 0){
            hasPredecessorsInProgress = true;
          } else {
            if (i < 1) {
              throw new RuntimeException("No sirve la secuencia");
            }
          }
          doableActivities = doableActivities.subList(0, i);
        }
      }
      if(!hasPredecessorsInProgress){
        doableActivities = getDoableJobsByPredecessorsAndResources(doableActivities);
        while (doableActivities.size() > 0) {
          Activity nextActivity = doableActivities.get(0);
          startJob(nextActivity);
          doableActivities = getDoableJobsByPredecessorsAndResources(doableActivities);
          doableActivities.remove(nextActivity);
        }
        if (jobsInProgresses.size() == 0) {
          System.out.println();
        }
      }
      pushWorld();
    }
    return simpleActivities;
  }

  List<Activity> getDoableJobsByPredecessors(Collection<Activity> activities) {
    return activities.stream()
        .filter(job -> job.getPredecessors().size() == 0)
        .collect(Collectors.toList());
  }

  List<Activity> getDoableJobsByPredecessorsAndResources(Collection<Activity> activities) {
    return activities.stream()
        .filter(job -> job.getPredecessors().size() == 0)
        .filter(availableResourcesPredicate)
        .collect(Collectors.toList());
  }

  Predicate<Activity> availableResourcesPredicate = job -> {
    int[] jobResources = job.getResources();
    for (int j = 0; j < jobResources.length; j++) {
      if (jobResources[j] > resources.get(j).getAmount()) {
        return false;
      }
    }
    return true;
  };

  void startJob(Activity activity) {
//    System.out.println("StartingJob: " + activity.getId());
    activity.start(worldTime);
    jobsInProgresses.add(activity);
    simpleActivities.add(new SimpleActivitie(activity));
    if (activity.getPredecessors().size() > 0) {
      throw new RuntimeException("El trabajo tiene predecesores");
    }
    int[] jobResources = activity.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources.get(i).getAmount() - jobResources[i];
      if (resourceAmount < 0) {
        throw new RuntimeException("No hay recursos");
      }
      resources.get(i).setAmount(resourceAmount);
    }
  }

  static List<Activity> removePredecessor(Activity completedActivity) {
    for (Activity successor : completedActivity.getSuccessors()) {
      successor.removePredecessor(completedActivity.getId());
    }
    return completedActivity.getSuccessors();
  }

  private void pushWorld() {
    Optional<Activity> nextFinishingJob = jobsInProgresses.stream()
        .filter(job -> job.getFinishTime() != -1)
        .min(Comparator.comparingInt(Activity::getFinishTime));

    if (nextFinishingJob.isPresent()) {
      worldTime = nextFinishingJob.get().getFinishTime();
    } else {
      throw new RuntimeException("No sirve la secuencia");
    }
    List<Activity> finishedActivities = jobsInProgresses.stream()
        .filter(job -> job.getFinishTime() == worldTime)
        .collect(Collectors.toList());
    //finishedActivities.forEach(completeJob);
    for (Activity activity : finishedActivities) {
      finishJob(activity);
    }
  }

  private void finishJob(Activity activity) {
    int[] jobResources = activity.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources.get(i).getAmount() + jobResources[i];
      resources.get(i).setAmount(resourceAmount);
    }
    activity.setFinishTime(worldTime);
    sequence.remove(activity);
    jobsInProgresses.remove(activity);
    removePredecessor(activity);
  }
}
