package validator;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import model.Job;
import model.Resource;
import model.Schedule;

public class Validator {

  private Resource[] resources;
  private List<Job> sequence;
  private int worldTime = 0;

  public Validator(Schedule schedule, List<Job> sequence) {
    this.resources = schedule.getResources();
    this.sequence = sequence;
  }

  public void validate() {
    while (sequence.size() > 0) {
      Job job = sequence.get(0);
      startJob(job);
      pushWorld();
    }
  }

  void startJob(Job job) {
    job.start(worldTime);
    if(job.getPredecessors().size() > 0) throw new RuntimeException("El trabajo tiene predecesores");
    int[] jobResources = job.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources[i].getAmount() - jobResources[i];
      if(resourceAmount < 0) throw new RuntimeException("No hay recursos");
      resources[i].setAmount(resourceAmount);
    }
  }

  static List<Job> removePredecessor(Job completedJob) {
    for (Job successor : completedJob.getSuccessors()) {
      successor.removePredecessor(completedJob.getId());
    }
    return completedJob.getSuccessors();
  }

  Consumer<Job> completeJob = completedJob -> {
    int[] jobResources = completedJob.getResources();
    for (int i = 0; i < jobResources.length; i++) {
      int resourceAmount = resources[i].getAmount() + jobResources[i];
      resources[i].setAmount(resourceAmount);
    }
    sequence.remove(completedJob);
    completedJob.setFinishTime(worldTime);
    removePredecessor(completedJob);
  };

  private void pushWorld() {
    worldTime = sequence.get(0).getFinishTime();
    List<Job> finishedJobs = sequence.stream()
        .filter(job -> job.getFinishTime() == worldTime)
        .collect(Collectors.toList());
    finishedJobs.forEach(completeJob);
  }
}
