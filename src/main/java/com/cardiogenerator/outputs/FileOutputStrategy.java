package com.cardiogenerator.outputs;
//package name changed to use only lowercase letters (Section 5.2.1)


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;


// Added Javadoc: Google Java Style Guide 7.3.1: every public class must have Javadoc.
/**
 * Implementation of {@link OutputStrategy} that writes patient data to
 * text files, organized by label in a configurable base directory.
 */

public class FileOutputStrategy implements OutputStrategy {

    //name of the variable changed to lowerCamelCase (Section 5.2.3)
    private String baseDirectory;

    //name of the variable changed to lowerCamelCase (Section 5.2.3)
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();


    // Added Javadoc: Google Java Style Guide 7.3.1: public member must have a javadoc
    /**
        * Constructs a {@code FileOutputStrategy} with the specified base directory.
        * @param baseDirectory the directory where output files will be created
    */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        //name of the variable changed to lowerCamelCase Section 5.2.4)
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}