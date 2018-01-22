package main;

import com.rits.cloning.Cloner;
import file.Read;

import java.util.List;

import model.Solution;
import project.Activity;
import model.Schedule;
import schedule.MogSequence;
import schedule.MogSolver;
import schedule.Sequence;
import schedule.Spea2Sequence;

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
   * Metodo por el cual se obtiene la primera secuencia. "MOG_SEQUENCE" para Mog y "SPEA2_SEQUENCE" para ...
   */
  private static final String METHOD_S = "SPEA2_SEQUENCE";

  /**
   *
   */
  private static final String METHOD_SX = "MOG_SOLVER";;
  private static int PROGRAM_EXECUTION_TIMES = 1;
  private static int MAX_SEQUENCE_X_TRIES = 10000;

  public static void main(String[] args) {
    for (int i = 0; i < PROGRAM_EXECUTION_TIMES; i++) {
      Read reader = new Read();
      // Procesa el archivo
      Schedule schedule = reader.processFile(DEFINITION_FILE, WEIGHT_FILE);
      Cloner cloner=new Cloner();
      List<Solution> sequences;
      switch(METHOD_S){
        case "MOG_SEQUENCE":
          MogSequence mogSequence = new MogSequence();
          sequences = mogSequence.getSolutions(schedule);
          break;
        case "SPEA2_SEQUENCE":
          Spea2Sequence spea2Sequence = new Spea2Sequence();
          sequences = spea2Sequence.getSolutions(schedule);
          break;
        default:
          throw new RuntimeException("Metodo no conocido");
      }

      switch(METHOD_SX){
        case "MOG_SOLVER":
          MogSolver.getSequencesSx(schedule, sequences.get(0).getSequence(), MAX_SEQUENCE_X_TRIES);
          break;
        case "X":
          break;
        default:
          throw new RuntimeException("Metodo no conocido");
      }
    }
    MogSolver.test();
  }
}
