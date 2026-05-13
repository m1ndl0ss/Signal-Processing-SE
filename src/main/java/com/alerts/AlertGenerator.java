package com.alerts;

import com.datamanagement.DataStorage;
import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();
  private List<AlertStrategy> strategies = new ArrayList<>();

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
     this.strategies.add(new BloodPressureStrategy());
      this.strategies.add(new HeartRateStrategy());
     this.strategies.add(new OxygenSaturationStrategy()); }

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }

    public void evaluateData(Patient patient) {
     for (AlertStrategy strategy : strategies) {
         List<Alert> alerts = strategy.checkAlert(patient);         
          for (Alert alert : alerts) {
            triggerAlert(alert);
         }}

     List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
      for (PatientRecord record : records) {
         if (record.getRecordType().equals("Alert") && record.getMeasurementValue() == 1.0){
            triggerAlert(new BaseAlert(String.valueOf(patient.getPatientId()), "Triggered Alert", record.getTimestamp()));          }
     }
    }

    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
        System.out.println("ALERT - Patient " + alert.getPatientId() + ": " + alert.getCondition() +" at " + alert.getTimestamp());
    }
}