package com.cardio_generator;

import com.datamanagement.DataStorage;
import com.cardiogenerator.HealthDataSimulator;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            HealthDataSimulator.main(new String[]{});
        }
    }
}
