import io.ParseFile;
import model.Schedule;
import solvers.GreedySolver;
import solvers.GreedySolverRandom;
import solvers.Solver;

public class Main {

    private static final String DEFINITION_FILE_CATALOG = "catalogo/";
    private static final String DEFINITION_FILE = "j301_1.sm";

    public static void main(String[] args) {
        ParseFile reader = new ParseFile();
        // Procesa el archivo
        Schedule schedule = reader.processFile(DEFINITION_FILE_CATALOG + DEFINITION_FILE);

        Solver solver = new GreedySolverRandom();
        solver.solve(schedule);
    }
}
