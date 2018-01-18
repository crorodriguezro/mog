package main;

import file.Read;

import java.io.Console;
import java.util.List;

import project.Activity;
import model.Schedule;
import schedule.MogSequence;
import schedule.MogSolver;
import schedule.Sequence;

/**
 * En el main se van a encontrar los archivos fuente, los cuales contienen la informacion de las actividades.
 * La informacion de los pesos asignados a cada actividad.
 * El metodo por el que se van a generar las "S".
 * EL metodo por el que van a ser generados los "S*"
 * El criterio de detencion del programa.
 * El nombre del archivo de salida.
 */
public class Main {

  private static final String DEFINITION_FILE = "catalogo/nico.sm";
  private static final String WEIGHT_FILE = "catalogo/nico.w";

  /**
   * Metodo por el cual se obtiene la primera secuencia. "MOG" para Mog y "X" para ...
   */
  private static final String METHOD_S = "MOG_SEQUENCE";

  /**
   *
   */
  private static final String METHOD_SX = "MOG_SOLVER";;
  private static int PROGRAM_EXECUTION_TIMES = 15;
  private static int MAX_SEQUENCE_X_TRIES = 1000000;

  public static void main(String[] args) {
    for (int i = 0; i < PROGRAM_EXECUTION_TIMES; i++) {
      Read reader = new Read();
      // Procesa el archivo
      Schedule schedule = reader.processFile(DEFINITION_FILE, WEIGHT_FILE);

      Sequence solver;
      switch(METHOD_S){
        case "MOG_SEQUENCE":
          solver = new MogSequence();
          break;
        case "X":
          solver = new MogSequence();
          break;
        default:
          throw new RuntimeException("Metodo no conocido");
      }
      List<Activity> sequence = solver.solve(schedule);
      switch(METHOD_SX){
        case "MOG_SOLVER":
          MogSolver.getSequencesSx(schedule, sequence, MAX_SEQUENCE_X_TRIES);
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
