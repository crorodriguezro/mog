package schedule;

import com.rits.cloning.Cloner;
import model.Schedule;
import model.Solution;
import project.Activity;
import validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

public class Spea2Solver {
    private static Random random = new Random();
    private static Set<Solution> allSolutions;
    private static Set<Solution> bestSolutions;
    private static Cloner cloner = new Cloner();

    /**
     * Metodo para generar las secuencias mutadas y recombinadas (hijos)
     * @param schedule Secuencia
     * @param solutions Mejores secuencias obtenidas
     * @param maxSequenceTries Numero maximo de intentos
     * @return Todas las nuevas soluciones
     */
    public static List<Solution> getSequencesSx(Schedule schedule, List<Solution> solutions, int maxSequenceTries) {
        List<Solution> clonedSolutions = cloneSolution(solutions);
        allSolutions = new HashSet<>();
        int neighborhoodSize = clonedSolutions.size();
        int solutionsCounter = 0;
        int triesCounter = 0;
        while (solutionsCounter < neighborhoodSize && triesCounter < maxSequenceTries) {
            List<Solution> recombinedNeighborhood = getRecombinationNeighborhood(clonedSolutions);
            List<Solution> mutatedNeighborhood = getMutationNeighborhood(clonedSolutions);
            List<Solution> neighborhood = new ArrayList<>(recombinedNeighborhood);
            neighborhood.addAll(mutatedNeighborhood);
            for (Solution solution : recombinedNeighborhood) {
                try {
                    Validator validator = new Validator(schedule.getResources(), solution.getSequence());
                    List<Activity> validatedSequence = validator.validate();
                    solutionsCounter++;
                    triesCounter++;
                    allSolutions.add(createSolution(validatedSequence));
                } catch (RuntimeException e) {
                    triesCounter++;
                    String message = e.getMessage();
                    if (!message.equals("El trabajo tiene predecesores") &&
                            !message.equals("No hay recursos") &&
                            !message.equals("No sirve la secuencia")) {
                        e.printStackTrace();
                    }
                }
//                System.out.println("intentos: " + triesCounter);
            }
        }
        return new ArrayList<>(allSolutions);
    }

    /**
     * Metodo que duplica la solucion tomada para no perderla en la mutacion o recombinacion
     * @param solutions Secuencia para mutar
     * @return Secuencia duplicada
     */
    private static List<Solution> cloneSolution(List<Solution> solutions) {
        List<Solution> clonedSolutions = new ArrayList<>();
        for (Solution solution : solutions) {
            clonedSolutions.add(cloner.deepClone(solution));
        }
        return clonedSolutions;
    }

    /**
     * Metodo para obtener un vecindario de secuencias mutadas
     * @param solutions Todas las mejores soluciones
     * @return Secuencias mutadas
     */
    private static List<Solution> getMutationNeighborhood(List<Solution> solutions) {
        List<Solution> mutatedSolutions = new ArrayList<>();
        for (Solution solution : solutions) {
            List<Activity> newSequence = cloner.deepClone(solution.getSequence());
            List<Activity> activities = solution.getSequence();
            int exchangeIndex1;
            int exchangeIndex2;
            exchangeIndex1 = random.nextInt(activities.size());
            exchangeIndex2 = random.nextInt(activities.size());
            Activity activity1 = activities.get(exchangeIndex1);
            Activity activity2 = activities.get(exchangeIndex2);
            activity1.setStartTime(-1);
            activity1.setFinishTime(-1);
            activity2.setStartTime(-1);
            activity2.setFinishTime(-1);
            newSequence.set(exchangeIndex2, activity1);
            newSequence.set(exchangeIndex1, activity2);
            solution = new Solution(newSequence, -1, -1);
            mutatedSolutions.add(solution);
        }
        return mutatedSolutions;
    }

    /**
     * Metodo para obtener un vecindario de secuencias recombinadas
     * @param solutions Todas las mejores soluciones
     * @return Secuencias recombinadas
     */
    private static List<Solution> getRecombinationNeighborhood(List<Solution> solutions) {
        List<Solution> mutatedSolutions = new ArrayList<>();
        for (Solution solution : solutions) {
            List<Activity> sequence = cloner.deepClone(solution.getSequence());
            int recombinationIndex = random.nextInt(solutions.size());
            Solution otherSolution = solutions.get(recombinationIndex);
            List<Activity> otherSequence = cloner.deepClone(otherSolution.getSequence());
            int sublistIndex;
            // se recombinan apartir del 4 nodo
            sublistIndex = 4;
            /*            do {
                sublistIndex = random.nextInt(sequence.size()) + 1;
            } while (sublistIndex != 0 && sublistIndex != sequence.size() -1);*/
            List<Activity> subSequence1 = sequence.subList(0, sublistIndex);
            subSequence1.get(subSequence1.size() - 1).setStartTime(-1);
            subSequence1.get(subSequence1.size() - 1).setFinishTime(-1);
            List<Activity> subSequence2 = otherSequence.subList(subSequence1.size() - 1, sequence.size() - 1);
            subSequence1.addAll(subSequence2);
            Solution newSolution = new Solution(subSequence1, -1, -1);
            mutatedSolutions.add(newSolution);
        }
        return mutatedSolutions;
    }

    /**
     * Ingresa la secuencia "S" para obtener el TWST y el Cmax.
     * @param sequence Nueva secuencia
     * @return sequence con el Cmax y el TWST
     */
    private static Solution createSolution(List<Activity> sequence) {
        int cMax = sequence.get(sequence.size() - 1).getFinishTime();
        double twst = sequence.stream()
                .mapToDouble(activity -> {
                    return (double)activity.getStartTime() / activity.getWeight();
                })
                .sum();
        return new Solution(sequence, cMax, twst);
    }

    /**
     *Metodo para imprimir la secuencia final
     * @param Activities Actividades
     */

    private static void printSequence(List<Activity> Activities) {
        for (Activity activity : Activities) {
            System.out.print(activity.getId() + " ");
        }
        System.out.println();
    }

    private static void printSequenceJ(List<Activity> newSequence) {
        for (Activity activity : newSequence) {
            System.out.print(activity.getId() + " ");
        }
        System.out.println();
    }
}
