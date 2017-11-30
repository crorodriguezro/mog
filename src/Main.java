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
import solvers.GreedySolver;
import solvers.GreedySolverRandom;
import solvers.Solver;
import validator.Validator;

public class Main {

    private static final String DEFINITION_FILE_CATALOG = "catalogo/";
    private static final String DEFINITION_FILE = "j301_1.sm";
    private static Random random = new Random();

    public static void main(String[] args) {
        ParseFile reader = new ParseFile();
        // Procesa el archivo
        Schedule schedule = reader.processFile(DEFINITION_FILE_CATALOG + DEFINITION_FILE);

        Solver solver = new GreedySolverRandom();
        List<Job> sequence = solver.solve(schedule);
        int method = random.nextInt(2);
        int neighborhoodSize;
        if(method == 0) {
            neighborhoodSize = sequence.size()*(sequence.size()-1)/2;
        } else {
            neighborhoodSize = (sequence.size() - 1) * (sequence.size() - 1);
        }
        int counter = 0;
        while(counter < neighborhoodSize) {
            try {
                List<Job> jobs = getClonedJobs(reader, sequence);
                List<Job> newSequence = getNeighborhood(jobs, method);
                Validator validator = new Validator(schedule, newSequence);
                validator.validate();
                counter ++;
                System.out.println(counter);
            } catch (Exception e) {
                System.out.println(e);;
            }
        }
    }

    private static List<Job> getClonedJobs(ParseFile reader, List<Job> sequence) {
        Schedule schedule2 = reader.processFile(DEFINITION_FILE_CATALOG + DEFINITION_FILE);
        List<Job> auxJobs = Arrays.asList(schedule2.getJobs());
        return sequence.stream().map(job -> {
            Job newJob = auxJobs.stream().filter(j -> j.getId() == job.getId()).findFirst().get();
            newJob.setFinishTime(job.getFinishTime());
            return newJob;
        }).collect(Collectors.toList());
    }

    private static List<Job> getNeighborhood(List<Job> sequence, int method) {
        List<Job> newSequence =  new ArrayList<>(sequence);
        int exchange1;
        int exchange2;
        if(method == 0) {
            exchange1 = random.nextInt(sequence.size());
            exchange2 = random.nextInt(sequence.size());
            Job job1 = newSequence.get(exchange1);
            Job job2 = newSequence.get(exchange2);
            newSequence.set(exchange2, job1);
            newSequence.set(exchange1, job2);
            return newSequence;
        }

        exchange1 = random.nextInt(sequence.size());
        int insertIndex = random.nextInt(sequence.size());
        Job job1 = newSequence.get(exchange1);
        newSequence.add(insertIndex, job1);
        return newSequence;
    }
}
