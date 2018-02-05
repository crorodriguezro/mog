package schedule;

import model.Schedule;
import model.Solution;
import project.Activity;
import validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

public class MogSolver {
    private static Random random = new Random();
    private static Set<Solution> allSolutions;

    /**
     * Metodo para generar los nuevos vecindarios
     *
     * @param schedule         Programador de las secuencias
     * @param solution         Secuencia base "S"
     * @param maxSequenceTries Numero maximo de intentos
     */
    public static List<Solution> getSequencesSx(Schedule schedule, Solution solution, int maxSequenceTries) {
        allSolutions = new HashSet<>();
        List<Activity> activities = solution.getSequence();
        int method = random.nextInt(2);
        int neighborhoodSize;
        if (method == 0) {
            neighborhoodSize = activities.size() * (activities.size() - 1) / 2;
        } else {
            neighborhoodSize = (activities.size() - 1) * (activities.size() - 1);
        }
        int solutionsCounter = 0;
        int triesCounter = 0;
        while (solutionsCounter < neighborhoodSize && triesCounter < maxSequenceTries) {
            try {
                List<Activity> clonedSequence = getClonedActivities(activities);
                List<Activity> newSequence = getNeighborhood(clonedSequence, method);
                Validator validator = new Validator(schedule.getResources(), newSequence);
                List<Activity> validatedSequence = validator.validate();
                solutionsCounter++;
                triesCounter++;
                allSolutions.add(new Solution(validatedSequence));
            } catch (RuntimeException e) {
                triesCounter++;
                String message = e.getMessage();
                if (!message.equals("El trabajo tiene predecesores") &&
                        !message.equals("No hay recursos") &&
                        !message.equals("La secuencia tiene actividades duplicadas") &&
                        !message.equals("No sirve la secuencia")) {
                    e.printStackTrace();
                }
            }
        }
        allSolutions.forEach(s -> {
            System.out.println(s.getcMax() + "\t" + s.getTwst());
        });
        return new ArrayList<>(allSolutions);
    }

    /**
     * Ingresa la secuencia "S" y el metodo que se obtuvo de manera aleatoria entre metodo de insercion o el metodo de intercambio
     *
     * @param sequence "S"
     * @param method   insercion o intercambio
     * @return newSequence
     */
    private static List<Activity> getNeighborhood(List<Activity> sequence, int method) {
        List<Activity> newSequence = new ArrayList<>(sequence);
        int exchange1;
        int exchange2;
        if (method == 0) {
            exchange1 = random.nextInt(sequence.size());
            exchange2 = random.nextInt(sequence.size());
            Activity activity1 = newSequence.get(exchange1);
            Activity activity2 = newSequence.get(exchange2);
            activity1.setStartTime(-1);
            activity1.setFinishTime(-1);
            activity2.setStartTime(-1);
            activity2.setFinishTime(-1);
            newSequence.set(exchange2, activity1);
            newSequence.set(exchange1, activity2);
            return newSequence;
        }

//    exchange1 = random.nextInt(sequence.size());
//    int insertIndex = random.nextInt(sequence.size());

        //Metodo de insercion
        exchange1 = random.nextInt(sequence.size());
        int insertIndex = random.nextInt(sequence.size());
        Activity activity1 = newSequence.remove(exchange1);
        activity1.setStartTime(-1);
        activity1.setFinishTime(-1);
        newSequence.add(insertIndex, activity1);
        if (newSequence.size() > sequence.size()) {
            System.out.println("error generando nueva secuencia");
        }
        return newSequence;
    }

    /**
     * Ingresa la secuencia "S" para ser duplicada y asi poder realizar los cambios en esta secuencia sin alterar la original
     *
     * @param sequence
     * @return sequence duplicada
     */
    private static List<Activity> getClonedActivities(List<Activity> sequence) {
        List<Activity> clonedActivities = sequence.stream().map(Activity::new).collect(Collectors.toList());
        clonedActivities.forEach(nj -> {
            Activity oldActivity = sequence.stream().filter(oj -> oj.getId() == nj.getId()).findFirst().get();
            oldActivity.getSuccessors().forEach(oldSuccessor -> {
                Activity newSuccessor = clonedActivities.stream().filter(clonedActivity -> clonedActivity.getId() == oldSuccessor.getId()).findFirst()
                        .get();
                nj.addSuccessor(newSuccessor);
            });
        });
        return clonedActivities;
    }

    /**
     * Se realiza las validaciones de las secuencias no dominadas, este metodo seleccion la secuencia con el mejor Cmax
     * y con el mejor TWST, posteriormente selecciona las secuencias que se encuentren en el intermedio de estas dos
     * y asi se obtienen las secuencias no dominadas.
     */
    public static void printBestSolutions(List<Solution> solutions) {
        List<Solution> bestSolutions;
        List<Solution> listWithoutDuplicates = solutions.stream()
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

        System.out.println("Mejores soluciones: ");
        bestSolutions.forEach(solution -> {
            System.out.println(solution.toString());
            printSequence(solution.getSequence());
        });
        System.out.printf("");
    }

    /**
     * Metodo de salida de las nuevas secuencias
     *
     * @param Activities Lista de actividades de la secuencia
     */

    private static void printSequence(List<Activity> Activities) {
        for (Activity activity : Activities) {
            System.out.print(activity.getId() + " ");
        }
        System.out.println();
    }
}
