package solvers;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import model.Job;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class GreedySolverRandom extends GreedySolver {

  void startNextJobs() {
    Random random = new Random();
    // Random para escoger el metodo
    int method = random.nextInt(3);
    List<Job> doableJobs = getDoableJobs(availableJobs);
    // Si no hay trabajos que se puedan hacer nos salimos del metodo
    if (doableJobs.size() == 0) {
      return;
    }
    // Ordenamos los trabajos segun el metodo
    List<Job> sortedJobs = getSortedJobsByMethod(doableJobs, method);

    List<Job> shrinkList;
    int high = sortedJobs.size() + 1;
    int low = 1;
    int maxRandom = Integer.max(1, high - low);
    int shrinkListMaxIndex = random.nextInt(maxRandom) + low;
    // "shrinkListMaxIndex" representa el numero de items que vamos a coger
    // Sacamos una sublista escogiendo los primeros "shrinkListMaxIndex" items
    shrinkList = sortedJobs.subList(0, shrinkListMaxIndex);
    //Empezamos trabajos hasta que ya no queden mas
    while (shrinkList.size() > 0) {
      startJob(shrinkList.get(0));
      shrinkList.remove(shrinkList.get(0));
      doableJobs = getDoableJobs(shrinkList);
      shrinkList = getSortedJobsByMethod(doableJobs, method);
    }
  }

  private List<Job> getSortedJobsByMethod(List<Job> doableJobs, int method) {
    Comparator<Job> comparator;
    switch (method) {
      case 0: {
        comparator = Comparator.comparingInt(Job::getDuration);
        break;
      }
      case 1: {
        comparator = Comparator.comparingInt(job -> -job.getSuccessors().size());
        break;
      }
      case 2: {
        comparator = Comparator.comparingInt(job -> -job.getWeight());
        break;
      }
      default:
        throw new RuntimeException("Opción: " + method);
    }
    return doableJobs.stream()
        .sorted(comparator)
        .collect(Collectors.toList());
  }
}
