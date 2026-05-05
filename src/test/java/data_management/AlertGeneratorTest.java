package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.AlertGenerator;
import com.datamanagement.DataStorage;
import com.datamanagement.Patient;

class AlertGeneratorTest {

    @Test
    void testSystolicIncreasingTrendTriggersAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 130.0, "SystolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Blood Pressure Trend Alert: Systolic")));
    }

    @Test
    void testSystolicDecreasingTrendTriggersAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 130.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "SystolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Blood Pressure Trend Alert: Systolic")));
    }

    @Test
    void testSystolicAbove180TriggersCriticalAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 185.0, "SystolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Critical Systolic Pressure")));
    }

    @Test
    void testSystolicBelow90TriggersCriticalAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Critical Systolic Pressure")));
    }

    @Test
    void testDiastolicAbove120TriggersCriticalAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 125.0, "DiastolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Critical Diastolic Pressure")));
    }

    @Test
    void testDiastolicBelow60TriggersCriticalAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 55.0, "DiastolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Critical Diastolic Pressure")));
    }

    @Test
    void testLowSaturationTriggersAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 91.0, "Saturation", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Low Saturation")));
    }

    @Test
    void testRapidSaturationDropTriggersAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 97.0, "Saturation", now);
        storage.addPatientData(1, 92.0, "Saturation", now + 5 * 60 * 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Rapid Saturation Drop")));
    }

    @Test
    void testHypotensiveHypoxemiaTriggersAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 91.0, "Saturation", 2000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Hypotensive Hypoxemia Alert")));
    }

    @Test
    void testTriggeredAlertRecordFiresAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 1.0, "Alert", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Triggered Alert")));
    }
}
