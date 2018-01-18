package schedule;

import model.Schedule;
import model.Solution;
import project.Activity;
import validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

public class MogSolver {
    private static Random random = new Random();
    private static Set<Solution> allSolutions = new HashSet<>();
    private static Set<Solution> bestSolutions = new HashSet<>();

    public static void getSequencesSx(Schedule schedule, List<Activity> sequence, int maxSequenceTries) {
        int method = random.nextInt(2);
        int neighborhoodSize;
        if (method == 0) {
            neighborhoodSize = sequence.size() * (sequence.size() - 1) / 2;
        } else {
            neighborhoodSize = (sequence.size() - 1) * (sequence.size() - 1);
        }
        int solutionsCounter = 0;
        int triesCounter = 0;
        //printSequenceJ(sequence);
        while (solutionsCounter < neighborhoodSize && triesCounter < maxSequenceTries) {
            try {
                List<Activity> clonedSequence = getClonedActivities(sequence);
                List<Activity> newSequence = getNeighborhood(clonedSequence, method);
                //printSequenceJ(newSequence);
                Validator validator = new Validator(schedule.getResources(), newSequence);
                List<Activity> validatedSequence = validator.validate();
                solutionsCounter++;
                triesCounter++;
//        System.out.println(solutionsCounter);
                allSolutions.add(createSolution(validatedSequence));
//          printSequence(validatedSequence);
            } catch (RuntimeException e) {
                triesCounter++;
                String message = e.getMessage();
                if (!message.equals("El trabajo tiene predecesores") &&
                        !message.equals("No hay recursos") &&
                        !message.equals("No sirve la secuencia")) {
                    e.printStackTrace();
                } else {
                    //System.out.print("");
                }
            }
            //System.out.println("intentos: " + triesCounter);
        }
    }

    /**
     * Ingresa la secuencia "S" y el metodo que se obtuvo de manera aleatoria entre metodo de insercion o el metodo de intercambio
     * @param sequence "S"
     * @param method insercion o intercambio
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
     * Ingresa la secuencia "S" para obtener el TWST y el Cmax.
     * @param sequence
     * @return sequence con el Cmax y el TWST
     */
    private static model.Solution createSolution(List<Activity> sequence) {
        int cMax = sequence.get(sequence.size() - 1).getFinishTime();
        int twst = sequence.stream()
                .mapToInt(activity -> {
                    return activity.getStartTime() / activity.getWeight();
                })
                .sum();
        return new Solution(sequence, cMax, twst);
    }

    /**
     * Se realiza las validaciones de las secuencias no dominadas, este metodo seleccion la secuencia con el mejor Cmax
     * y con el mejor TWST, posteriormente selecciona las secuencias que se encuentren en el intermedio de estas dos
     * y asi se obtienen las secuencias no dominadas.
     */
    public static void test() {
        System.out.println("Encontrar mejores soluciones");
        //allSolutions.forEach(solution -> printSequence(solution.getSequence()));
        Solution bestOverallSolution = new Solution(null, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Solution bestCMaxSolution = new Solution(null, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Solution bestTwstSolution = new Solution(null, Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (Solution currentSolution : allSolutions) {
            if (currentSolution.getcMax() < bestCMaxSolution.getcMax()){
                bestCMaxSolution = currentSolution;
            } else if (currentSolution.getTwst() < bestTwstSolution.getTwst()){
                bestTwstSolution = currentSolution;
            }
        }
        if (bestCMaxSolution == bestTwstSolution) {
            bestSolutions.add(bestCMaxSolution);
        } else {
            int cMaxMax = bestCMaxSolution.getcMax();
            int cMaxMin = bestTwstSolution.getcMax();
            int cTwstMin = bestCMaxSolution.getTwst();
            int cTwstMax = bestTwstSolution.getTwst();
            if (cMaxMax == cMaxMin){
                bestSolutions.add(bestTwstSolution);
            } else if (cTwstMin == cTwstMax){
                bestSolutions.add(bestCMaxSolution);
            } else {
                bestSolutions.add(bestCMaxSolution);
                bestSolutions.add(bestTwstSolution);
                for (Solution currentSolution : allSolutions) {
                    if(currentSolution.getcMax() >= cMaxMin && currentSolution.getcMax() <= cMaxMax &&
                            currentSolution.getTwst() >= cTwstMin && currentSolution.getTwst() <= cTwstMax){
                        bestSolutions.add(currentSolution);
                    }
                }
            }
        }

        /**
         * Ingresan las mejores soluciones obtenidas y las muestra en pantalla
         */

        System.out.println("Mejores soluciones: ");

        bestSolutions.forEach(solution -> {
            System.out.println(solution.toString());
            printSequence(solution.getSequence());
        });

//    allSolutions.forEach(solution -> {
//      System.out.println(solution.getcMax() + "\t" + solution.getTwst());
//    });
        System.out.printf("");
    }

    /**
     *
     * @param Activities
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
