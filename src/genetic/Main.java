package genetic;

import io.MSRCPSPIO;
import model.Schedule;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class to help with understanding of the library.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String definitionFileCatalog = "catalogo/";
    private static final String definitionFile = "j301_1.sm";
    private static final String writeFile = "solucion/solucion";

    public static void main(String[] args) {

        MSRCPSPIO reader = new MSRCPSPIO();
        Schedule schedule = reader.readDefinition(definitionFileCatalog + definitionFile);
        if (schedule == null) {
            LOGGER.log(Level.WARNING, "Could not read the Definition " + definitionFile);
        }

        GeneticAlgorithmManager geneticAlgorithmManager = new GeneticAlgorithmManager(schedule);
        geneticAlgorithmManager.run();

        ChartManager chartManager = new ChartManager();
        chartManager.displayChart(
                geneticAlgorithmManager.getBestSpecimenData(),
                geneticAlgorithmManager.getAverageSpecimenData(),
                geneticAlgorithmManager.getWorstSpecimenData(),
                definitionFile,
                "Tournament"
        );

        try {
            reader.write(schedule, writeFile);
        } catch (IOException e) {
            System.out.print("Writing to a file failed");
        }
    }
}
