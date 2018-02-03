package main;

import file.Read;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import model.Solution;
import model.Schedule;
import performancemesures.DistanceMetrics;
import schedule.*;

/**
 * En el main se van a encontrar los archivos fuente, los cuales contienen la informacion de las actividades.
 * La informacion de los pesos asignados a cada actividad.
 * El metodo por el que se van a generar las "S".
 * EL metodo por el que van a ser generados los "S*"
 * El criterio de detencion del programa.
 */
public class Main {
    /**
     * Archivo fuente con la informacion de las actividades
     */
    private static final String DEFINITION_FILE = "catalogo/j301_1.sm";
    /**
     * Archivo fuente con los pesos de cada actividad
     */
    private static final String WEIGHT_FILE = "catalogo/j301_1.w";
    /**
     * Metodo por el cual se obtiene la primera secuencia "S". "MOG" para Mog y "SPEA2" para ...
     */
    private static final String METHOD_S = "MOG";
    /**
     * Numero de veces que se va a ejecutar el programa (numero de "S")
     */
    private static int PROGRAM_EXECUTION_TIMES = 5;
    /**
     * Criterio de detencion cuando no se encuentren nuevas soluciones
     */
    private static int MAX_SEQUENCE_X_TRIES = 10000;

    /**
     * La cantidad de ejecuciones que va a tener el programa
     *
     * @param args Ejecuciones segun el numero suministrado
     */
    public static void main(String[] args) {
        Read reader = new Read();
        // Procesa el archivo
        Schedule schedule = reader.processFile(DEFINITION_FILE, WEIGHT_FILE);
        List<Solution> mogSolutions;
        List<Solution> spea2Solutions;
        MogSequence mogSequence = new MogSequence();
        Spea2Sequence spea2Sequence = new Spea2Sequence();
        switch (METHOD_S) {
            case "MOG":
                System.out.println("_______________MOG_______________");
                mogSolutions = getMogSolutions(reader, mogSequence);
                System.out.println("Mejores soluciones: ");
                printBestSolutions(mogSolutions);
                break;
            case "SPEA2":
                System.out.println("_______________SPEA_______________");
                spea2Solutions = getSpea2Solutions(schedule, spea2Sequence);
                printBestSolutions(spea2Solutions);
                break;
            case "BOTH":
                System.out.println("_______________MOG_______________");
                mogSolutions = getMogSolutions(reader, mogSequence);
                System.out.println("_______________SPEA_______________");
                spea2Solutions = getSpea2Solutions(schedule, spea2Sequence);
                System.out.println();
                System.out.println("_______________Mejores soluciones MOG_______________");
                printBestSolutions(mogSolutions);
                System.out.println("_______________Mejores soluciones Spea2_______________");
                printBestSolutions(spea2Solutions);

                DistanceMetrics.measurePerformance(mogSolutions, spea2Solutions);
                break;
            default:
                throw new RuntimeException("Metodo no conocido");
        }
    }

    private static void printBestSolutions(List<Solution> solutions) {
        solutions.forEach(solution -> {
            System.out.println(solution.toString());
            solution.printSequence();
        });
        solutions.forEach(s -> {
            DecimalFormatSymbols separadores = new DecimalFormatSymbols();
            separadores.setDecimalSeparator(',');
            DecimalFormat formato = new DecimalFormat("##.##",separadores);

            System.out.println(s.getcMax() + "\t" + formato.format(s.getTwst()));
        });
        System.out.println();
    }

    private static List<Solution> getMogSolutions(Read reader, MogSequence mogSequence) {
        List<Solution> mogSolutions;
        Schedule schedule;
        mogSolutions = new ArrayList<>();
        for (int i = 0; i < PROGRAM_EXECUTION_TIMES; i++) {
            System.out.println("Ejecucion " + (i + 1));
            schedule = reader.processFile(DEFINITION_FILE, WEIGHT_FILE);
            Solution solution = mogSequence.getSolution(schedule);
            List<Solution> newSolutions = MogSolver.getSequencesSx(schedule, solution, MAX_SEQUENCE_X_TRIES);
            mogSolutions.addAll(newSolutions);
            mogSolutions.add(Sequence.createSolution(solution.getSequence()));
        }
        return Sequence.getNonDominated(mogSolutions);
    }

    private static List<Solution> getSpea2Solutions(Schedule schedule, Spea2Sequence spea2Sequence) {
        List<Solution> spea2Solutions;
        System.out.println("Ejecucion " + 1);
        spea2Solutions = spea2Sequence.getSolutions(schedule);
        for (int i = 0; i < PROGRAM_EXECUTION_TIMES - 1; i++) {
            System.out.println("Ejecucion " + (i + 2));
            // Pprima equivale al 20% pero puede cambiarse al porcentaje que queramos de P
            List<Solution> pPrima = spea2Solutions.subList(0, (int) (spea2Solutions.size() * 0.2));
            List<Solution> sequencesSx = Spea2Solver.getSequencesSx(schedule, pPrima, MAX_SEQUENCE_X_TRIES);
            spea2Solutions = new ArrayList<>(spea2Solutions.subList(0, spea2Solutions.size() - sequencesSx.size()));
            spea2Solutions.addAll(sequencesSx);
            spea2Solutions = spea2Sequence.getSolutions2(schedule, spea2Solutions);
        }
        return Sequence.getNonDominated(spea2Solutions);
    }
}
