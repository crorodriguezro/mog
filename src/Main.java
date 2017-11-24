import io.ParseFile;
import model.Schedule;
import solvers.GreedySolver;
import solvers.GreedySolver2;
import solvers.GreedySolver3;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String DEFINITION_FILE_CATALOG = "catalogo/";
    private static final String DEFINITION_FILE = "j301_1.sm";

    public static void main(String[] args) {
        ParseFile reader = new ParseFile();
        // Procesa el archivo
        Schedule schedule = reader.processFile(DEFINITION_FILE_CATALOG + DEFINITION_FILE);

        GreedySolver3 solver = new GreedySolver3();
        List<Integer> solution = new ArrayList<>();
        solution = solver.solve(schedule);
        for (int i = 0; i < solution.size(); i++) {
            System.out.println(solution.get(i));
        }
        //GreedySolver2.solve(schedule);
    }
}
