package schedule;

import com.rits.cloning.Cloner;
import model.Schedule;
import model.Solution;
import project.Activity;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Spea2Sequence extends Sequence {
  private static Cloner cloner = new Cloner();

  private static final Map<Integer, Integer> cambiarNombre;
  static
  {
    cambiarNombre = new HashMap<>();
    cambiarNombre.put(50, 500);
    cambiarNombre.put(75, 500);
    cambiarNombre.put(100, 600);
    cambiarNombre.put(125, 600);
    cambiarNombre.put(150, 750);
    cambiarNombre.put(175, 900);
    cambiarNombre.put(200, 1000);
  }

  @Override
  public List<Solution> getSolutions(Schedule schedule) {
    int activitiesCount = schedule.getActivities().length;
    int sequencesTotal;
    if (activitiesCount <= 50) {
      sequencesTotal = cambiarNombre.get(50);
    } else if (activitiesCount <= 75) {
      sequencesTotal = cambiarNombre.get(75);
    } else if (activitiesCount <= 100) {
      sequencesTotal = cambiarNombre.get(100);
    } else if (activitiesCount <= 125) {
      sequencesTotal = cambiarNombre.get(125);
    } else if (activitiesCount <= 150) {
      sequencesTotal = cambiarNombre.get(150);
    } else if (activitiesCount <= 175) {
      sequencesTotal = cambiarNombre.get(175);
    } else {
      sequencesTotal = cambiarNombre.get(200);
    }

    List<Solution> solutions = new ArrayList<>();
    Cloner cloner=new Cloner();
    for (int i = 0; i < sequencesTotal; i++) {
      Schedule clone = cloner.deepClone(schedule);
      List<Activity> sequence = generateSequence(clone);
      solutions.add(createSolution(sequence));
    }

    List<Solution> nonDominatedSolutions = getNonDominated(solutions);

    double k = Math.pow(solutions.size() + nonDominatedSolutions.size(), 0.5);
    for (Solution solution : solutions) {
      List<Solution> dominatedBy = nonDominatedSolutions.stream()
              .filter(nonDominatedSolition -> !nonDominatedSolition.equals(solution))
              .filter(nonDominatedSolution -> solution.getcMax() >= nonDominatedSolution.getcMax() && solution.getTwst() >= nonDominatedSolution.getTwst())
              .collect(Collectors.toList());

      double distances[] = new double[dominatedBy.size()];
      for (int j = 0; j < dominatedBy.size(); j++) {
        Solution nonDominatedSolution = dominatedBy.get(j);
        double cMaxSquare = Math.pow(nonDominatedSolution.getcMax() - solution.getcMax(), 2);
        double twstSquare = Math.pow(nonDominatedSolution.getTwst() - solution.getTwst(), 2);
        distances[j] = Math.pow(cMaxSquare + twstSquare, 0.5);
        solution.setDominatedByDistance(distances);
      }
      solution.setDominatedBy(dominatedBy);
      BigDecimal density = new BigDecimal(1 / (Math.pow(getBiggestDistance(distances), k) + 2));
      BigDecimal fitness = density.add(new BigDecimal(dominatedBy.size()));
      if (fitness.compareTo(new BigDecimal(0.5)) == 0) {
        System.out.println();
      }
      solution.setFitness(fitness);
    }
//    solutions.forEach(solution -> {
//            System.out.println(solution.getcMax() + "\t" + solution.getTwst());
//        });
    solutions.sort(Comparator.comparing(Solution::getFitness));

    return solutions;
  }

  public List<Solution> getSolutions2(Schedule schedule, List<Solution> solutions) {
    Cloner cloner = new Cloner();
    List<Solution> clonedSolutions = cloneSolution(solutions);
    List<Solution> nonDominatedSolutions = getNonDominated(clonedSolutions);
    double k = Math.pow(clonedSolutions.size() + nonDominatedSolutions.size(), 0.5);
    for (Solution solution : clonedSolutions) {
      List<Solution> dominatedBy = nonDominatedSolutions.stream()
              .filter(nonDominatedSolition -> !nonDominatedSolition.equals(solution))
              .filter(nonDominatedSolution -> solution.getcMax() >= nonDominatedSolution.getcMax() && solution.getTwst() >= nonDominatedSolution.getTwst())
              .collect(Collectors.toList());

      double distances[] = new double[dominatedBy.size()];
      for (int j = 0; j < dominatedBy.size(); j++) {
        Solution nonDominatedSolution = dominatedBy.get(j);
        double cMaxSquare = Math.pow(nonDominatedSolution.getcMax() - solution.getcMax(), 2);
        double twstSquare = Math.pow(nonDominatedSolution.getTwst() - solution.getTwst(), 2);
        distances[j] = Math.pow(cMaxSquare + twstSquare, 0.5);
        solution.setDominatedByDistance(distances);
      }
      solution.setDominatedBy(dominatedBy);
      BigDecimal density = new BigDecimal(1 / (Math.pow(getBiggestDistance(distances), k) + 2));
      BigDecimal fitness = density.add(new BigDecimal(dominatedBy.size()));
      if (fitness.compareTo(new BigDecimal(0.5)) == 0) {
        System.out.println();
      }
      solution.setFitness(fitness);
    }
//    solutions.forEach(solution -> {
//            System.out.println(solution.getcMax() + "\t" + solution.getTwst());
//        });
    clonedSolutions.sort(Comparator.comparing(Solution::getFitness));

    return clonedSolutions;
  }

  double getBiggestDistance(double distances[]) {
    if(distances.length == 0) return 0;
    double maxDistance = Arrays.stream(distances).max().getAsDouble();
    return maxDistance;
  }

  /**
   * Esta clase realiza las acciones para obtener la secuencia "S" por el modelo MOG
   */
  void startNextActivities() {
    Random random = new Random();
    /* Random para escoger la regla de prioridad
        Determine randomly a priority rule;
    (0) Lower duration: a solution s is generated by sequencing
        activities in non-decreasing order of the value of its duration;
    (2) Bigger number of successors activities: a solution s is generated
        by sequencing activities in non-increasing order of its numbers
        of successors activities;
    (3) Lower weight: a solution s is generated by sequencing activities
        in non-decreasing order of the value of its weight.
     */
    int method = random.nextInt(3);
    List<Activity> doableActivities = getDoableActivities(availableActivities);
    // Si no hay trabajos que se puedan hacer nos salimos del metodo
    if (doableActivities.size() == 0) {
      return;
    }

    int[] resourcesNeeded = new int[resources.length];
    for (int i = 0; i < resourcesNeeded.length; i++) {
      int finalI = i;
      resourcesNeeded[finalI] = doableActivities.stream()
          .mapToInt(activity -> activity.getResources()[finalI]).sum();
    }

    if(areAvailableResources(resourcesNeeded)){
      doableActivities.forEach(this::startActivity);
      return;
    }

    int activityTodoIndex;
    while (doableActivities.size() > 0) {
      activityTodoIndex = random.nextInt(doableActivities.size());
      startActivity(doableActivities.get(activityTodoIndex));
      doableActivities.remove(doableActivities.get(activityTodoIndex));
      // Vamos a quitar de la lista "shrinkList" el trabajo iniciado
      doableActivities = getDoableActivities(doableActivities);

      // Si ya no hay trabajos que se puedan haccer entoncnes no sasalimos del ciclo
      if(doableActivities.size() == 0){
        break;
      }
    }
  }

  private boolean areAvailableResources(int[] resources) {
    for (int j = 0; j < resources.length; j++) {
      if (resources[j] > this.resources[j].getAmount()) {
        return false;
      }
    }
    return true;
  }

  public static boolean isBetween(int x, int lower, int upper) {
    return lower <= x && x <= upper;
  }

  private static List<Solution> getNonDominated(List<Solution> allSolutions) {
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

  private static List<Solution> cloneSolution(List<Solution> solutions) {
    List<Solution> clonedSolutions = new ArrayList<>();
    for (Solution solution : solutions) {
      clonedSolutions.add(cloner.deepClone(solution));
    }
    return clonedSolutions;
  }
}
