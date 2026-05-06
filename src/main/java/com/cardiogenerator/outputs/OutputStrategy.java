package com.cardiogenerator.outputs;

/**
 * Defines a strategy for outputting patient data.
 */


public interface OutputStrategy {
    /**
     * Outputs the specified patient data.
     * @param patientId The ID of the patient.
     * @param timestamp The timestamp of the data generation.
     * @param label   The label or category of the data (e.g., "Saturation").
     * @param data     The actual data value to be outputted.*/
    void output(int patientId, long timestamp, String label, String data);
}
