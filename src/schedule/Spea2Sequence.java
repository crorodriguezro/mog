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

  /**
   * Numero de secuencias de la poblacion inicial
   */
  private static final Map<Integer, Integer> initialPopulation;
  static
  {
    initialPopulation = new HashMap<>();
    initialPopulation.put(50, 500);
    initialPopulation.put(75, 500);
    initialPopulation.put(100, 600);
    initialPopulation.put(125, 600);
    initialPopulation.put(150, 750);
    initialPopulation.put(175, 900);
    initialPopulation.put(200, 1000);
  }

  public List<Solution> getSolutions(Schedule schedule) {
    int activitiesCount = schedule.getActivities().length;
    int sequencesTotal;
    if (activitiesCount <= 50) {
      sequencesTotal = initialPopulation.get(50);
    } else if (activitiesCount <= 75) {
      sequencesTotal = initialPopulation.get(75);
    } else if (activitiesCount <= 100) {
      sequencesTotal = initialPopulation.get(100);
    } else if (activitiesCount <= 125) {
      sequencesTotal = initialPopulation.get(125);
    } else if (activitiesCount <= 150) {
      sequencesTotal = initialPopulation.get(150);
    } else if (activitiesCount <= 175) {
      sequencesTotal = initialPopulation.get(175);
    } else {
      sequencesTotal = initialPopulation.get(200);
    }

  /**
   * Se crea la variable para almacenar las seuencias no dominadas
   */
    List<Solution> solutions = new ArrayList<>();
    Cloner cloner=new Cloner();
    for (int i = 0; i < sequencesTotal; i++) {
      Schedule clone = cloner.deepClone(schedule);
      List<Activity> sequence = generateSequence(clone);
      solutions.add(createSolution(sequence));
    }
/**
 * Obtiene las secuencias no dominadas
 */
    List<Solution> nonDominatedSolutions = getNonDominated(solutions);
    double k = Math.pow(solutions.size() + nonDominatedSolutions.size(), 0.5);
    for (Solution solution : solutions) {
      List<Solution> dominatedBy = nonDominatedSolutions.stream()
              .filter(nonDominatedSolition -> !nonDominatedSolition.equals(solution))
              .filter(nonDominatedSolution -> solution.getcMax() >= nonDominatedSolution.getcMax() && solution.getTwst() >= nonDominatedSolution.getTwst())
              .collect(Collectors.toList());
/**
 * Obtiene la distancia maxima entre cada secuencia no dominada y la secuencia dominada
 */
      double distances[] = new double[dominatedBy.size()];
      for (int j = 0; j < dominatedBy.size(); j++) {
        Solution nonDominatedSolution = dominatedBy.get(j);
        double cMaxSquare = Math.pow(nonDominatedSolution.getcMax() - solution.getcMax(), 2);
        double twstSquare = Math.pow(nonDominatedSolution.getTwst() - solution.getTwst(), 2);
        distances[j] = Math.pow(cMaxSquare + twstSquare, 0.5);
        solution.setDominatedByDistance(distances);
      }
      /**
       * Obtiene la densidad de cada secuencia
       */
      solution.setDominatedBy(dominatedBy);
      BigDecimal density = new BigDecimal(1 / (Math.pow(getBiggestDistance(distances), k) + 2));
      BigDecimal fitness = density.add(new BigDecimal(dominatedBy.size()));
      solution.setFitness(fitness);
    }
//    solutions.forEach(solution -> {
//            System.out.println(solution.getcMax() + "\t" + solution.getTwst());
//        });
    solutions.sort(Comparator.comparing(Solution::getFitness));

    return solutions;
  }


  public List<Solution> getSolutions2(Schedule schedule, List<Solution> solutions) {
    List<Solution> clonedSolutions = cloneSolution(solutions);
    List<Solution> nonDominatedSolutions = getNonDominated(clonedSolutions);
    /**
     * Se obtiene el valor de K
     */
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
      solution.setFitness(fitness);
    }
//    solutions.forEach(solution -> {
//            System.out.println(solution.getcMax() + "\t" + solution.getTwst());
//        });
    clonedSolutions.sort(Comparator.comparing(Solution::getFitness));

    return clonedSolutions;
  }

  /**
   * Metodo para obtener la mayor distancia con las secuencias no dominadas
   * @param distances Distancia entre las secuencias
   * @return Maxima distancia entre las secuencias
   */
  double getBiggestDistance(double distances[]) {
    if(distances.length == 0) return 0;
    double maxDistance = Arrays.stream(distances).max().getAsDouble();
    return maxDistance;
  }


  void startNextActivities() {
    Random random = new Random();
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

      // Si ya no hay trabajos que se puedan hacer entonces nos salimos del ciclo
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

  private static List<Solution> cloneSolution(List<Solution> solutions) {
    List<Solution> clonedSolutions = new ArrayList<>();
    for (Solution solution : solutions) {
      clonedSolutions.add(cloner.deepClone(solution));
    }
    return clonedSolutions;
  }
}
