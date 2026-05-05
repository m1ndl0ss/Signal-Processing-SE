package com.alerts;

import com.datamanagement.DataStorage;
import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }

    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        checkBloodPressureTrend(patient, records);
        checkBloodPressureCritical(patient, records);
        checkLowSaturation(patient, records);
        checkRapidSaturationDrop(patient, records);
        checkHypotensiveHypoxemia(patient, records);
        checkEcgAbnormalPeak(patient, records);
        checkTriggeredAlert(patient, records);
    }

    private void checkBloodPressureTrend(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");

        if (hasTrend(systolic)) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Blood Pressure Trend Alert: Systolic", System.currentTimeMillis()));
        }
        if (hasTrend(diastolic)) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Blood Pressure Trend Alert: Diastolic", System.currentTimeMillis()));
        }
    }

    private boolean hasTrend(List<PatientRecord> records) {
        for (int i = 2; i < records.size(); i++) {
            double diff1 = records.get(i - 1).getMeasurementValue() - records.get(i - 2).getMeasurementValue();
            double diff2 = records.get(i).getMeasurementValue() - records.get(i - 1).getMeasurementValue();
            if (diff1 > 10 && diff2 > 10) return true;
            if (diff1 < -10 && diff2 < -10) return true;
        }
        return false;
    }

    private void checkBloodPressureCritical(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double val = record.getMeasurementValue();
            if (type.equals("SystolicPressure") && (val > 180 || val < 90)) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Critical Systolic Pressure", record.getTimestamp()));
            }
            if (type.equals("DiastolicPressure") && (val > 120 || val < 60)) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Critical Diastolic Pressure", record.getTimestamp()));
            }
        }
    }

    private void checkLowSaturation(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : filterByType(records, "Saturation")) {
            if (record.getMeasurementValue() < 92) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Low Saturation", record.getTimestamp()));
            }
        }
    }

    private void checkRapidSaturationDrop(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> satRecords = filterByType(records, "Saturation");
        long tenMinutes = 10 * 60 * 1000;
        for (int i = 0; i < satRecords.size(); i++) {
            for (int j = i + 1; j < satRecords.size(); j++) {
                long timeDiff = satRecords.get(j).getTimestamp() - satRecords.get(i).getTimestamp();
                if (timeDiff > tenMinutes) break;
                double drop = satRecords.get(i).getMeasurementValue() - satRecords.get(j).getMeasurementValue();
                if (drop >= 5) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Rapid Saturation Drop", satRecords.get(j).getTimestamp()));
                }
            }
        }
    }

    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> saturation = filterByType(records, "Saturation");

        for (PatientRecord sp : systolic) {
            if (sp.getMeasurementValue() < 90) {
                for (PatientRecord sat : saturation) {
                    if (sat.getMeasurementValue() < 92) {
                        triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Hypotensive Hypoxemia Alert", sp.getTimestamp()));
                        return;
                    }
                }
            }
        }
    }

    private void checkEcgAbnormalPeak(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = filterByType(records, "ECG");
        int windowSize = 10;
        for (int i = windowSize; i < ecgRecords.size(); i++) {
            double sum = 0;
            for (int j = i - windowSize; j < i; j++) {
                sum += ecgRecords.get(j).getMeasurementValue();
            }
            double avg = sum / windowSize;
            double current = ecgRecords.get(i).getMeasurementValue();
            if (Math.abs(current - avg) > 2 * avg) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "ECG Abnormal Peak", ecgRecords.get(i).getTimestamp()));
            }
        }
    }

    private void checkTriggeredAlert(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : filterByType(records, "Alert")) {
            if (record.getMeasurementValue() == 1.0) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Triggered Alert", record.getTimestamp()));
            }
        }
    }

    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> result = new ArrayList<>();
        for (PatientRecord record : records) {
            if (record.getRecordType().equals(type)) {
                result.add(record);
            }
        }
        return result;
    }

    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
        System.out.println("ALERT - Patient " + alert.getPatientId() +
                ": " + alert.getCondition() +
                " at " + alert.getTimestamp());
    }
}
