package com.alerts;

import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HeartRateStrategy implements AlertStrategy {
    private AlertFactory factory = new ECGAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();

        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        List<PatientRecord> ecgRecords = records.stream(). filter(r -> r.getRecordType().equals("ECG")).collect(Collectors.toList());

        int windowSize = 10;
        for (int i = windowSize; i < ecgRecords.size(); i++) {
            double sum = 0;
            for (int j = i - windowSize; j < i; j++) {
                sum += ecgRecords.get(j).getMeasurementValue();}
            double avg = sum / windowSize;
            double current = ecgRecords.get(i).getMeasurementValue();
            if (Math.abs(current - avg) > 2 * avg) {
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()), "ECG Abnormal Peak", ecgRecords.get(i).getTimestamp()));
            }
        }
        return alerts;}
}