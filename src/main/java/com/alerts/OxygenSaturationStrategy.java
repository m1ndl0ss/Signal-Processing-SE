package com.alerts;

import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OxygenSaturationStrategy implements AlertStrategy {
    private AlertFactory factory = new BloodOxygenAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        
        List<PatientRecord> satRecords = records.stream().filter(r -> r.getRecordType().equals("Saturation")).collect(Collectors.toList());
        List<PatientRecord> systolicRecords = records.stream() .filter(r -> r.getRecordType().equals("SystolicPressure")).collect(Collectors.toList());

        for (PatientRecord record : satRecords) {
            if (record.getMeasurementValue() < 92) {
                    Alert alert = factory.createAlert(String.valueOf(patient.getPatientId()), "Low Saturation", record.getTimestamp());     
                    alerts.add(new RepeatedAlertDecorator(alert)); 
                }
         }
        long tenMinutes = 10 * 60 * 1000;
        for (int i = 0; i < satRecords.size(); i++) {
            for (int j = i + 1; j < satRecords.size(); j++) {
                long timeDiff = satRecords.get(j).getTimestamp() - satRecords.get(i).getTimestamp();
                if (timeDiff > tenMinutes) break;
                double drop = satRecords.get(i).getMeasurementValue() - satRecords.get(j).getMeasurementValue();
                if (drop >= 5) {
                    alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()), "Rapid Saturation Drop", satRecords.get(j).getTimestamp()));
                }
            }
        }
        for (PatientRecord sp : systolicRecords) {
            if (sp.getMeasurementValue()  < 90) {
                for (PatientRecord sat : satRecords) {
                    if (sat.getMeasurementValue() < 92) {
                        alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()), "Hypotensive Hypoxemia Alert", sp.getTimestamp()));
                        return alerts; // Returns immediately once found to mirror original logic
                    }
                }
            }
        }
        return alerts;
    }
}