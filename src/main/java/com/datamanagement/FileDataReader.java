package com.datamanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {
    private String directory;

    public FileDataReader(String directory) {
        this.directory = directory;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File folder = new File(directory);
        File[] files = folder.listFiles();
        if (files == null) {
            throw new IOException("Directory not found or empty: " + directory);
        }
        for (File file : files) {
            if (file.isFile()) {
                readFile(file, dataStorage);
            }
        }
    }

    private void readFile(File file, DataStorage dataStorage) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length != 4) {
                    continue;
                }
                int patientId = Integer.parseInt(parts[0].split(": ")[1].trim());
                long timestamp = Long.parseLong(parts[1].split(": ")[1].trim());
                String recordType = parts[2].split(": ")[1].trim();
                double measurementValue = Double.parseDouble(parts[3].split(": ")[1].trim());
                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
        }
    }
}
