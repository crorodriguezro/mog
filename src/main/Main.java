package main;

import file.Read;

import java.util.ArrayList;
import java.util.List;

import model.Solution;
import model.Schedule;
import schedule.*;

/**
 * En el main se van a encontrar los archivos fuente, los cuales contienen la informacion de las actividades.
 * La informacion de los pesos asignados a cada actividad.
 * El metodo por el que se van a generar las "S".
 * EL metodo por el que van a ser generados los "S*"
 * El criterio de detencion del programa.
 * El nombre del archivo de salida.
 */
public class Main {

    private static final String DEFINITION_FILE = "catalogo/j1201_1.sm";
    private static final String WEIGHT_FILE = "catalogo/j1201_1.w";

    /**
     * Metodo por el cual se obtiene la primera secuencia. "MOG" para Mog y "SPEA2" para ...
     */
    private static final String METHOD_S = "MOG";
    private static int PROGRAM_EXECUTION_TIMES = 3;
    private static int MAX_SEQUENCE_X_TRIES = 10000;

    public static void main(String[] args) {
        Read reader = new Read();
        // Procesa el archivo
        Schedule schedule = reader.processFile(DEFINITION_FILE, WEIGHT_FILE);
        List<Solution> solutions;
        switch (METHOD_S) {
            case "MOG":
                MogSequence mogSequence = new MogSequence();
                for (int i = 0; i < PROGRAM_EXECUTION_TIMES; i++) {
                    schedule = reader.processFile(DEFINITION_FILE, WEIGHT_FILE);
                    solutions = mogSequence.getSolutions(schedule);
                    MogSolver.getSequencesSx(schedule, solutions.get(0).getSequence(), MAX_SEQUENCE_X_TRIES);
                    MogSolver.test();
                }
                break;
            case "SPEA2":
                Spea2Sequence spea2Sequence = new Spea2Sequence();
                solutions = spea2Sequence.getSolutions(schedule);
                for (int i = 0; i < PROGRAM_EXECUTION_TIMES - 1; i++) {
                    System.out.println("Ejecucion " + (i + 2));
                    List<Solution> pPrima = solutions.subList(0, (int)(solutions.size() * 0.2));
                    List<Solution> sequencesSx = Spea2Solver.getSequencesSx(schedule, pPrima, MAX_SEQUENCE_X_TRIES);
                    solutions = new ArrayList<>(solutions.subList(0, solutions.size() - sequencesSx.size()));
                    solutions.addAll(sequencesSx);
                    solutions = spea2Sequence.getSolutions2(schedule, solutions);
                    System.out.println("");
                }
                break;
            default:
                throw new RuntimeException("Metodo no conocido");
        }
    }
}
