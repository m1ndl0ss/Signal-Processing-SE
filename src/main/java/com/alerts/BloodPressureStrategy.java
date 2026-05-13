package com.alerts;

import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BloodPressureStrategy implements AlertStrategy {
    private AlertFactory factory = new BloodPressureAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord>  records = patient.getRecords(0, Long.MAX_VALUE);
        List<PatientRecord> systolic = records.stream().filter(r ->  r.getRecordType().equals("SystolicPressure")).collect(Collectors.toList());
        List<PatientRecord> diastolic = records.stream().   filter(r -> r.getRecordType().equals("DiastolicPressure")).collect(Collectors.toList());

        if (hasTrend(systolic)) {
            alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()), "Blood Pressure Trend Alert: Systolic", System.currentTimeMillis()));
        }
        if (hasTrend(diastolic)) {
            alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()), "Blood Pressure Trend Alert: Diastolic", System.currentTimeMillis()));}
        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double val = record.getMeasurementValue();
            if (type.equals("SystolicPressure") && (val > 180 || val < 90)) {
                Alert alert = factory.createAlert(String.valueOf(patient.getPatientId()), "Critical Systolic Pressure", record.getTimestamp());
                alerts.add(new PriorityAlertDecorator(alert));}
            else if (type.equals("DiastolicPressure") && (val > 120 || val < 60)) {
                Alert alert = factory.createAlert(String.valueOf(patient.getPatientId()), "Critical Diastolic Pressure", record.getTimestamp());     
                    alerts.add(new PriorityAlertDecorator(alert)); }}
        return alerts;
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
}