package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.AlertGenerator;
import com.alerts.Alert;
import com.alerts.BaseAlert;
import com.alerts.PriorityAlertDecorator;
import com.alerts.RepeatedAlertDecorator;
import com.datamanagement.DataStorage;
import com.datamanagement.Patient;

class AlertGeneratorTest {

    @Test
    void testSystolicIncreasingTrendTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 130.0, "SystolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Blood Pressure Trend Alert: Systolic")));
    }

    @Test
    void testSystolicDecreasingTrendTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 130.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "SystolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Blood Pressure Trend Alert: Systolic")));
    }

    @Test
    void testSystolicAbove180TriggersCriticalAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 185.0, "SystolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Critical Systolic Pressure")));
    }

    @Test
    void testSystolicBelow90TriggersCriticalAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Critical Systolic Pressure")));
    }

    @Test
    void testDiastolicAbove120TriggersCriticalAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 125.0, "DiastolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Critical Diastolic Pressure")));
    }

    @Test
    void testDiastolicBelow60TriggersCriticalAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 55.0, "DiastolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Critical Diastolic Pressure")));
    }

    @Test
    void testLowSaturationTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 91.0, "Saturation", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Low Saturation")));
    }

    @Test
    void testRapidSaturationDropTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 97.0, "Saturation", now);
        storage.addPatientData(1, 92.0, "Saturation", now + 5 * 60 * 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Rapid Saturation Drop")));
    }

    @Test
    void testHypotensiveHypoxemiaTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 91.0, "Saturation", 2000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Hypotensive Hypoxemia Alert")));
    }

    @Test
    void testTriggeredAlertRecordFiresAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 1.0, "Alert", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(generator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Triggered Alert")));
    }

    @Test
    void testDecoratorsAlterConditionString() {
        Alert base1 = new BaseAlert("1", "Critical", 1000L);
        Alert priority = new PriorityAlertDecorator(base1);
        assertTrue(priority.getCondition().startsWith("[URGENT]"));
        
        Alert base2 = new BaseAlert("1", "Low Oxygen", 1000L);
        Alert repeated = new RepeatedAlertDecorator(base2);
        assertTrue(repeated.getCondition().endsWith("(Repeated Check Initiated)"));
    }
}