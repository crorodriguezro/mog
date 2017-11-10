package io;

import model.Resource;
import model.ResourceType;
import model.Scheduler;
import model.Job;

import java.io.*;
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
        int resourceTypesAmount;
        Job[] jobsWithSuccessors;
        Job[] jobs;
        try {
            line = reader.readLine();
            // Leemos numero total de tareas
            jobsAmount = readNumber(reader, line, "jobs");
            // Nos saltamos hasta RESOURCES
            skipTo(reader, line, "RESOURCES");
            // Leemos los recursos
            resourceTypesAmount = readResourceTypesAmount(reader);
            // Nos saltamos hasta jobnr
            skipTo(reader, line, "jobnr.");
            // Leemos los trabajos
            jobsWithSuccessors = readJobWithSuccessors(reader, jobsAmount);
            //Leemos Time And Resources
            skipTo(reader,line,"----");
            jobs = readTimeAndResources(reader, jobsAmount, jobsWithSuccessors);
            skipTo(reader,line,"RESOURCEAVAILABILITIES");

            Resource[] resources = readTotalResources(reader);
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
            resources[i-1] = new Resource(Integer.parseInt(parts[i]), ResourceType.RENEWABLE);
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
    private Job[] readJobWithSuccessors(BufferedReader reader, int numTasks) throws IOException {
        Job[] tasks = new Job[numTasks];
        String line;
        String[] parts;
        int id;
        int[] successors;

        for (int i = 0; i < numTasks; ++i) {
            line = reader.readLine();

            parts = line.split("\\s+");
            id = Integer.parseInt(parts[1]);
            successors = readSuccessors(parts);

            tasks[i] = new Job(id,-1, -1, successors, null);
        }

        return tasks;
    }

    private Job[] readTimeAndResources(BufferedReader reader, int jobsAmount, Job[] jobs) throws IOException {
        Job[] newJobs = new Job[jobsAmount];
        String line;
        String[] parts;
        int id;
        int duration;
        int[] resources;

        for (int i = 0; i < jobsAmount; ++i) {
            line = reader.readLine();
            parts = line.split("\\s+");
            id = Integer.parseInt(parts[2]);
            duration = Integer.parseInt(parts[3]);
            resources = readResources (parts);
            newJobs[i] = new Job(id, duration, -1, jobs[i].getSuccessors(), resources);
        }

        return newJobs;
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

    private int[] readResources(String[] parts) {
        int[] resources = new int[parts.length - 4];
        for (int i = 0; i < resources.length; ++i)
        {
            resources[i] = Integer.parseInt(parts[i + 4]);
        }

        return resources;
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
}
