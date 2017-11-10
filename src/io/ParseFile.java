package io;

import model.Resource;
import model.Scheduler;
import model.Job;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParseFile {

    private static final Logger LOGGER = Logger.getLogger(ParseFile.class.getName());

    public Scheduler processFile(String fileName) {
        // Represents the read info
        Scheduler schedule;

        // Ayuda a almacenar las lineas leidas
        BufferedReader reader;
        try {
            //Insanciamos el reader con el nombre del archivo
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, e.toString());
            return null;
        }
        String line;
        int jobsAmount;
        Resource[] resources;
        Job[] jobs;
        try {
            line = reader.readLine();
            // Leemos numero total de tareas
            jobsAmount = readNumber(reader, line, "jobs");
            // Nos saltamos hasta RESOURCES
            skipTo(reader, line, "RESOURCES");
            // Leemos los recursos
            resources = readResources(reader, 3);
            // Nos saltamos hasta jobnr
            skipTo(reader, line, "jobnr.");
            // Leemos los trabajos
            jobs = readJobs(reader, jobsAmount);
            // Creamos el Scheduler con los trabajos y las tareas
            schedule = new Scheduler(jobs, resources);

        } catch (IOException e) {
            LOGGER.log(Level.FINE, e.toString());
            return null;
        } finally {
            closeReader(reader);
        }
        return schedule;
    }

    /**
     * Reads the number of tasks or resources in the schedule.
     *
     * @param reader used reader
     * @param line   current line of the file
     * @param toRead determines what to processFile, either "Tasks" or "Resources"
     * @return number of tasks or resources
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

    /**
     * Reads resources from the file.
     *
     * @param reader       used reader
     * @param numResources number of resources to processFile
     * @return Array of resources
     * @throws IOException when file is in the wrong format
     */
    private Resource[] readResources(BufferedReader reader, int numResources) throws IOException {
        Resource[] resources = new Resource[numResources];
        String line;
        String[] parts;
        int resourceAmount;
        String resourceType;

        for (int i = 0; i < numResources; ++i) {
            line = reader.readLine();

            parts = line.split("\\s+");
            resourceAmount = Integer.parseInt(parts[parts.length - 2]);
            resourceType = parts[parts.length - 1];
            resources[i] = new Resource(resourceAmount, resourceType);
        }

        return resources;
    }

    /**
     * Reads tasks from the file.
     *
     * @param reader   used reader
     * @param numTasks number of tasks
     * @return array of tasks
     * @throws IOException when file is in the wrong format
     */
    private Job[] readJobs(BufferedReader reader, int numTasks) throws IOException {
        Job[] tasks = new Job[numTasks];
        String line;
        String[] parts;
        int id;
        int duration;
        int[] successors;

        for (int i = 0; i < numTasks; ++i) {
            line = reader.readLine();

            parts = line.split("\\s+");
            id = Integer.parseInt(parts[1]);
            duration = Integer.parseInt(parts[1]);
            successors = readSuccessors(parts);

            tasks[i] = new Job(id,null, duration, successors);
        }

        return tasks;
    }

    /**
     * Creates an array of successors from parts containing the ids.
     *
     * @param parts        line processFile from the file containing ids of the successors
     * @return array of successors
     */
    private int[] readSuccessors(String[] parts) {
        int[] successors = new int[parts.length - 4];
        for (int i = 0; i < successors.length; ++i) {
            successors[i] = Integer.parseInt(parts[i + 4]);
        }

        return successors;
    }

    /**
     * Skips the reader to the line starting with the desired string
     *
     * @param reader  used reader
     * @param line    current line of the file
     * @param desired desired start of the line
     * @return line, that starts with <code>desired</code> String
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

    /**
     * Saves a schedule to the file.
     *
     * @param schedule schedule to save
     * @param filename path to the file
     * @throws IOException
     */
    // TODO: taken from legacy code - refactor!
    public void write(Scheduler schedule, String filename)
            throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        Map<Integer, List<Job>> map = new TreeMap<Integer, List<Job>>();
        writer.write("Hour \t Resource assignments (resource ID - task ID) \n");
        for (Job t : schedule.getJobs()) {
            if (!map.containsKey(t.getStart())) {
                map.put(t.getStart(), new LinkedList<Job>());
            }
            map.get(t.getStart()).add(t);
        }
        for (int i : map.keySet()) {
            writer.write(i + " ");
            for (Job t : map.get(i)) {
                writer.write(t.getResourceId() + "-" + t.getId() + " ");
            }
            writer.write("\n");
        }
        writer.close();

    }

}
