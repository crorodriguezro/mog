package file;

import project.Activity;
import project.Resource;
import model.Schedule;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase se encarga de la lectura de los archivos planos de donde se toma la informacion de las actividades
 */
public class Read {

    private static final Logger LOGGER = Logger.getLogger(Read.class.getName());

    /**
     * Se toman los archivos iniciales y se inicia la lectura de estos
     * @param fileName Nombre del archivo con las actividades
     * @param weightFile Nombre del archivo con el peso de cada actividad
     * @return No se retorna nada
     */
    public Schedule processFile(String fileName, String weightFile) {
        // Represents the read info
        Schedule schedule;

        // Ayuda a almacenar las lineas leidas
        BufferedReader mainFileReader;
        BufferedReader weightFileReader;
        try {
            //Iniciamos el reader con el nombre del archivo
            mainFileReader = new BufferedReader(new FileReader(fileName));
            weightFileReader = new BufferedReader(new FileReader(weightFile));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, e.toString());
            return null;
        }
        String line;
        int activitiesAmount;
        int resourceTypesAmount;
        Activity[] activitiesWithSuccessors;
        Activity[] activities;
        try {
            line = mainFileReader.readLine();
            // Leemos numero total de tareas
            activitiesAmount = readNumber(mainFileReader, line, "activities");
            // Nos saltamos hasta RESOURCES
            skipTo(mainFileReader, line, "RESOURCES");
            // Leemos los recursos
            resourceTypesAmount = readResourceTypesAmount(mainFileReader);
            // Nos saltamos hasta jobnr
            skipTo(mainFileReader, line, "jobnr.");
            // Leemos los trabajos
            activitiesWithSuccessors = readActivityWithSuccessors(mainFileReader, activitiesAmount);
            //Leemos Time And Resources
            skipTo(mainFileReader,line,"----");
            activities = readTimeAndResources(mainFileReader, activitiesAmount, activitiesWithSuccessors);
            skipTo(mainFileReader,line,"RESOURCEAVAILABILITIES");

            activities = readWeights(weightFileReader, activitiesAmount, activities);

            Resource[] resources = readTotalResources(mainFileReader);
            // Creamos el Scheduler con los trabajos y las tareas
            schedule = new Schedule(activities, resources);

        } catch (IOException e) {
            LOGGER.log(Level.FINE, e.toString());
            return null;
        } finally {
            closeReader(mainFileReader);
        }
        return schedule;
    }

    /**
     * Se toma la informacion de los pesos anteriormente leida y se asosia a cada actividad
     * @param weightFileReader
     * @param activitiesAmount
     * @param activities
     * @return
     * @throws IOException
     */
    private Activity[] readWeights(BufferedReader weightFileReader, int activitiesAmount, Activity[] activities)
        throws IOException {
        String line;
        for (int i = 0; i < activitiesAmount; i++) {
            line = weightFileReader.readLine();
            int weight = Integer.valueOf(line);
            activities[i].setWeight(weight);
        }
        return activities;
    }

    /**
     * Lee el numero de actividades y recursos.
     *
     * @param reader
     * @param line   linea actual del archivo
     * @param toRead determina que lee, actividades o recursos
     * @return el numero de actividades o de recursos
     * @throws IOException when there is no <code>toRead in the file</code>
     */
    private int readNumber(BufferedReader reader, String line, String toRead) throws IOException {
        line = skipTo(reader, line, toRead);
        if (null == line) {
            LOGGER.log(Level.FINE, "No number specified for given type");
            return -1;
        }
        int lastCharacterIndex = line.lastIndexOf(' ');
        return Integer.parseInt(line.substring(lastCharacterIndex + 1));
    }

    private int readResourceTypesAmount(BufferedReader reader) throws IOException {
        String line;
        String[] parts;
        int resourceAmount = 0;
        for (int i = 0; i < 3; ++i) {
            line = reader.readLine();
            parts = line.split("\\s+");
            resourceAmount = resourceAmount + Integer.parseInt(parts[parts.length - 2]);
        }

        return resourceAmount;
    }

    private Resource[] readTotalResources(BufferedReader reader) throws IOException {
        reader.readLine();
        String line = reader.readLine();
        String[] parts = line.split("\\s+");
        Resource[] resources = new Resource[parts.length - 1];
        for (int i = 1; i < parts.length; i++) {
            resources[i-1] = new Resource(Integer.parseInt(parts[i]), Resource.RENEWABLE);
        }
        return resources;
    }

    /**
     * Lee las actividades del archivo plano.
     *
     * @param reader   used reader
     * @param numTasks numero de actividades
     * @return arreglo de actividades
     * @throws IOException when file is in the wrong format
     */
    private Activity[] readActivityWithSuccessors(BufferedReader reader, int numTasks) throws IOException {
        Activity[] activities = new Activity[numTasks];
        String line;
        String[] parts;
        int id;
        int[] successors;

        for (int i = 0; i < numTasks; ++i) {
            line = reader.readLine();
            parts = line.split("\\s+");
            id = Integer.parseInt(parts[1]);
            successors = readSuccessors(parts);
            Activity currentActivity;

            if (activities[id - 1] == null) {
                currentActivity = new Activity(id,-1, -1, null);
            }else {
                currentActivity = activities[id - 1];
            }

            for (int successorId : successors) {
                if (activities[successorId - 1] == null) {
                    Activity successor = new Activity(successorId);
                    activities[successorId - 1] = successor;
                    currentActivity.addSuccessor(successor);
                } else {
                    currentActivity.addSuccessor(activities[successorId - 1]);
                }
            }
            activities[i] = currentActivity;
        }

        return activities;
    }

    private Activity[] readTimeAndResources(BufferedReader reader, int activitiesAmount, Activity[] activities) throws IOException {
        String line;
        String[] parts;
        int duration;
        int[] resources;

        for (int i = 0; i < activitiesAmount; ++i) {
            line = reader.readLine();
            parts = line.split("\\s+");
            duration = Integer.parseInt(parts[3]);

            if(i > 98 ){
                resources = readResources (parts, 3);
            } else {
                resources = readResources (parts, 4);
            }
            activities[i].setDuration(duration);
            activities[i].setResources(resources);
        }
        return activities;
    }

    /**
     * Crea un arreglo con los sucesores de cada actividad.
     *
     * @param parts        linea del formato que contiene los sucesores
     * @return arreglo de los sucesores
     */
    private int[] readSuccessors(String[] parts) {
        int[] successors = new int[parts.length - 4];
        for (int i = 0; i < successors.length; ++i) {
            successors[i] = Integer.parseInt(parts[i + 4]);
        }

        return successors;
    }

    private int[] readResources(String[] parts, int partsOffset) {
        int[] resources = new int[parts.length - partsOffset];
        for (int i = 0; i < resources.length; ++i)
        {
            resources[i] = Integer.parseInt(parts[i + partsOffset]);
        }

        return resources;
    }
    /**
     * Salta el lector hasta la linea que comiensa con el texto descrito
     *
     * @param reader  used reader
     * @param line    linea actual del archivo
     * @param desired texto deseado de encontrar en la linea
     * @return linea, esta inicia con <code>desired</code> String
     * @throws IOException exception during IO operation
     */
    private String skipTo(BufferedReader reader, String line, String desired) throws IOException {
        while (null != line && !line.startsWith(desired)) {
            line = reader.readLine();
        }
        return line;
    }

    /**
     * Closes the reader. Should be used in <code>finally</code>
     * part of reading the file.
     *
     * @param reader closed reader
     */
    protected void closeReader(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, e.toString());
        }
    }
}
