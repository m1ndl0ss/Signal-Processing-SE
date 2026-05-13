package com.alerts;

import com.datamanagement.Patient;
import java.util.List;

public interface AlertStrategy {
    List<Alert> checkAlert(Patient patient);
}