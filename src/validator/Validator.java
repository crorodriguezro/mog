package validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import model.Job;
import model.Resource;
import model.SimpleJob;

public class Validator {

  private List<SimpleJob> simpleJobs;

  private List<Resource> resources;
  private List<Job> sequence;
  private List<Job> jobsInProgress;
  private int worldTime;

  public Validator(Resource[] resources, List<Job> sequence) {
    this.resources = Arrays.stream(resources)
        .map(Resource::new)
        .collect(Collectors.toList());
    this.sequence = sequence;
    this.jobsInProgress = new ArrayList<>();
    this.worldTime = 0;
  }

  public List<SimpleJob> validate() {
//    System.out.println("Inicio");
    simpleJobs = new ArrayList<>();
    while (sequence.size() > 0) {
      if (sequence.get(0).getStartTime() != -1) {
        List<Job> test = sequence.stream()
            .filter(job -> {
              return sequence.get(0).getStartTime() == job.getStartTime()
                  && job.getStartTime() >= worldTime;
            })
            .collect(Collectors.toList());
        for (Job job : test) {
          startJob(job);
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
      List<Job> doableJobs = getDoableJobsByPredecessors(sequence).stream()
          .filter(doableJob -> jobsInProgress.stream()
              .noneMatch(jip -> jip.getId() == doableJob.getId()))
          .collect(Collectors.toList());

      List<Job> sequenceWithoutJobsInProgress = sequence.stream()
          .filter(doableJob -> jobsInProgress.stream()
              .noneMatch(jip -> jip.getId() == doableJob.getId()))
          .collect(Collectors.toList());

      boolean hasPredecessorsInProgress = false;
      for (int i = 0; i < doableJobs.size(); i++) {
        if (doableJobs.get(i).getId() != sequenceWithoutJobsInProgress.get(i).getId()) {
          List<Job> nextJobPredecessors = sequenceWithoutJobsInProgress.get(i).getPredecessors();
          int predecessorsInProgress = nextJobPredecessors.stream()
              .filter(jobsInProgress::contains)
              .toArray().length;

          if( predecessorsInProgress > 0){
            hasPredecessorsInProgress = true;
          } else {
            if (i < 1) {
              throw new RuntimeException("No sirve la secuencia");
            }
          }
          doableJobs = doableJobs.subList(0, i);
        }
      }
      if(!hasPredecessorsInProgress){
        doableJobs = getDoableJobsByPredecessorsAndResources(doableJobs);
        while (doableJobs.size() > 0) {
          Job nextJob = doableJobs.get(0);
          startJob(nextJob);
          doableJobs = getDoableJobsByPredecessorsAndResources(doableJobs);
          doableJobs.remove(nextJob);
        }
        if (jobsInProgress.size() == 0) {
          System.out.println();
        }
      }
      pushWorld();
    }
    return simpleJobs;
  }

  List<Job> getDoableJobsByPredecessors(Collection<Job> jobs) {
    return jobs.stream()
        .filter(job -> job.getPredecessors().size() == 0)
        .collect(Collectors.toList());
  }

  List<Job> getDoableJobsByPredecessorsAndResources(Collection<Job> jobs) {
    return jobs.stream()
        .filter(job -> job.getPredecessors().size() == 0)
        .filter(availableResourcesPredicate)
        .collect(Collectors.toList());
  }

  Predicate<Job> availableResourcesPredicate = job -> {
    int[] jobResources = job.getResources();
    for (int j = 0; j < jobResources.length; j++) {
      if (jobResources[j] > resources.get(j).getAmount()) {
        return false;
      }
    }
    return true;
  };

  void startJob(Job job) {
//    System.out.println("StartingJob: " + job.getId());
    job.start(worldTime);
    jobsInProgress.add(job);
    simpleJobs.add(new SimpleJob(job));
    if (job.getPredecessors().size() > 0) {
      throw new RuntimeException("El trabajo tiene predecesores");
    }
    int[] jobResources = job.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources.get(i).getAmount() - jobResources[i];
      if (resourceAmount < 0) {
        throw new RuntimeException("No hay recursos");
      }
      resources.get(i).setAmount(resourceAmount);
    }
  }

  static List<Job> removePredecessor(Job completedJob) {
    for (Job successor : completedJob.getSuccessors()) {
      successor.removePredecessor(completedJob.getId());
    }
    return completedJob.getSuccessors();
  }

  private void pushWorld() {
    Optional<Job> nextFinishingJob = jobsInProgress.stream()
        .filter(job -> job.getFinishTime() != -1)
        .min(Comparator.comparingInt(Job::getFinishTime));

    if (nextFinishingJob.isPresent()) {
      worldTime = nextFinishingJob.get().getFinishTime();
    } else {
      throw new RuntimeException("No sirve la secuencia");
    }
    List<Job> finishedJobs = jobsInProgress.stream()
        .filter(job -> job.getFinishTime() == worldTime)
        .collect(Collectors.toList());
    //finishedJobs.forEach(completeJob);
    for (Job job : finishedJobs) {
      finishJob(job);
    }
  }

  private void finishJob(Job job) {
    int[] jobResources = job.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources.get(i).getAmount() + jobResources[i];
      resources.get(i).setAmount(resourceAmount);
    }
    job.setFinishTime(worldTime);
    sequence.remove(job);
    jobsInProgress.remove(job);
    removePredecessor(job);
  }
}
