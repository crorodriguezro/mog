import io.ParseFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import model.Job;
import model.Schedule;
import model.SimpleJob;
import model.Solution;
import solvers.GreedySolverRandom;
import solvers.Solver;
import validator.Validator;

public class Main {

  private static final String DEFINITION_FILE_CATALOG = "catalogo/";
  private static final String DEFINITION_FILE = "j301_1.sm";
  private static final String WEIGHT_FILE = "j301_1.w";
  private static Random random = new Random();
  private static Set<Solution> allSolutions = new HashSet<>();
  private static Set<Solution> bestSolutions = new HashSet<>();

  public static void main(String[] args) {
    for (int i = 0; i < 500; i++) {
      ParseFile reader = new ParseFile();
      // Procesa el archivo
      Schedule schedule = reader.processFile(DEFINITION_FILE_CATALOG + DEFINITION_FILE, DEFINITION_FILE_CATALOG + WEIGHT_FILE);

      Solver solver = new GreedySolverRandom();
      List<Job> sequence = solver.solve(schedule);
      int method = random.nextInt(2);
      int neighborhoodSize;
      if (method == 0) {
        neighborhoodSize = sequence.size() * (sequence.size() - 1) / 2;
      } else {
        neighborhoodSize = (sequence.size() - 1) * (sequence.size() - 1);
      }
      int solutionsCounter = 0;
      int triesCounter = 0;
      printSequenceJ(sequence);
      while (solutionsCounter < neighborhoodSize && triesCounter < 1000000) {
        try {
          List<Job> clonedSequence = getClonedJobs(sequence);
          List<Job> newSequence = getNeighborhood(clonedSequence, method);
          //printSequenceJ(newSequence);
          Validator validator = new Validator(schedule.getResources(), newSequence);
          List<SimpleJob> validatedSequence = validator.validate();
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
    test();
  }

  private static void test() {
    System.out.println("Encontrar mejores soluciones");
    allSolutions.forEach(solution -> printSequence(solution.getSequence()));
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

    allSolutions.forEach(solution -> {
      System.out.println(solution.getcMax() + "\t" + solution.getTwst());
    });
    System.out.printf("");
  }

  private static Solution createSolution(List<SimpleJob> sequence) {
    int cMax = sequence.get(sequence.size() - 1).getFinishTime();
    int twst = sequence.stream()
        .mapToInt(job -> {
          return job.getStartTime() * job.getWeight();
        })
        .sum();
    return new Solution(sequence, cMax, twst);
  }

  private static List<Job> getClonedJobs(ParseFile reader, List<Job> sequence) {
    Schedule schedule2 = reader.processFile(DEFINITION_FILE_CATALOG + DEFINITION_FILE,
        DEFINITION_FILE_CATALOG + WEIGHT_FILE);
    List<Job> auxJobs = Arrays.asList(schedule2.getJobs());
    return sequence.stream().map(job -> {
      Job newJob = auxJobs.stream().filter(j -> j.getId() == job.getId()).findFirst().get();
      newJob.setFinishTime(job.getFinishTime());
      newJob.setStartTime(job.getStartTime());
      return newJob;
    }).collect(Collectors.toList());
  }

  private static List<Job> getClonedJobs(List<Job> sequence) {
    List<Job> clonedJobs = sequence.stream().map(Job::new).collect(Collectors.toList());
    clonedJobs.forEach(nj -> {
      Job oldJob = sequence.stream().filter(oj -> oj.getId() == nj.getId()).findFirst().get();
      oldJob.getSuccessors().forEach(oldSuccessor -> {
        Job newSuccessor = clonedJobs.stream().filter(clonedJob -> clonedJob.getId() == oldSuccessor.getId()).findFirst()
            .get();
        nj.addSuccessor(newSuccessor);
      });
    });
    return clonedJobs;
  }

  private static List<Job> getNeighborhood(List<Job> sequence, int method) {
    List<Job> newSequence = new ArrayList<>(sequence);
    int exchange1;
    int exchange2;
    if (false) {
      exchange1 = random.nextInt(sequence.size());
      exchange2 = random.nextInt(sequence.size());
      Job job1 = newSequence.get(exchange1);
      Job job2 = newSequence.get(exchange2);
      job1.setStartTime(-1);
      job1.setFinishTime(-1);
      job2.setStartTime(-1);
      job2.setFinishTime(-1);
      newSequence.set(exchange2, job1);
      newSequence.set(exchange1, job2);
      return newSequence;
    }

//    exchange1 = random.nextInt(sequence.size());
//    int insertIndex = random.nextInt(sequence.size());
    exchange1 = 0;
    int insertIndex = 0;
    Job job1 = newSequence.remove(exchange1);
    job1.setStartTime(-1);
    job1.setFinishTime(-1);
    newSequence.add(insertIndex, job1);
    if (newSequence.size() > sequence.size()) {
      System.out.println("error generando nueva secuencia");
    }
    return newSequence;
  }

  private static void printSequence(List<SimpleJob> simpleJobs) {
    System.out.print(simpleJobs.get(simpleJobs.size() - 1).getFinishTime() + " ");
    for (SimpleJob job : simpleJobs) {
      System.out.print(job.getId() + " ");
    }
    System.out.println();
  }

  private static void printSequenceJ(List<Job> newSequence) {
    for (Job job : newSequence) {
      System.out.print(job.getId() + " ");
    }
    System.out.println();
  }
}
