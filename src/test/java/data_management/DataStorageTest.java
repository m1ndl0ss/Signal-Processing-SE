package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.datamanagement.DataStorage;
import com.datamanagement.PatientRecord;

import java.util.List;

class DataStorageTest {

    @BeforeEach
    void setUp() {}
    @Test
    void testSingletonInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        assertSame(instance1, instance2, "DataStorge instances should be exactly the same object");
     }

    @Test
    void testAddAndGetRecords() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        boolean foundFirst = records.stream().anyMatch(r -> r.getMeasurementValue() == 100.0 && r.getTimestamp() == 1714376789050L);
        boolean foundSecond = records.stream().anyMatch(r -> r.getMeasurementValue() == 200.0 && r.getTimestamp() == 1714376789051L);

        assertTrue(foundFirst, "First record should be retrieved");
        assertTrue(foundSecond, "Second record should be retrieved");
    }
}