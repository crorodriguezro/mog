package io;

import model.Resource;
import model.Schedule;
import model.Skill;
import model.Task;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles loading .def files and saving solutions to the file for
 * Multi Skill Resource Constraint Project Scheduling Problem
 */
public class MSRCPSPIO {

    private static final Logger LOGGER = Logger.getLogger(MSRCPSPIO.class.getName());

    public Schedule readDefinition(String fileName) {
        Schedule schedule;

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, e.toString());
            return null;
        }
        String line;
        int numTasks;
        int numResources;
        Resource[] resources;
        Task[] tasks;

        try {
            line = reader.readLine();
            numTasks = readNumber(reader, line, "jobs");
            skipTo(reader, line, "RESOURCES");
            resources = readResources(reader, 3);
            skipTo(reader, line, "jobnr.");
            tasks = readTasks(reader, numTasks);

            schedule = new Schedule(tasks, resources);

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
     * @param toRead determines what to readDefinition, either "Tasks" or "Resources"
     * @return number of tasks or resources
     * @throws IOException when there is no <code>toRead in the file</code>
     */
    private int readNumber(BufferedReader reader, String line, String toRead) throws IOException {
        line = skipTo(reader, line, toRead);
        if (null == line) {
            LOGGER.log(Level.FINE, "No number specified for given type");
            return -1;
        }
        return Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
    }

    /**
     * Reads resources from the file.
     *
     * @param reader       used reader
     * @param numResources number of resources to readDefinition
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
    private Task[] readTasks(BufferedReader reader, int numTasks) throws IOException {
        Task[] tasks = new Task[numTasks];
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

            tasks[i] = new Task(id,null, duration, successors);
        }

        return tasks;
    }

    /**
     * Creates an array of successors from parts containing the ids.
     *
     * @param parts        line readDefinition from the file containing ids of the successors
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
     * Reads a solution file saving all the assignments into
     * the given schedule
     *
     * @param fileName solution file
     * @param schedule schedule to fill
     * @return scheduled with assignments read from the file
     */
    public Schedule readSolution(String fileName, Schedule schedule) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, e.toString());
            return null;
        }
        String line;

        try {
            // skip headers
            reader.readLine();
            line = reader.readLine();
            while (line != null) {
                String parts[] = line.split("\\s");
                int timestamp = Integer.parseInt(parts[0]);
                for (int i = 1; i < parts.length; ++i) {
                    String[] idParts = parts[i].split("-");
                    int resourceId = Integer.parseInt(idParts[0]);
                    int taskId = Integer.parseInt(idParts[1]);
                    schedule.assign(taskId, resourceId, timestamp);
                }
                line = reader.readLine();
            }

        } catch (IOException e) {
            LOGGER.log(Level.FINE, e.toString());
            return null;
        } finally {
            closeReader(reader);
        }
        return schedule;
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
    public void write(Schedule schedule, String filename)
            throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        Map<Integer, List<Task>> map = new TreeMap<Integer, List<Task>>();
        writer.write("Hour \t Resource assignments (resource ID - task ID) \n");
        for (Task t : schedule.getTasks()) {
            if (!map.containsKey(t.getStart())) {
                map.put(t.getStart(), new LinkedList<Task>());
            }
            map.get(t.getStart()).add(t);
        }
        for (int i : map.keySet()) {
            writer.write(i + " ");
            for (Task t : map.get(i)) {
                writer.write(t.getResourceId() + "-" + t.getId() + " ");
            }
            writer.write("\n");
        }
        writer.close();

    }

}
